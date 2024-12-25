package cn.com.farben.gptcoder.operation.platform.group.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import lombok.Data;

/**
 *
 * 工作组用户关联实体
 *
 */
@Data
public class UserGroupRelevanceEntity implements IEntity {
    /** 记录ID */
    private String id;

    /** 用户id */
    private String userId;

    /** 工作组id */
    private String groupId;
}
