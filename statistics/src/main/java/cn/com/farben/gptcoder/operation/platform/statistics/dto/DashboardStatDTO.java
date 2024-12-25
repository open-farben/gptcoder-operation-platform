package cn.com.farben.gptcoder.operation.platform.statistics.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import lombok.Data;

import java.math.BigDecimal;


/**
 * 概览页统计指标相关统计返回对象
 * @author wuanhui
 */
@Data
public class DashboardStatDTO implements IDTO {

    /** 活跃人数 */
    private int userNum;

    /** 总调用次数 */
    private int totalNum;

    /** 生成代码行数 */
    private int genNum;

    /** 确认代码行数 */
    private int confirmNum;

    /** 平均响应时长 */
    private double avgTime;

    /** 代码补齐率 */
    private BigDecimal completionRate;

    /** 基线速率： 用户设置 */
    private BigDecimal baseSpeed;

    /** 总响应时长 */
    private BigDecimal totalResponse;
}
