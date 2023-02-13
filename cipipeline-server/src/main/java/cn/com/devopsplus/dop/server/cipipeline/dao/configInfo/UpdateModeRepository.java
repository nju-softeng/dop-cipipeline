package cn.com.devopsplus.dop.server.cipipeline.dao.configInfo;

import cn.com.devopsplus.dop.server.cipipeline.model.po.configInfo.UpdateMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 模型更新策略信息仓库接口
 *
 * @author yangyuyan
 * @since 2022-12-02
 */

@Repository
public interface UpdateModeRepository extends JpaRepository<UpdateMode,Long> {

    /**
     * 根据模型更新策略id查找模型更新策略
     * @param updateModeId
     * @return
     */
    UpdateMode findUpdateModeByUpdateModeId(Long updateModeId);
}
