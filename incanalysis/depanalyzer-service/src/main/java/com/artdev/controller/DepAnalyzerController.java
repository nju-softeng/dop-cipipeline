package com.artdev.controller;


import com.artdev.service.DepAnalyzerService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import po.File;
import vo.ResultVO;
import vo.file.FileVO;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/depAnalyze")
public class DepAnalyzerController {

    @Resource
    DepAnalyzerService depAnalyzerService;

    @PostMapping("/analyze")
    public ResultVO depAnalyze(@RequestParam String content,@RequestPart("file") MultipartFile[] file) {
        return depAnalyzerService.depAnalyze(content,file);
    }

}
