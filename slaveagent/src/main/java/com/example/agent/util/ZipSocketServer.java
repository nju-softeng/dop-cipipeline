package com.example.agent.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class ZipSocketServer implements CommandLineRunner {

    @Value("${filesystem.default.directory}")
    private  String DEFAULTDIR;

    @Autowired
    ServerSocket serverZipSocket;
    @Override
    public void run(String... args) throws Exception {

        while (true) {
//            System.out.println(DEFAULTDIR);
            Socket socket = serverZipSocket.accept();
            // Handle the clientSocket request here
            byte[] buffer = new byte[1024];
            InputStream is = socket.getInputStream();
            ZipInputStream zis = new ZipInputStream(is);

            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                File newFile = new File(DEFAULTDIR+ File.separator + fileName);

                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zis.closeEntry();
                zipEntry = zis.getNextEntry();
            }
            zis.close();
            is.close();
            socket.close();
        }
    }
}
