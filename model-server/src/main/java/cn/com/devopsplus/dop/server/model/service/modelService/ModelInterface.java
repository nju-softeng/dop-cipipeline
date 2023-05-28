package cn.com.devopsplus.dop.server.model.service.modelService;

import cn.com.devopsplus.dop.server.model.pojo.Model;
import cn.com.devopsplus.dop.server.model.pojo.TrainSetMode;
import cn.com.devopsplus.dop.server.model.pojo.UpdateMode;
import net.sf.json.JSONObject;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

import java.io.IOException;
 @Component
public interface ModelInterface  {

     public JSONObject add_update(String modelType , Model model, TrainSetMode trainSetMode ,UpdateMode updateMode) throws IOException, GitAPIException, SchedulerException;
 }

