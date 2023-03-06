package cn.com.devopsplus.dop.server.cipipeline.model.vo;
import cn.com.devopsplus.dop.server.cipipeline.model.po.pipeline.CIPipeline;
import lombok.*;

import javax.persistence.*;

/**
 * 展示持续集成流水线运行信息
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CIpipelineVo {
    /**
     * 持续集成流水线运行时信息id
     */
    private Long ciPipelineId;

    /**
     * 持续集成流水线监听代码仓库分支产生新的pull-request，pr序号
     */
    private Long prNumber;

    /**
     * 发起pr的源代码仓库url
     */
    private String sourceCodeBaseUrl;

    /**
     * 发起pr的源代码仓库分支
     */
    private String sourceCodeBaseBranch;

    /**
     * 流水线运行状态
     */
    private String runningState;
}
