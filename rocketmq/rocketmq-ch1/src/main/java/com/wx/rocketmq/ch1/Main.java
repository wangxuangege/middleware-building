package com.wx.rocketmq.ch1;

import org.apache.rocketmq.client.consumer.*;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author xinquan.huangxq
 */
public class Main {

    private static final String NAME_SRV_ADDRESS = "192.168.171.130:9876";

    public static void main(String[] args) throws Exception {
        // produce();
        // consume4Push();
        consume4Pull();
    }

    private static void consume4Pull() throws MQClientException {
        final MQPullConsumerScheduleService scheduleService = new MQPullConsumerScheduleService("MyPullConsumer");
        scheduleService.getDefaultMQPullConsumer().setNamesrvAddr(NAME_SRV_ADDRESS);
        scheduleService.setMessageModel(MessageModel.CLUSTERING);
        scheduleService.registerPullTaskCallback("TestTopic", new PullTaskCallback() {

            public void doPullTask(MessageQueue mq, PullTaskContext context) {
                MQPullConsumer consumer = context.getPullConsumer();
                try {
                    // 获取从哪里拉取
                    long offset = consumer.fetchConsumeOffset(mq, false);
                    if (offset < 0)
                        offset = 0;

                    PullResult pullResult = consumer.pull(mq, "*", offset, 32);
                    System.out.println(offset + "\t" + mq + "\t" + pullResult);
                    switch (pullResult.getPullStatus()) {
                        case FOUND:
                            for (MessageExt messageExt : pullResult.getMsgFoundList()) {
                                System.out.println(messageExt);
                            }
                            break;
                        case NO_MATCHED_MSG:
                            break;
                        case NO_NEW_MSG:
                        case OFFSET_ILLEGAL:
                            break;
                        default:
                            break;
                    }
                    // 存储Offset，客户端每隔5s会定时刷新到Broker
                    consumer.updateConsumeOffset(mq, pullResult.getNextBeginOffset());
                    // 设置再过100ms后重新拉取
                    context.setPullNextDelayTimeMillis(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        scheduleService.start();
    }

    private static void consume4Push() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("MyPushConsumer");
        consumer.setNamesrvAddr(NAME_SRV_ADDRESS);
        consumer.subscribe("TestTopic", "*");
        consumer.setConsumeTimeout(2);
        consumer.setMessageListener(new MessageListenerConcurrently() {
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgList, ConsumeConcurrentlyContext context) {
                for (MessageExt msg : msgList) {
                    System.out.print(msg);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
    }

    private static void produce() throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        // 构造Producer
        DefaultMQProducer producer = new DefaultMQProducer("MyProducer");
        producer.setNamesrvAddr(NAME_SRV_ADDRESS);
        // 初始化Producer，整个应用生命周期内，只需要初始化1次
        producer.start();

        // 构造Message
        Message msg = new Message("TestTopic",// topic
                "TagA",// tag：给消息打标签,用于区分一类消息，可为null
                null,// key：自定义Key，可以用于去重，可为null
                ("这是第三条测试消息").getBytes());// body：消息内容

        // 发送消息并返回结果
        SendResult sendResult = producer.send(msg);
        System.out.println(sendResult);

        // 清理资源，关闭网络连接，注销自己
        producer.shutdown();
    }
}
