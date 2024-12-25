package cn.com.farben.gptcoder.operation.platform.user.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import lombok.Data;

import java.util.Date;

/**
 *
 * 系统组织机构实体
 * @author wuanhui
 *
 */
@Data
public class OrganMngEntity implements IEntity {

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

    /** 机构所在地址 */
    private String organAddress;

    /** 机构所在邮政编码 */
    private String organPost;

    /** 描述 */
    private String mark;

    /** 修改原因 */
    private String reason;

    /** 默认0：启用 */
    private Byte status;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private Date createDate;

    /** 修改者 */
    private String updateBy;

    /** 修改时间 */
    private Date updateDate;

    /** 逻辑删除标记 0 正常  1 删除 */
    private Byte logicDelete;
}
