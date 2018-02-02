package com.wx.grafana.ch1;

import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author xinquan.huangxq
 */
public class Main {

    public static void main(String[] args) {
        // 统计服务
        StatisticsService statisticsService = new StatisticsService("http://192.168.171.131:8086", "test", "autogen", "admin", null);
        statisticsService.init();

        // 统计对象
        Statistics statistics = new Statistics(statisticsService, "middleware-building", "grafana-ch1");
        statistics.init();

        SecureRandom random = new SecureRandom();

        // 轮询添加手机信息
        ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
        service.scheduleAtFixedRate(() -> {
            long count = random.nextInt(10) + 1;
            System.out.println("random=" + count);
            statistics.accumulate("test", "test", random.nextBoolean() ? "success" : "fail", count, 1L);
        }, 0, 1, TimeUnit.SECONDS);
    }
}
