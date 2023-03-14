package com.example.agent.service;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import java.io.File;
import java.io.IOException;
import com.alibaba.fastjson.JSONObject;
public class FileService {
    public String sendPost(JSONObject jsonObject, int agentid) throws Exception {
        //根据agentid获得post请求的url
        String urlCom = "http://localhost:8081/sendfile";//这里是服务B的接口地址


        return doPost(urlCom,jsonObject);
    }


    public String doPost(String url ,JSONObject jsonObject) {
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
