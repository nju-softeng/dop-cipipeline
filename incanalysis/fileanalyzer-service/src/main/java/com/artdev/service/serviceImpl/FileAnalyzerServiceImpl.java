package com.artdev.service.serviceImpl;

import com.artdev.analyzers.checkstyle.CheckStyleAnalyzer;
import com.artdev.analyzers.pylint.PylintAnalyzer;
import com.artdev.service.FileAnalyzerService;
import util.CONST;
import util.FileHelper;
import vo.ResultVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

@Service
public class FileAnalyzerServiceImpl implements FileAnalyzerService {

    @Value("${web.file-save-path}")
    private String savePath;

    @Resource
    CheckStyleAnalyzer checkStyleAnalyzer;
    @Resource
    PylintAnalyzer pylintAnalyzer;

    @Override
    public ResultVO fileAnalyze(MultipartFile[] multipartFiles) {
        String res="";
        boolean hasJava=false;
        boolean hasPy=false;
        ResultVO javaRes=null;
        ResultVO pyRes=null;
        try {
            FileHelper.deleteAllFiles(savePath);
            for(int i=0;i<multipartFiles.length;i++){
                MultipartFile curFile=multipartFiles[i];
                FileOutputStream fileOutputStream=new FileOutputStream(savePath+curFile.getOriginalFilename());
                String originalName = curFile.getOriginalFilename();
                String type=originalName.substring(originalName.lastIndexOf(".")+1);
                if(type.equals("java")){
                    hasJava=true;
                }else if(type.equals("py")){
                    hasPy=true;
                }
                FileCopyUtils.copy(multipartFiles[i].getInputStream(),fileOutputStream);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(hasJava){
            javaRes=checkStyleAnalyzer.analyze();
            String tempJavaRes=javaRes.getMsg().substring(javaRes.getMsg().indexOf("[ERROR]"));
            res+="在新增java文件中";
            res+=tempJavaRes.substring(0,tempJavaRes.indexOf("[ERROR]",210))+"\n";
        }
        if (hasPy){
            pyRes=pylintAnalyzer.analyze();
            res+="在新增python文件中";
            res+=pyRes.getMsg()+"\n";
        }
        return new ResultVO(CONST.REQUEST_SUCCESS,res);
    }
}
