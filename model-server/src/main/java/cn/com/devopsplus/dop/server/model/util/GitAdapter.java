package cn.com.devopsplus.dop.server.model.util;


import cn.com.devopsplus.dop.server.model.pojo.CommitMessage;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;



public class GitAdapter {


    private final static String REF_HEADS = "refs/heads/";
    private final static String MASTER_BRANCH = "master";
    // 远程仓库路径  用的就是.git
    private String remotePath;

    // 本地仓库路径  包含了项目工程名projectName
    private String localPath;

    private Git git;

    private Repository repository;


    public String branchName;
    //  Git授权
    private static UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider;

    /**
     * 构造函数：没有传分支信息则默认拉取master代码
     * @param remotePath
     * @param localPath
     */
    public GitAdapter(String remotePath,String localPath) {
        this(remotePath,localPath,MASTER_BRANCH);
    }

    public GitAdapter(String remotePath, String localPath, String branchName) {
        this.remotePath = remotePath;
        this.localPath = localPath;
        this.branchName = branchName;
        // github账户密码
        this.usernamePasswordCredentialsProvider = new UsernamePasswordCredentialsProvider("username","password");
        // 初始化git
        this.initGit();
        repository = git.getRepository();
    }

    /**
     * 默认初始化的时候会自动拉取 @branchName 的最新代码
     */
    private void initGit() {
        File file = new File(localPath);
        System.out.println("文件路径"+localPath);
        // 如果文件存在 说明已经拉取过代码,则拉取最新代码
        if(file.exists()) {
            try {
                // 打开git
                git = Git.open(new File(localPath));
                // 拉取最新的提交
                git.pull().setCredentialsProvider(usernamePasswordCredentialsProvider).call();
            } catch (GitAPIException | IOException e) {

                e.printStackTrace();
            } finally {

                git.close();
            }
        }
        // 文件不存在，说明未拉取过代码 则拉取最新代码
        else {
            try {
                git = Git.cloneRepository()
                        .setCredentialsProvider(usernamePasswordCredentialsProvider)
                        .setURI(remotePath)
                        .setBranch(branchName)
                        .setDirectory(new File(localPath))
                        .call();
                // 拉取最新的提交
                git.pull().setCredentialsProvider(usernamePasswordCredentialsProvider).call();
            } catch (GitAPIException e) {
                e.printStackTrace();
            } finally {
                git.close();
            }
        }
    }

///  此处省略其他不重要代码

    /**
     * 获取当前分支的所有提交记录
     * @return
     * @throws IOException
     * @throws GitAPIException
     */
    public List<CommitMessage> getCommitMessages() throws IOException, GitAPIException {
        List<CommitMessage> commitMessages = new ArrayList<>();
        CommitMessage commitMessage = null;
        Iterable<RevCommit> commits = git.log().all().call();
        RevWalk walk = new RevWalk(repository);
        for(RevCommit commit:commits) {
            commitMessage = new CommitMessage();
            boolean foundInThisBranch = false;
            RevCommit targetCommit = walk.parseCommit(commit.getId());
            for(Map.Entry< String,Ref> e : repository.getAllRefs().entrySet()){
//                e.getKey()
                if(e.getKey().startsWith("refs/remotes/origin")) {
                    if(walk.isMergedInto(targetCommit,walk.parseCommit(e.getValue().getObjectId()))) {
                        String foundInBranch = e.getValue().getTarget().getName();
//
                        if(foundInBranch.contains(branchName)) {
                            foundInThisBranch = true;
                            break;
                        }
                    }
                }

            }
            if(foundInThisBranch) {
                commitMessage.setCommitId(commit.getName());
                commitMessage.setCommitIdent(commit.getAuthorIdent().getName());
                commitMessage.setCommitMessage(commit.getFullMessage());
                commitMessage.setCommitDate(new Date(commit.getCommitTime()*1000L).toString());
                commitMessages.add(commitMessage);
            }
        }
        return commitMessages;
    }

    public static void main(String[] args) throws IOException, GitAPIException {
        List<CommitMessage> commitMessages = new GitAdapter("", "C:\\Users\\jack\\Desktop\\gitTest").getCommitMessages();
         commitMessages.stream().forEach(v->System.out.println(v.getCommitDate()));;
    }


}

