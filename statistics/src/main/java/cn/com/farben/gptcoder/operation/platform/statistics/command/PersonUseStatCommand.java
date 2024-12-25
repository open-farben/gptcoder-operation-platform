package cn.com.farben.gptcoder.operation.platform.statistics.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 人员使用情况统计参数
 * @author wuanhui
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PersonUseStatCommand extends DashboardStatCommand {

    /** 组织机构号 */
    private String organNo;

    /** 组织机构长编码 */
    private String fullOrganNo;

}
