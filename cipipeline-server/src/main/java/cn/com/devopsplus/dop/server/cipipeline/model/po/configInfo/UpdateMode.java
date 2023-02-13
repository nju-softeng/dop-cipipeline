package cn.com.devopsplus.dop.server.cipipeline.model.po.configInfo;

import lombok.*;
import javax.persistence.*;

/**
 * 模型更新策略信息持久层对象
 *
 * @author yangyuyan
 * @since 2022-01-01
 */

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "updateMode")
public class UpdateMode {
    /**
     * 模型更新策略类型枚举
     */
    public enum UpdateModeType{
        /**
         * 模型从不更新
         */
        NeverUpdate("NeverUpdate"),
        /**
         * 模型定期更新
         */
        RegularInterval("RegularInterval");
        private String code;
        UpdateModeType(String code) {
        }
    }

    /**
     * 模型更新策略id
     */
    @Id
    @Column(name="updateModeId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long updateModeId;

    /**
     * 模型更新策略类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name="updateModeType")
    private UpdateModeType updateModeType;

    /**
     * 定期更新时间间隔（单位：天）
     */
    @Column(name="intervalTime")
    private Integer intervalTime;

    public Long getUpdateModeId() {
        return updateModeId;
    }
}
