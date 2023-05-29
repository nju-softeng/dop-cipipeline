package com.artdev.service.client;

import com.artdev.config.FeignMultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import po.File;
import vo.ResultVO;

import java.util.List;

@FeignClient(name = "depanalyzerservice",configuration = FeignMultipartSupportConfig.class)
public interface DepAnalyzerClient {

    @PostMapping(value = "/depAnalyze/analyze",headers = "content-type=multipart/form-data")
    ResultVO depAnalyze(@RequestParam String content,@RequestPart("file") MultipartFile[] file);


}
