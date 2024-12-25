package cn.com.farben.gptcoder.operation.platform.group.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import lombok.Data;

/**
 *
 * 工作组信息DTO<br>
 *
 */
@Data
public class GroupMemberDTO implements IDTO {
    /** 工作组id */
    private String groupId;

    /** 工作组名 */
    private String groupName;

    /** 是否只读 */
    private boolean readonly;
}
