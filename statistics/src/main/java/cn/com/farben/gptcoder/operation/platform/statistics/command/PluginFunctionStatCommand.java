package cn.com.farben.gptcoder.operation.platform.statistics.command;

import cn.com.farben.gptcoder.operation.commons.user.command.BaseAuthOrganCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.enums.OrderEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询插件功能使用统计
 * @author wuanhui
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PluginFunctionStatCommand extends BaseAuthOrganCommand {

    private String startDay;

    private String endDay;

    private String fullOrganNo;

    private String orderStr;

    private OrderEnum orderBy;

    private String searchText;

    private long pageSize;

    private long pageNo;
}
