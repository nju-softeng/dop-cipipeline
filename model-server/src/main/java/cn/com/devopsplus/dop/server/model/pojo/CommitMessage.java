package cn.com.devopsplus.dop.server.model.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommitMessage {

    private String commitId;
    private String commitIdent;
    private String commitMessage;
    private String commitDate;
    //如果只需要获取merge的提交记录时，使用以下两个字段记录merge的来源
    private String lastCommitId; // <自己的上一次commitId>合并时的上一次commitId
    private String mergeBranchCommitId;  // 合并分支的commitId


}

