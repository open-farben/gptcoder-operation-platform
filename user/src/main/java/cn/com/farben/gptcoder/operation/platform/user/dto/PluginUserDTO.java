package cn.com.farben.gptcoder.operation.platform.user.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums.PluginUserStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 * 插件用户返回前端DTO<br>
 * 创建时间：2023/8/24<br>
 * @author ltg
 *
 */
@Data
public class PluginUserDTO implements IDTO {
    /** 记录ID */
    private String id;

    /** 用户账号 */
    private String account;

    /** 工号 */
    private String jobNumber;

    /** 姓名 */
    private String name;

    /** 所属组织 */
    private String organization;

    /** 所属组织名称 */
    private String organizationName;

    /** 所属机构长编码 */
    private String fullOrganization;

    /** 职务 */
    private String duty;

    /** 职务名称 */
    private String dutyName;

    /** 手机号 */
    private String mobile;

    /** 邮箱 */
    private String email;

    /** 使用的插件 */
    private String plugin;

    /** 插件版本 */
    private String version;
    private int locked;

    /** 最近使用时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastUsedTime;

    /** 插件用户状态 */
    private PluginUserStatusEnum status;
}
