package cn.com.devopsplus.dop.server.cipipeline.dao.configInfo;

import cn.com.devopsplus.dop.server.cipipeline.model.po.configInfo.TrainSetMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 模型训练集选择策略信息仓库接口
 *
 * @author yangyuyan
 * @since 2022-12-02
 */

@Repository
public interface TrainSetModeRepository extends JpaRepository<TrainSetMode,Long> {
    /**
     * 根据模型训练集使用策略id查询对应使用策略
     * @param trainSetModeId
     * @return
     */
    TrainSetMode findTrainSetModeByTrainSetModeId(Long trainSetModeId);
}
