package com.artdev.analyzers;

import vo.ResultVO;
import org.springframework.web.multipart.MultipartFile;

public interface Analyzer {
    public ResultVO analyze();
}
