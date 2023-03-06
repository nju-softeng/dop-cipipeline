package cn.com.devopsplus.dop.server.cipipeline.controller;

import cn.com.devopsplus.dop.server.cipipeline.config.HttpHeadersConfig;
import cn.com.devopsplus.dop.server.cipipeline.model.vo.ConfigInfoVo;
import cn.com.devopsplus.dop.server.cipipeline.service.CIPipelineConfigInfoService;
import com.alibaba.fastjson.JSONObject;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.annotations.ApiParam;

import java.util.List;

/**
 * 持续集成流水线管理配置信息管理模块接口实现
 * @author yangyuyan
 * @since 2022-12-01
 */

@RestController
@RequestMapping("/v1/cipipeline/configInfo")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CIPipelineConfigInfoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    CIPipelineConfigInfoService ciPipelineConfigInfoService;

    @PostMapping("/uploadConfigFile")
    public void uploadConfigFile(@RequestHeader(HttpHeadersConfig.HttpHeaders.X_LOGIN_USER) Long userId,
                              @RequestBody JSONObject configFile){
        logger.info("[uploadConfigFile] request comming userId={}, configFile={}",userId,configFile);
        ciPipelineConfigInfoService.uploadConfigFile(userId,configFile.getString("fileContent"));
    }

    @GetMapping("/jenkinsFiles/{configInfoId}")
    public String getJenkinsFile(
            @ApiParam(value = "configInfoId", name = "configInfoId", required = true) @PathVariable(value = "configInfoId") Long configInfoId){
        logger.info("[getJenkinsFile] request comming configInfoId={}",configInfoId);
        return ciPipelineConfigInfoService.getJenkinsFile(configInfoId);
    }

    @GetMapping("/getConfigInfos/{userId}")
    public List<ConfigInfoVo> getConfigInfos(@PathVariable(value = "userId")long userId){
        logger.info("[getConfigInfos] request comming userId={}",userId);
        return ciPipelineConfigInfoService.getConfigInfoForTable(userId);
    }
}
