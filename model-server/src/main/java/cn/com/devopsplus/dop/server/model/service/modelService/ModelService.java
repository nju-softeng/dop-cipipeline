package cn.com.devopsplus.dop.server.model.service.modelService;


import cn.com.devopsplus.dop.server.model.pojo.Model;
import cn.com.devopsplus.dop.server.model.pojo.TrainSetMode;
import cn.com.devopsplus.dop.server.model.pojo.UpdateMode;

import net.sf.json.JSONObject;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class ModelService  implements ModelInterface {

    @Override
    public JSONObject add_update(String modelType , Model model, TrainSetMode trainSetMode, UpdateMode updateMode) throws IOException, GitAPIException, SchedulerException {
        switch (modelType){
            case "failurePredict":{
                ModelTypeInterface modelTypeInterface = new DetectModelService();
                JSONObject jo = modelTypeInterface.train(model,trainSetMode,updateMode);
                return jo;
            }
        }
        return null;
    }

//    public JSONObject get(Integer userId){
//        Map<String,Object> map=new HashMap<>();
//        List<Map<String,String>> mapdatalist=new ArrayList<>();
//        List<TrainSetMode> models = trainSetMapper.selectByUserId(userId);
//        for (int i =0;i<models.size();i++){
//            Map<String,String> mapdata=new HashMap<>();
//
//            mapdata.put("trainSetModeId",models.get(i).getTrainSetModeId());
//            mapdata.put("trainSetModeType",models.get(i).getTrainSetModeType().toString());
//            mapdata.put("commitNumber",models.get(i).getCommitNumber()+"");
//            SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String startTime=dateFormat.format(models.get(i).getStartTime());
//            String endTime=dateFormat.format(models.get(i).getEndTime());
//            mapdata.put("startTime", startTime);
//            mapdata.put("endTime", endTime);
//            mapdatalist.add(mapdata);
//        }
//        map.put("success",true);
//        map.put("data",mapdatalist);
//        JSONObject JO = JSONObject.fromObject(map);
//        return JO;
//    }
}
