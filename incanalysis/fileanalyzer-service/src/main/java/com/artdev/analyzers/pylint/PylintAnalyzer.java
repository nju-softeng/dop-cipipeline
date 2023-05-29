package com.artdev.analyzers.pylint;

import com.artdev.analyzers.Analyzer;
import org.springframework.beans.factory.annotation.Value;
import util.CONST;
import util.ExecHelper;
import vo.ResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PylintAnalyzer implements Analyzer {
    @Value("${web.file-save-path}")
    private String savePath;

    private List<ResultVO> resList;

    @Override
    public ResultVO analyze() {
        File dir=new File(savePath);
        resList=new ArrayList<>();
        analysisDir(dir);
        String res="";
        for(int i=0;i<resList.size();i++){
            res+=resList.get(i).getMsg();
        }
        return new ResultVO(CONST.REQUEST_SUCCESS,res);
    }

    private void analysisDir(File dir){
        File[] files = dir.listFiles();
        //遍历这个数组，取出每个File对象
        if (files != null) {
            for (File f : files) {
                //判断这个File是否是一个文件，是：
                if (f.isFile()) {
                    String name=f.getName();
                    if(name.substring(name.lastIndexOf(".")+1).equals("py"))
                    resList.add(ExecHelper.execCommand("pylint "+f.getAbsolutePath(),""));
                } else {//否则就是一个目录，继续递归
                    //递归调用
                    analysisDir(f);
                }
            }
        }
    }
}
