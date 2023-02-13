package cn.com.devopsplus.dop.server.cipipeline.model.po.pipeline;

import lombok.*;

import javax.persistence.*;

/**
 * 持续集成流水线测试结果
 *
 * @author yangyuyan
 * @since 2023-02-05
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CITestResult {

    /**
     * 持续集成流水线运行时信息id
     */
    @Id
    @Column(name = "ciPipelineId")
    private Long ciPipelineId;

    @Column(name = "prMergeResult")
    private String prMergeResult;

    @Column(name = "prMergeLog")
    private String prMergeLog;

    @Column(name = "staticCodeCheckResult")
    private String staticCodeCheckResult;

    @Column(name = "staticCodeCheckLog")
    @Lob
    private String staticCodeCheckLog;

    @Column(name = "testResult")
    private String testResult;

    @Column(name = "testLog")
    @Lob
    private String testLog;
}
