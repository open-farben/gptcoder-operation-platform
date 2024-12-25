package cn.com.farben.gptcoder.operation.platform.statistics.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 概览页各职务使用插件次数统计参数
 * @author wuanhui
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserDutyCountStatCommand extends DashboardStatCommand {

    /** 组织机构ID：来源码表字典 */
    private String organNo;

    /** 组织机构长编码 */
    private String fullOrganNo;

}
