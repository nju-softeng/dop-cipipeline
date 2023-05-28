package cn.com.devopsplus.dop.server.model.pojo;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * 模型训练集选择策略信息持久层对象
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
@Table(name = "trainSetMode")
public class TrainSetMode {
    /**
     * 训练集选取策略类型枚举
     */
    public enum TrainSetModeType{
        /**
         * 使用所有提交作为训练集
         */
        AllCommit("AllCommit"),
        /**
         * 使用最近的n次提交作为训练集
         */
        LastCommit("LastCommit"),
        /**
         * 使用一段时间内的提交作为训练集
         */
        PeriodOfTime("PeriodOfTime")
        ;
        private String code;
        TrainSetModeType(String code) {
        }
    }

    /**
     * 模型训练集使用策略id
     */
    @Id
    @Column(name="trainSetModeId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trainSetModeId;

    /**
     * 训练集选取策略类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name="trainSetModeType")
    private TrainSetModeType trainSetModeType;

    /**
     * 最近n次提交
     */
    @Column(name="commitNumber")
    private Integer commitNumber;

    /**
     * 开始时间
     */
    @Column(name="startTime")
    private LocalDate startTime;

    /**
     * 结束时间
     */
    @Column(name="endTime")
    private LocalDate endTime;

    public Long getTrainSetModeId() {
        return trainSetModeId;
    }
}
