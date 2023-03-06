package cn.com.devopsplus.dop.server.cipipeline.model.vo;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * 持续集成流水线测试结果
 *
 * @author yangyuyan
 * @since 2023-02-05
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CITestResultVo {

    private String prMergeResult;

    private String prMergeLog;

    private String staticCodeCheckResult;

    private String staticCodeCheckLog;

    private String testResult;

    private String testLog;
}
