package cn.com.devopsplus.dop.server.cipipeline.dao.configInfo;

import cn.com.devopsplus.dop.server.cipipeline.model.po.configInfo.ConfigInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 持续集成流水线信息仓库接口
 *
 * @author yangyuyan
 * @since 2022-12-02
 */

@Repository
public interface ConfigInfoRepository extends JpaRepository<ConfigInfo,Long> {

    /**
     * 根据流水线配置信息id查询流水线配置信息
     * @param configInfoId
     * @return
     */
    ConfigInfo findAllByConfigInfoId(long configInfoId);

    /**
     * 根据代码仓库地址和分支查询流水线配置信息
     * @param codeBaseUrl
     * @param codeBaseBranch
     * @return
     */
    ConfigInfo findAllByCodeBaseUrlAndCodeBaseBranch(String codeBaseUrl,String codeBaseBranch);

    /**
     * 根据代码仓库地址和分支查询流水线配置信息id
     * @param codeBaseUrl
     * @param codeBaseBranch
     * @return
     */
    Long findConfigInfoIdByCodeBaseUrlAndCodeBaseBranch(String codeBaseUrl,String codeBaseBranch);

    /**
     * 根据用户id查询查询流水线配置信息
     * @param userId
     * @return
     */
    List<ConfigInfo> findAllByUserId(long userId);

    /**
     * 判断代码仓库地址和分支对应的持续集成流水线是否存在
     * @param codeBaseUrl
     * @param codeBaseBranch
     * @return
     */
    boolean existsByCodeBaseUrlAndCodeBaseBranch(String codeBaseUrl,String codeBaseBranch);

    /**
     * 根据流水线配置信息id获取对应的JenkinsFile存储路径
     * @param configInfoId
     * @return
     */
    @Query(value = "select jenkinsFilePath from ConfigInfo where configInfoId=:configInfoId")
    String getJenkinsFilePathByConfigInfoId(@Param("configInfoId") Long configInfoId);

    /**
     * 获得所有的configInfoId
     * @return
     */
    @Query(value = "select configInfoId from ConfigInfo")
    List<Long> getConfigInfoIds();
}
