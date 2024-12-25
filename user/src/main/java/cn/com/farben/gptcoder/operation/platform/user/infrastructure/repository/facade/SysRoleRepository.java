package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.platform.user.command.role.QueryRoleListCommand;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.SysRoleEntity;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysRoleListDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysRoleTreeDTO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 系统权限与菜单配置管理接口
 * @author wuanhui
 */
public interface SysRoleRepository {

    /**
     * 新增角色
     * @param entity 角色信息
     * @return 操作结果
     */
    Boolean addRole(SysRoleEntity entity);

    /**
     * 根据角色ID查询
     * @param roleId 角色ID
     * @return 操作结果
     */
    SysRoleEntity findRoleById(String roleId);

    /**
     * 修改角色
     * @param entity 角色信息
     * @return 操作结果
     */
    Boolean editRole(SysRoleEntity entity);

    /**
     * 查询角色列表信息
     * @param param 查询参数
     * @return 角色列表
     */
    Page<SysRoleListDTO> roleList(QueryRoleListCommand param);

    /**
     * 删除角色信息
     * @param roleList 角色ID列表
     * @return 操作结果
     */
    Boolean deleteRole(List<String> roleList);

    /**
     * 可选角色下拉框
     * @param rangeScope 当前用户所有角色都最高层级
     * @return 角色列表
     */
    List<SysRoleTreeDTO> selectRoleTree(Integer rangeScope);

    /**
     * 查询指定角色编码是否存在
     * @param roleKey 角色编码
     * @return 数量
     */
    Integer countRoleKey(String roleKey);
}
