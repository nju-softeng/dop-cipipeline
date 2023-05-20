package com.example.agent.service;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FileService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public String sendPost(JSONObject jsonObject, int agentid) throws Exception {
        logger.info("[sendPost]");
        //根据agentid获得post请求的url
        String urlCom = "http://127.0.0.1:8080/sendfile";//这里是服务B的接口地址


        return doPost(urlCom,jsonObject);
    }


    public String doPost(String url ,JSONObject jsonObject) {
        logger.info("[doPost]");
        // 建立Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String res = "";
        String json=jsonObject.toString();
        try {
            // 建立Http Post请求
            HttpPost post = new HttpPost(url);
            // 建立请求内容 ContentType：请求格式设置
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            post.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(post);
            res = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }



}
