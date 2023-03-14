package com.example.agent.controller;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileControllerTest {
    FileController fileController=new FileController();
    @Test
    void transFile() throws Exception {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("1","zhangsan");
        jsonObject.put("2","lisi");
        fileController.transFile(1,jsonObject);

    }
}