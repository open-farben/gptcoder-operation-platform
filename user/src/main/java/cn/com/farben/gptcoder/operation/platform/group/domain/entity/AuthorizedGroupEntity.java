package cn.com.farben.gptcoder.operation.platform.group.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import lombok.Data;

@Data
public class AuthorizedGroupEntity implements IEntity {
    /** 机构号 */
    private Integer organNo;

    /** 机构名称 */
    private String organName;

    /** 父机构 */
    private Integer parentOrganNo;

    /** 工作组id */
    private String groupId;

    /** 工作组名 */
    private String groupName;
}
