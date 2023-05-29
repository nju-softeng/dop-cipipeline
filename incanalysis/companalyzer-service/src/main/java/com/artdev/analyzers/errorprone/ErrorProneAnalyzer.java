package com.artdev.analyzers.errorprone;

import com.artdev.analyzers.Analyzer;
import org.springframework.beans.factory.annotation.Value;
import util.ExecHelper;
import vo.ResultVO;

public class ErrorProneAnalyzer implements Analyzer {
    @Value("${web.file-save-path}")
    private String savePath;

    @Value("${web.path}")
    private String path;

    @Value("${errorprone.errorprone-jar-name}")
    private String errorproneJarName;

    @Override
    public ResultVO analyze(String fileName) {
        ResultVO res= ExecHelper.execCommand("java -Xbootclasspath/p:"+path+errorproneJarName+" com.google.errorprone.ErrorProneCompiler "+savePath+fileName,"");
        return res;
    }
}
