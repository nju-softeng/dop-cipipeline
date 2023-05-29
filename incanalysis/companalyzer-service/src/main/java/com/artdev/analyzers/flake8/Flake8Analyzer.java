package com.artdev.analyzers.flake8;

import com.artdev.analyzers.Analyzer;
import org.springframework.beans.factory.annotation.Value;
import util.ExecHelper;
import vo.ResultVO;

public class Flake8Analyzer implements Analyzer {
    @Value("${web.file-save-path}")
    private String savePath;

    @Override
    public ResultVO analyze(String fileName) {
        ResultVO res= ExecHelper.execCommand("flake8 --select F "+savePath+fileName,"","\n");
        return res;
    }
}
