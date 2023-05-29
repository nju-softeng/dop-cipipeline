package com.artdev.service.serviceImpl;

import com.artdev.analyzers.errorprone.ErrorProneAnalyzer;
import com.artdev.analyzers.flake8.Flake8Analyzer;
import com.artdev.service.CompAnalyzerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import util.CONST;
import util.FileHelper;
import vo.ResultVO;

import javax.annotation.Resource;
import java.io.FileOutputStream;


@Service
public class CompAnalyzerServiceImpl implements CompAnalyzerService {

    @Value("${web.file-save-path}")
    private String savePath;

    @Resource
    ErrorProneAnalyzer errorProneAnalyzer;
    @Resource
    Flake8Analyzer flake8Analyzer;

    @Override
    public ResultVO compAnalyze(MultipartFile[] multipartFiles) {
        String res="";
        try {
            FileHelper.deleteAllFiles(savePath);
            for(int i=0;i<multipartFiles.length;i++){
                MultipartFile curFile=multipartFiles[i];
                FileOutputStream fileOutputStream=new FileOutputStream(savePath+curFile.getOriginalFilename());
                String originalName = curFile.getOriginalFilename();
                String type=originalName.substring(originalName.lastIndexOf(".")+1);
                ResultVO tempRes=null;
                FileCopyUtils.copy(multipartFiles[i].getInputStream(),fileOutputStream);
                if(type.equals("java")){
                    tempRes=errorProneAnalyzer.analyze(originalName);
                }else if(type.equals("py")){
                    tempRes=flake8Analyzer.analyze(originalName);
                }
                if(tempRes.getMsg()!=null && !tempRes.getMsg().equals("")){
                    if(type.equals("java")){
                        res+=originalName+"中错误如下\n"+tempRes.getMsg().substring(0,tempRes.getMsg().indexOf("^")).replace(" ","")+"\n";
                    }else {
                        res+=originalName+"中错误如下\n"+tempRes.getMsg()+"\n";
                    }
                }else{
                    res+=originalName+"本次更新无错误\n";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResultVO(CONST.REQUEST_SUCCESS,res);
    }


}
