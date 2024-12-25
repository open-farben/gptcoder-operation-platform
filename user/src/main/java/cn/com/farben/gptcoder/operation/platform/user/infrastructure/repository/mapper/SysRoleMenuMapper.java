package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper;

import cn.com.farben.gptcoder.operation.platform.user.domain.entity.SysRoleMenuEntity;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysMenuListDTO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.SysRoleMenuPO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统角色与菜单关联mapper
 * @author wuanhui
 */
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenuPO> {

    /**
     * 根据角色ID查询该角色拥有的菜单列表
     * @param roleId 角色ID
     * @return 菜单列表
     */
    @Select({
        """
        <script>
            select distinct m.`id`, m.menu_name as menuName, m.parent_id as parentId, m.order_num as orderNum,
                    m.path, m.component, m.query, m.blank, m.menu_type as menuType, m.icon, m.create_by as createBy,
            m.create_time as createTime
            from sys_menu m left join sys_role_menu r on m.`id` = r.menu_id
            <where>
                m.status = 0 and role_id in
                <foreach collection="roleList" open="(" close=")" separator="," item="roleId">
                    #{roleId}
                </foreach>
            </where>
            order by m.order_num asc, m.create_time asc
        </script>
        """
    })
    List<SysMenuListDTO> findMenuByRole(@Param("roleList") List<String> roleId);

    /**
     * 批量新增
     * @param dataList 新增数据
     * @return 影响行数
     */
    @Insert({
        """
        <script>
            insert into sys_role_menu (role_id, menu_id)
            values
            <foreach collection="dataList" separator="," item="adopt">
                 (#{adopt.roleId}, #{adopt.menuId})
            </foreach>
        </script>
        """
    })
    Integer addRoleMenu(@Param("dataList") List<SysRoleMenuEntity> dataList);

    /**
     * 根据角色ID删除
     * @param roleList 多个角色ID
     * @return 影响行数
     */
    @Delete({
        """
        <script>
            delete from sys_role_menu
            <where>
                role_id in
                <foreach collection="roleList" open="(" close=")" separator="," item="roleId">
                    #{roleId}
                </foreach>
            </where>
        </script>
        """
    })
    Integer deleteByRoleId(@Param("roleList") List<String> roleList);

    /**
     * 根据菜单ID删除
     * @param menuList 多个菜单ID
     * @return 影响行数
     */
    @Delete({
        """
        <script>
            delete from sys_role_menu
            <where>
                menu_id in
                <foreach collection="menuList" open="(" close=")" separator="," item="menuId">
                    #{menuId}
                </foreach>
            </where>
        </script>
        """
    })
    Integer deleteByMenuId(@Param("menuList") List<String> menuList);


    /**
     * 根据角色ID查询该角色拥有的菜单ID
     * @param roleId 角色ID
     * @return 菜单ID
     */
    @Select({
        """
        <script>
            select menu_id from sys_role_menu
            <where>
                role_id = #{roleId}
            </where>
        </script>
        """
    })
    List<String> findMenuRoleIds(@Param("roleId") String roleId);


    /**
     * 查询指定的菜单ID是否被授权
     * @param menuIds 菜单ID
     * @return 菜单ID
     */
    @Select({
        """
        <script>
            select count(*) from sys_role_menu
            <where>
                menu_id in
                <foreach collection="menuIds" open="(" close=")" separator="," item="menuId">
                    #{menuId}
                </foreach>
            </where>
        </script>
        """
    })
    Integer findRoleByMenuIds(@Param("menuIds") List<String> menuIds);
}
