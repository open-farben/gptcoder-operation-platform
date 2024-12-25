package cn.com.farben.gptcoder.operation.platform.plugin.version.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import lombok.Data;

import java.util.List;

/**
 *
 * 插件下载DTO<br>
 * 创建时间：2023/10/17<br>
 * @author ltg
 *
 */
@Data
public class PluginDownloadDTO implements IDTO {
    /** 插件类型 */
    private String type;

    /** 版本列表 */
    private List<PluginDownloadItemDTO> itemList;
}
