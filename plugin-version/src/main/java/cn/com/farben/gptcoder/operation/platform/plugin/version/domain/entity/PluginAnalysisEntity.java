package cn.com.farben.gptcoder.operation.platform.plugin.version.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import lombok.Data;

/**
 *
 * 插件文件分析结果实体<br>
 *
 */
@Data
public class PluginAnalysisEntity implements IEntity {
    /** 文件名 */
    private String fileName;

    /** 插件类型 */
    private String pluginType;

    /** 版本号 */
    private String version;

    /*文件hash工具*/
    private String hashToolName;

    /*文件hash值*/
    private String hashValue;
}
