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
@FeignClient(value = "testmanage-testnode-server", configuration = FeignConfig.class)
public interface TestManageFeign {
    /**
     *
     * @param testData
     * @return
     */
    @PostMapping("/v1/testManager/testNode/handleTestRequest")
    String handleTestRequest(@RequestBody String testData);
}
