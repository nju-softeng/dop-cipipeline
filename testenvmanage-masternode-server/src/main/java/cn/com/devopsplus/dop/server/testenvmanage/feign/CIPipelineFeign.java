package cn.com.devopsplus.dop.server.testenvmanage.feign;

import cn.com.devopsplus.dop.server.testenvmanage.config.FeignConfig;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

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
