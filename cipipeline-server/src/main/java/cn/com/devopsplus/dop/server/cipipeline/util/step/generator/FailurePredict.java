package cn.com.devopsplus.dop.server.cipipeline.util.step.generator;

import cn.com.devopsplus.dop.server.cipipeline.util.step.StepUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FailurePredict {

    private static final List<String> steps = new ArrayList<>(Arrays.asList(
            "// Failure Predict"
    ));

    public static String generate(String directory,Map<String,Object> valuesMap) {
        List<String> steps = FailurePredict.steps;
        steps = StepUtil.wrapperWithDirectory(directory, FailurePredict.steps);

        return StepUtil.generate(steps,valuesMap);
    }
}
