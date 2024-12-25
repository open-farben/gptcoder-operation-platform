package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.impl;

import cn.com.farben.gptcoder.operation.commons.user.dto.SysRoleCacheDTO;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.SysAccountRoleEntity;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.SysAccountRoleRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper.SysAccountRoleMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.table.SysAccountRoleTableDef.SYS_ACCOUNT_ROLE;

/**
 *
 * 系统角色与用户关联仓储实现
 * @author wuanhui
 *
 */
@Repository
public class SysAccountRoleRepositoryImpl implements SysAccountRoleRepository {

    private final SysAccountRoleMapper sysAccountRoleMapper;

    public SysAccountRoleRepositoryImpl(SysAccountRoleMapper sysAccountRoleMapper) {
        this.sysAccountRoleMapper = sysAccountRoleMapper;
    }

    /**
     * 批量新增关联信息表数据
     *
     * @param list 数据
     * @return 操作结果
     */
    @Override
    public Boolean batchAddUserRole(List<SysAccountRoleEntity> list) {
        return sysAccountRoleMapper.batchAddUserRole(list) > 0;
    }

    /**
     * 根据用户ID删除
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    @Override
    public Boolean deleteByUser(String userId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.where(SYS_ACCOUNT_ROLE.USER_ID.in(userId));
        return sysAccountRoleMapper.deleteByQuery(queryWrapper) > 0;
    }

    /**
     * 根据用户ID查询其拥有的角色信息
     *
     * @param userId 用户ID
     * @return 角色信息
     */
    @Override
    public List<SysRoleCacheDTO> findRoleList(String userId) {
        return sysAccountRoleMapper.findRoleList(userId);
    }

    /**
     * 用户ID批量查询其拥有的角色信息
     * @param userIds 用户ID列表
     * @return 角色信息列表
     */
    @Override
    public List<SysAccountRoleEntity> findRoleIdsByUser(List<String> userIds) {
        return sysAccountRoleMapper.findRoleIdsByUser(userIds);
    }
}
