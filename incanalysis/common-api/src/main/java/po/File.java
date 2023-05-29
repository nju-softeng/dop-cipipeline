package po;

import vo.file.FileVO;

import java.util.Date;

public class File {
    private Integer id;

    private Integer repositoryId;

    private String name;

    private String type;

    private String resourceDir;

    private Date createTime;

    private Boolean isChanged;


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

    public Boolean getIsChanged() {
        return isChanged;
    }

    public void setIsChanged(Boolean isChanged) {
        this.isChanged = isChanged;
    }

    public File(){}

    public File(FileVO fileVO) {
        this.id=fileVO.getId();
        this.repositoryId=fileVO.getRepositoryId();
        this.name=fileVO.getName();
        this.type=fileVO.getType();
        this.resourceDir=fileVO.getResourceDir();
        this.createTime=fileVO.getCreateTime();
        this.isChanged=fileVO.getIsChanged();
    }
}