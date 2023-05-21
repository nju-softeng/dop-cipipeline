package com.example.agent.feign;

import com.example.agent.config.FeignConfig;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(value = "cipipeline-server", configuration = FeignConfig.class)
public interface CIPipelineFeign {
    /**
     *
     * @param configInfoId
     * @return
     */
    @GetMapping("/v1/cipipeline/configInfo/jenkinsFiles/{configInfoId}")
    String getJenkinsFile(
            @ApiParam(value = "configInfoId", name = "configInfoId", required = true) @PathVariable(value = "configInfoId") Long configInfoId);

    @PostMapping("/v1/cipipeline/coreScheduler/saveTestResult")
    void saveTestResult(@RequestBody String resultStr);
}
