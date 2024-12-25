package cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade.impl;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.gptcoder.operation.platform.statistics.command.DashboardStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.PersonUseStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.PluginUsageStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.domain.entity.PluginUsageStatisticsEntity;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.DashboardStatDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.PluginUsageStatisticsDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.UseStatDetailDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.enums.OrderEnum;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade.PluginUsageStateRepository;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.mapper.PluginUsageStatisticsMapper;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.po.PluginUsageStatisticsPO;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.po.table.DictCodeTableDef.DICT_CODE;
import static cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.po.table.PluginFunctionStatisticsTableDef.PLUGIN_FUNCTION_STATISTICS;
import static cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.po.table.PluginUsageStatisticsTableDef.PLUGIN_USAGE_STATISTICS;
import static cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.table.OrganMngTableDef.ORGAN_MNG;
import static cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.table.PluginUserTableDef.PLUGIN_USER;

/**
 *
 * 插件使用统计仓储接口实现
 * 创建时间：2023/10/27
 * @author wuanhui
 *
 */
@Repository
public class PluginUsageStateRepositoryImpl implements PluginUsageStateRepository {
    /** 插件使用统计DB服务 */
    private final PluginUsageStatisticsMapper pluginUsageStatisticsMapper;
    private static final Log logger = LogFactory.get();

    public PluginUsageStateRepositoryImpl(PluginUsageStatisticsMapper pluginUsageStatisticsMapper) {
        this.pluginUsageStatisticsMapper = pluginUsageStatisticsMapper;
    }

    /**
     * 使用人数统计图表
     * @param query 参数
     * @return 统计结果
     */
    @Override
    public List<UseStatDetailDTO> personUseStat(PersonUseStatCommand query) {
        return pluginUsageStatisticsMapper.personUseStat(query);
    }

    @Override
    public PluginUsageStatisticsEntity getUsageStatisticsByDay(String account, String day) {
        logger.info("根据账号和日期查询用户某一天的统计数据，account: [{}], day: [{}]", account, day);
        PluginUsageStatisticsEntity pluginUsageStatisticsEntity = new PluginUsageStatisticsEntity();
        CommonAssemblerUtil.assemblerPOToEntity(
                QueryChain.of(pluginUsageStatisticsMapper)
                        .select(PLUGIN_USAGE_STATISTICS.ALL_COLUMNS)
                        .where(PLUGIN_USAGE_STATISTICS.ACCOUNT.eq(account).and(PLUGIN_USAGE_STATISTICS.DAY.eq(day)))
                        .one(),
                pluginUsageStatisticsEntity
        );

        return pluginUsageStatisticsEntity;
    }

    @Override
    public void addPluginUsageStatistics(@NonNull List<PluginUsageStatisticsEntity> statisticsEntityList) {
        pluginUsageStatisticsMapper.addPluginUsageStatistics(statisticsEntityList);
    }

    @Override
    public void refreshPluginUsageStatistics(@NonNull List<PluginUsageStatisticsEntity> statisticsEntityList) {
        pluginUsageStatisticsMapper.refreshPluginUsageStatistics(statisticsEntityList);
    }

    /**
     * 概览首页统计汇总
     *
     * @param command 参数
     * @return 统计结果
     */
    @Override
    public DashboardStatDTO dashboardSummary(DashboardStatCommand command) {
        if(CharSequenceUtil.isNotBlank(command.getIsAll())) {
            return pluginUsageStatisticsMapper.dashboardSummary(command.getStartDate(), command.getEndDate());
        }
        return pluginUsageStatisticsMapper.dashboardSummaryByOrgan(command);
    }

    @Override
    public Page<PluginUsageStatisticsDTO> pagePluginUsageStatistics(PluginUsageStatCommand command) {
        String fullOrganNo = command.getFullOrganNo();
        String searchText = command.getSearchText();
        String isOnly = command.getIsOnly();
        String userId = command.getUserId();
        List<Integer> organNoList = command.getOrganNoList();
        String orderStr = command.getOrderStr();
        OrderEnum orderBy = command.getOrderBy();
        QueryChain<PluginUsageStatisticsPO> queryChain = QueryChain.of(pluginUsageStatisticsMapper);
        queryChain.select("p.account", "p.job_number as jobNumber", "p.name", "p.organization",
                "o.organ_name as organizationName", "p.duty", "dc.kind_value as dutyName",
                "CAST((SUM(case when fs.model_ability='code_hinting' then us.active_time else 0 end) / 60) AS DECIMAL(10,2)) AS activeTime",
                "IFNULL(CAST((SUM(case when fs.model_ability='code_hinting' then us.total_completion else 0 end) / SUM(case when fs.model_ability='code_hinting' then fs.accept_count else 0 end)) AS DECIMAL(10,4)), 0) AS avgCompletionRate",
                "sum(fs.generate_lines) as generationLines", "sum(fs.accept_lines) as acceptLines",
                "CAST((SUM(case when fs.model_ability='code_hinting' then us.total_response else 0 end) / SUM(case when fs.model_ability='code_hinting' then us.total_call_number ELSE 0 end)) AS DECIMAL(10,2)) AS avgResponseRate",
                "p.current_model as currentModel", "p.plugin as currentPlugin", "p.version as currentPluginVersion",
                "p.last_used_time as lastUsedTime");
        queryChain.from(PLUGIN_USAGE_STATISTICS).as("us");
        queryChain.join(PLUGIN_USER).as("p").on(PLUGIN_USER.ACCOUNT.eq(PLUGIN_USAGE_STATISTICS.ACCOUNT)
                .and(PLUGIN_USER.FULL_ORGANIZATION.like(fullOrganNo).when(CharSequenceUtil.isNotBlank(fullOrganNo))));
        queryChain.leftJoin(ORGAN_MNG).as("o").on(PLUGIN_USER.ORGANIZATION.eq(ORGAN_MNG.ORGAN_NO));
        queryChain.leftJoin(DICT_CODE).as("dc").on(DICT_CODE.KIND_CODE.eq(PLUGIN_USER.DUTY)
                .and(DICT_CODE.DICT_CODE_.eq("JOB_INFO")));
        queryChain.leftJoin(PLUGIN_FUNCTION_STATISTICS).as("fs")
                .on(PLUGIN_FUNCTION_STATISTICS.ACCOUNT.eq(PLUGIN_USAGE_STATISTICS.ACCOUNT)
                        .and(PLUGIN_FUNCTION_STATISTICS.DAY.eq(PLUGIN_USAGE_STATISTICS.DAY)));
        queryChain.where(PLUGIN_USAGE_STATISTICS.DAY.between(command.getStartDay(), command.getEndDay()));
        queryChain.where(PLUGIN_USER.ACCOUNT.like(searchText)
                .or(PLUGIN_USER.NAME.like(searchText)).or(PLUGIN_USER.JOB_NUMBER.like(searchText))
                .when(CharSequenceUtil.isNotBlank(searchText)));
        queryChain.where(PLUGIN_USER.ACCOUNT.eq(userId).when(CharSequenceUtil.isNotBlank(isOnly)));
        queryChain.where(PLUGIN_USER.ORGANIZATION.in(organNoList).when(CollUtil.isNotEmpty(organNoList)));
        queryChain.groupBy(PLUGIN_USER.ACCOUNT);
        if (CharSequenceUtil.isNotBlank(orderStr)) {
            queryChain.orderBy(orderStr, orderBy == OrderEnum.ASC);
        } else {
            queryChain.orderBy("lastUsedTime desc");
        }

        return queryChain.pageAs(Page.of(command.getPageNo(), command.getPageSize()), PluginUsageStatisticsDTO.class);
    }

    @Override
    public List<PluginUsageStatisticsDTO> listPluginUsageStatistics(PluginUsageStatCommand command) {
        return pluginUsageStatisticsMapper.listPluginUsageStatistics(command);
    }
}
