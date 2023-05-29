package com.artdev.service;

import org.springframework.web.multipart.MultipartFile;
import vo.ResultVO;

public interface CompAnalyzerService {
    public ResultVO compAnalyze(MultipartFile[] multipartFiles);
}
