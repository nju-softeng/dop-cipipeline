package com.artdev.service;

import vo.ResultVO;
import org.springframework.web.multipart.MultipartFile;

public interface FileAnalyzerService {
    public ResultVO fileAnalyze(MultipartFile[] multipartFile);
}
