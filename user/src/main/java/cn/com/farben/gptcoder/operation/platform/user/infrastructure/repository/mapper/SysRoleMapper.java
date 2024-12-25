package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper;

import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysRoleTreeDTO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.SysRolePO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 *
 * 系统角色表mapper
 * @author wuanhui
 *
 */
public interface SysRoleMapper extends BaseMapper<SysRolePO> {

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
     * @param rangeScope 当前用户所有角色都最高层级
     * @return 角色列表
     */
    @Select({
        """
        <script>
            select `id`, role_name as roleName, role_key as roleKey, role_sort as roleSort, data_scope as dataScope,
           menu_check as menuCheck, dept_check as deptCheck, status, remark, role_type, range_scope as rangeScope,
           create_by as createBy, create_time as createTime
            from sys_role
            where status = 0 and role_type = 1 and range_scope &gt;= #{rangeScope}
        </script>
        """
    })
    List<SysRoleTreeDTO> selectRoleTree(@Param("rangeScope") Integer rangeScope);


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
