package cn.com.devopsplus.dop.server.cipipeline.dao.configInfo;

import cn.com.devopsplus.dop.server.cipipeline.model.po.configInfo.CIStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 持续集成流水线步骤仓库接口
 *
 * @author yangyuyan
 * @since 2022-12-02
 */

@Repository
public interface CIStageRepository extends JpaRepository<CIStage,Long> {
    /**
     * 根据步骤id查询步骤信息
     */
    CIStage findAllByCiStageId(long ciStageId);

    /**
     * 根据步骤id删除步骤
     */
    void deleteAllByCiStageId(long ciStageId);
}
