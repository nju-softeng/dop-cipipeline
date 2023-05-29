package com.artdev.analyzers.checkstyle;

import com.artdev.analyzers.Analyzer;
import org.springframework.beans.factory.annotation.Value;
import util.ExecHelper;
import vo.ResultVO;

import javax.xml.transform.Result;


public class CheckStyleAnalyzer implements Analyzer {
    @Value("${web.file-save-path}")
    private String savePath;

    @Value("${web.path}")
    private String path;

    @Value("${checkstyle.checkstyle-jar-name}")
    private String checkstyleJarName;

    @Override
    public ResultVO analyze() {
        ResultVO res=ExecHelper.execCommand("java -jar "+path
                +checkstyleJarName
        +" -c "+path+"checkstyleConfig.xml "+savePath,"UTF-8");
        return res;
    }
}
