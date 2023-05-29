package com.artdev.analyzers.jdeps;

import com.artdev.analyzers.Analyzer;
import org.springframework.beans.factory.annotation.Value;
import util.ExecHelper;
import vo.ResultVO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JdepsAnalyzer implements Analyzer {

    @Value("${web.file-save-path}")
    private String savePath;


    @Override
    public ResultVO analyze(String filename) {
        ResultVO res= ExecHelper.execCommand("jdeps "+savePath+filename,"");
        return res;
    }


}
