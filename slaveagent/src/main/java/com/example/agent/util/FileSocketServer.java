package com.example.agent.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class FileSocketServer implements CommandLineRunner {

    @Value("${filesystem.default.filedirectory}")
    private  String DEFAULTFILEDIR;

    @Autowired
    ServerSocket serverFileSocket;
    @Override
    public void run(String... args) throws Exception {

        while (true) {
//            System.out.println(DEFAULTDIR);
            Socket socket = serverFileSocket.accept();
            // Handle the clientSocket request here


            // 获取输入输出流
            DataInputStream in = new DataInputStream(socket.getInputStream());

            // 读取文件名和大小
            String fileName = in.readUTF();
            // 创建文件输出流
            FileOutputStream fileOut = new FileOutputStream(DEFAULTFILEDIR+File.separator+fileName);

            // 缓冲区大小
            byte[] buffer = new byte[1024];
            int read;

            // 读取文件数据并写入本地文件
            while ((read = in.read(buffer)) != -1) {
                fileOut.write(buffer, 0, read);
            }
            // 关闭所有流
            fileOut.close();
            in.close();
            socket.close();
        }
    }
}
