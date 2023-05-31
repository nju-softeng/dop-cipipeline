package cn.com.devopsplus.dop.server.model.pojo;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Model {
    @Id
    public Integer  model_id;
    public Integer  flow_line_id;
    public  String  project_name; //项目名称
    public  String  project_address; //项目地址
    public  String  version;
    public  String  branch;
    public  String  model_name;
    public  String  last_commit_time;//最后一次更新时间 如果是一样的没有更新就不训练了

    @Override
    public String toString() {
        return "Model{" +
                "model_id=" + model_id +
                ", flow_line_id=" + flow_line_id +
                ", project_name='" + project_name + '\'' +
                ", project_address='" + project_address + '\'' +
                ", version='" + version + '\'' +
                ", branch='" + branch + '\'' +
                ", model_name='" + model_name + '\'' +
                '}';
    }
}
