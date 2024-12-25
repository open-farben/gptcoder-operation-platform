package cn.com.farben.gptcoder.operation.platform.plugin.version.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import cn.com.farben.gptcoder.operation.commons.user.enums.PluginTypesEnum;
import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.enums.PluginStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 * 插件版本返回前端DTO<br>
 * 创建时间：2023/10/7<br>
 * @author ltg
 *
 */
@Data
public class PluginVersionDTO implements IDTO {
    /** 记录ID */
    private String id;

    /** 版本号 */
    private String version;

    /** 版本描述 */
    private String description;

    /** 支持的IDE版本 */
    private String ideVersion;

    /** 插件类型 */
    private PluginTypesEnum type;

    /** 插件状态 */
    private PluginStatusEnum status;

    /** 文件大小(Byte) */
    private Long fileSize;

    /** 创建者 */
    private String createUser;

    /** 创建时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /*文件hash工具*/
    private String hashToolName;

    /*文件hash值*/
    private String hashValue;
}
