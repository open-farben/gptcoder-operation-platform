package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.impl;

import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.RoleAccountRangeRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.service.RoleAccountRangeService;
import org.springframework.stereotype.Repository;

/**
 * 系统角色授权操作用户管理仓储实现
 * @author wuanhui
 */
@Repository
public class RoleAccountRangeRepositoryImpl implements RoleAccountRangeRepository {

    private final RoleAccountRangeService roleAccountRangeService;

    public RoleAccountRangeRepositoryImpl(RoleAccountRangeService roleAccountRangeService) {
        this.roleAccountRangeService = roleAccountRangeService;
    }
}
