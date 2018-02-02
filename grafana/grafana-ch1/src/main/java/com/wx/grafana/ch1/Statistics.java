package com.wx.grafana.ch1;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xinquan.huangxq
 */
public class Statistics {

    private static final int INTERVAL = 15000;

    private StatisticsService statisticsService;

    private String serverName;

    private String appName;

    private Map<String, StatData> map = new ConcurrentHashMap<String, StatData>();

    private Timer reportTimer;

    public Statistics(StatisticsService statisticsService, String appName, String serverName) {
        this.statisticsService = statisticsService;

        this.appName = appName;
        this.serverName = serverName;
    }

    /**
     * 初始化
     */
    public void init() {
        reportTimer = new Timer(true);
        reportTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    System.out.println("Report statistics data.");
                    if (map.size() > 0) {
                        // make a copy of original map to avoid changing during collecting.
                        Map<String, StatData> copy = new HashMap<String, StatData>(map.size());
                        copy.putAll(map);
                        map.clear();

                        statisticsService.collect(serverName, copy);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, INTERVAL, INTERVAL);
    }

    /**
     * 统计
     *
     * @param category
     * @param action
     * @param result
     * @param count
     * @param cost
     */
    public void accumulate(String category, String action, String result, long count, double cost) {
        try {
            String key = String.format("%s.%s.%s.%s", appName, category, action, result);
            StatData c = map.get(key);
            if (c == null) {
                c = new StatData(key, appName, category, action, result, new ReentrantLock());
                map.put(key, c);
            }
            c.accumulate(count, cost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
