package cn.com.devopsplus.dop.server.cipipeline.mq;

/**
 * 消息发送者接口, 对不同消息队列发送消息进行统一
 *
 * @author yangyuyan
 * @since 2023-01-01
 */
public interface MessageSender {

    /**
     * 各项目的pr请求持续集成结果预测失败或缓存中等待超时，加入到等待执行队列等待执行
     * @param messageId
     * @param data
     * @throws MessageQueueException
     */
    void sendToExecuteQueue(String messageId, String data) throws MessageQueueException;
}