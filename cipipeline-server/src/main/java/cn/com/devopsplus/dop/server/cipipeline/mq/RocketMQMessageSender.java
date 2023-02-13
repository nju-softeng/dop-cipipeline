package cn.com.devopsplus.dop.server.cipipeline.mq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.UnsupportedEncodingException;

/**
 * RocketMQ发送消息实现类
 *
 * @author yangyuyan
 * @since 2023-01-01
 */
public class RocketMQMessageSender implements MessageSender {

    private static final Logger logger = LoggerFactory.getLogger(RocketMQMessageSender.class);

    private DefaultMQProducer producer;

    @Value("${mq.RocketMQ.namesrvAddr}")
    private String namesrvAddr;
    @Value("${mq.RocketMQ.groupName}")
    private String groupName;
    @Value("${mq.RocketMQ.instanceName}")
    private String instanceName;
    @Value("${mq.RocketMQ.maxMessageSize}")
    private Integer maxMessageSize;
    @Value("${mq.RocketMQ.sendMessageTimeout}")
    private Integer sendMessageTimeout;
    @Value("${mq.RocketMQ.queueTopic}")
    private String queueTopic;

    /**
     * 发送消息到执行等待队列
     *
     * @param messageId
     * @param data
     * @throws MessageQueueException
     * @see MessageSender#sendToExecuteQueue(String, String)
     */
    @Override
    public void sendToExecuteQueue(String messageId, String data) throws MessageQueueException {
        logger.info("[sendToExecuteQueue] send a message to ExecuteQueue: messageId={}, data={}", messageId, data);
        try {
            byte[] body = data.getBytes(RemotingHelper.DEFAULT_CHARSET);
            this.send(this.queueTopic, "ExecuteQueue", messageId, body);
        } catch (UnsupportedEncodingException e) {
            logger.error("RocketMQ producer send fail! key:{}, error:{}", messageId, e.getMessage());
            throw new MessageQueueException(e);
        }
    }

    /**
     * 发送消息
     *
     * @param topic 主题,相当于queue
     * @param tags  标记,二级分类
     * @param keys  key业务唯一标记
     * @param body  消息内容
     */
    private void send(String topic, String tags, String keys, byte[] body) throws MessageQueueException {
        logger.info("[send] send a message to queue: topic={}, tags={}, keys={}, body", topic, tags, keys);
        Message message = new Message(topic, tags, keys, body);
        try {
            this.producer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    logger.info("RocketMQ producer send success! {}", sendResult);
                }

                @Override
                public void onException(Throwable throwable) {
                    logger.error("RocketMQ producer send fail! topic:{}, key:{}, error:{}"
                            , topic, keys, throwable.getMessage());
                }
            });
        } catch (Exception e) {
            logger.error("RocketMQ producer send fail! topic:{}, key:{}, error:{}", topic, keys, e.getMessage());
            throw new MessageQueueException(e);
        }
    }

    /**
     * 初始化RocketMQ生产者
     */
    @PostConstruct
    public void start() throws MessageQueueException {
        this.producer = new DefaultMQProducer(this.groupName);
        this.producer.setNamesrvAddr(this.namesrvAddr);
        this.producer.setInstanceName(instanceName);
        this.producer.setMaxMessageSize(this.maxMessageSize);
        this.producer.setSendMsgTimeout(this.sendMessageTimeout);

        try {
            this.producer.start();
            logger.info("RocketMQ producer is running ! groupName:{}, namesrvAddr:{}", this.groupName, this.namesrvAddr);
        } catch (MQClientException e) {
            logger.error("RocketMQ producer start fail! groupName:{}, namesrvAddr:{}, error:{}", this.groupName, this.namesrvAddr, e.getMessage());
            throw new MessageQueueException(e);
        }
    }

    /**
     * 关闭mq生产者
     */
    @PreDestroy
    public void stop() {
        if (this.producer != null) {
            this.producer.shutdown();
            logger.info("RocketMQ producer is shutdown ! groupName:{} ,namesrvAddr:{}", this.groupName, this.namesrvAddr);
        }
    }
}