package cn.com.farben.gptcoder.operation.platform.user.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import lombok.Data;

import java.util.Date;

/**
 *
 * 用户账号实体<br>
 * 创建时间：2023/8/15<br>
 * @author ltg
 *
 */
@Data
public class UserAccountEntity implements IEntity {
    /** 记录ID */
    private String id;

    /** 用户账号 */
    private String userId;

    /** 用户姓名 */
    private String userName;

    /** 用户密码 */
    private String pwd;

    /** 邮箱 */
    private String email;

    /** 角色ID */
    private String roleId;

    /** 最后登录时间 */
    private Date lastLoginDate;

    /** 最后登录IP*/
    private String lastLoginIp;

    /** 状态（0 启用，1 禁用） */
    private Integer inUse;

    /** 用户工号 */
    private String jobNumber;

    /** 联系手机号 */
    private String phone;

    /** 职位ID */
    private String duty;

    /** 组织架构ID */
    private String organization;

    /** 组织架构长编码Code */
    private String fullOrganization;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private Date createDate;

    /** 修改者 */
    private String updateBy;

    /** 修改时间 */
    private Date updateDate;

    /** 逻辑删除标记 0 正常  1 删除 */
    private Integer logicDelete;

    /*** 账户类型 0 系统账户 1 用户账户 */
    private Byte accountType;
}
