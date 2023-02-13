package cn.com.devopsplus.dop.server.cipipeline.dao.pipeline;

import cn.com.devopsplus.dop.server.cipipeline.model.po.pipeline.CIPipeline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 持续集成流水线运行信息仓库接口
 *
 * @author yangyuyan
 * @since 2022-12-10
 */

public interface CIPipelineRepository extends JpaRepository<CIPipeline,Long> {

    /**
     * 根据持续集成流水线id获得持续集成流水线信息
     * @param ciPipelineId
     * @return
     */
    CIPipeline findAllByCiPipelineId(long ciPipelineId);

    /**
     * 根据持续集成流水线配置信息id或者对应的所有运行流水线
     * @param configInfoId
     * @return
     */
    List<CIPipeline> findAllByConfigInfoId(long configInfoId);

    /**
     * 根据持续集成流水线创建用户id获得他创建的所有运行流水线
     * @param userId
     * @return
     */
    List<CIPipeline> findAllByUserId(long userId);

    /**
     * 根据配置信息id和prNumber获得对应流水线
     * @param configInfoId
     * @param prNumber
     * @return
     */
    CIPipeline findAllByConfigInfoIdAndPrNumber(long configInfoId,long prNumber);
}
