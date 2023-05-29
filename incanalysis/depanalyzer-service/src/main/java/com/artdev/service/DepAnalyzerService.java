package com.artdev.service;

import org.springframework.web.multipart.MultipartFile;
import po.File;
import vo.ResultVO;
import vo.file.FileVO;

import java.util.List;

public interface DepAnalyzerService {
    public ResultVO depAnalyze(String content,MultipartFile[] multipartFile);
}
