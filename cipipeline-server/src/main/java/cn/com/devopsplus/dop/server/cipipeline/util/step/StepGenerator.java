package cn.com.devopsplus.dop.server.cipipeline.util.step;

import java.util.Map;

public interface StepGenerator {
    String generate(String directory,Map<String,Object> valuesMap);
}
