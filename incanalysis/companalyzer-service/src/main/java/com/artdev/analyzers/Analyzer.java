package com.artdev.analyzers;

import vo.ResultVO;

public interface Analyzer {
    public ResultVO analyze(String fileName);
}
