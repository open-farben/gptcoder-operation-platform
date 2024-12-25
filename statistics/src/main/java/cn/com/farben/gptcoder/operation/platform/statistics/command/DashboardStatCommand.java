package cn.com.farben.gptcoder.operation.platform.statistics.command;

import cn.com.farben.gptcoder.operation.commons.user.command.BaseAuthOrganCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 概览页统计相关接口参数定义
 * @author wuanhui
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DashboardStatCommand extends BaseAuthOrganCommand {

    /** 查询统计的维度: day、week、month、year */
    private String type;

    /** 查询开始日期，格式：yyyy-MM-dd */
    private String startDate;

    /** 查询结束日期, 格式：yyyy-MM-dd */
    private String endDate;

    /** 查询开始日期, 转换后的date类型 */
    private Date startDateTime;

    /** 查询结束日期, 转换后的date类型 */
    private Date endDateTime;
}
