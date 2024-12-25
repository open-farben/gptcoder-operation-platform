package cn.com.farben.gptcoder.operation.platform.statistics.command;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 平均响应速率设置参数
 * @author wuanhui
 */
@Data
public class SpeedConfigCommand {

    /** 平均速率 */
    private BigDecimal baseSpeed;
}
