package cn.com.farben.gptcoder.operation.platform.plugin.version.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import lombok.Data;

/**
 *
 * 插件类型返回前端DTO<br>
 * 创建时间：2023/10/16<br>
 * @author ltg
 *
 */
@Data
public class PluginTypesDTO implements IDTO {
    /** 记录ID */
    private String type;

    /** 版本号 */
    private String describe;
}
