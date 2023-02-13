package cn.com.devopsplus.dop.server.cipipeline.config;

public class RuleException extends RuntimeException{
    private static final long serialVersionUID = -8624533394127244753L;

    public RuleException() {
    }

    public RuleException(String msg) {
        super(msg);
    }

    public RuleException(Exception e) {
        super(e);
        e.printStackTrace();
    }
}
