package cn.com.farben.gptcoder.operation.platform.statistics.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 功能使用情况统计参数
 * @author wuanhui
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FunUseStatCommand extends DashboardStatCommand {

    /** 组织机构号 */
    private String organNo;

    /** 组织机构长编码 */
    private String fullOrganNo;

    /** 指定统计方法 */
    private List<String> funList;

}
