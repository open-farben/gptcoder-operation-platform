package cn.com.farben.gptcoder.operation.platform.user.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统角色 - 用户授权范围表
 * @author wuanhui
 */
@Data
public class RoleAccountRangeEntity implements IEntity {
    /** 主键ID */
    private String id;

    /** 角色ID */
    private String roleId;

    /** 用户账号 */
    private String account;

    /** 机构号 */
    private String organNo;

    /** 机构长编码 */
    private String fullOrganNo;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
