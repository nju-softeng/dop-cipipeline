package cn.com.devopsplus.dop.server.model.service.commitService;

import cn.com.devopsplus.dop.server.model.pojo.CommitMessage;
import cn.com.devopsplus.dop.server.model.util.GitAdapter;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Component
public class CommitService {

    private HashMap<String,String> map = new HashMap<String, String>(){
        {
            put("Jan","1");
            put("Feb","2");
            put("Mar","3");
            put("Apr","4");
            put("May","5");
            put("Jun","6");
            put("Jul","7");
            put("Aug","8");
            put("Sep","9");
            put("Oct","10");
            put("Nov","11");
            put("Dec","12");

        }
    };

    //越往上是最新的  最近的n条commit记录
    public String[] getTime(Integer commitNumber, String projectPath,String remote,String branch) throws IOException, GitAPIException {
        GitAdapter gitAdapter = new GitAdapter(remote,projectPath,branch);
        List<CommitMessage> commitMessages = gitAdapter.getCommitMessages();
        String endTime = turn(commitMessages.get(0).getCommitDate());
        String startTime = turn(commitMessages.get(commitNumber).getCommitDate());
        return  new String[]{startTime,endTime};
    }
    //全部commit记录
    public String[] getTime(String projectPath,String remote,String branch) throws IOException, GitAPIException {
        GitAdapter gitAdapter = new GitAdapter(remote,projectPath,branch);
        List<CommitMessage> commitMessages = gitAdapter.getCommitMessages();
        String endTime = turn(commitMessages.get(0).getCommitDate());
        String startTime = turn(commitMessages.get(commitMessages.size()-1).getCommitDate());
        return  new String[]{startTime,endTime};
    }



    //Thu Oct 06 22:51:50 CST 2022  -> 2022-10-06
    //01234567891123456789212345678
    public   String turn (String commitTime){
        String year = commitTime.substring(24,28);
        String month = this.map.get(commitTime.substring(4,7));
        String day = commitTime.substring(8,10);
        if(day.charAt(0)=='0') day=day.substring(1);
        return year+"-"+month+"-"+day;
    }

    public static void main(String[] args) throws IOException, GitAPIException {
        System.out.println(new CommitService().getTime
                ("C:\\Users\\jack\\Desktop\\新建文件夹 (4)\\course-selection-system-backend",
                        "https://gitee.com/xie-mingtong/course-selection-system-backend.git","master"));
    }
}
