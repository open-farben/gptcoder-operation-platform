package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper;

import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.PluginUserPO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 *
 * 插件用户表mapper<br>
 * 创建时间：2023/8/24<br>
 * @author ltg
 *
 */
public interface PluginUserMapper extends BaseMapper<PluginUserPO> {
    /**
     * 更新插件用户的最后使用时间、插件类型、插件版本、模型
     * @param dataList 数据列表
     */
    @Update("""
            <script>
                <foreach collection="dataList" item="item" separator=";">
                    update plugin_user
                    <set>
                        version = #{item.version}, last_used_time = #{item.lastUsedTime},
                        plugin = #{item.plugin}, current_model = #{item.currentModel}
                    </set>
                    where account = #{item.account} and (last_used_time is null or last_used_time &lt; #{item.lastUsedTime})
                </foreach>
            </script>
            """)
    void updateLastUsedTime(@Param("dataList") List<PluginUserPO> dataList);

    /**
     * 查询指定账号是否在数据库存在
     * @param accountList 指定账号列表
     * @return 已存在的账号列表
     */
    @Select({
        """
        <script>
            select account from plugin_user
            <where>
                account in
                <foreach collection="accountList" open="(" close=")" separator="," item="account">
                    #{account}
                </foreach>
            </where>
        </script>
        """
    })
    List<String> findExistAccount(@Param("accountList") List<String> accountList);


    /**
     * 批量新增插件用户
     * @param userList 用户列表
     * @return 影响行数
     */
    @Insert({
        """
        <script>
            insert into plugin_user (`id`, account, job_number, name, password, organization, duty, mobile,
            email, status, full_organization, create_user, update_user)
            values
            <foreach collection="userList" separator="," item="adopt">
                (#{adopt.id}, #{adopt.account}, #{adopt.jobNumber}, #{adopt.name}, #{adopt.password}, #{adopt.organization}, #{adopt.duty}, #{adopt.mobile},
                 #{adopt.email}, #{adopt.status}, #{adopt.fullOrganization}, #{adopt.createUser}, #{adopt.updateUser})
            </foreach>
        </script>
        """
    })
    Integer batchAddUser(@Param("userList") List<PluginUserPO> userList);


    /**
     * 修改用户长机构编码
     * @param oldOrgan 旧机构
     * @param newOrgan 新机构
     * @return 影响行数
     */
    @Update({
        """
        <script>
            update plugin_user set full_organization = #{newOrgan}
            where full_organization = #{oldOrgan}
        </script>
        """
    })
    Integer updateUserOrgan(@Param("oldOrgan") String oldOrgan, @Param("newOrgan") String newOrgan);
}
