package cn.com.devopsplus.dop.server.model.service.modelService;
import cn.com.devopsplus.dop.server.model.config.ModelConfig;
import cn.com.devopsplus.dop.server.model.mapper.modelMapper;

import cn.com.devopsplus.dop.server.model.mapper.userModelMapper;
import cn.com.devopsplus.dop.server.model.pojo.Model;
import cn.com.devopsplus.dop.server.model.pojo.TrainSetMode;
import cn.com.devopsplus.dop.server.model.pojo.UpdateMode;
import cn.com.devopsplus.dop.server.model.service.commitService.CommitService;
import cn.com.devopsplus.dop.server.model.util.CloneUrl;
import cn.com.devopsplus.dop.server.model.util.Run;
import lombok.SneakyThrows;
import net.sf.json.JSONObject;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DetectModelService  implements Job ,ModelTypeInterface {
//    @Autowired
//    private userModelMapper  userModelmapper;
    @Autowired
    private ModelConfig modelConfig = new ModelConfig();
    @Autowired
    private CloneUrl cloneUrl = new CloneUrl();
    @Autowired
    private Run run = new Run();
    @Autowired
    private CommitService commitService = new CommitService();
    @Autowired
    private modelMapper modelmapper;
    


    @Override
    public JSONObject train(Model model, TrainSetMode trainSetMode, UpdateMode updateMode) throws IOException, GitAPIException, SchedulerException {
        Map<String, Object> map = new HashMap<>();
        String projectName = model.getProject_name();
        String projectPath = modelConfig.getDataPath() + "\\" + projectName;
        LocalDate startTime = trainSetMode.getStartTime();
        LocalDate endTime = trainSetMode.getEndTime();



        if (startTime != null && endTime != null && startTime.compareTo(endTime) >= 0) {
            System.out.println("clone false");
            map.put("BuildResult", false);
            JSONObject JO = JSONObject.fromObject(map);
            return JO;
        } else if (!cloneUrl.cloneRepo(projectPath, model.getProject_address())) {
            System.out.println("clone false");
            map.put("BuildResult", false);
            JSONObject JO = JSONObject.fromObject(map);
            return JO;
        } else {
            //构建模型
            //System.out.println("train begin");
            String result = "Model Build Success";
            model.setVersion("1");
            switch (trainSetMode.getTrainSetModeType()) {

                case AllCommit: {
                    String[] time = commitService.getTime(projectPath,model.getProject_name(),model.getBranch());
                    String start = time[0];
                    String end = time[1];
                    model.setLast_commit_time(time[1]);

                    result = run.startbuildmodel(modelConfig.getBuildModelPythonPath(), projectName, modelConfig.getPythonProjectPath(),
                            modelConfig.getDataPath(), start, end, model.getModel_name()+model.getVersion());

                    break;
                }
                case LastCommit: {
                    String[] time = commitService.getTime(trainSetMode.getCommitNumber(), projectPath,model.getProject_name(),model.getBranch());
                    String start = time[0];
                    String end = time[1];
                    model.setLast_commit_time(time[1]);
                    result = run.startbuildmodel(modelConfig.getBuildModelPythonPath(), projectName, modelConfig.getPythonProjectPath(),
                            modelConfig.getDataPath(), start, end, model.getModel_name()+model.getVersion());
                    break;
                }
                case PeriodOfTime: {

                    String temp1 = startTime.toString();
                    temp1 = temp1.substring(0, temp1.indexOf("T"));
                    String temp2 = endTime.toString();
                    temp2 = temp2.substring(0, temp2.indexOf("T"));
                    result = run.startbuildmodel(modelConfig.getBuildModelPythonPath(), projectName, modelConfig.getPythonProjectPath(),
                            modelConfig.getDataPath(), temp1, temp2, model.getModel_name()+model.getVersion());
                    break;
                }

            }

           // System.out.println("train over");
            //失败
            if (!result.equals("Model Build Success")) {
                System.out.println("train fail");
                map.put("BuildResult", false);
                JSONObject JO = JSONObject.fromObject(map);
                return JO;
            }
            //成功
            else {
                //System.out.println("train success");
                /*数据库插入 训练策略模型  训练模型v0
                   如果是更新 则插入 更新
                 */


                modelmapper.save(model);

                if (updateMode.getUpdateModeType().equals(UpdateMode.UpdateModeType.RegularInterval)) {
                    int time = updateMode.getIntervalTime();
                    // 1、创建调度器Scheduler
                    SchedulerFactory schedulerFactory = new StdSchedulerFactory();
                    Scheduler scheduler = schedulerFactory.getScheduler();
                    // 2、，创建JobDetail实例，并与PrintWordsJob类绑定(Job执行内容)
                    Date date = new Date();

                    SimpleDateFormat dateFormat= new
                             SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


                    JobDetail jobDetail = JobBuilder.newJob(DetectModelService.class)
                            .withIdentity(model.model_name, model.model_name)
                            .usingJobData("project_name",model.project_name).usingJobData("project_address",model.project_address).usingJobData("version",Integer.parseInt(model.getVersion()) + "").usingJobData("branch",model.branch).usingJobData("model_name",model.model_name)
                            .usingJobData("trainSetModeType", String.valueOf(trainSetMode.getTrainSetModeType())).usingJobData("startTime", String.valueOf(trainSetMode.getStartTime())).usingJobData("endTime", String.valueOf(trainSetMode.getEndTime())).usingJobData("commitNumber",trainSetMode.getCommitNumber())
                            .usingJobData("updateModeType", String.valueOf(updateMode.getUpdateModeType())).usingJobData("intervalTime",updateMode.getIntervalTime())
                            .usingJobData("now",dateFormat.format(date))
                            .usingJobData("last_commit",model.getLast_commit_time())
                            .build();

                    // 3、构建Trigger实例,每隔1s执行一次
                    Trigger trigger = TriggerBuilder.newTrigger().withIdentity(model.model_name, model.model_name)
                            .startNow()//立即生效
                            .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                    .withIntervalInSeconds(time)//每隔1s执行一次
                                    .repeatForever()).build();//一直执行

                    //4、执行
                    scheduler.scheduleJob(jobDetail, trigger);
                    scheduler.start();



                }
            }
        }

        return null;
    }

    @Override
    public void update(Model model, TrainSetMode trainSetMode, UpdateMode updateMode) throws IOException, GitAPIException {

        String projectName = model.getProject_name();

        String projectPath = modelConfig.getDataPath() + "\\" + projectName;

        LocalDate startTime = trainSetMode.getStartTime();
        LocalDate endTime = trainSetMode.getEndTime();
        // todo add branch and test
        if (startTime != null && endTime != null && startTime.compareTo(endTime) >= 0) {
            //System.out.println("clone false");
            return;
        }
        if (false||!cloneUrl.cloneRepo(projectPath, model.getProject_address())) {
            System.out.println("clone false");
            return;
        } else {
            //构建模型

            String result = "Model Build Success";
            switch (trainSetMode.getTrainSetModeType()) {

                case AllCommit: {
                    String[] time = commitService.getTime(projectPath,model.getProject_name(),model.getBranch());
                    String start = time[0];
                    String end = time[1];
                    result = run.startbuildmodel(modelConfig.getBuildModelPythonPath(), projectName, modelConfig.getPythonProjectPath(),
                            modelConfig.getDataPath(), start, end, model.getModel_name()+model.getVersion());
                    break;
                }
                case LastCommit: {
                    String[] time = commitService.getTime(trainSetMode.getCommitNumber(), projectPath,model.getProject_name(),model.getBranch());
                    String start = time[0];
                    String end = time[1];
                    result = run.startbuildmodel(modelConfig.getBuildModelPythonPath(), projectName, modelConfig.getPythonProjectPath(),
                            modelConfig.getDataPath(), start, end, model.getModel_name()+model.getVersion());
                    break;
                }
                case PeriodOfTime: {
                    String temp1 = startTime.toString();
                    temp1 = temp1.substring(0, temp1.indexOf("T"));
                    String temp2 = endTime.toString();
                    temp2 = temp2.substring(0, temp2.indexOf("T"));
                    result = run.startbuildmodel(modelConfig.getBuildModelPythonPath(), projectName, modelConfig.getPythonProjectPath(),
                            modelConfig.getDataPath(), temp1, temp2, model.getModel_name()+model.getVersion());
                    break;
                }

            }

            System.out.println("train over");

            if (!result.equals("Model Build Success")) {
                System.out.println("train fail");
                return;
            }
            //成功
            else {
                //System.out.println("train success");
                /*数据库插入 训练策略模型  训练模型v0
                   如果是更新 则插入 更新
                 */

                modelmapper.save(model);

            }

        }


    }


    @SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        UpdateMode updateMode = new UpdateMode();
        Model model = new Model();
        TrainSetMode trainSetMode = new TrainSetMode();


        model.setProject_name((String) jobExecutionContext.getJobDetail().getJobDataMap().get("project_name"));
        model.setProject_address((String) jobExecutionContext.getJobDetail().getJobDataMap().get("project_address"));
        model.setBranch((String) jobExecutionContext.getJobDetail().getJobDataMap().get("branch"));
        model.setModel_name((String) jobExecutionContext.getJobDetail().getJobDataMap().get("model_name"));
        model.setLast_commit_time((String) jobExecutionContext.getJobDetail().getJobDataMap().get("last_commit"));


        trainSetMode.setTrainSetModeType(TrainSetMode.TrainSetModeType.valueOf((String) jobExecutionContext.getJobDetail().getJobDataMap().get("trainSetModeType")));
        if(trainSetMode.getStartTime()!=null)trainSetMode.setStartTime(LocalDate.parse((String) jobExecutionContext.getJobDetail().getJobDataMap().get("startTime"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        if(trainSetMode.getEndTime()!=null)trainSetMode.setEndTime(LocalDate.parse((String) jobExecutionContext.getJobDetail().getJobDataMap().get("endTime"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        trainSetMode.setCommitNumber((Integer) jobExecutionContext.getJobDetail().getJobDataMap().get("commitNumber"));

//LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")



        updateMode.setUpdateModeType(UpdateMode.UpdateModeType.valueOf((String) jobExecutionContext.getJobDetail().getJobDataMap().get("updateModeType")));
        updateMode.setIntervalTime((Integer) jobExecutionContext.getJobDetail().getJobDataMap().get("intervalTime"));

        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date a = dateFormat.parse((String) jobExecutionContext.getJobDetail().getJobDataMap().get("now"));
        Date date = new Date();

        String a1= dateFormat.format(a);
        String a2=dateFormat.format(date);
        Date b = dateFormat.parse(a1);
        Date n = dateFormat.parse(a2);
        long c = n.getTime() - b.getTime();


        int version = (int) (c/(1000*updateMode.getIntervalTime())) +2; // 0是第一次更新 --- v2  time +2
        model.setVersion(version+"");
        String projectPath = modelConfig.getDataPath() + "\\" + model.getProject_name();
        if (!cloneUrl.cloneRepo(projectPath, model.getProject_address())) {
            System.out.println("clone false");
        }else {
            String[] time = commitService.getTime(projectPath,model.getProject_address(),model.getBranch());
            String end = time[1];
            if(!model.getLast_commit_time().equals(end)){//如果没有commit信息 就不会更新
                update(model,trainSetMode,updateMode);
            }

        }
        update(model,trainSetMode,updateMode);

    }

    public static void main(String[] args) throws ParseException {
//        Model model = new Model();
//        TrainSetMode trainSetMode = new TrainSetMode();
//        UpdateMode updateMode = new UpdateMode();
//        String s = trainSetMode.toString();
//        s = s.substring(s.indexOf('{')+1,s.indexOf('}'));
//        String[] t = s.split(", ");
//
//
//        for (int i = 0; i < t.length; i++) {
//            String substring = t[i].substring(0, t[i].indexOf('='));
//            System.out.println("trainSetMode " +"jobExecutionContext.getJobDetail().getJobDataMap().get(\""+ substring+"\")");
//        }


        //.usingJobData("project_name",model.project_name).usingJobData("project_address",model.project_address).usingJobData("version",model.version).usingJobData("branch",model.branch).usingJobData("model_name",model.model_name)
/*
2023-03-03 12:58:44
2023-03-03 12:58:44

 */
        String str ="2023-03-03 12:58:44";
        String str1 ="2023-03-03 12:58:59";
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date a = dateFormat.parse(str);
        Date n = dateFormat.parse(str1);
        System.out.println(a.getTime() - n.getTime());
    }





}


