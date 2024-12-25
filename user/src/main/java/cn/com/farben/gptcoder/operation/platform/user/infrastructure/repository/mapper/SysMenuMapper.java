package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper;

import cn.com.farben.gptcoder.operation.platform.user.command.role.QueryMenuCommand;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysMenuListDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysMenuTreeDTO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.SysMenuPO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 *
 * 系统菜单表mapper
 * @author wuanhui
 *
 */
public interface SysMenuMapper extends BaseMapper<SysMenuPO> {

    /**
     * 超管查询所有系统菜单
     *
     * @return 菜单列表
     */
    @Select({
        """
        <script>
            select `id`, menu_name as menuName, parent_id as parentId, order_num as orderNum, path, component, query,
           blank, menu_type as menuType, icon, create_by as createBy, create_time as createTime
            from sys_menu
            where status = 0
        </script>
        """
    })
    List<SysMenuListDTO> findAllMenu();

    /**
     * 列表页查询菜单列表
     * @param param 参数
     * @return 列表数据
     */
    @Select({
        """
        <script>
            select `id`, menu_name as menuName, parent_id as parentId, order_num as orderNum, path, component, query,
           blank, menu_type as menuType, icon, status, create_by as createBy, create_time as createTime
            from sys_menu
            <where>
                <if test = 'param.menuName != null and param.menuName != ""'>
                    and menu_name like concat('%', #{param.menuName}, '%')
                </if>
                <if test = 'param.status != null'>
                    and status = #{param.status}
                </if>
            </where>
            order by order_num asc, create_time desc
        </script>
        """
    })
    List<SysMenuListDTO> menuList(@Param("param") QueryMenuCommand param);

    /**
     * 编辑角色时，弹出的可选菜单目录下拉框
     * @param menuType 类型
     * @return 列表数据
     */
    @Select({
            """
            <script>
                select `id`, menu_name as menuName, parent_id as parentId, order_num as orderNum, path, component,
           query, blank, menu_type as menuType, icon, create_by as createBy, create_time as createTime
                from sys_menu
                <where>
                    status = 0
                    <if test = 'menuType != null and menuType != ""'>
                        and menu_type = #{menuType}
                    </if>
                </where>
                order by order_num asc, create_time desc
            </script>
            """
    })
    List<SysMenuTreeDTO> selectMenuTree(@Param("menuType") String menuType);
}
