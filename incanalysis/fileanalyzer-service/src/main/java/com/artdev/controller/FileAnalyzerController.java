package com.artdev.controller;

import com.artdev.service.FileAnalyzerService;
import org.springframework.web.bind.annotation.*;
import vo.ResultVO;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/fileAnalyze")
public class FileAnalyzerController {
    @Resource
    private FileAnalyzerService fileAnalyzerService;
    @PostMapping("/analyze")
    public ResultVO fileAnalyze(@RequestPart("file") MultipartFile[] file) {
        return fileAnalyzerService.fileAnalyze(file);
    }
}
