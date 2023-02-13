package cn.com.devopsplus.dop.server.cipipeline.model.po.configInfo;

import lombok.*;
import javax.persistence.*;

/**
 * 持续集成流水线步骤持久层对象
 *
 * @author yangyuyan
 * @since 2022-12-01
 */

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="CIStage")
public class CIStage {

    /**
     * 持续集成流水线步骤id
     */
    @Id
    @Column(name="ciStageId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ciStageId;

    /**
     * 持续集成流水线步骤名
     */
    @Column(name="ciStageName")
    private String ciStageName;

    /**
     * 步骤是否使用优化技术模型
     */
    @Column(name="hasModel",columnDefinition = "boolean default false")
    private Boolean hasModel;

    /**
     * 优化技术模型名（需要与模型库中名字对应）
     */
    @Column(name="modelName")
    private String modelName;

    /**
     * 模型训练集使用策略
     */
    @OneToOne
    @JoinColumn(name="modelTrainSetMode", referencedColumnName = "trainSetModeId")
    private TrainSetMode modelTrainSetMode;

    /**
     * 模型更新策略
     */
    @OneToOne
    @JoinColumn(name="modelUpdateMode", referencedColumnName = "updateModeId")
    private UpdateMode modelUpdateMode;
}
