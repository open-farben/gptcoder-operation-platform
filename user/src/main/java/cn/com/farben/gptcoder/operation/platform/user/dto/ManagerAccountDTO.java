package cn.com.farben.gptcoder.operation.platform.user.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 查询管理用户详情返回对象
 * @author wuanhui
 * @version 1.0
 */
@Data
public class ManagerAccountDTO implements IDTO {

    /** 记录ID */
    private String id;

    /** 用户账号 */
    private String userId;

    private String userName;

    /** 邮箱 */
    private String email;

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

    /** 角色ID，多个以,分割 */
    private String roleId;

    /** 角色名称， 多个以,分割 */
    private String roleName;

    /** 角色ID列表 */
    private List<String> roleList;

    /*** 账户类型 0 系统账户 1 用户账户 */
    private Byte accountType;
}
