package vo.file;

import lombok.Data;
import po.File;

import java.util.Date;

@Data
public class FileVO {
    private Integer id;

    private Integer repositoryId;

    private String name;

    private String type;

    private String resourceDir;

    private Boolean isChanged;

    private Date createTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Integer repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getResourceDir() {
        return resourceDir;
    }

    public void setResourceDir(String resourceDir) {
        this.resourceDir = resourceDir == null ? null : resourceDir.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public FileVO(){}

    public FileVO(File file){
        this.id=file.getId();
        this.repositoryId=file.getRepositoryId();
        this.name=file.getName();
        this.type=file.getType();
        this.resourceDir=file.getResourceDir();
        this.createTime=file.getCreateTime();
        this.isChanged=file.getIsChanged();
    }
}
