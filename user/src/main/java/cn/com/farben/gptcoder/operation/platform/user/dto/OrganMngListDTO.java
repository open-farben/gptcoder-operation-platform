package cn.com.farben.gptcoder.operation.platform.user.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import lombok.Data;

/**
 *
 * 系统组织机构实体
 * @author wuanhui
 *
 */
@Data
public class OrganMngListDTO implements IDTO {

    /** 记录ID */
    private String id;

    /** 机构号 */
    private Integer organNo;

    /** 机构号全称 */
    private String fullOrganNo;

    /** 机构等级：0 顶级机构 */
    private Integer organLevel;

    /** 机构名称 */
    private String organName;

    /** 机构简称 */
    private String shortName;

    /** 所属父级机构号 */
    private Integer parentOrganNo;

    /** 上级机构名称 */
    private String parentOrganName;

    /** 机构所在地址 */
    private String organAddress;

    /** 机构所在邮政编码 */
    private String organPost;


}
