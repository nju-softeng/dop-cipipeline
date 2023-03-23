package com.example.agent.service;


import com.example.agent.po.AgentattributePO;
import com.example.agent.po.ToolPO;
import com.example.agent.pojo.ResultMsg;
import com.example.agent.vo.ToolVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@Service
public class ToolService {
    @Autowired
    DataSource dataSource;

    @Autowired
    JdbcTemplate jdbcTemplate;

    public ResultMsg saveToolConfig(ToolPO toolPO){
        String sql="insert into tools(toolurl,toolname,tooldir,fixcmd) values(?,?,?,?)";
        int res=jdbcTemplate.update(sql,toolPO.getToolurl(),toolPO.getToolname(),toolPO.getTooldir(),toolPO.getFixcmd());
        return new ResultMsg(res);
    }

    public List<ToolPO> getAllTools(){
        String sql="select * from tools";
        List<ToolPO> toolPOS=jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(ToolPO.class));
        return toolPOS;
    }

    public static void downloadNetResource(String urlStr, String fileName, String dir) {
        // 下载网络文件
        System.out.println("開始下載！");
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
}
