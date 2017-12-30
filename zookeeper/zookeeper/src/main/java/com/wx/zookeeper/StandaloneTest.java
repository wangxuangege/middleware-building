package com.wx.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * zookeeper单机部署测试
 *
 * @author xinquan.huangxq
 */
public class StandaloneTest {

    private static final ZooKeeper zk;

    static {
        try {
            zk = new ZooKeeper("localhost:2181", 30000, new TestWatcher());
            System.out.println("zk connect");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws KeeperException, InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            public void run() {
                try {
                    Stat stat;
                    do {
                        stat = zk.exists("/test", true);
                        Thread.sleep(1000);
                    } while (stat != null);
                    zk.close();
                    System.out.println("zk close");
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        executorService.shutdown();
    }

    private static class TestWatcher implements Watcher {

        public void process(WatchedEvent watchedEvent) {
            StringBuilder sb = new StringBuilder();
            sb.append("path").append(":").append(watchedEvent.getPath()).append(" ")
                    .append("type").append(":").append(watchedEvent.getType()).append(" ")
                    .append("stat").append(":").append(watchedEvent.getState()).append(" ");
            System.out.println(sb.toString());
        }
    }
}
