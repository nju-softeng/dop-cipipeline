package cn.com.devopsplus.dop.server.cipipeline.feign;

import cn.com.devopsplus.dop.server.cipipeline.config.FeignConfig;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiParam;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.quartz.SchedulerException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

@Component
@FeignClient(value = "model-manage-server", configuration = FeignConfig.class)
public interface ModelManageFeign {

    @PostMapping("/v1/modelManage/addModels")
    public JSONObject addModels(@RequestBody String data) throws IOException, GitAPIException, SchedulerException;
}
