package com.artdev.controller;

import com.artdev.service.CompAnalyzerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vo.ResultVO;

import javax.annotation.Resource;

@RestController
@RequestMapping("/compAnalyze")
public class CompAnalyzerController {
    @Resource
    CompAnalyzerService compAnalyzerService;

    @PostMapping("/analyze")
    public ResultVO compAnalyze(@RequestPart("file") MultipartFile[] file) {
        return compAnalyzerService.compAnalyze(file);
    }
}
