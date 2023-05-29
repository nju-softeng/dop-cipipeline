package com.artdev.analyzers.pipreqs;

import com.artdev.analyzers.Analyzer;
import org.springframework.beans.factory.annotation.Value;
import util.ExecHelper;
import vo.ResultVO;

public class PipreqsAnalyzer implements Analyzer {

    @Value("${web.py-save-path}")
    private String savePath;

    @Value("${web.pypi-server}")
    private String pypiServer;

    @Override
    public ResultVO analyze(String filename) {
        //ResultVO res= ExecHelper.execCommand("pipreqs --pypi-server "+pypiServer+"  --print ./"+savePath,"","\n");
        ResultVO res= ExecHelper.execCommand("pipreqs --use-local --print ./"+savePath,"","\n");
        return res;
    }

    public ResultVO analyze() {
        ResultVO res= ExecHelper.execCommand("pipreqs --use-local --print ./"+savePath,"","\n");
        //ResultVO res= ExecHelper.execCommand("pipreqs --pypi-server "+pypiServer+"  --print ./"+savePath,"","\n");
        return res;
    }
}
