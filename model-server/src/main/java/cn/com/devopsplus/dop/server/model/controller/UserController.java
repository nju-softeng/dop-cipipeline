package cn.com.devopsplus.dop.server.model.controller;

import cn.com.devopsplus.dop.server.model.pojo.Model;
import cn.com.devopsplus.dop.server.model.service.modelService.ModelInterface;
import cn.com.devopsplus.dop.server.model.service.userService.UserService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping("/getAllModel")
    public List<String> getAllModel(){return userService.getAllModel();
    }

    @RequestMapping("/run")
    public JSONObject run(@RequestBody String data){
        Model model = new Model();
        JSONObject mapJson=JSONObject.fromObject(data);
        String model_name=String.valueOf(mapJson.get("modelNames"));
        String project_name=String.valueOf(mapJson.get("project_Names"));
        String git_url=String.valueOf(mapJson.get("git_url"));
        model.setModel_name(model_name);
        model.setProject_name(project_name);
        return userService.predict_locate(model,git_url);
    }

}
