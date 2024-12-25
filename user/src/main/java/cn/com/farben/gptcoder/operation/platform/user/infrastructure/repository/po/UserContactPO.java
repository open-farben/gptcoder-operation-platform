package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po;

import cn.com.farben.commons.ddd.po.IPO;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * 系统用户扩展信息
 * @author wuanhui（60832）
 * @version 1.0
 * @title UserAccount
 * @create 2023/7/20 10:01
 */
@Data
@Table("t_account_contact")
public class UserContactPO implements IPO {
    /** 记录ID */
    @Id(keyType = KeyType.None)
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
