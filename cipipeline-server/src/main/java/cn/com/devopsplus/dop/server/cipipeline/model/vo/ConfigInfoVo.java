package cn.com.devopsplus.dop.server.cipipeline.model.vo;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * 展示持续集成流水线配置信息
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigInfoVo {
    /**
     * 流水线配置信息id
     */
    private Long configInfoId;

    /**
     * 配置流水线名
     */
    private String configName;

    /**
     * 代码仓库url
     */
    private String codeBaseUrl;

    /**
     * 代码仓库分支
     */
    private String codeBaseBranch;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
