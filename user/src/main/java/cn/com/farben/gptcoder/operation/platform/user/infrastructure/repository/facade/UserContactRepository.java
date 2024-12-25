package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.platform.user.domain.entity.UserContactEntity;

/**
 *
 * 用户扩展信息仓储接口<br>
 * 创建时间：2023/8/16<br>
 * @author ltg
 *
 */
public interface UserContactRepository {
    /**
     * 根据用户id获取用户扩展信息
     * @param userId 用户id
     * @return 用户扩展信息实体
     */
    UserContactEntity findByUserid(String userId);
}
