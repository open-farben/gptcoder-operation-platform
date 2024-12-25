package cn.com.farben.gptcoder.operation.platform.user.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums.UserStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 * 平台用户实体<br>
 * 创建时间：2023/8/29<br>
 * @author ltg
 *
 */
@Data
public class UserEntity implements IEntity {
    /** 记录ID */
    private String id;

    /** 用户账号 */
    private String account;

    /** 工号 */
    private String jobNumber;

    /** 姓名 */
    private String name;

    /** 密码 */
    private String password;

    /** 所属组织 */
    private String organization;

    /** 职务 */
    private String duty;

    /** 手机号 */
    private String mobile;

    /** 邮箱 */
    private String email;

    /** 用户状态 */
    private UserStatusEnum status;

    /** 创建者 */
    private String createUser;

    /** 创建时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /** 更新者 */
    private String updateUser;

    /** 更新时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
