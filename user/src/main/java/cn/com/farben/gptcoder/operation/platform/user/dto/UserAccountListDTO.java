package cn.com.farben.gptcoder.operation.platform.user.dto;

import lombok.Data;

import java.util.Date;


/**
 * 查询运营账号列表返回对象
 * @author wuanhui
 */
@Data
public class UserAccountListDTO {

    /** 记录ID */
    private String id;

    /** 用户账号 */
    private String userId;

    /** 用户姓名 */
    private String userName;

    /** 联系手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 角色ID */
    private String roleId;

    /** 角色名称， 多个以,分割 */
    private String roleName;

    /** 用户工号 */
    private String jobNumber;

    /** 最后登录时间 */
    private Date lastLoginDate;

    /** 最后登录IP */
    private String lastLoginIp;

    /** 状态（0 启用，1 禁用） */
    private Integer inUse;

    /** 逻辑删除标记 0 正常  1 删除 */
    private Integer logicDelete;

    /** 职位ID */
    private String duty;

    /** 职位名称 */
    private String dutyName;

    /** 组织架构ID */
    private String organization;

    /** 组织架构ID */
    private String fullOrganization;

    /** 组织架构名称 */
    private String organizationName;

    /*** 账户类型 0 系统账户 1 用户账户 */
    private Byte accountType;
    private int locked;
}
