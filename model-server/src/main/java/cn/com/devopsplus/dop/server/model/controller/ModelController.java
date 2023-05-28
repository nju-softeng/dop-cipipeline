package cn.com.devopsplus.dop.server.model.controller;

import cn.com.devopsplus.dop.server.model.mapper.TrainSetModeMapper;
import cn.com.devopsplus.dop.server.model.mapper.UpdateModeMapper;
import cn.com.devopsplus.dop.server.model.pojo.Model;
import cn.com.devopsplus.dop.server.model.pojo.TrainSetMode;
import cn.com.devopsplus.dop.server.model.pojo.UpdateMode;
import cn.com.devopsplus.dop.server.model.service.modelService.ModelInterface;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;


@CrossOrigin
@RestController
@RequestMapping("/v1/modelManage")
public class ModelController {
    @Autowired
    public  ModelInterface modelInterface;
    @Autowired
    public TrainSetModeMapper trainSetModeMapper;
    @Autowired
    public UpdateModeMapper updateModeMapper;

    @PostMapping("/addModels")
    public JSONObject addModels(@RequestBody String data) throws IOException, GitAPIException, SchedulerException {
        JSONObject modelInfos=JSONObject.fromObject(data);
        String configInfoId=modelInfos.getString("configInfoId");
        String configName=modelInfos.getString("configName");
        String codeBaseUrl=modelInfos.getString("codeBaseUrl");
        String codeBaseBranch=modelInfos.getString("codeBaseBranch");
        JSONArray modelList = modelInfos.getJSONArray("modelList");
        JSONObject resultObject=new JSONObject();
        for(int i=0;i<modelList.size();i++){
            JSONObject modelObject=modelList.getJSONObject(i);
            String modelName=modelObject.getString("modelName");
            TrainSetMode trainSetMode=trainSetModeMapper.findTrainSetModeByTrainSetModeId(modelObject.getLong("trainSetModeId"));
            UpdateMode updateMode=updateModeMapper.findUpdateModeByUpdateModeId(modelObject.getLong("updateModeId"));
            Model model=Model.builder()
                    .flow_line_id(Integer.parseInt(configInfoId))
                    .project_name(configName)
                    .project_address(codeBaseBranch)
                    .branch(codeBaseBranch)
                    .model_name(modelName)
                    .build();
            JSONObject jo=modelInterface.add_update(modelName,model,trainSetMode,updateMode);
            if(jo==null){
                return null;
            }
            resultObject.put(modelName,jo.get("BuildResult"));
        }
        return resultObject;
    }

    @RequestMapping("/add")
    public JSONObject add(@RequestBody String data) throws IOException, GitAPIException, SchedulerException {
        JSONObject mapJson=JSONObject.fromObject(data);
        JSONObject models = JSONObject.fromObject(mapJson.get("ciStages"));
        Iterator<String> keys = models.keys();
        JSONObject JO = null;
        while(keys.hasNext()) {
            String key = keys.next();
            if (models.get(key) instanceof JSONObject &&  JSONObject.fromObject((models.get(key))).get("model")instanceof JSONObject) {
                //每一个都会有一个构建模型的请求
                JSONObject model= (JSONObject) JSONObject.fromObject((models.get(key))).get("model");
                String name= (String) model.get("name");
                JSONObject trainSetMode = JSONObject.fromObject(model.get("trainSetMode"));
                JSONObject updateMode = JSONObject.fromObject(model.get("updateMode"));
                Iterator<String> keys1 = trainSetMode.keys();
                TrainSetMode trainSetMode1 = new TrainSetMode();
                UpdateMode   updateMode1   = new UpdateMode();
                Iterator<String> keys2 = updateMode.keys();
                while ((keys1.hasNext())){
                    String key1 =  keys1.next();

                    if(key1.equals("lastCommit")){ //最近n次提交
                        JSONObject a = JSONObject.fromObject(trainSetMode.get("lastCommit"));
                        Integer commitNumber = Integer.parseInt(String.valueOf(a.get("commitNumber")));
                        trainSetMode1.setCommitNumber(commitNumber);
                        trainSetMode1.setTrainSetModeType(TrainSetMode.TrainSetModeType.LastCommit);
                    }
                    if(key1.equals("allCommit")){ //最近n次提交
                        trainSetMode1.setTrainSetModeType(TrainSetMode.TrainSetModeType.AllCommit);
                    }
                    if(key1.equals("periodOfTime")){ //最近n次提交
                        JSONObject a = JSONObject.fromObject(trainSetMode.get("periodOfTime"));
                        String startTime = a.getString("startTime")+" 00:00:00";
                        String endTime = a.getString("endTime")+" 00:00:00";
                        LocalDate localDate1=LocalDate.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        LocalDate localDate2=LocalDate.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        trainSetMode1.setTrainSetModeType(TrainSetMode.TrainSetModeType.PeriodOfTime);
                        trainSetMode1.setStartTime(localDate1);
                        trainSetMode1.setEndTime(localDate2);
                    }

                }
                while ((keys2.hasNext())){
                    String key2 =  keys2.next();
                    if(key2.equals("regularInterval")){ //定时更新
                        JSONObject a = JSONObject.fromObject(updateMode.get("regularInterval"));
                        Integer interval = Integer.parseInt(String.valueOf(a.get("interval")));
                        updateMode1.setUpdateModeType(UpdateMode.UpdateModeType.RegularInterval);
                        updateMode1.setIntervalTime(interval);
                    }
                    if(key2.equals("neverUpdate")){ //从不更新
                        updateMode1.setUpdateModeType(UpdateMode.UpdateModeType.NeverUpdate);
                    }

                }

                Model model1 = new Model();
                String codeBaseUrl =(String) mapJson.get("codeBaseUrl");
                int index = codeBaseUrl.lastIndexOf('.');
                int index2 = codeBaseUrl.lastIndexOf('/');
                String t = codeBaseUrl.substring(index2+1,index);
                model1.setProject_name(t);
                model1.setProject_address((String) mapJson.get("codeBaseUrl"));
                model1.setBranch((String) mapJson.get("codeBaseBranch"));
                model1.setModel_name((String) model.get("name"));

//                System.out.println(model1.getModel_name());
//                System.out.println(key);
//                System.out.println(model1);
//                System.out.println(trainSetMode1);
//                System.out.println(updateMode1);
                JO = modelInterface.add_update(key,model1,trainSetMode1,updateMode1);
            };

        }
        return  JO;

    }
}
