package com.example.agent.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class MultiThreadDownloader implements Runnable {
    private final URL url;
    private final File file;
    private final long startByte;
    private final long endByte;


    public MultiThreadDownloader(URL url, File file, long startByte, long endByte) {
        this.url = url;
        this.file = file;
        this.startByte = startByte;
        this.endByte = endByte;
    }

    @Override
    public void run() {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);

            InputStream inputStream = connection.getInputStream();
            RandomAccessFile output = new RandomAccessFile(file, "rw");
            output.seek(startByte);

            byte[] buffer = new byte[1024];
            int bytesRead;
            long totalBytesRead = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                printProgress(totalBytesRead, endByte - startByte + 1);
            }

            output.close();
            inputStream.close();
            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printProgress(long bytesRead, long totalBytes) {
        DecimalFormat df = new DecimalFormat("#.00");
        double percent = (double) bytesRead / totalBytes * 100;
        String progress = df.format(percent) + "%";
        System.out.print("\r" + progress);
    }
}