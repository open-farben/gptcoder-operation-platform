package cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade.impl;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
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
import cn.com.farben.gptcoder.operation.platform.statistics.dto.PluginUsageStatisticsDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.UseFunctionDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.enums.OrderEnum;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade.PluginFunStateRepository;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.mapper.PluginFunStatisticsMapper;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.po.PluginFunctionStatisticsPO;
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
 * 插件功能统计仓储接口实现
 * 创建时间：2023/10/27
 * @author wuanhui
 *
 */
@Repository
public class PluginFunStateRepositoryImpl implements PluginFunStateRepository {
    /** 插件使用统计DB服务 */
    private final PluginFunStatisticsMapper pluginFunStatisticsMapper;
    private static final Log logger = LogFactory.get();

    public PluginFunStateRepositoryImpl(PluginFunStatisticsMapper pluginFunStatisticsMapper) {
        this.pluginFunStatisticsMapper = pluginFunStatisticsMapper;
    }

    /**
     * 概览页功能使用情况统计
     *
     * @param query 查询参数
     * @return 统计结果
     */
    @Override
    public List<FunStatDataDTO> funUseStat(FunUseStatCommand query) {
        return pluginFunStatisticsMapper.funUseStat(query);
    }

    /**
     * 概览页各职务使用插件次数统计
     *
     * @param query 参数
     * @return 统计结果
     */
    @Override
    public List<UseFunctionDTO> userDutyCountStat(UserDutyCountStatCommand query) {
        return pluginFunStatisticsMapper.userDutyCountStat(query);
    }

    /**
     * 概览页个职务使用插件功能次数统计
     *
     * @param query 参数
     * @return 统计结果
     */
    @Override
    public List<FunStatDataDTO> userJobFunctionStat(JobUseFunStatCommand query) {
        return pluginFunStatisticsMapper.userJobFunctionStat(query);
    }

    @Override
    public PluginFunStatisticsEntity getFunStatisticsByDay(String account, ModelAbilityEnum modelAbilityEnum, String day) {
        logger.info("根据账号、模型能力和日期查询用户某一天的统计数据，account: [{}], modelAbilityEnum: [{}], day: [{}]", account, modelAbilityEnum, day);
        PluginFunStatisticsEntity pluginFunStatisticsEntity = new PluginFunStatisticsEntity();
        CommonAssemblerUtil.assemblerPOToEntity(
                QueryChain.of(pluginFunStatisticsMapper)
                        .select(PLUGIN_FUNCTION_STATISTICS.ALL_COLUMNS)
                        .where(PLUGIN_FUNCTION_STATISTICS.ACCOUNT.eq(account)
                                .and(PLUGIN_FUNCTION_STATISTICS.MODEL_ABILITY.eq(modelAbilityEnum))
                                .and(PLUGIN_FUNCTION_STATISTICS.DAY.eq(day))).one(),
                pluginFunStatisticsEntity
        );

        return pluginFunStatisticsEntity;
    }

    @Override
    public void addPluginFunStatistics(@NonNull List<PluginFunStatisticsEntity> statisticsEntityList) {
        pluginFunStatisticsMapper.addPluginFunStatistics(statisticsEntityList);
    }

    @Override
    public void refreshPluginFunStatistics(@NonNull List<PluginFunStatisticsEntity> statisticsEntityList) {
        pluginFunStatisticsMapper.refreshPluginFunStatistics(statisticsEntityList);
    }

    /**
     * 概览首页统计代码行信息
     *
     * @param query 参数
     * @param modelAbility 模型能力
     * @return 统计结果
     */
    @Override
    public DashboardStatDTO dashboardSummary(DashboardStatCommand query, String modelAbility) {
        if(CharSequenceUtil.isNotBlank(query.getIsAll())) {
            return pluginFunStatisticsMapper.dashboardSummary(query.getStartDate(), query.getEndDate(), modelAbility);
        }
        return pluginFunStatisticsMapper.dashboardSummaryOrgan(query, modelAbility);
    }

    @Override
    public Page<PluginFunctionStatisticsDTO> pagePluginFunctionStatistics(PluginFunctionStatCommand command) {
        String fullOrganNo = command.getFullOrganNo();
        String searchText = command.getSearchText();
        String isOnly = command.getIsOnly();
        String userId = command.getUserId();
        List<Integer> organNoList = command.getOrganNoList();
        String orderStr = command.getOrderStr();
        OrderEnum orderBy = command.getOrderBy();
        QueryChain<PluginFunctionStatisticsPO> queryChain = QueryChain.of(pluginFunStatisticsMapper);
        queryChain.select("p.account", "p.job_number as jobNumber", "p.name", "p.organization", "o.organ_name as organizationName",
                "p.duty", "dc.kind_value as dutyName", "sum(fs.use_count) as totalUseCount",
                "sum(case when fs.model_ability='code_hinting' then fs.use_count else 0 end) as hintingCount",
                "sum(case when fs.model_ability='code_explain' then fs.use_count else 0 end) as explainCount",
                "sum(case when fs.model_ability='code_correction' then fs.use_count else 0 end) as correctionCount",
                "sum(case when fs.model_ability='code_comment' then fs.use_count else 0 end) as commentCount",
                "sum(case when fs.model_ability='code_conversion' then fs.use_count else 0 end) as conversionCount",
                "sum(case when fs.model_ability='ai_answer' then fs.use_count else 0 end) as questionCount",
                "sum(case when fs.model_ability='unit_test' then fs.use_count else 0 end) as testCount",
                "sum(case when fs.model_ability='knowledge_qa' then fs.use_count else 0 end) as knowledgeQaCount",
                "sum(case when fs.model_ability='code_search' then fs.use_count else 0 end) as codeSearchCount");
        queryChain.from(PLUGIN_FUNCTION_STATISTICS).as("fs");
        queryChain.join(PLUGIN_USER).as("p").on(PLUGIN_USER.ACCOUNT.eq(PLUGIN_FUNCTION_STATISTICS.ACCOUNT)
                .and(PLUGIN_USER.FULL_ORGANIZATION.like(fullOrganNo).when(CharSequenceUtil.isNotBlank(fullOrganNo))));
        queryChain.leftJoin(ORGAN_MNG).as("o").on(PLUGIN_USER.ORGANIZATION.eq(ORGAN_MNG.ORGAN_NO));
        queryChain.leftJoin(DICT_CODE).as("dc").on(DICT_CODE.KIND_CODE.eq(PLUGIN_USER.DUTY)
                .and(DICT_CODE.DICT_CODE_.eq("JOB_INFO")));
        queryChain.where(PLUGIN_FUNCTION_STATISTICS.DAY.between(command.getStartDay(), command.getEndDay()));
        queryChain.where(PLUGIN_USER.ACCOUNT.like(searchText)
                .or(PLUGIN_USER.NAME.like(searchText)).or(PLUGIN_USER.JOB_NUMBER.like(searchText))
                .when(CharSequenceUtil.isNotBlank(searchText)));
        queryChain.where(PLUGIN_USER.ACCOUNT.eq(userId).when(CharSequenceUtil.isNotBlank(isOnly)));
        queryChain.where(PLUGIN_USER.ORGANIZATION.in(organNoList).when(CollUtil.isNotEmpty(organNoList)));
        queryChain.groupBy(PLUGIN_USER.ACCOUNT);
        if (CharSequenceUtil.isNotBlank(orderStr)) {
            queryChain.orderBy(orderStr, orderBy == OrderEnum.ASC);
        } else {
            queryChain.orderBy("p.last_used_time desc");
        }

        return queryChain.pageAs(Page.of(command.getPageNo(), command.getPageSize()), PluginFunctionStatisticsDTO.class);
    }

    @Override
    public List<PluginFunctionStatisticsDTO> listPluginFunctionStatistics(PluginFunctionStatCommand command) {
        return pluginFunStatisticsMapper.listPluginFunctionStatistics(command);
    }
}
