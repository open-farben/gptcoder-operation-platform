package cn.com.farben.gptcoder.operation.platform.user.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 * 插件用户登陆返回前端DTO<br>
 * 创建时间：2023/11/21<br>
 * @author ltg
 *
 */
@Data
public class PluginUserLoginDTO implements IDTO {
    /** 标识码，a80f740c-0779-4294-a700-2020f82769d4 */
    private String uuid;

    /** 机器id，7208f1a75ce6149a83fc4e752e32e569c87fc6309a455ef43945ec6bebbab7a5 */
    private String machineId;

    /** 设备，vscode_desktop */
    private String device;

    /** ide名称 */
    private String ideName;

    /** ide版本 */
    private String ideVersion;

    /** 插件类型 */
    private String pluginType;

    /** 插件版本 */
    private String pluginVersion;

    /** 登陆时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime loginTime;
}
