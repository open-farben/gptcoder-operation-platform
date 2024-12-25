package cn.com.farben.gptcoder.operation.platform.plugin.version.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 * 插件下载子项DTO<br>
 * 创建时间：2023/10/17<br>
 * @author ltg
 *
 */
@Data
public class PluginDownloadItemDTO implements IDTO {
    /** 记录ID */
    private String id;

    /** 版本号 */
    private String version;

    /** 版本描述 */
    private String description;

    /** 支持的IDE版本 */
    private String ideVersion;

    /** 文件大小(Byte) */
    private Long fileSize;

    /** 创建时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDateTime createTime;

    /*文件hash工具*/
    private String hashToolName;

    /*文件hash值*/
    private String hashValue;
}
