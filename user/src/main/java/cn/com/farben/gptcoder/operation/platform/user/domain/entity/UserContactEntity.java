package cn.com.farben.gptcoder.operation.platform.user.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import lombok.Data;

/**
 *
 * 用户扩展信息实体<br>
 * 创建时间：2023/8/16<br>
 * @author ltg
 *
 */
@Data
public class UserContactEntity implements IEntity {
    /** 记录ID */
    private String userId;

    /** 用户姓名 */
    private String userName;

    /** 用户工号 */
    private String jobNumber;

    /** 联系手机号 */
    private String phone;

    /** 性别 */
    private String sex;

    /** 籍贯 */
    private String nativePlace;

    /** 联系地址 */
    private String address;

    /** 注册邮箱 */
    private String email;

    /** 职位ID */
    private String positionId;

    /** 职位名称 */
    private String positionName;

    /** 组织架构ID */
    private String structId;

    /** 组织架构名称 */
    private String structName;

    /** 部门ID */
    private String departmentId;

    /** 部门名称 */
    private String departmentName;

    /** 个人联系邮箱 */
    private String personEmail;
}
