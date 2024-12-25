package cn.com.farben.gptcoder.operation.platform.statistics.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import lombok.Data;

/**
 * 统计明细数量
 * @author wuanhui
 */
@Data
public class UseStatDetailDTO implements IDTO {

    /** 日期 */
    private String date;

    /** 统计数量 */
    private Integer value;

    /** 职务信息 */
    private String type;

    /** 职务信息码表key */
    private String duty;
}
