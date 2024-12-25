package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper;

import cn.com.farben.gptcoder.operation.platform.user.domain.entity.SysRoleRangeEntity;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysRoleRangeDTO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.SysRoleRangePO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 *
 * 系统角色授权表mapper
 * @author wuanhui
 *
 */
public interface SysRoleRangeMapper extends BaseMapper<SysRoleRangePO> {

    /**
     * 添加自定义角色范围表
     *
     * @return 角色列表
     */
    @Insert({
        """
        <script>
            insert into sys_role_range(`id`, role_id, organ_no, full_organ_no, create_by)
            values
            <foreach collection="rangeList" separator="," item="range">
                (#{range.id}, #{range.roleId}, #{range.organNo}, #{range.fullOrganNo}, #{range.createBy})
            </foreach>
        </script>
        """
    })
    Integer addRoleRange(@Param("rangeList") List<SysRoleRangeEntity> list);

    /**
     * 根据角色ID查询该角色关联的机构范围
     * @param roleId 角色ID
     * @return 角色列表
     */
    @Select({
        """
        <script>
            select role_id as roleId, organ_no as organNo, full_organ_no as fullOrganNo, create_by as createBy, create_time as createTime
            from sys_role_range
            where role_id = #{roleId}
        </script>
        """
    })
    List<SysRoleRangeDTO> findRangeListByRole(@Param("roleId") String roleId);

    /**
     * 根据角色ID查询该角色关联的机构号
     * @param roleId 角色ID
     * @return 机构范围
     */
    @Select({
        """
        <script>
            select organ_no from sys_role_range
            where role_id = #{roleId}
        </script>
        """
    })
    List<Integer> findRangeOrganNoListByRole(@Param("roleId") String roleId);

}
