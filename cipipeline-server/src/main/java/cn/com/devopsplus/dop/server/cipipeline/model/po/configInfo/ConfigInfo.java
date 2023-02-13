package cn.com.devopsplus.dop.server.cipipeline.model.po.configInfo;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 持续集成流水线配置数据持久层对象
 *
 * @author yangyuyan
 * @since 2022-11-30
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "configInfo",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"codeBaseUrl", "codeBaseBranch"})},
        indexes = {@Index(columnList = "codeBaseUrl, codeBaseBranch"),
                @Index(columnList = "userId")})
public class ConfigInfo {

    /**
     * 流水线配置信息id
     */
    @Id
    @Column(name = "configInfoId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long configInfoId;

    /**
     * 配置流水线名
     */
    @Column(name = "configName",columnDefinition="varchar(255) default 'default'")
    private String configName;

    /**
     * 代码仓库url
     */
    @Column(name = "codeBaseUrl")
    private String codeBaseUrl;

    /**
     * 代码仓库owner和仓库名
     */
    @Column(name = "ownerAndRepo")
    private String ownerAndRepo;

    /**
     * 代码仓库所有者的access-token
     */
    @Column(name="codeBaseAccessToken")
    private String codeBaseAccessToken;

    /**
     * 代码仓库分支
     */
    @Column(name = "codeBaseBranch")
    private String codeBaseBranch;

    /**
     * 创建用户id
     */
    @Column(name = "userId")
    private Long userId;

    /**
     * 创建时间
     */
    @Column(name = "createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "updateTime")
    private LocalDateTime updateTime;

    /**
     * 流水线步骤数
     */
    @Column(name="ciStageNum")
    private Integer ciStageNum;

    /**
     * 流水线所有步骤
     */
    @Column(name = "allStages")
    private String allStages;

    /**
     * 持续集成流水线步骤（暂时设定最多10个步骤）
     */
    @OneToOne
    @JoinColumn(name="ciStage1", referencedColumnName = "ciStageId")
    private CIStage ciStage1;

    @OneToOne
    @JoinColumn(name="ciStage2", referencedColumnName = "ciStageId")
    private CIStage ciStage2;

    @OneToOne
    @JoinColumn(name="ciStage3", referencedColumnName = "ciStageId")
    private CIStage ciStage3;

    @OneToOne
    @JoinColumn(name="ciStage4", referencedColumnName = "ciStageId")
    private CIStage ciStage4;

    @OneToOne
    @JoinColumn(name="ciStage5", referencedColumnName = "ciStageId")
    private CIStage ciStage5;

    @OneToOne
    @JoinColumn(name="ciStage6", referencedColumnName = "ciStageId")
    private CIStage ciStage6;

    @OneToOne
    @JoinColumn(name="ciStage7", referencedColumnName = "ciStageId")
    private CIStage ciStage7;

    @OneToOne
    @JoinColumn(name="ciStage8", referencedColumnName = "ciStageId")
    private CIStage ciStage8;
    @OneToOne
    @JoinColumn(name="ciStage9", referencedColumnName = "ciStageId")
    private CIStage ciStage9;

    @OneToOne
    @JoinColumn(name="ciStage10", referencedColumnName = "ciStageId")
    private CIStage ciStage10;

    /**
     * 持续集成流水线是否使用持续集成结果预测
     */
    @Column(name = "ciResultPredict")
    private boolean ciResultPredict;

    /**
     * 持续集成流水线是否使用静态代码检查
     */
    @Column(name = "staticCodeCheck")
    private boolean staticCodeCheck;

    /**
     * 生成JenkinsFile路径
     */
    @Column(name = "jenkinsFilePath")
    private String jenkinsFilePath;

    public Long getConfigInfoId() {
        return configInfoId;
    }

    public String getJenkinsFilePath() {return jenkinsFilePath;}
}
