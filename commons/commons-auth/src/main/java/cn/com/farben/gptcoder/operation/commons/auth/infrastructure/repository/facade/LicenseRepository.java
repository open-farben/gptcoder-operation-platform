package cn.com.farben.gptcoder.operation.commons.auth.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.commons.auth.domain.entity.LicenseEntity;
import java.time.LocalDateTime;

/**
 *
 * license仓储接口<br>
 * 创建时间：2023/9/21<br>
 * @author ltg
 *
 */
public interface LicenseRepository {
    /**
     * 获取license
     * @return license
     */
    String getLicense();

    /**
     * 根据license查询数量
     * @param lic 授权码
     * @return 数量
     */
    long countByLicense(String lic);

    /**
     * 存储license
     * @param entity 新的license实体
     */
    void storeLicense(LicenseEntity entity);

    /**
     * 获取最近一次授权时间
     * @return 最近一次授权时间
     */
    LocalDateTime getLastAuthDate();

    /**
     * 查询启用的插件用户数量
     */
    long countEnabledUser();
}
