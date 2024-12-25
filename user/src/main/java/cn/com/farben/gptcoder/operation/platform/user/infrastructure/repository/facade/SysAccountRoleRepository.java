package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.commons.user.dto.SysRoleCacheDTO;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.SysAccountRoleEntity;

import java.util.List;

/**
 * 系统角色与用户关联仓储接口
 * @author wuanhui
 */
public interface SysAccountRoleRepository {

    /**
     * 批量新增关联信息表数据
     * @param list 数据
     * @return 操作结果
     */
    Boolean batchAddUserRole(List<SysAccountRoleEntity> list);

    /**
     * 根据用户ID删除
     * @param userId 用户ID
     * @return 操作结果
     */
    Boolean deleteByUser(String userId);

    /**
     * 根据用户ID查询其拥有的角色信息
     * @param userId 用户ID
     * @return 角色信息
     */
    List<SysRoleCacheDTO> findRoleList(String userId);

    /**
     * 用户ID批量查询其拥有的角色信息
     * @param userIds userIds列表
     * @return 角色信息列表
     */
    List<SysAccountRoleEntity> findRoleIdsByUser(List<String> userIds);
}
