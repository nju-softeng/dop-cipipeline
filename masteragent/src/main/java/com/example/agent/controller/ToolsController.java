package com.example.agent.controller;


import com.example.agent.pojo.ResultMsg;
import com.example.agent.service.ToolService;
import com.example.agent.vo.ToolVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ToolsController {

    @Autowired
    ToolService toolService;

    @PostMapping("addtools")
    public ResultMsg addTools(ToolVO toolVO){
        ResultMsg rst=toolService.saveToolConfig(toolVO);
        return null;
    }
}
