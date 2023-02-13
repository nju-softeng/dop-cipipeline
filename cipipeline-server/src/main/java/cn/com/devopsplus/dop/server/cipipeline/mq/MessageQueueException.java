package cn.com.devopsplus.dop.server.cipipeline.mq;

/**
 * MQ自定义异常
 * @author yangyuyan
 * @since 2023-01-01
 */

public class MessageQueueException extends Exception {
    public MessageQueueException() {
        super();
    }

    public MessageQueueException(String message) {
        super(message);
    }

    public MessageQueueException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageQueueException(Throwable cause) {
        super(cause);
    }

    protected MessageQueueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}