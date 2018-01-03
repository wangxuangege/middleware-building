package com.wx.zookeeper.myrpc.task;

import com.wx.zookeeper.myrpc.config.RpcConfig;
import com.wx.zookeeper.myrpc.config.RpcConstant;
import com.wx.zookeeper.myrpc.context.RpcContext;
import com.wx.zookeeper.myrpc.exception.RpcException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.*;

/**
 * @author xinquan.huangxq
 */
@Slf4j
@Service
public class TaskExecutor {

    @Autowired
    private RpcConfig config;

    private ScheduledExecutorService scheduledExecutor;

    private ExecutorService synchronousExecutor;

    @PostConstruct
    public void start() {
        scheduledExecutor = new ScheduledThreadPoolExecutor(config.getScheduledTaskExecutorPoolSize());
        synchronousExecutor = new ThreadPoolExecutor(config.getMinTaskExecutorPoolSize(), config.getMaxTaskExecutorPoolSize(), 600L, TimeUnit.SECONDS, new SynchronousQueue<>());
    }

    @PreDestroy
    public void stop() {
        scheduledExecutor.shutdown();
        synchronousExecutor.shutdown();
    }

    /**
     * 立即执行任务，如果线程池满，直接拒绝执行
     *
     * @param context
     * @param task
     */
    public void executeImmediately(final RpcContext context, final Task task) {
        try {
            synchronousExecutor.execute(TaskRun.of(context, task));
        } catch (RejectedExecutionException e) {
            handleRejectedExecutionException(context, e);
        }
    }

    /**
     * 等待delay毫秒后执行任务
     *
     * @param context
     * @param task
     * @param delay
     */
    public void execute(final RpcContext context, final Task task, int delay) {
        try {
            scheduledExecutor.schedule(TaskRun.of(context, task), delay, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            handleRejectedExecutionException(context, e);
        }
    }

    /**
     * 立即执行任务，使用无界队列
     *
     * @param context
     * @param task
     */
    public void execute(final RpcContext context, final Task task) {
        try {
            scheduledExecutor.execute(TaskRun.of(context, task));
        } catch (RejectedExecutionException e) {
            handleRejectedExecutionException(context, e);
        }
    }

    /**
     * 处理线程池拒绝异常
     *
     * @param context
     * @param e
     */
    private static void handleRejectedExecutionException(final RpcContext context, final RejectedExecutionException e) {
        log.error("系统超载，线程池耗尽！");
        if (context != null && context.getExceptionHandler() != null) {
            RpcException rpcException = new RpcException(RpcConstant.ErrorCode.SYSTEM_BUSY, "系统繁忙，请稍后再试");
            context.getExceptionHandler().handleException(context, rpcException);
        }
    }

    /**
     * 任务执行包装
     */
    private static class TaskRun implements Runnable {

        private final RpcContext context;

        private final Task task;

        private TaskRun(final RpcContext context, final Task task) {
            this.context = context;
            this.task = task;
        }

        public static TaskRun of(final RpcContext context, final Task task) {
            return new TaskRun(context, task);
        }

        @Override
        public void run() {
            try {
                task.run();
            } catch (Throwable e) {
                if (context != null && context.getExceptionHandler() != null) {
                    context.getExceptionHandler().handleException(context, e);
                } else {
                    log.error("未捕获的异常", e);
                }
            }
        }
    }

}
