package cn.com.farben.gptcoder.operation.platform.user.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * 用户扩展联系信息
 * @author Administrator
 * @version 1.0
 * @title UserAccountRsp
 * @create 2023/7/24 11:14
 */
@Data
public class UserContactDTO implements IDTO {
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

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate createDate;

    /** 修改者 */
    private String updateBy;

    /** 修改时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate updateDate;

    /** 逻辑删除标记 0 正常  1 删除 */
    private Integer logicDelete;
}
