package cn.com.devopsplus.dop.server.cipipeline.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import cn.com.devopsplus.dop.server.cipipeline.mq.MessageSender;
import cn.com.devopsplus.dop.server.cipipeline.mq.RocketMQMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息队列配置类, 可根据配置决定初始化哪个MQ的{@link MessageSender}实现
 *
 * @author yangyuyan
 * @since 2023-01-01
 */
@SpringBootConfiguration
public class MessageQueueConfig {
    private static final Logger logger = LoggerFactory.getLogger(MessageQueueConfig.class);

    @Bean(name = "MessageSender")
    public MessageSender createMessageSender() {
        logger.info("begin init message queue config RocketMQ");
        return new RocketMQMessageSender();
    }
}