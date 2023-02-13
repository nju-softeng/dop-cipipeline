package cn.com.devopsplus.dop.server.cipipeline.model.po.pipeline;

import lombok.*;

import javax.persistence.*;

/**
 * 持续集成流水线运行信息持久层对象
 *
 * @author yangyuyan
 * @since 2022-12-09
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CIPipeline {

    /**
     * 流水线运行状态
     */
    public enum RunningState {
        /**
         * 流水线开始运行
         */
        StartRunning("StartRunning"),
        /**
         * 执行测试验证
         */
        RunningForTest("RunningForTest"),
        /**
         * 运行结束
         */
        Done("Done");

        private String code;

        RunningState(String code) {
        }
    }

    /**
     * 持续集成流水线运行时信息id
     */
    @Id
    @Column(name = "ciPipelineId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ciPipelineId;

    /**
     * 持续集成流水线对应配置信息id
     */
    @Column(name = "configInfoId")
    private Long configInfoId;

    /**
     * 持续集成流水线对应配置信息名
     */
    @Column(name = "configName")
    private String configName;

    /**
     * 持续集成流水线创建用户id
     */
    @Column(name = "userId")
    private Long userId;

    /**
     * 持续集成流水线监听代码仓库url
     */
    @Column(name = "baseCodeBaseUrl")
    private String baseCodeBaseUrl;

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
     * 持续集成流水线监听代码仓库分支
     */
    @Column(name = "baseCodeBaseBranch")
    private String baseCodeBaseBranch;

    /**
     * 持续集成流水线监听代码仓库分支产生新的pull-request，pr序号
     */
    @Column(name = "prNumber")
    private Long prNumber;

    /**
     * 发起pr的源代码仓库url
     */
    @Column(name = "sourceCodeBaseUrl")
    private String sourceCodeBaseUrl;

    /**
     * 发起pr的源代码仓库分支
     */
    @Column(name = "sourceCodeBaseBranch")
    private String sourceCodeBaseBranch;

    /**
     * 持续集成流水线是否使用持续集成结果预测
     */
    @Column(name = "ciResultPredict")
    private Boolean ciResultPredict;

    /**
     * 持续集成流水线持续集成结果预测结果
     */
    @Column(name = "ciResultPredictResult")
    private Boolean ciResultPredictResult;

    /**
     * 持续集成流水线是否使用静态代码检查
     */
    @Column(name = "staticCodeCheck")
    private Boolean staticCodeCheck;

    /**
     * 持续集成流水线在测试机上运行的jenkinsFile存放路径
     */
    @Column(name = "jenkinsFilePath")
    private String jenkinsFilePath;

    /**
     * 流水线运行状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "runningState")
    private RunningState runningState;


    public Long getCiPipelineId() {
        return ciPipelineId;
    }
}
