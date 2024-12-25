package cn.com.farben.gptcoder.operation.platform.plugin.version.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import cn.com.farben.gptcoder.operation.commons.user.enums.PluginTypesEnum;
import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.enums.PluginStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 * 插件版本实体<br>
 * 创建时间：2023/9/27<br>
 * @author ltg
 *
 */
@Data
public class PluginVersionEntity implements IEntity {
    /** 记录ID */
    private String id;

    /** 版本号 */
    private String version;

    /** 版本描述 */
    private String description;

    /** 插件类型 */
    private PluginTypesEnum type;

    /** 插件状态 */
    private PluginStatusEnum status;

    /** 支持的IDE版本 */
    private String ideVersion;

    /** 文件大小(bit) */
    private Long fileSize;

    /** 文件关联id */
    private String fileId;

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

    /*文件hash工具*/
    private String hashToolName;

    /*文件hash值*/
    private String hashValue;
}
