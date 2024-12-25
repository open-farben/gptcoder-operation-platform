package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper;

import cn.com.farben.gptcoder.operation.platform.user.command.role.QueryRoleListCommand;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysRoleListDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysRoleTreeDTO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.RoleAccountRangePO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.SysRolePO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 *
 * 系统角色与用户授权关联表mapper
 * @author wuanhui
 *
 */
public interface RoleAccountRangeMapper extends BaseMapper<RoleAccountRangePO> {

    /**
     * 角色列表查询角色信息
     *
     * @return 角色列表
     */
    @Select({
        """
        <script>
            select `id`, role_name, role_key, role_sort, data_scope, menu_check, dept_check, status, remark, create_by, create_time
            from sys_role
            <where>
                role_type = 1
                <if test = 'param.roleName != null and param.roleName != ""'>
                    and (role_name like concat('%',#{param.roleName},'%') or role_key like concat('%',#{param.roleName},'%'))
                </if>
                <if test = 'param.status != null and param.status != ""'>
                    and status = #{status}
                </if>
            </where>
            order by create_time DESC
        </script>
        """
    })
    Page<SysRoleListDTO> roleList(@Param("param") QueryRoleListCommand param, Page<SysRolePO> page);

    /**
     * 删除角色信息
     * @param roleList 角色ID
     * @return 操作结果
     */
    @Delete({
        """
        <script>
            delete from sys_role
            <where>
                role_type = 1 and `id` in
                <foreach collection="roleList" open="(" close=")" separator="," item="roleId">
                    #{roleId}
                </foreach>
            </where>
        </script>
        """
    })
    Integer deleteRole(@Param("roleList") List<String> roleList);

    /**
     * 可选角色下拉框
     * @param admin 排除超级管理员
     * @return 角色列表
     */
    @Select({
        """
        <script>
            select `id`, role_name, role_key, role_sort, data_scope, menu_check, dept_check, status, remark, create_by, create_time
            from sys_role
            where status = 0 and role_type = 1
        </script>
        """
    })
    List<SysRoleTreeDTO> selectRoleTree(@Param("admin") String admin);


    /**
     * 查询指定角色编码是否存在
     * @param roleKey 角色编码
     * @return 数量
     */
    @Select({
        """
        <script>
            select count(*) from sys_role
            where role_key = #{roleKey} and status = 0
        </script>
        """
    })
    Integer countRoleKey(@Param("roleKey") String roleKey);
}
