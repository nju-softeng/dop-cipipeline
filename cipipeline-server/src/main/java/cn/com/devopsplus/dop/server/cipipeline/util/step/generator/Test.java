package cn.com.devopsplus.dop.server.cipipeline.util.step.generator;

import cn.com.devopsplus.dop.server.cipipeline.util.step.StepUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Test {

    private static final List<String> steps = new ArrayList<>(Arrays.asList(
            "// Test"

    ));

    public static String generate(String directory,Map<String,Object> valuesMap) {
        List<String> steps = Test.steps;
        steps = StepUtil.wrapperWithDirectory(directory, Test.steps);

        return StepUtil.generate(steps,valuesMap);
    }
}
