package cn.com.devopsplus.dop.server.model.service.userService;

import cn.com.devopsplus.dop.server.model.config.ModelConfig;
import cn.com.devopsplus.dop.server.model.mapper.modelMapper;
import cn.com.devopsplus.dop.server.model.pojo.Model;
import cn.com.devopsplus.dop.server.model.util.CloneUrl;
import cn.com.devopsplus.dop.server.model.util.Run;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
      @Autowired
      private ModelConfig modelConfig;
      @Autowired
      private CloneUrl cloneUrl;
      @Autowired
      private modelMapper modelmapper;

      @Autowired
      private Run run;
      public JSONObject predict_locate (Model model,String predict_url){
          Map<String, Object> map = new HashMap<>();
          List<Map<String,String>> mapdatalist=new ArrayList<>();
          String predict_project=predict_url.substring(predict_url.lastIndexOf("/")+1,predict_url.lastIndexOf("."));
          Object predictConfig;
          String projectpath=modelConfig.getDataPath()+predict_project;
          if (!cloneUrl.cloneRepo(projectpath,predict_url)){
              map.put("success", false);
              JSONObject JO = JSONObject.fromObject(map);
              return JO;

          }
          //预测
          Boolean result=run.startpredict(modelConfig.getRunModelPythonPath(),predict_project,modelConfig.getPythonProjectPath(),model.getProject_name(),model.getModel_name());
          System.out.println(result);
          //无缺陷
          if(!result){
              map.put("success",true);
              map.put("data",false);
          }
          else {
              map.put("success",true);
              String res=run.startlocate(modelConfig.getLocationModelPythonPath(),predict_project,model.getProject_name(),modelConfig.getPythonProjectPath(),model.getModel_name());
              System.out.println(res);

              JSONArray entryArray = JSONArray.fromObject(res);
              List datalist=JSONArray.toList(entryArray, String.class);

              String str;
              int i=0;
              while (i<datalist.size()) {
                  str= (String) datalist.get(i);
                  Map<String,String> mapdata=new HashMap<>();
                  mapdata.put("id",String.valueOf(i+1));
                  mapdata.put("defectCode",str.substring(0,str.indexOf("----")));
                  mapdata.put("defectLocation",str.substring(str.indexOf("----")+4,str.length()));
                  mapdatalist.add(mapdata);
                  i++;
              }
              map.put("data",mapdatalist);
          }
          JSONObject JO = JSONObject.fromObject(map);
          return JO;

      }

      public  List<String> getAllModel(){
          List<Model> model = modelmapper.findAll();

          List<String> modelName = new ArrayList<>();
          model.stream().forEach(v->modelName.add(v.getModel_name()+v.getVersion()));
          return modelName;

      }




}
