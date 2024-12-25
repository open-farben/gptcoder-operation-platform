package cn.com.farben.gptcoder.operation.platform.user.command.organ;

import lombok.Data;

/**
 * 修改机构命令
 * @author wuanhui
 */
@Data
public class EditOrganCommand {

    /** 机构ID */
    private String id;

    /** 机构名称 */
    private String organName;

    /** 机构简称 */
    private String shortName;

    /** 所属父级机构号 */
    private Integer parentOrganNo;

    /** 机构所在地址 */
    private String organAddress;

    /** 机构所在邮政编码 */
    private String organPost;

    /** 描述 */
    private String mark;

    /** 修改原因 */
    private String reason;

}
