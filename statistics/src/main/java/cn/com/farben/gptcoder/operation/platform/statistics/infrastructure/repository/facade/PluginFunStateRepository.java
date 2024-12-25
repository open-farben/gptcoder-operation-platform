package cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.commons.model.enums.ModelAbilityEnum;
import cn.com.farben.gptcoder.operation.platform.statistics.command.DashboardStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.FunUseStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.JobUseFunStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.PluginFunctionStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.UserDutyCountStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.domain.entity.PluginFunStatisticsEntity;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.DashboardStatDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.FunStatDataDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.PluginFunctionStatisticsDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.UseFunctionDTO;
import com.mybatisflex.core.paginate.Page;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 *
 * 插件功能统计仓储接口
 * 创建时间：2023/10/27
 * @author wuanhui
 *
 */
public interface PluginFunStateRepository {

    /**
     * 概览页功能使用情况统计
     * @param query 查询参数
     * @return 统计结果
     */
    List<FunStatDataDTO> funUseStat(FunUseStatCommand query);

    /**
     * 概览页各职务使用插件次数统计
     * @param query 参数
     * @return 统计结果
     */
     List<UseFunctionDTO> userDutyCountStat(UserDutyCountStatCommand query);

    /**
     * 概览页个职务使用插件功能次数统计
     * @param query 参数
     * @return 统计结果
     */
    List<FunStatDataDTO> userJobFunctionStat(JobUseFunStatCommand query);

    /**
     * 根据账号、模型能力和日期查询用户某一天的统计数据
     * @param account 账号
     * @param modelAbilityEnum 模型能力
     * @param day 日期
     * @return 用户这一天的统计数据
     */
    PluginFunStatisticsEntity getFunStatisticsByDay(String account, ModelAbilityEnum modelAbilityEnum, String day);

    /**
     * 批量增加插件能力统计信息
     * @param statisticsEntityList 插件能力统计实体列表
     */
    void addPluginFunStatistics(@NonNull List<PluginFunStatisticsEntity> statisticsEntityList);

    /**
     * 批量更新插件能力统计信息
     * @param statisticsEntityList 插件能力统计实体列表
     */
    void refreshPluginFunStatistics(@NonNull List<PluginFunStatisticsEntity> statisticsEntityList);

    /**
     * 概览首页统计代码行信息
     * @param query 参数
     * @param modelAbility 模型能力
     * @return 统计结果
     */
    DashboardStatDTO dashboardSummary(DashboardStatCommand query, String modelAbility);

    /**
     * 分页查询用户使用功能情况
     * @param command 参数
     * @return 插件用户使用情况统计结果DTO,按照最近使用时间倒序排序
     */
    Page<PluginFunctionStatisticsDTO> pagePluginFunctionStatistics(PluginFunctionStatCommand command);

    /**
     * 查询用户使用功能情况
     * @param command 参数
     * @return 插件用户使用情况统计结果DTO,按照最近使用时间倒序排序
     */
    List<PluginFunctionStatisticsDTO> listPluginFunctionStatistics(PluginFunctionStatCommand command);
}
