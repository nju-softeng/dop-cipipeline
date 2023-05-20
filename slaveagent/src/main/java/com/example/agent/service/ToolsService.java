package com.example.agent.service;


import com.example.agent.pojo.ResultMsg;
import com.example.agent.util.MultiThreadDownloader;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class ToolsService {

    @Value("${toolsystem.download.threadNum}")
    private  int MAX_THREADS;

    private static final Logger logger = LoggerFactory.getLogger(ToolsService.class);

    public List<String> getUrls(){
        return null;
    }

    public static void downloadNetResource(String urlStr, String fileName, String dir) {
        logger.info("[downloadNetResource] urlStr={} fileName={} dir={}",urlStr,fileName,dir);
        // 下载网络文件
        int byteSum = 0;
        int byteRead = 0;
        InputStream inStream = null;
        FileOutputStream fos = null;
        try {
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();
//            fileName = getFileName(url, fileName);
            // 设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //模拟浏览器访问,防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36");
            // 拿到输入流就相当于拿到了文件
            inStream = conn.getInputStream();
            // 文件保存位置
            File saveDir = new File(dir);
            if (!saveDir.exists()) {
                saveDir.mkdir();
            }
            File file = new File(saveDir + File.separator + fileName);
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[1204];
            while ((byteRead = inStream.read(buffer)) != -1) {
                byteSum += byteRead;
                fos.write(buffer, 0, byteRead);
            }
//            logger.info("文件 {} 的大小为 {}", fileName, byteSum);
        } catch (Exception e) {
//            logger.error("下载网络资源 {} 失败，请及时处理，", fileName, e);
        } finally {
//            IOUtils.closeQuietly(inStream, null);
//            IOUtils.closeQuietly(fos, null);
        }
    }



    public  void downloadNetResourceByMultiThread(String urlStr, String fileName, String dir) {
        logger.info("[registerAgent] urlStr={} fileName={} dir={}",urlStr,fileName,dir);
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
        URL url = null;
        try {
            url = new URL(urlStr);
            File file = new File(dir+File.separator+fileName);

            long fileSize = url.openConnection().getContentLength();
            long blockSize = (long) Math.ceil(fileSize / MAX_THREADS);

            for (int i = 0; i < MAX_THREADS; i++) {
                long startByte = i * blockSize;
                long endByte = ((i + 1) * blockSize) - 1;
                if (i == MAX_THREADS - 1) {
                    endByte = fileSize - 1;
                }

                MultiThreadDownloader task = new MultiThreadDownloader(url, file, startByte, endByte);
                executor.execute(task);
            }

            executor.shutdown();
            while (!executor.isTerminated()) {
                // Wait for all tasks to complete
            }

            System.out.println("File downloaded successfully");


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public ResultMsg fixWar(String dirPath,String fileName){
        logger.info("[registerAgent] dirPath={} fileName={}",dirPath,fileName);
        try {
            Runtime mt = Runtime.getRuntime();
//            String[] cmd = new String[1];
            String tar=dirPath+File.separator+fileName;
            String cmd="java -jar "+tar+" --httpPort=8087";
//            cmd[0]="echo hello";
//            String cmd="echo hello > test.txt";
            Process pro = mt.exec(cmd);
            InputStream ers= pro.getErrorStream();
            pro.waitFor();
        } catch (IOException | InterruptedException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }



}
