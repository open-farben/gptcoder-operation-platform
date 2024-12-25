package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.platform.user.domain.entity.SysRoleMenuEntity;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysMenuListDTO;

import java.util.List;

/**
 * 系统权限与菜单配置管理接口
 * @author wuanhui
 */
public interface SysRoleMenuRepository {

    /**
     * 根据角色ID查询该角色拥有的菜单列表
     * @param roleId 角色ID
     * @return 菜单列表
     */
    List<SysMenuListDTO> findMenuByRole(List<String> roleId);

    /**
     * 批量新增角色和菜单关联数据
     * @param dataList 数据
     * @return 操作结果
     */
    Boolean addRoleMenu(List<SysRoleMenuEntity> dataList);

    /**
     * 根据角色ID删除
     * @param list 角色ID
     * @return 操作结果
     */
    Boolean deleteByRoleId(List<String> list);

    /**
     * 根据菜单ID删除
     * @param list 菜单ID
     * @return 操作结果
     */
    Boolean deleteByMenuId(List<String> list);

    /**
     * 根据角色ID查询该角色拥有的菜单ID
     * @param roleId 角色ID
     * @return 菜单ID
     */
    List<String> findMenuRoleIds(String roleId);

    /**
     * 查询指定的菜单ID是否被授权
     * @param menuIds 菜单ID
     * @return 菜单ID
     */
    Integer findRoleByMenuIds(List<String> menuIds);
}
