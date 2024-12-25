package cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.platform.group.domain.entity.UserGroupRelevanceEntity;

import java.util.List;

/**
 *
 * 工作组用户关联仓储接口<br>
 *
 */
public interface UserGroupRelevanceRepository {
    /**
     * 获取工作组的用户
     * @param groupId 工作组id
     * @return 工作组的用户
     */
    List<String> listGroupUsers(String groupId);

    /**
     * 清空工作组的授权
     * @param groupId 工作组id
     */
    void clearGroupPermissions(String groupId);

    /**
     * 绑定用户和工作组
     * @param entityList 工作组用户关联实体
     */
    void bindPermissions(List<UserGroupRelevanceEntity> entityList);
}
