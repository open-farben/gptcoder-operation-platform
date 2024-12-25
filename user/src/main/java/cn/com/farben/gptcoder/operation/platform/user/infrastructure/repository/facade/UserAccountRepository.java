package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.platform.user.command.UserAccountListCommand;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.UserAccountEntity;
import cn.com.farben.gptcoder.operation.platform.user.domain.event.UserAccountChangeEvent;
import cn.com.farben.gptcoder.operation.platform.user.dto.UserAccountListDTO;
import com.mybatisflex.core.paginate.Page;
import org.springframework.context.ApplicationListener;

import java.util.List;

/**
 *
 * 用户账号仓储接口<br>
 * 创建时间：2023/8/15<br>
 * @author ltg
 *
 */
public interface UserAccountRepository extends ApplicationListener<UserAccountChangeEvent> {
    /**
     * 根据用户id获取用户信息
     * @param userId 用户id
     * @return 用户实体
     */
    UserAccountEntity findByUserid(String userId);

    /**
     * 根据主键查询
     * @param id 主键
     * @return 用户
     */
    UserAccountEntity findById(String id);

    /**
     * 根据用户id获取用户数量
     * @param userId 用户id
     * @return 用户数量
     */
    long countByUserId(String userId);

    /**
     * 增加用户账号
     * @param userAccountEntity 用户账号
     */
    boolean addAccount(UserAccountEntity userAccountEntity);

    /**
     * 修改用户账号
     * @param userAccountEntity 用户账号
     */
    boolean editAccount(UserAccountEntity userAccountEntity);

    /**
     * 分页查询用户信息
     * @param command 参数
     * @return 查询结果
     */
    Page<UserAccountListDTO> findAccountList(UserAccountListCommand command);

    /**
     * 根据用户账号删除用户信息
     * @param userIdList 账号列表
     * @return 操作结果
     */
    boolean deleteAccount(List<String> userIdList);

    /**
     * 修改机构上下级关系时，由于机构长编码的变化，需要联级更新插件用户的机构
     * @param oldOrgan 旧机构
     * @param newOrgan 新机构
     */
    void updateSysUserOrgan(String oldOrgan, String newOrgan);

    /**
     * 查询指定角色知否存在用户使用
     * @param roleIdList 角色ID列表
     * @return 查询结果
     */
    Integer countUserByRoleId(List<String> roleIdList);
}
