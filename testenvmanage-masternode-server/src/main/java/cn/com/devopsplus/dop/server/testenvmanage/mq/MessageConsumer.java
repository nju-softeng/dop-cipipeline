package cn.com.devopsplus.dop.server.testenvmanage.mq;

import cn.com.devopsplus.dop.server.testenvmanage.service.DataTransmitService;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

/**
 * 消息队列消费者
 *
 * @author yangyuyan
 * @since 2023-01-25
 */
@Component
public class MessageConsumer implements MessageListenerOrderly {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataTransmitService dataTransmitService;
    private DefaultMQPushConsumer consumer;

    @Value("${mq.RocketMQ.namesrvAddr}")
    private String namesrvAddr;
    @Value("${mq.RocketMQ.queueTopic}")
    private String topic;
    @Value("${mq.RocketMQ.consumerGroup}")
    private String consumerGroup;


    /**
     * 初始化
     */
    @PostConstruct
    public void start() {
        this.dataTransmitService.setFreeNodeNumber(0);
        try {
            logger.info("MQ：启动MessageConsumer, namesrvAddr: [{}],  topic: [{}], consumerGroup: [{}]",
                    namesrvAddr, topic, consumerGroup);
            consumer = new DefaultMQPushConsumer(this.consumerGroup);
            consumer.setNamesrvAddr(namesrvAddr);
            // 从消息队列头部消费
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            // 集群消费模式
            consumer.setMessageModel(MessageModel.CLUSTERING);
            // 订阅主题
            consumer.subscribe(topic, "*");
            // 注册消息监听器
            consumer.registerMessageListener(this);
            // 启动消费端
            consumer.start();
        } catch (MQClientException e) {
            logger.error("MQ：启动MessageConsumer失败：{}-{}", e.getResponseCode(), e.getErrorMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 消费消息
     */
    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        while(this.dataTransmitService.getFreeNodeNumber()==0){}
        int index = 0;
        try {
            for (; index < msgs.size(); index++) {
                MessageExt msg = msgs.get(index);
                String messageBody = new String(msg.getBody(), RemotingHelper.DEFAULT_CHARSET);
                logger.info("MQ：MessageConsumer接收新信息: msgId={} topic={} tags={} keys={} messageBody={}", msg.getMsgId(), msg.getTopic(), msg.getTags(), msg.getKeys(), messageBody);
                this.dataTransmitService.subFreeNodeNumber();
                JSONObject transmitDataJsonObject=this.dataTransmitService.generateTransmitData(msg.getKeys(),messageBody);
                System.out.println(transmitDataJsonObject);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return ConsumeOrderlyStatus.SUCCESS;
    }

    @PreDestroy
    public void stop() {
        if (consumer != null) {
            consumer.shutdown();
            logger.error("MQ：关闭MessageConsumer");
        }
    }

}
