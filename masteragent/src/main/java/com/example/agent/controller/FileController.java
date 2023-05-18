package com.example.agent.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.agent.pojo.ResultMsg;
import com.example.agent.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.xml.transform.Result;
import java.io.File;
import java.io.IOException;

@RestController
public class FileController {
    @Autowired
    FileService fileService;


    @GetMapping(value = "sendMessage")
    public ResultMsg sendMessage(int agentid, JSONObject jsonobject) throws Exception {
        System.out.println("FileController [sendMessage]");
        String msg=fileService.sendPost(jsonobject,agentid);
        ResultMsg rst=new ResultMsg(msg);
        return rst;
    }

    @PostMapping("/getMessage")
    public ResultMsg getMessage(MultipartHttpServletRequest request) {
        System.out.println("FileController [getMessage]");
        MultipartFile file = request.getFile("upload");
        System.out.println(file);
        return null;
    }

    @GetMapping("/sendZip")
    public ResultMsg sendZipFile() throws IOException {
        System.out.println("FileController [sendZipFile]");
        String zipName=fileService.compressDirectory("F:\\aa_agent\\masteragent\\sql","F:\\aa_agent\\masteragent\\src\\main\\resources\\directory\\zp.zip");
        fileService.sendZipFile(zipName,"localhost");
        return null;
    }

    @GetMapping("/sendFile")
    public ResultMsg sendNormalFile() throws IOException {
        System.out.println("FileController [sendNormalFile]");
        fileService.sendNormalFile("localhost","F:\\aa_agent\\masteragent\\src\\main\\java\\com\\example\\agent\\service\\ToolService.java");
        return null;
    }


}
