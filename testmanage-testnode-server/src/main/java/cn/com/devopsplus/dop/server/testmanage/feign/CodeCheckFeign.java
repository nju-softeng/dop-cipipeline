package cn.com.devopsplus.dop.server.testmanage.feign;

import cn.com.devopsplus.dop.server.testmanage.config.FeignConfig;
import com.alibaba.fastjson.JSONObject;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Component
@FeignClient(value = "driverservice", configuration = FeignConfig.class)
public interface CodeCheckFeign {

    @GetMapping("/analyzer/analyze")
    public String getAnalyzerResult(@RequestParam String path);
}
