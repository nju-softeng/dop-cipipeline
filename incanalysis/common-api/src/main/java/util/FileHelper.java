package util;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

@Slf4j
public class FileHelper {
    /**
     * 保存文件
     * @param directoryPath 目录路径（以 / 结尾）
     * @param file 文件
     * @return 保存成功后的文件名
     */
    public static ResultVO save(String directoryPath, MultipartFile file) throws IOException {
        if(!checkDirectoryPath(directoryPath)){
            throw new IOException("服务器端错误，用于存放上传文件的文件夹不存在或创建失败！");
        }
        // 原文件名
        String name = file.getOriginalFilename();
        String dir=directoryPath+"/"+name;
        // 根据目标地址构造文件输出流
        FileOutputStream fileOutputStream = new FileOutputStream(dir);
        // 将文件复制到映射的地址
        FileCopyUtils.copy(file.getInputStream(),fileOutputStream);

        return new ResultVO(CONST.REQUEST_SUCCESS,"文件保存成功",dir);
    }

    /**
     * 加载文件为资源
     * @param directoryPath 目录路径（以 / 结尾）
     * @return 输入流资源
     */
    public static Resource loadFileAsResource(String directoryPath) {
        try {
            Path filePath = Paths.get(directoryPath);
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists())
                return resource;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static ResultVO delete(String directoryPath){
        if(StringUtils.hasText(directoryPath)){
            File file = new File(directoryPath);
            if(file.exists()) {
                // 当且仅当文件被成功删除后返回true
                if(file.delete()){
                    return new ResultVO(CONST.REQUEST_SUCCESS,"成功删除");
                }
            }
        }
        return new ResultVO(CONST.REQUEST_FAIL,"删除失败");

    }

    public static void download(String path,String name, HttpServletResponse response){
        InputStream inputStream = null;
        OutputStream outputStream = null;
        response.setContentType("application/x-msdownload");
        try {
            Resource resource = FileHelper.loadFileAsResource(path);
            if(resource == null)
                throw new IOException("找不到文件"+path);
            inputStream = resource.getInputStream();
            //1.设置文件ContentType类型
            response.setContentType("application/octet-stream;charset=UTF-8");
            outputStream = response.getOutputStream();
            //3.设置 header  Content-Disposition
            response.setHeader("Content-Disposition", "attachment; filename=" + name);
            int b = 0;
            byte[] buffer = new byte[2048];
            while (b != -1) {
                b = inputStream.read(buffer);
                if (b != -1) {
                    outputStream.write(buffer, 0, b);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(inputStream != null)
                    inputStream.close();
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 检查目录路径是否有效，若当前路径对应的目录不存在，则尝试创建目录
     * @param directoryPath 目录路径
     * @return 若目录不存在且创建失败则返回false，否则返回true
     */
    private static boolean checkDirectoryPath(String directoryPath) {
        File dir = new File(directoryPath);
        // 如果文件夹不存在则创建
        if(!dir.exists() && !dir.isDirectory()){
            log.debug("用于存放上传文件的文件夹不存在，正在尝试创建..");
            dir.mkdirs();
        }
        return true;
    }

    public static MultipartFile getMultipartFile(File file){
        FileItem item = new DiskFileItemFactory().createItem("file"
                , MediaType.MULTIPART_FORM_DATA_VALUE
                , true
                , file.getName());
        try (InputStream input = new FileInputStream(file);
             OutputStream os = item.getOutputStream()) {
            // 流转移
            IOUtils.copy(input, os);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid file: " + e, e);
        }

        return new CommonsMultipartFile(item);
    }


    public static String downRemoteFile(String remoteFileUrl, String saveFileName, String saveDir) {

        HttpURLConnection conn = null;
        OutputStream oputstream = null;
        InputStream iputstream = null;

        try {
            // 创建保存文件的目录
            File savePath = new File(saveDir);
            if (!savePath.exists()) {
                savePath.mkdir();
            }
            // 创建保存的文件
            File file = new File(savePath + "/" + saveFileName);
            if (file != null && !file.exists()) {
                file.createNewFile();
            }

            URL url = new URL(remoteFileUrl);
            // 将url以open方法返回的urlConnection连接强转为HttpURLConnection连接(标识一个url所引用的远程对象连接)
            // 此时cnnection只是为一个连接对象,待连接中
            conn = (HttpURLConnection) url.openConnection();
            // 设置是否要从 URL连接读取数据,默认为true
            conn.setDoInput(true);
            // 建立连接
            // (请求未开始,直到connection.getInputStream()方法调用时才发起,以上各个参数设置需在此方法之前进行)
            conn.connect();
            // 连接发起请求,处理服务器响应 (从连接获取到输入流)
            iputstream = conn.getInputStream();
            // 创建文件输出流，用于保存下载的远程文件
            oputstream = new FileOutputStream(file);
            //  用来存储响应数据
            byte[] buffer = new byte[4 * 1024];
            int byteRead = -1;
            //  循环读取流
            while ((byteRead = (iputstream.read(buffer))) != -1) {
                oputstream.write(buffer, 0, byteRead);
            }
            //  输出完成后刷新并关闭流
            oputstream.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //  重要且易忽略步骤 (关闭流,切记!)
                if (iputstream != null) {
                    iputstream.close();
                }
                if (oputstream != null) {
                    oputstream.close();
                }
                // 销毁连接
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 返回保存后的文件路径
        return saveDir + "/" + saveFileName;
    }

    public static void deleteAllFiles(String Fpath){
        try {
            Path path= Paths.get(Fpath);
            Files.walkFileTree(path,new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    System.out.printf("文件被删除 : %s%n", file);
                    return FileVisitResult.CONTINUE;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void deleteAllFilesAndDir(String Fpath){
        try {
            Path path= Paths.get(Fpath);
            Files.walkFileTree(path,new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return super.visitFile(file, attrs);
                }
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return super.postVisitDirectory(dir, exc);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
