package cn.com.farben.gptcoder.operation.platform.user.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums.PluginUserStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 * 插件用户实体<br>
 * 创建时间：2023/8/24<br>
 * @author ltg
 *
 */
@Data
public class PluginUserEntity implements IEntity {
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

    /** 组织架构长编码Code */
    private String fullOrganization;

    /** 职务 */
    private String duty;

    /** 手机号 */
    private String mobile;

    /** 邮箱 */
    private String email;

    /** 使用的插件 */
    private String plugin;

    /** 插件版本 */
    private String version;

    /** 最近使用时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastUsedTime;

    /** 当前使用模型 */
    private String currentModel;

    /** 插件用户状态 */
    private PluginUserStatusEnum status;

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
