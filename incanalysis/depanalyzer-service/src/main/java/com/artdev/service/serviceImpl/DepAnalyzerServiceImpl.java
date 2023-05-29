package com.artdev.service.serviceImpl;

import com.artdev.analyzers.jdeps.JdepsAnalyzer;
import com.artdev.analyzers.pipreqs.PipreqsAnalyzer;
import com.artdev.mapperservice.DependencyMapper;
import com.artdev.service.DepAnalyzerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import po.Dependency;
import po.File;
import util.CONST;
import util.FileHelper;
import vo.ResultVO;
import vo.file.FileVO;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class DepAnalyzerServiceImpl implements DepAnalyzerService {

    @Value("${web.file-save-path}")
    private String savePath;

    @Value("${web.py-save-path}")
    private String pyPath;

    @Resource
    JdepsAnalyzer jdepsAnalyzer;

    @Resource
    PipreqsAnalyzer pipreqsAnalyzer;

    @Resource
    DependencyMapper dependencyMapper;


    @Override
    public ResultVO depAnalyze(String content,MultipartFile[] multipartFile) {
        String res="";
        List<String> contents= Arrays.asList(content.split("\\|"));
        int repositoryId=Integer.parseInt(contents.get(0));
        List<Integer> fileIds=new ArrayList<>();
        for(int i=1;i<contents.size();i++){
            fileIds.add(Integer.parseInt(contents.get(i)));
        }
        try {
            FileHelper.deleteAllFiles(savePath);
            boolean hasPy=false;
            for(int i=0;i<multipartFile.length;i++){
                MultipartFile curFile=multipartFile[i];
                String originalName = curFile.getOriginalFilename();
                String type=originalName.substring(originalName.lastIndexOf(".")+1);
                if(type.equals("class")){
                    FileOutputStream fileOutputStream=new FileOutputStream(savePath+curFile.getOriginalFilename());
                    FileCopyUtils.copy(multipartFile[i].getInputStream(),fileOutputStream);
                }else {
                    FileOutputStream fileOutputStream=new FileOutputStream(pyPath+curFile.getOriginalFilename());
                    FileCopyUtils.copy(multipartFile[i].getInputStream(),fileOutputStream);
                }
                Integer curFileId=fileIds.get(i);
                if(type.equals("class")){
                    ResultVO javaRes=jdepsAnalyzer.analyze(originalName);
                    String message=javaRes.getMsg();
                    List<String> deps= Arrays.asList(message.replace(" ","").split("->"));
                    List<Dependency> curDep=new ArrayList<>();
                    List<String> curDepName=new ArrayList<>();
                    for(int j=0;j<deps.size();j++){
                        if(deps.get(j).matches("(([a-zA-Z])+\\.?)+" )){
                            Dependency dependency=new Dependency(repositoryId,curFileId,deps.get(j));
                            curDep.add(dependency);
                            curDepName.add(deps.get(j));
                        }
                    }
                    res+=processDep(originalName,curDep,curDepName,curFileId,repositoryId)+"\n";
                }else if(type.equals("py")){
                    ResultVO pyRes=pipreqsAnalyzer.analyze();
                    String message=pyRes.getMsg();
                    int begin=message.indexOf("requirements")+14;
                    if(begin<message.length()){
                        List<String> deps= Arrays.asList(message.substring(begin).split("\n"));
                        List<Dependency> curDep=new ArrayList<>();
                        List<String> curDepName=new ArrayList<>();
                        for(int j=0;j<deps.size();j++){
                            Dependency dependency=new Dependency(repositoryId,curFileId,deps.get(j));
                            curDep.add(dependency);
                            curDepName.add(deps.get(j));
                        }
                        res+=processDep(originalName,curDep,curDepName,curFileId,repositoryId)+"\n";
                    }else {
                        res+=originalName+"无依赖更新";
                    }
                    FileHelper.deleteAllFiles(pyPath);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResultVO(CONST.REQUEST_SUCCESS,res);
    }

    private String processDep(String originalName,List<Dependency> curDep,List<String> curDepName,Integer curFileId,Integer repositoryId){
        boolean isChanged=false;
        List<Dependency> dependencies=dependencyMapper.selectByFileId(curFileId);
        String res=originalName+"在本次更新中";
        for(int i=0;i<dependencies.size();i++){
            Dependency oldDependency=dependencies.get(i);
            if(!curDepName.contains(oldDependency.getName())){
                if(dependencyMapper.deleteByPrimaryKey(oldDependency.getId())!=0){
                    res+="删除依赖"+oldDependency.getName();
                    isChanged=true;
                }
            }
        }
        for(int i=0;i<curDep.size();i++){
            Dependency curDependency=curDep.get(i);
            String depName=curDependency.getName();
            Dependency dependency=dependencyMapper.selectByNameAndFileId(depName,curFileId);
            if(dependency==null){
                Dependency newDep=new Dependency(repositoryId,curFileId,depName);
                if(dependencyMapper.insert(newDep)!=0){
                    res+="创建新依赖"+depName;
                    isChanged=true;
                }
            }
        }
        if(isChanged){
            return res;
        }else {
            return res+"无依赖更新";
        }
    }

}
