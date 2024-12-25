package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.impl;

import cn.com.farben.gptcoder.operation.platform.user.domain.entity.SysRoleMenuEntity;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysMenuListDTO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.SysRoleMenuRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper.SysRoleMenuMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * 系统组织机构仓储实现
 * @author wuanhui
 *
 */
@Repository
public class SysRoleMenuRepositoryImpl implements SysRoleMenuRepository {

    private final SysRoleMenuMapper sysRoleMenuMapper;

    public SysRoleMenuRepositoryImpl(SysRoleMenuMapper sysRoleMenuMapper) {
        this.sysRoleMenuMapper = sysRoleMenuMapper;
    }

    /**
     * 根据角色ID查询该角色拥有的菜单列表
     *
     * @param roleId 角色ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenuListDTO> findMenuByRole(List<String> roleId) {
        return sysRoleMenuMapper.findMenuByRole(roleId);
    }

    /**
     * 批量新增角色和菜单关联数据
     *
     * @param dataList 数据
     * @return 操作结果
     */
    @Override
    public Boolean addRoleMenu(List<SysRoleMenuEntity> dataList) {
        return sysRoleMenuMapper.addRoleMenu(dataList) > 0;
    }

    /**
     * 根据角色ID删除
     *
     * @param list 角色ID
     * @return 操作结果
     */
    @Override
    public Boolean deleteByRoleId(List<String> list) {
        return sysRoleMenuMapper.deleteByRoleId(list) > 0;
    }

    /**
     * 根据菜单ID删除
     *
     * @param list 菜单ID
     * @return 操作结果
     */
    @Override
    public Boolean deleteByMenuId(List<String> list) {
        return sysRoleMenuMapper.deleteByMenuId(list) > 0;
    }

    /**
     * 根据角色ID查询该角色拥有的菜单ID
     *
     * @param roleId 角色ID
     * @return 菜单ID
     */
    @Override
    public List<String> findMenuRoleIds(String roleId) {
        return sysRoleMenuMapper.findMenuRoleIds(roleId);
    }

    /**
     * 查询指定的菜单ID是否被授权
     * @param menuIds 菜单ID
     * @return 菜单ID
     */
    @Override
    public Integer findRoleByMenuIds(List<String> menuIds) {
        return sysRoleMenuMapper.findRoleByMenuIds(menuIds);
    }
}
