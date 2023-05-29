package com.artdev.service.serviceimpl.analyzer;

import com.artdev.service.analyzer.AnalyzerService;
import com.artdev.service.client.CompAnalyzerClient;
import com.artdev.service.client.DepAnalyzerClient;
import com.artdev.service.client.FileAnalyzerClient;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import po.File;
import util.CONST;
import util.ExecHelper;
import util.FileHelper;
import vo.ResultVO;
import org.springframework.stereotype.Service;
import vo.file.FileVO;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyzerServiceImpl implements AnalyzerService {
    @Resource
    FileAnalyzerClient fileAnalyzerClient;

    List<MultipartFile> repositoryFile=null;

    @Value("${web.file-process-path}")
    private String processPath;
    @Resource
    DepAnalyzerClient depAnalyzerClient;

    @Resource
    CompAnalyzerClient compAnalyzerClient;

    public CredentialsProvider createCredential(String userName, String password) {
        return new UsernamePasswordCredentialsProvider(userName, password);
    }

    @Override
    public String getAnalyzeResult(String path) {
        java.io.File reposiotry=new java.io.File(path);
//        if(path==null || path.isEmpty()){
//            return "无git地址";
//        }
        try {
            //getRepositoryFile(path,reposiotry);
            repositoryFile=new ArrayList<>();
            dirFile2MulFile(reposiotry);
            ResultVO fileResult=fileAnalyzerClient.fileAnalyze(repositoryFile.toArray(new MultipartFile[repositoryFile.size()]));
            ResultVO compResult=compAnalyzerClient.compAnalyze(repositoryFile.toArray(new MultipartFile[repositoryFile.size()]));
            return fileResult.getMsg()+compResult.getMsg();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "测试";
    }

    private void getRepositoryFile(String path,java.io.File dir) throws GitAPIException {
        Git result = Git.cloneRepository()
                .setURI(path)
                .setDirectory(dir)
                .call();
        result.getRepository().close();
    }

    private void dirFile2MulFile(java.io.File dir){
        java.io.File[] files = dir.listFiles();
        //遍历这个数组，取出每个File对象
        if (files != null) {
            for (java.io.File f : files) {
                //判断这个File是否是一个文件，是：
                if (f.isFile()) {
                    MultipartFile multipartFile = FileHelper.getMultipartFile(f);
                    repositoryFile.add(multipartFile);
                } else {//否则就是一个目录，继续递归
                    //递归调用
                    dirFile2MulFile(f);
                }
            }
        }
    }


    private MultipartFile[] file2MulFileDepStage(List<File> files,List<File> failFiles) {
        List<MultipartFile> multipartFiles=new ArrayList<>();
        for(int i=0;i<files.size();i++){
            java.io.File file=null;
            File curFile=files.get(i);
            if(curFile.getType().equals("java")){
                String command="javac -d "+processPath+" "+curFile.getResourceDir();
                ResultVO res=ExecHelper.execCommand(command,"");
                if(!res.getMsg().contains("错误")){
                    file=new java.io.File(processPath+"/work/"+curFile.getName()+".class");
                }else {
                    failFiles.add(curFile);
                    continue;
                }
            }else {
                file=new java.io.File(curFile.getResourceDir());
            }
            MultipartFile multipartFile = FileHelper.getMultipartFile(file);
            multipartFiles.add(multipartFile);
        }
        return multipartFiles.toArray(new MultipartFile[multipartFiles.size()]);
    }

    private MultipartFile[] file2MulFileFileStage(List<File> files){
        MultipartFile[] multipartFiles=new MultipartFile[files.size()];
        for(int i=0;i<files.size();i++){
            java.io.File file=null;
            File curFile=files.get(i);
            file=new java.io.File(curFile.getResourceDir());
            MultipartFile multipartFile = FileHelper.getMultipartFile(file);
            multipartFiles[i]=multipartFile;
        }
        return multipartFiles;
    }
}
