package cn.com.devopsplus.dop.server.cipipeline.util.step.generator;

import java.util.Map;

public class Default {

    private static final String LINE_SEPARATOR = System.lineSeparator();

    public static String generate(String directory,Map<String,Object> valuesMap) {
        return "echo 'no such step type'" + LINE_SEPARATOR +
               "echo 'please check out your pipeline stages definition'";
    }
}
