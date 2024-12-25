package cn.com.farben.gptcoder.operation.platform.statistics.application.service;

import cn.com.farben.commons.errorcode.exception.IllegalParameterException;
import cn.com.farben.gptcoder.operation.commons.model.enums.ModelAbilityEnum;
import cn.com.farben.gptcoder.operation.commons.user.enums.SystemDictCodeTypeEnum;
import cn.com.farben.gptcoder.operation.commons.user.utils.CommonDateUtils;
import cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.utils.LocalCacheUtils;
import cn.com.farben.gptcoder.operation.platform.statistics.command.DashboardStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.FunUseStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.JobUseFunStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.PersonUseStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.SpeedConfigCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.UserDutyCountStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.domain.SpeedConfigAggregateRoot;
import cn.com.farben.gptcoder.operation.platform.statistics.domain.entity.SpeedConfigEntity;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.DashboardStatDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.FunStatDataDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.UseFunctionDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.UseStatDetailDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.enums.DateTypeEnum;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade.PluginFunStateRepository;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade.PluginUsageStateRepository;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade.SpeedConfigRepository;
import cn.com.farben.gptcoder.operation.platform.user.application.component.AccountOrganComponent;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页 、 概览相关统计指标操作业务类
 * @author wuanhui
 */
@Component
public class DashboardStatService {

    private static final Log log = LogFactory.get();

    private static boolean running = false;

    private final SpeedConfigRepository speedRepository;

    private final LocalCacheUtils localCache;

    /** 插件用户统计中间表业务处理 */
    private final PluginUsageStateRepository statisticsRepository;

    /** 插件功能统计中间表 */
    private final PluginFunStateRepository funStateRepository;

    private final AccountOrganComponent accountOrganComponent;

    @Value("${config.base-speed:100}")
    private String sysSpeed;

    private static final String ERROR_MESSAGE = "时间格式错误，请检查参数";

    private static final String SPEED_ERROR_MESSAGE = "设置速率不能为空";


    /**
     * 概览页统计指标实现
     * @param query 参数
     * @param operator 操作员
     * @return 统计结果
     */
    public DashboardStatDTO dashboardStat(DashboardStatCommand query, String operator) {
        if(CharSequenceUtil.isBlank(query.getEndDate())) {
            throw new IllegalParameterException(ERROR_MESSAGE);
        }
        if(CharSequenceUtil.isBlank(query.getStartDate())) {
            throw new IllegalParameterException(ERROR_MESSAGE);
        }
        //权限过滤
        accountOrganComponent.filterUserAuth(query, operator);
        log.info("------ 概览页统计条件 ------- ：{}", query);
        DashboardStatDTO dto = statisticsRepository.dashboardSummary(query);
        if(dto == null) {
            dto = new DashboardStatDTO();
        }
        //计算平均响应时长
        if(dto.getTotalResponse() != null && dto.getTotalNum() > 0) {
            BigDecimal total = dto.getTotalResponse().compareTo(new BigDecimal(0)) <= 0 ? new BigDecimal(0) : dto.getTotalResponse();
            BigDecimal avgTime = total.divide(new BigDecimal(dto.getTotalNum()),2, RoundingMode.HALF_DOWN);
            dto.setAvgTime(avgTime.doubleValue());
        }
        BigDecimal rate = dto.getCompletionRate();
        dto.setCompletionRate(rate == null ? new BigDecimal(0) : rate.setScale(4, RoundingMode.HALF_DOWN));

        DashboardStatDTO lineStat = funStateRepository.dashboardSummary(query, ModelAbilityEnum.CODE_HINTING.getAbilityCode());
        if(lineStat != null){
            dto.setGenNum(lineStat.getGenNum());
            dto.setConfirmNum(lineStat.getConfirmNum());
        }
        //默认速率配置
        SpeedConfigEntity configEntity = speedRepository.findConfig();
        dto.setBaseSpeed(configEntity == null ? new BigDecimal(sysSpeed) : configEntity.getSpeed());
        return dto;
    }


    /**
     * 使用插件人数统计图表
     * @param query 参数
     * @param operator 操作员
     * @return 统计结果
     */
    public List<UseStatDetailDTO> personUseStat(PersonUseStatCommand query, String operator) {
        log.info("------ 使用人数图表统计条件 ------- ：{}", query);
        //校验参数，转换时间格式
        checkBaseParam(query);
        //权限过滤
        accountOrganComponent.filterUserAuth(query, operator);
        List<UseStatDetailDTO> list = statisticsRepository.personUseStat(query);
        //封装职务名称, 没有职务的数据丢弃调
        List<UseStatDetailDTO> result = new ArrayList<>(list.size());
        for(UseStatDetailDTO item : list) {
            //排除职务为空的数据
            String jobName = localCache.getDictValue(SystemDictCodeTypeEnum.JOB_INFO.getCode(), item.getDuty());
            if(CharSequenceUtil.isNotBlank(jobName)) {
                item.setType(jobName);
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 概览页功能使用情况统计
     * @param query 统计参数
     * @param operator 操作员
     * @return 统计结果
     */
    public List<FunStatDataDTO> funUseStat(FunUseStatCommand query, String operator) {
        log.info("------ 功能使用图表条件 ------- ：{}", query);
        //校验参数，转换时间格式
        checkBaseParam(query);
        //权限过滤
        accountOrganComponent.filterUserAuth(query, operator);
        List<FunStatDataDTO> queryList = funStateRepository.funUseStat(query);
        if(queryList.isEmpty()) {
            return queryList;
        }
        for(FunStatDataDTO item : queryList) {
            item.setType(item.getModelAbility().getDescribe());
            item.setFunId(item.getModelAbility().getAbilityCode());
        }
        return queryList;
    }

    /**
     * 概览页各职务使用插件次数统计
     * @param query 参数
     * @param operator 操作员
     * @return 统计结果
     */
    public List<UseFunctionDTO> userDutyCountStat(UserDutyCountStatCommand query, String operator) {
        //校验参数，转换时间格式
        query.setType(DateTypeEnum.DAY.getCode());
        checkBaseParam(query);
        //权限过滤
        accountOrganComponent.filterUserAuth(query, operator);
        List<UseFunctionDTO> list = funStateRepository.userDutyCountStat(query);
        List<UseFunctionDTO> result = new ArrayList<>(list.size());
        for(UseFunctionDTO item : list) {
            //排除职务为空的数据
            String jobName = localCache.getDictValue(SystemDictCodeTypeEnum.JOB_INFO.getCode(), item.getDuty());
            if(CharSequenceUtil.isNotBlank(jobName)) {
                item.setType(jobName);
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 概览页个职务使用插件功能次数统计
     * @param query 参数
     * @param operator 操作员
     * @return 统计结果
     */
    public List<FunStatDataDTO> userJobFunctionStat(JobUseFunStatCommand query, String operator) {
        //校验参数，转换时间格式
        query.setType(DateTypeEnum.DAY.getCode());
        checkBaseParam(query);
        //权限过滤
        accountOrganComponent.filterUserAuth(query, operator);
        List<FunStatDataDTO> list = funStateRepository.userJobFunctionStat(query);
        for(FunStatDataDTO item : list) {
            item.setFunId(item.getModelAbility().getAbilityCode());
            item.setType(item.getModelAbility().getDescribe());
        }
        return list;
    }

    /**
     * 响应速率基线设置
     * @param query 参数
     * @return 操作结果
     */
    public boolean speedEdit(SpeedConfigCommand query) {
        if(query.getBaseSpeed() == null) {
            throw new IllegalParameterException(SPEED_ERROR_MESSAGE);
        }
        //速率范围校验
        if(query.getBaseSpeed().compareTo(new BigDecimal(0)) <= 0 || query.getBaseSpeed().compareTo(new BigDecimal(10000000)) > 0) {
            throw new IllegalParameterException(SPEED_ERROR_MESSAGE);
        }
        if(running) {
           return true;
        }
        try {
            running = true;
            SpeedConfigAggregateRoot aggregateRoot = new SpeedConfigAggregateRoot.Builder(speedRepository).build();
            SpeedConfigEntity config = speedRepository.findConfig();
            //为空，新增
            if(config == null) {
                config = new SpeedConfigEntity();
                config.setSpeed(query.getBaseSpeed());
                return aggregateRoot.addConfig(config);
            }
            config.setSpeed(query.getBaseSpeed());
            return aggregateRoot.editConfig(config);
        }finally {
            running = false;
        }
    }

    public DashboardStatService(SpeedConfigRepository speedRepository,
                                LocalCacheUtils localCache,
                                PluginUsageStateRepository statisticsRepository,
                                PluginFunStateRepository funStateRepository, AccountOrganComponent accountOrganComponent) {
        this.speedRepository = speedRepository;
        this.localCache = localCache;
        this.funStateRepository = funStateRepository;
        this.statisticsRepository = statisticsRepository;
        this.accountOrganComponent = accountOrganComponent;
    }

    /**
     * 校验统计查询参数
     * @param command 查询参数
     */
    private void checkBaseParam(DashboardStatCommand command) {
        if(CharSequenceUtil.isBlank(command.getType())) {
            throw new IllegalParameterException("统计维度不能为空");
        }
        if(DateTypeEnum.exist(command.getType()) == null) {
            throw new IllegalParameterException("统计维度不存在");
        }
        if(CharSequenceUtil.isBlank(command.getStartDate())) {
            throw new IllegalParameterException("开始时间不能为空");
        }
        if(CharSequenceUtil.isBlank(command.getEndDate())) {
            throw new IllegalParameterException("结束时间不能为空");
        }
        //转换时间格式
        command.setStartDateTime(CommonDateUtils.convertStrToDate(command.getStartDate(), CommonDateUtils.FORMAT_YYYY_MM_DD));
        command.setEndDateTime(CommonDateUtils.convertStrToDate(command.getEndDate(), CommonDateUtils.FORMAT_YYYY_MM_DD));
    }

}


