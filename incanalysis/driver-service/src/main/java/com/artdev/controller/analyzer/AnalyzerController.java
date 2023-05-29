package com.artdev.controller.analyzer;

import com.artdev.service.analyzer.AnalyzerService;
import vo.ResultVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/analyzer")
public class AnalyzerController {
    @Resource
    AnalyzerService analyzerService;

    @GetMapping("/analyze")
    public String getAnalyzerResult(@RequestParam String path){
        return analyzerService.getAnalyzeResult(path);
    }
}
