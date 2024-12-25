package cn.com.farben.gptcoder.operation.platform.group.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import lombok.Data;

/**
 *
 * 组员信息DTO<br>
 *
 */
@Data
public class MemberDTO implements IDTO {
    /** 用户id */
    private String userId;

    /** 用户名 */
    private String userName;

    /** 是否只读 */
    private boolean readonly;
}
