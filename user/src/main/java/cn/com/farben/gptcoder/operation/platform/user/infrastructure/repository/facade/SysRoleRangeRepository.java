package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.platform.user.domain.entity.SysRoleRangeEntity;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysRoleRangeDTO;

import java.util.List;

/**
 * 系统角色授权配置管理接口
 * @author wuanhui
 */
public interface SysRoleRangeRepository {

    /**
     * 添加自定义角色范围表
     * @param list 列表数据
     * @return 操作结果
     */
    Boolean addRoleRange(List<SysRoleRangeEntity> list);

    /**
     * 根据角色ID查询该角色关联的机构范围
     * @param roleId 角色ID
     * @return 机构范围
     */
    List<SysRoleRangeDTO> findRangeListByRole(String roleId);

    /**
     * 根据角色ID查询该角色关联的机构号
     * @param roleId 角色ID
     * @return 机构范围
     */
    List<Integer> findRangeOrganNoListByRole(String roleId);

    /**
     * 删除角色需同步删除对应的机构范围
     * @param roleIds 多个角色ID
     * @return 机构范围
     */
    Boolean delByRoleIds(List<String> roleIds);
}
