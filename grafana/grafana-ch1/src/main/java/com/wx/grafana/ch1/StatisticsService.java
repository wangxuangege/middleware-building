package com.wx.grafana.ch1;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xinquan.huangxq
 */
public class StatisticsService {

    private static final int INTERVAL = 15000;

    private String database;

    private String retentionPolicy;

    private String hostUrl;

    private String user;

    private String pwd;

    private InfluxDB influxdb = null;

    private List<Point> points = Collections.synchronizedList(new ArrayList<>());

    private ReentrantLock lock = new ReentrantLock();

    private Timer reportTimer;

    public StatisticsService(String hostUrl, String database, String retentionPolicy, String user, String pwd) {
        this.hostUrl = hostUrl;
        this.database = database;
        this.retentionPolicy = retentionPolicy;
        this.user = user;
        this.pwd = pwd;
    }

    /**
     * 初始化
     */
    public void init() {
        influxdb = InfluxDBFactory.connect(hostUrl, user, pwd);

        reportTimer = new Timer(true);
        reportTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Point[] pointArray;
                lock.lock();
                try {
                    pointArray = points.toArray(new Point[0]);
                    points.clear();
                } finally {
                    lock.unlock();
                }
                refresh(pointArray);
            }
        }, INTERVAL, INTERVAL);
    }

    /**
     * 刷新入库
     * （持久化）
     *
     * @param pointArray
     */
    private void refresh(Point[] pointArray) {
        if (pointArray == null || pointArray.length == 0) {
            return;
        }

        try {
            BatchPoints batchPoints = BatchPoints
                    .database(database)
                    .retentionPolicy(retentionPolicy)
                    .consistency(InfluxDB.ConsistencyLevel.ALL)
                    .points(pointArray)
                    .build();
            influxdb.write(batchPoints);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 收集数据
     *
     * @param serverName
     * @param dataMap
     */
    public void collect(String serverName, Map<String, StatData> dataMap) {
        lock.lock();
        try {
            long now = System.currentTimeMillis();
            for (StatData data : dataMap.values()) {
                try {
                    Point point = Point.measurement("statistics")
                            .time(now, TimeUnit.MILLISECONDS)
                            .tag("server", serverName)
                            .tag("event", data.getKey())
                            .tag("app", data.getAppName())
                            .tag("category", data.getCategory())
                            .tag("action", data.getAction())
                            .tag("result", data.getResult())
                            .addField("count", data.getCount())
                            .addField("min", data.getMin())
                            .addField("max", data.getMax())
                            .addField("total", data.getTotal())
                            .addField("average", data.getAverage())
                            .build();
                    points.add(point);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
