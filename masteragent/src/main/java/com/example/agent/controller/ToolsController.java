package com.example.agent.controller;


import com.example.agent.po.ToolPO;
import com.example.agent.pojo.ResultMsg;
import com.example.agent.service.ToolService;
import com.example.agent.vo.ToolVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
public class ToolsController {

    @Autowired
    ToolService toolService;

//    @PostMapping("/addtools")
//    public ResultMsg addTools(ToolPO toolPO){
//        ResultMsg rst=toolService.saveToolConfig(toolPO);
//        return null;
//    }
    @GetMapping("/addtools")
    public  ResultMsg addTools(String toolurl, String toolname, String tooldir, String fixcmd)
    {
        ResultMsg msg=toolService.saveToolConfig(new ToolPO(toolurl,toolname,tooldir,fixcmd));
        return null;
    }
    @GetMapping("/getTools")
    public List<ToolVO> getTools(){
        List<ToolPO> toolPOS=toolService.getAllTools();
        List<ToolVO> toolVOS=new ArrayList<>();
        for(ToolPO toolPO:toolPOS){
            toolVOS.add(new ToolVO(toolPO.getToolurl(),toolPO.getToolname()));
        }
        return toolVOS;
    }
}
