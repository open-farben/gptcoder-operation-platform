package cn.com.farben.gptcoder.operation.platform.plugin.version.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import cn.com.farben.gptcoder.operation.commons.user.enums.PluginTypesEnum;
import lombok.Data;

/**
 *
 * 插件文件解析结果DTO<br>
 * 创建时间：2023/10/18<br>
 * @author ltg
 *
 */
@Data
public class PluginAnalysisDTO implements IDTO {
    /** 版本号 */
    private String version;

    /** 版本描述 */
    private String description;

    /** 插件类型 */
    private PluginTypesEnum type;

    /** redis的key */
    private String analysisKey;
}
