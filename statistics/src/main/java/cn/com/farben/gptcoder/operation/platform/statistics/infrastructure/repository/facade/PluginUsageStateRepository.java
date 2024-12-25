package cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.platform.statistics.command.DashboardStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.PersonUseStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.PluginUsageStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.domain.entity.PluginUsageStatisticsEntity;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.DashboardStatDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.PluginUsageStatisticsDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.UseStatDetailDTO;
import com.mybatisflex.core.paginate.Page;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 *
 * 插件使用统计仓储接口
 * 创建时间：2023/10/27
 * @author wuanhui
 *
 */
public interface PluginUsageStateRepository {

    /**
     * 使用人数统计图表
     * @param query 参数
     * @return 统计结果
     */
    List<UseStatDetailDTO> personUseStat(PersonUseStatCommand query);

    /**
     * 根据账号和日期查询用户某一天的统计数据
     * @param account 账号
     * @param day 日期
     * @return 用户这一天的统计数据
     */
    PluginUsageStatisticsEntity getUsageStatisticsByDay(String account, String day);

    /**
     * 批量增加插件使用统计信息
     * @param statisticsEntityList 插件使用统计实体列表
     */
    void addPluginUsageStatistics(@NonNull List<PluginUsageStatisticsEntity> statisticsEntityList);

    /**
     * 批量更新插件使用统计信息
     * @param statisticsEntityList 插件使用统计实体列表
     */
    void refreshPluginUsageStatistics(@NonNull List<PluginUsageStatisticsEntity> statisticsEntityList);

    /**
     * 概览首页统计汇总
     * @param command 参数
     * @return 统计结果
     */
    DashboardStatDTO dashboardSummary(DashboardStatCommand command);

    /**
     * 分页查询插件使用记录
     * @param command 参数
     * @return 插件用户使用情况统计结果DTO,按照最近使用时间倒序排序
     */
    Page<PluginUsageStatisticsDTO> pagePluginUsageStatistics(PluginUsageStatCommand command);

    /**
     * 查询插件使用记录
     * @param command 参数
     * @return 插件用户使用情况统计结果DTO,按照最近使用时间倒序排序
     */
    List<PluginUsageStatisticsDTO> listPluginUsageStatistics(PluginUsageStatCommand command);
}
