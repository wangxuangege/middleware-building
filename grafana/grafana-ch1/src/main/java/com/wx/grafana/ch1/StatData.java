package com.wx.grafana.ch1;

import lombok.Getter;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xinquan.huangxq
 */
public class StatData implements Serializable {

    @Getter
    private String key;

    /**
     * fields
     */
    @Getter
    private long count;

    @Getter
    private double min;

    @Getter
    private double max;

    @Getter
    private double total;

    @Getter
    private double average;

    /**
     * tags
     */
    @Getter
    private String appName;

    @Getter
    private String category;

    @Getter
    private String action;

    @Getter
    private String result;

    /**
     * 计数时候可能需要加锁
     */
    private transient ReentrantLock lock;

    public StatData(String key, String appName, String category, String action, String result, ReentrantLock lock) {
        this.key = key;
        this.lock = lock;
        this.appName = appName;
        this.category = category;
        this.action = action;
        this.result = result;

        count = 0;
        min = 0;
        max = 0;
        total = 0;
        average = 0;
    }

    public StatData(String key) {
        this.key = key;

        count = 0;
        min = 0;
        max = 0;
        total = 0;
        average = 0;
    }

    /**
     * 累计
     *
     * @param count
     * @param cost
     */
    public void accumulate(long count, double cost) {
        if (lock != null) {
            lock.lock();
        }
        try {
            this.count += count;
            if (min < 0.00000001 || cost < min) {
                min = cost;
            }
            if (cost > max) {
                max = cost;
            }
            total += cost;
            if (this.count > 0) {
                average = total / this.count;
            }
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    /**
     * 累计
     *
     * @param data
     */
    public void accumulate(StatData data) {
        if (lock != null) {
            lock.lock();
        }
        try {
            this.count += data.count;
            if (min < 0.00000001 || data.min < min) {
                min = data.min;
            }
            if (data.max > max) {
                max = data.max;
            }
            total += data.total;
            if (this.count > 0) {
                average = total / this.count;
            }
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer(key)
                .append(" ")
                .append(count)
                .append(" ")
                .append(min)
                .append(" ")
                .append(max)
                .append(" ")
                .append(total)
                .append(" ")
                .append(average);
        return buf.toString();
    }
}
