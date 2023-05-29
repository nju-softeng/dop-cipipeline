package po;

public class Dependency {
    private Integer id;

    private Integer repositoryId;

    private Integer fileId;

    private String name;

    private Integer useCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
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

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public Dependency(Integer repositoryId,Integer fileId,String name){
        this.repositoryId=repositoryId;
        this.name=name;
        this.fileId=fileId;
        useCount=1;
    }
}