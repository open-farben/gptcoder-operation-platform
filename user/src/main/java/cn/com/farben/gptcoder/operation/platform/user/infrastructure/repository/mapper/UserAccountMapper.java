package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper;

import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.UserAccountPO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 *
 * 用户账号表mapper<br>
 * 创建时间：2023/8/15<br>
 * @author ltg
 *
 */
public interface UserAccountMapper extends BaseMapper<UserAccountPO> {

    @Delete({
            """
            <script>
            delete t_account, sys_account_role from t_account left join sys_account_role
           on t_account.user_id = sys_account_role.user_id
            <where>
                t_account.account_type = 1 and t_account.user_id in
                <foreach collection="userIdList" open="(" close=")" separator="," item="userId">
                    #{userId}
                </foreach>
            </where>
            </script>
            """
    })
    Integer deleteAccount(List<String> userIdList);

    /**
     * 修改用户长机构编码
     * @param oldOrgan 旧机构
     * @param newOrgan 新机构
     * @return
     */
    @Update({
        """
        <script>
            update t_account set full_organization = #{newOrgan}
            where full_organization = #{oldOrgan}
        </script>
        """
    })
    Integer updateSysUserOrgan(@Param("oldOrgan") String oldOrgan, @Param("newOrgan") String newOrgan);


    /**
     * 查询指定角色知否存在用户使用
     * @param roleIdList 角色ID列表
     * @return 查询结果
     */
    @Select({
        """
        <script>
            select count(*) from t_account
            where role_id in
            <foreach collection="roleList" open="(" close=")" separator="," item="roleId">
                #{roleId}
            </foreach>
        </script>
        """
    })
    Integer countUserByRoleId(@Param("roleList") List<String> roleIdList);
}
