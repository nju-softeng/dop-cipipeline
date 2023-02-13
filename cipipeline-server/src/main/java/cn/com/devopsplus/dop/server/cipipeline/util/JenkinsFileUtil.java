package cn.com.devopsplus.dop.server.cipipeline.util;

import cn.com.devopsplus.dop.server.cipipeline.model.po.configInfo.ConfigInfo;
import cn.com.devopsplus.dop.server.cipipeline.util.step.StepGenerator;

import java.util.*;
import java.util.function.Function;
import cn.com.devopsplus.dop.server.cipipeline.util.step.generator.FailurePredict;
import cn.com.devopsplus.dop.server.cipipeline.util.step.generator.BuildMaven;
import cn.com.devopsplus.dop.server.cipipeline.util.step.generator.TestCaseSelect;
import cn.com.devopsplus.dop.server.cipipeline.util.step.generator.Test;
import cn.com.devopsplus.dop.server.cipipeline.util.step.generator.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * JenkinsFile文件自动生成
 *
 * @author yangyuyan
 * @since 2023-01-16
 */
public class JenkinsFileUtil {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final Map<StepType, StepGenerator> stepGenerators = new HashMap<>();
    @Autowired
    private Environment environment;

    public enum StepType {
        FailurePredict,
        BuildMaven,
        TestCaseSelect,
        Test,
        Default
    }

    static {
        stepGenerators.put(StepType.FailurePredict, FailurePredict::generate);
        stepGenerators.put(StepType.BuildMaven, BuildMaven::generate);
        stepGenerators.put(StepType.TestCaseSelect, TestCaseSelect::generate);
        stepGenerators.put(StepType.Test, Test::generate);
    }

    public String generate(ConfigInfo configInfo) {
        if (configInfo == null){
            return "";
        }
        String directory=environment.getProperty("localCodeBasePath")+configInfo.getOwnerAndRepo();
        Map<String,Object> valuesmap=new HashMap<>();
        valuesmap.put("ownerAndRepo",configInfo.getOwnerAndRepo());
        StringBuilder stagesBuilder = new StringBuilder();
        for (String stage : configInfo.getAllStages().split("\\-")) {
            System.out.println(stage);
            StepGenerator stepGenerator=null;
            if(stage.equals("failurePredict")){
                stepGenerator = stepGenerators.getOrDefault(StepType.FailurePredict, Default::generate);
            }
            else if(stage.equals("build")){
                stepGenerator = stepGenerators.getOrDefault(StepType.BuildMaven, Default::generate);
            }
            else if(stage.equals("testCaseSelect")){
                stepGenerator = stepGenerators.getOrDefault(StepType.TestCaseSelect, Default::generate);
            }
            else if(stage.equals("test")){
                stepGenerator = stepGenerators.getOrDefault(StepType.Test, Default::generate);
            }
            else{
                continue;
            }
            String stageHeader = String.format("stage('%s') {", stage);
            stagesBuilder.append(stageHeader).append(LINE_SEPARATOR);
            stagesBuilder
                    .append("steps {").append(LINE_SEPARATOR)
                    .append(stepGenerator.generate(directory,valuesmap)).append(LINE_SEPARATOR)
                    .append("}").append(LINE_SEPARATOR);
            stagesBuilder.append("}").append(LINE_SEPARATOR);
        }

        String jenkinsfile = String.join(LINE_SEPARATOR,
                "pipeline {",
                "agent any",
                "stages {",
                stagesBuilder,
                "}",
                "}"
        );

        return indent(jenkinsfile);
    }

    private String indent(String jenkinsfile) {
        StringBuilder builder = new StringBuilder();
        Function<Integer, String> indentWhitespace = count ->
                String.join("", Collections.nCopies(count * 2, " "));

        int count = 0;
        boolean isInQuotation = false;
        for (String line : jenkinsfile.split(LINE_SEPARATOR)) {
            // a line only contains whitespaces need to be dropped
            if (line.trim().isEmpty()) continue;

            if (line.contains("}"))
                count = Math.max(count - 1, 0);

            builder.append(indentWhitespace.apply(isInQuotation?0:count))
                    .append(line)
                    .append(LINE_SEPARATOR);

            if (line.contains("'''"))
                isInQuotation = !isInQuotation;
            if (line.contains("{"))
                count++;
        }

        return builder.toString();
    }
}
