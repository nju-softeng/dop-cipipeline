package cn.com.devopsplus.dop.server.model.util;

import org.springframework.stereotype.Component;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.net.URL;

@Component
public class CloneUrl {
    public static boolean deleteFile(File dirFile) {
        if (!dirFile.exists()) {
            return false;
        }
        if (dirFile.isFile()) {
            return dirFile.delete();
        } else {
            for (File file : dirFile.listFiles()) {
                deleteFile(file);
            }
        }
        return dirFile.delete();
    }

    public boolean cloneRepo(String directory, String uri ) {
        CloneCommand cmd = Git.cloneRepository();
        File file=new File( directory );
        deleteFile(file);
        System.out.println("start clone");
        cmd.setDirectory( file );
        cmd.setURI( uri );
        //cmd.setCredentialsProvider( credentialsProvider );
        try {
            Git git = cmd.call();
            git.close();
            System.out.println("clone success");
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public String s(String projectName, String pythonProjectPath,
                    String dataPath,String startTime,String endTime,String modelName){


        return ( "?" + "projectName=" + projectName +
                    "&pythonProjectPath=" + pythonProjectPath + "&dataPath=" + dataPath +
                    "&start_time=" + startTime + "&end_time=" + endTime + "&modelName=" + modelName);
        }

    public static void main(String[] args) {
        //System.out.println(new CloneUrl().s("Feeder", "/code/JITO-Identification", "/usr/project/", "2015-1-1", "2016-9-1","FFFF"));
        System.out.println(new CloneUrl().cloneRepo("C:\\Users\\jack\\Desktop\\新建文件夹 (4)\\model-training","https://gitee.com/xie-mingtong/model-training.git"));
    }
}
