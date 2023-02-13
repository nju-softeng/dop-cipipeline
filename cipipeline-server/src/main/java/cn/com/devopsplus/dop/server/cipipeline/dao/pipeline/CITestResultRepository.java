package cn.com.devopsplus.dop.server.cipipeline.dao.pipeline;

import cn.com.devopsplus.dop.server.cipipeline.model.po.pipeline.CIPipeline;
import cn.com.devopsplus.dop.server.cipipeline.model.po.pipeline.CITestResult;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 持续集成流水线测试结果仓库接口
 *
 * @author yangyuyan
 * @since 2023-02-05
 */
public interface CITestResultRepository extends JpaRepository<CITestResult,Long> {
    /**
     * 根据持续集成流水线id获得持续集成流水线测试结果
     * @param ciPipelineId
     * @return
     */
    CITestResult findAllByCiPipelineId(long ciPipelineId);
}
