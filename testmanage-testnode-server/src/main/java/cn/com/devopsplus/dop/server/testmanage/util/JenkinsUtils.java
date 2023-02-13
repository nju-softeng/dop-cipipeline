package cn.com.devopsplus.dop.server.testmanage.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JenkinsUtils {

    public static String uri="http://127.0.0.1:8080";
    public static String username="dop";
    public static String password="123456";

    public JenkinsUtils() {
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
