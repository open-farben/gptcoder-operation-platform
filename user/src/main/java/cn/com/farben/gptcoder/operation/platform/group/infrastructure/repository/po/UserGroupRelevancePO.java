package cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.po;

import cn.com.farben.commons.ddd.po.IPO;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 *
 * 工作组用户关联表
 *
 */
@Data
@Table("user_group_relevance")
public class UserGroupRelevancePO implements IPO {
    /** 记录ID */
    @Id(keyType = KeyType.None)
    private String id;

    /** 用户id */
    private String userId;

    /** 工作组id */
    private String groupId;
}
