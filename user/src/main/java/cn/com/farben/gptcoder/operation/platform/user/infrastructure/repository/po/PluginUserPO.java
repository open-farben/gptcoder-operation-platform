package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po;

import cn.com.farben.commons.ddd.po.IPO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums.PluginUserStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 插件用户表
 */
@Data
@Table("plugin_user")
public class PluginUserPO implements IPO {
    /** 记录ID */
    @Id(keyType = KeyType.None)
    private String id;

    /** 用户账号 */
    private String account;

    /** 工号 */
    private String jobNumber;

    /** 姓名 */
    private String name;

    /** 密码 */
    @Column(isLarge = true)
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
    private int locked;

    /** 更新时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
