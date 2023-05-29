package po;



import vo.repository.RepositoryVO;

import java.util.Date;

public class Repository {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
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

    public Repository(){}

    public Repository(RepositoryVO repositoryVO){
        this.id=repositoryVO.getId();
        this.name=repositoryVO.getName();
        this.description=repositoryVO.getDescription();
        this.createTime=repositoryVO.getCreateTime();
    }
}