package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.platform.user.domain.entity.PluginUserEntity;
import cn.com.farben.gptcoder.operation.platform.user.domain.event.PluginUserChangeEvent;
import cn.com.farben.gptcoder.operation.platform.user.dto.PluginUserDTO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums.PluginUserStatusEnum;
import com.mybatisflex.core.paginate.Page;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 *
 * 插件用户仓储接口<br>
 * 创建时间：2023/8/24<br>
 * @author ltg
 *
 */
public interface PluginUserRepository extends ApplicationListener<PluginUserChangeEvent> {
    /**
     * 根据用户账号获取用户信息
     * @param account 用户账号
     * @return 用户信息实体
     */
    PluginUserEntity findByAccount(String account);

    /**
     * 分页查询插件用户
     * @param searchText 用户账号/姓名/或工号的查询
     * @param organization 用户机构
     * @param status 用户状态
     * @param organList 机构列表
     * @param pageSize 每页数据量
     * @param page 当前页
     * @return 插件用户信息
     */
    Page<PluginUserDTO> pageUser(String searchText, String organization, PluginUserStatusEnum status,
                                 List<Integer> organList, long pageSize, long page);

    /**
     * 根据用户账号查询用户数量
     * @param account 用户账号
     * @return 用户数量
     */
    long countUserByAccount(String account);

    /**
     * 新增插件用户
     * @param userEntity 插件用户实体
     */
    void addUser(PluginUserEntity userEntity);

    /**
     * 根据用户id查询用户数量
     * @param id 用户id
     * @return 用户数量
     */
    long countUserById(String id);

    /**
     * 根据id删除插件用户
     * @param id 用户id
     */
    void deleteUser(String id);

    /**
     * 更新插件用户的最后使用时间和插件版本
     * @param dataList 数据列表
     */
    void updateLastUsedTime(@NonNull List<PluginUserEntity> dataList);

    /**
     * 查询已启用的插件用户数量
     * @return 用户数量
     */
    long countEnabledUser();

    /**
     * 根据用户id获取用户信息
     * @param id 用户id
     * @return 用户信息实体
     */
    PluginUserEntity findById(String id);

    /**
     * 查询指定账号是否在数据库存在
     * @param accountList 指定账号列表
     * @return 已存在的账号列表
     */
    List<String> findExistAccount(List<String> accountList);

    /**
     * 批量新增插件用户
     * @param userList 用户列表
     * @return 操作结果
     */
    boolean batchAddUser(List<PluginUserEntity> userList);

    /**
     * 修改机构上下级关系时，由于机构长编码的变化，需要联级更新插件用户的机构
     * @param oldOrgan 旧机构
     * @param newOrgan 新机构
     */
    void updateUserOrgan(String oldOrgan, String newOrgan);

    /**
     * 获取用户列表的机构
     * @param userList 用户列表
     * @return 用户所属机构列表
     */
    List<Integer> listOrgans(List<String> userList);

    /**
     * 获取用户列表里的用户数量
     * @param userList 用户列表
     * @return 数量
     */
    long countByIds(List<String> userList);
}
