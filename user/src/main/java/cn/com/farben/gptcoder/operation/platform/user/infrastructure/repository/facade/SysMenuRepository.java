package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.platform.user.command.role.QueryMenuCommand;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.SysMenuEntity;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysMenuListDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysMenuTreeDTO;

import java.util.List;

public interface SysMenuRepository {

    /**
     * 根据主键ID查询菜单
     * @param id 主键ID
     * @return 菜单信息
     */
    SysMenuEntity findMenuById(String id);

    /**
     * 超管查询所有系统菜单
     * @return 菜单列表
     */
    List<SysMenuListDTO> findAllMenu();

    /**
     * 新增菜单操作
     * @param entity 菜单对象
     * @return 影响行数
     */
    Boolean addMenu(SysMenuEntity entity);

    /**
     * 修改菜单操作
     * @param entity 菜单对象
     * @return 影响行数
     */
     Boolean editMenu(SysMenuEntity entity);

    /**
     * 删除指定ID的菜单信息
     * @param id 菜单ID
     * @return 影响行数
     */
     Boolean deleteMenu(String id);

    /**
     * 列表页查询菜单列表
     * @param param 参数
     * @return 查询结果
     */
    List<SysMenuListDTO> menuList(QueryMenuCommand param);

    /**
     * 编辑角色时，弹出的可选菜单目录下拉框
     * @param menuType 菜单类型
     * @return 列表数据
     */
    List<SysMenuTreeDTO> selectMenuTree(String menuType);
}
