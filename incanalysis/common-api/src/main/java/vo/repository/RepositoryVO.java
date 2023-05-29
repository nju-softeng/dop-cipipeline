package vo.repository;


import lombok.Data;
import po.Repository;

import java.util.Date;

@Data
public class RepositoryVO {
    private Integer id;

    private String name;

    private String description;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public RepositoryVO(){}

    public RepositoryVO(Repository repository){
        this.id=repository.getId();
        this.name=repository.getName();
        this.description=repository.getDescription();
        this.createTime=repository.getCreateTime();
    }
}
