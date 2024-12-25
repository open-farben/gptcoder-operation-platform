package cn.com.farben.gptcoder.operation.platform.group.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import lombok.Data;

import java.util.List;

/**
 *
 * 机构信息DTO<br>
 *
 */
@Data
public class OrganDTO implements IDTO {
    /** 机构号 */
    private Integer organNo;

    /** 机构名称 */
    private String organName;

    /** 父机构 */
    private Integer parentOrganNo;

    /** 用户信息 */
    private List<MemberDTO> users;
}
