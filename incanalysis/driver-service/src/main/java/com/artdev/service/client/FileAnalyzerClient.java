package com.artdev.service.client;

import com.artdev.config.FeignMultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import vo.ResultVO;

@FeignClient(name = "fileanalyzerservice",configuration = FeignMultipartSupportConfig.class)
public interface FileAnalyzerClient {

    @PostMapping(value = "/fileAnalyze/analyze",headers = "content-type=multipart/form-data")
    ResultVO fileAnalyze(@RequestPart("file") MultipartFile[] file);
}
