package cn.com.farben.gptcoder.operation.platform.statistics.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import lombok.Data;

/**
 * 概览页各职务使用插件功能次数统计
 * @author wuanhui
 */
@Data
public class UseFunctionDTO  implements IDTO {

    private String duty;

    private String type;

    private Integer value;
}
