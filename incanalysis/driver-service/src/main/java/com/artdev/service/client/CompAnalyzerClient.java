package com.artdev.service.client;

import com.artdev.config.FeignMultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import vo.ResultVO;

@FeignClient(name = "companalyzerservice",configuration = FeignMultipartSupportConfig.class)
public interface CompAnalyzerClient {
    @PostMapping(value = "/compAnalyze/analyze",headers = "content-type=multipart/form-data")
    ResultVO compAnalyze(@RequestPart("file") MultipartFile[] file);
}
