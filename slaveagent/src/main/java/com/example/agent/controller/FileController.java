package com.example.agent.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.agent.pojo.ResultMsg;
import com.example.agent.service.FileService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.xml.transform.Result;
import java.io.File;

@Controller
public class FileController {

    FileService fileService=new FileService();


    @GetMapping(value = "/transfile")
    public ResultMsg transFile(int agentid, JSONObject jsonobject) throws Exception {
        String msg=fileService.sendPost(jsonobject,agentid);
        ResultMsg rst=new ResultMsg(msg);
        return rst;
    }

    @PostMapping("/getfile")
    public ResultMsg getFile(MultipartHttpServletRequest request) {
        MultipartFile file = request.getFile("upload");
        System.out.println(file);
        return null;
    }

    @PostMapping("/sendfile")
    public ResultMsg GetFile(@RequestBody String jsonObject) {

        System.out.println(jsonObject);
        return null;
    }


}
