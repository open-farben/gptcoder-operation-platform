package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po;

import cn.com.farben.commons.ddd.po.IPO;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.util.Date;

/**
 * 用户账号信息
 * @author wuanhui（60832）
 * @version 1.0
 * @title UserAccount
 * @create 2023/7/20 10:01
 */
@Data
@Table("t_account")
public class UserAccountPO implements IPO {
    /** 记录ID */
    @Id(keyType = KeyType.None)
    private String id;

    /** 用户账号 */
    private String userId;

    /** 用户姓名 */
    private String userName;

    /** 用户密码 */
    @Column(isLarge = true)
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

    /*** 账户类型 0 系统账户 1 用户账户 */
    private Byte accountType;
    /*** 账户类型 0 系统账户 1 用户账户 */
    private int locked;

}
