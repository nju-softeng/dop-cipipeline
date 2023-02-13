package cn.com.devopsplus.dop.server.cipipeline.config;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

/**
 * 业务断言
 *
 * @author yangyuyan
 * @since 2022-12-02
 */
public class BusinessAssert {

    /**
     * 不满足断言抛出异常，显示错误信息
     *
     * @param expression
     * @param message
     */
    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new RuntimeException(message);
        }
    }

    /**
     * 对象为空
     *
     * @param object
     * @param msg
     */
    public static void notNull(Object object, String msg) {

        state(object != null, msg);
    }

    /**
     * 字符串不在数组中
     *
     * @param str
     * @param strs
     * @param msg
     */
    public static void notIn(String str,String[] strs,String msg){
        state(Arrays.asList(strs).contains(str),msg);
    }

    /**
     * long类型数据不相等
     *
     * @param num1
     * @param num2
     * @param msg
     */
    public static void notEqual(long num1,long num2,String msg){
        state(num1==num2,msg);
    }

}
