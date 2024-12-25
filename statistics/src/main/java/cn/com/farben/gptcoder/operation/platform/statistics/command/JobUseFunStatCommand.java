package cn.com.farben.gptcoder.operation.platform.statistics.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 各职务使用插件功能使用情况统计参数
 * @author wuanhui
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class JobUseFunStatCommand extends DashboardStatCommand {

    /** 组织机构ID：来源码表字典 */
    private String organNo;

    /** 组织机构长编码 */
    private String fullOrganNo;

    /** 职务ID */
    private String duty;

}
