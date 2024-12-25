package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper;

import cn.com.farben.gptcoder.operation.commons.user.dto.SysRoleCacheDTO;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.SysAccountRoleEntity;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.SysAccountRolePO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 *
 * 系统角色与用户关联表mapper
 * @author wuanhui
 *
 */
public interface SysAccountRoleMapper extends BaseMapper<SysAccountRolePO> {

    /**
     * 批量新增关联信息表数据
     * @param list 列表数据
     * @return 影响行数
     */
    @Insert({
        """
        <script>
            insert into sys_account_role (user_id, role_id)
            values
            <foreach collection="adoptions" separator="," item="adopt">
                (#{adopt.userId}, #{adopt.roleId})
            </foreach>
        </script>
        """
    })
    Integer batchAddUserRole(@Param("adoptions") List<SysAccountRoleEntity> list);

    /**
     * 根据用户ID查询其拥有的角色信息
     *
     * @param userId 用户ID
     * @return 角色信息
     */
    @Select({
        """
        <script>
            select r.`id`, r.role_name as roleName, r.role_key as roleKey, r.role_sort as roleSort, r.data_scope as dataScope,
           r.menu_check as menuCheck, r.dept_check as deptCheck, r.status, r.create_by as createBy, r.create_time as createTime,
            r.update_by as updateBy, r.update_time as updateTime, r.remark, r.role_type as roleType, r.range_scope as rangeScope
            from sys_role r left join sys_account_role t on r.`id` = t.role_id
            where t.user_id = #{userId} and r.status = 0
        </script>
        """
    })
    List<SysRoleCacheDTO> findRoleList(@Param("userId") String userId);

    @Select({
        """
        <script>
            select t.user_id as userId, t.role_id as roleId from sys_account_role t
            where t.user_id in
            <foreach collection="userIdList" open="(" close=")" separator="," item="userId">
                 #{userId}
            </foreach>
        </script>
        """
    })
    List<SysAccountRoleEntity> findRoleIdsByUser(@Param("userIdList") List<String> userIds);
}
