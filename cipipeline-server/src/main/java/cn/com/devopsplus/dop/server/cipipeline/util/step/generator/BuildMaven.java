package cn.com.devopsplus.dop.server.cipipeline.util.step.generator;

import cn.com.devopsplus.dop.server.cipipeline.util.step.StepUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BuildMaven {

    private static final List<String> steps = new ArrayList<>(Arrays.asList(
            "// Build Maven",
            "sh 'mvn --version'",
            "sh 'mvn -U -am clean package'"
    ));

    public static String generate(String directory,Map<String,Object> valuesMap) {
        List<String> steps = BuildMaven.steps;
        steps = StepUtil.wrapperWithDirectory(directory, BuildMaven.steps);

        return StepUtil.generate(steps,valuesMap);
    }
}
