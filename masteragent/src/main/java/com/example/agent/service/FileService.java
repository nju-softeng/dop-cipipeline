package com.example.agent.service;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.*;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileService {
    @Value("${filesystem.file.port}")
    private  int FILEPORT;

    @Value("${filesystem.default.directory}")
    private  String DEFAULTDIR;

    @Value("${filesystem.zipfile.port}")
    private  int ZIPFILEPORT;
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

    public String compressDirectory(String srcFolder, String destZipFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(destZipFile);
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        File fileToZip = new File(srcFolder);
        zipFile(fileToZip, fileToZip.getName(), zipOut);

        zipOut.close();
        fos.close();
        return destZipFile;
    }


    // 递归压缩文件夹中的所有文件
    private  void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        //如果是文件夹的话
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        //如果是文件的话
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }

        fis.close();
    }

    public  void sendZipFile(String filePath, String ipAddress){
        Socket socket = null;
        try {
            socket = new Socket(ipAddress, ZIPFILEPORT);

            File file = new File(filePath);
            byte[] fileBytes = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(fileBytes, 0, fileBytes.length);

            OutputStream os = socket.getOutputStream();
            os.write(fileBytes, 0, fileBytes.length);
            os.flush();

            bis.close();
            fis.close();
            os.close();
            socket.close();
            file.delete();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendNormalFile(String ipAddress,String filePath) throws IOException {
        Socket socket=new Socket(ipAddress,FILEPORT);
        OutputStream outputStream=socket.getOutputStream();
        DataOutputStream dataOutputStream=new DataOutputStream(outputStream);
        FileInputStream fileInputStream=new FileInputStream(new File(filePath));
        String[] nameArr=filePath.split("\\\\");
        String name=nameArr[nameArr.length-1];
        dataOutputStream.writeUTF(name);

        byte [] buffer=new byte[1024];
        int len;
        while((len=fileInputStream.read(buffer))>0){
            dataOutputStream.write(buffer,0,len);
        }
        fileInputStream.close();
        dataOutputStream.close();
        outputStream.close();
        socket.close();
    }




}
