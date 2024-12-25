package cn.com.farben.gptcoder.operation.platform.user.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import lombok.Data;

import java.util.Date;

/**
 * 查询用户登录信息对象bean
 * @author wuanhui
 * @version 1.0
 * @date 2023/8/29 11:11
 */
@Data
public class UserLoginDTO implements IDTO {

    /** 用户账号 */
    private String userId;

    private String userName;

    /** 邮箱 */
    private String email;

    /** 角色ID */
    private String roleId;

    /** 最后登录时间 */
    private Date lastLoginDate;

    /** 最后登录IP */
    private String lastLoginIp;

    /** 状态（0 启用，1 禁用） */
    private Integer inUse;

    /** 逻辑删除标记 0 正常  1 删除 */
    private Integer logicDelete;

    /** 用户工号 */
    private String jobNumber;

    /** 联系手机号 */
    private String phone;

    /** 职位ID */
    private String duty;

    /** 职位名称 */
    private String dutyName;

    /** 组织架构ID */
    private String organization;

    /** 组织架构长编码Code */
    private String fullOrganization;

    /** 组织架构I名称 */
    private String organizationName;

    /** 其他扩展信息 */
    private UserContactDTO contact;
}
