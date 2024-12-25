package cn.com.farben.gptcoder.operation.platform.statistics.job;

import cn.com.farben.gptcoder.operation.commons.model.enums.ModelAbilityEnum;
import cn.com.farben.gptcoder.operation.platform.statistics.domain.entity.AdoptionInfoEntity;
import cn.com.farben.gptcoder.operation.platform.statistics.domain.entity.PluginFunStatisticsEntity;
import cn.com.farben.gptcoder.operation.platform.statistics.domain.entity.PluginUsageStatisticsEntity;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade.AdoptionInfoRepository;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade.PluginFunStateRepository;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade.PluginUsageStateRepository;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.PluginUserEntity;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.PluginUserRepository;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.IdUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 统计插件使用情况任务，每十分钟一次
 */
@Component
@DisallowConcurrentExecution
public class PluginStatisticsJob extends QuartzJobBean {
    private final AdoptionInfoRepository adoptionInfoRepository;
    private final TransactionTemplate transactionTemplate;
    private final PluginUsageStateRepository pluginUsageStateRepository;
    private final PluginFunStateRepository pluginFunStateRepository;
    private final PluginUserRepository pluginUserRepository;
    // 活跃状态间隔(180s)
    private static final long ACTIVE_TIME_GAP = 180L;
    private static final Log logger = LogFactory.get();

    public PluginStatisticsJob(AdoptionInfoRepository adoptionInfoRepository, TransactionTemplate transactionTemplate,
                               PluginUsageStateRepository pluginUsageStateRepository, PluginFunStateRepository pluginFunStateRepository,
                               PluginUserRepository pluginUserRepository) {
        this.adoptionInfoRepository = adoptionInfoRepository;
        this.transactionTemplate = transactionTemplate;
        this.pluginUsageStateRepository = pluginUsageStateRepository;
        this.pluginFunStateRepository = pluginFunStateRepository;
        this.pluginUserRepository = pluginUserRepository;
    }

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        // 10分钟一次
        logger.info("------统计插件使用情况任务启动------");
        // 执行时间，毫秒。最多允许执行9分半
        long limit = System.currentTimeMillis() + ((9 * 60) + 30) * 1000L;
        List<AdoptionInfoEntity> dataList = null;
        while(true) {
            long nowMills;
            boolean breakFlg = false;
            nowMills = System.currentTimeMillis();
            if (nowMills >= limit) {
                // 到达执行时间
                logger.info("执行时间到达9分半，退出");
                breakFlg = true;
            } else {
                dataList = adoptionInfoRepository.listDealData();
            }
            if (breakFlg || CollUtil.isEmpty(dataList)) {
                logger.info("------统计插件使用情况任务时间到达或数据已处理完------");
                removeProcessedData();
                break;
            }
            pluginDataProcessing(dataList);
            removeProcessedData();
        }
        logger.info("------统计插件使用情况任务结束------");
    }

    // 清除已处理过的数据
    private void removeProcessedData() {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(@NonNull TransactionStatus status) {
                try {
                    adoptionInfoRepository.removeProcessedData();
                } catch (Exception e) {
                    logger.error("清除已处理过的数据失败", e);
                    status.setRollbackOnly();
                }
            }
        });
    }

    /**
     * 插件使用原数据处理
     * @param dataList 原数据列表
     */
    private void pluginDataProcessing(List<AdoptionInfoEntity> dataList) {
        DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 插件使用统计map，key：账号、value：统计情况
        Map<String, PluginUsageStatisticsEntity> usageStatisticsMap = new HashMap<>();
        // 用户活跃记录map，key：账号、value：上次使用时间
        Map<String, LocalDateTime> activeMap = new HashMap<>();
        // 插件功能统计map，key：账号+功能、value：功能统计情况
        Map<String, PluginFunStatisticsEntity> functionStatisticsMap = new HashMap<>();
        // 记录用户最近使用时间、插件类型、版本和模型
        Map<String, PluginUserEntity> lastTimeMap = new HashMap<>();

        List<PluginUsageStatisticsEntity> insertUsageList = new ArrayList<>();
        List<PluginUsageStatisticsEntity> updateUsageList = new ArrayList<>();
        List<PluginFunStatisticsEntity> insertFunctionList = new ArrayList<>();
        List<PluginFunStatisticsEntity> updateFunctionList = new ArrayList<>();
        List<String> adoptionIdList = new ArrayList<>();

        for (AdoptionInfoEntity adoptionInfoEntity : dataList) {
            String account = adoptionInfoEntity.getUserId();
            LocalDateTime createDate = adoptionInfoEntity.getCreateDate();
            // 对应ModelAbilityEnum
            String funId = adoptionInfoEntity.getFunId();
            adoptionIdList.add(adoptionInfoEntity.getInfoId());

            String day = dayFormat.format(createDate);
            Boolean usageInsertFlag = handleUsageStatistics(account, day, usageStatisticsMap);
            Boolean functionInsertFlag = handleFunctionStatistics(account, day, funId, functionStatisticsMap);
            String key = account + StrPool.DOT + funId;
            setActiveMap(activeMap, account, day);

            // 按时间排序，所以直接更新
            PluginUserEntity pluginUserEntity = lastTimeMap.containsKey(account) ? lastTimeMap.get(account) : new PluginUserEntity();
            pluginUserEntity.setAccount(account);
            pluginUserEntity.setLastUsedTime(adoptionInfoEntity.getCreateDate());
            pluginUserEntity.setVersion(adoptionInfoEntity.getPluginsVer());
            pluginUserEntity.setPlugin(adoptionInfoEntity.getPluginType());
            pluginUserEntity.setCurrentModel(adoptionInfoEntity.getModelName());
            lastTimeMap.put(account, pluginUserEntity);

            PluginUsageStatisticsEntity pluginUsageStatisticsEntity = usageStatisticsMap.get(account);
            PluginFunStatisticsEntity pluginFunStatisticsEntity = functionStatisticsMap.get(key);
            computeStatistics(adoptionInfoEntity, pluginUsageStatisticsEntity, pluginFunStatisticsEntity, activeMap);

            if (Boolean.TRUE == usageInsertFlag) {
                pluginUsageStatisticsEntity.setId(IdUtil.objectId());
                insertUsageList.add(pluginUsageStatisticsEntity);
            }
            if (Boolean.FALSE == usageInsertFlag) {
                updateUsageList.add(pluginUsageStatisticsEntity);
            }
            if (Boolean.TRUE == functionInsertFlag) {
                pluginFunStatisticsEntity.setId(IdUtil.objectId());
                insertFunctionList.add(pluginFunStatisticsEntity);
            }
            if (Boolean.FALSE == functionInsertFlag) {
                updateFunctionList.add(pluginFunStatisticsEntity);
            }
        }
        if (CollUtil.isEmpty(adoptionIdList)) {
            return;
        }
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(@NonNull TransactionStatus status) {
                try {
                    // 数据入库
                    saveStatistics(adoptionIdList, insertUsageList, updateUsageList, insertFunctionList,
                            updateFunctionList, lastTimeMap);
                } catch (Exception e) {
                    logger.error("更新插件统计数据失败", e);
                    status.setRollbackOnly();
                }
            }
        });
    }

    private void setActiveMap(Map<String, LocalDateTime> activeMap, String account, String day) {
        if (!activeMap.containsKey(account)) {
            // 上次活跃时间还没记录，取最近处理过的最大时间用作计算
            LocalDateTime userLastDealTime = adoptionInfoRepository.getUserLastDealTime(account, day);
            if (Objects.nonNull(userLastDealTime)) {
                activeMap.put(account, userLastDealTime);
            }
        }
    }

    /**
     * 处理使用统计
     * @param account 账号
     * @param day 日期
     * @param usageStatisticsMap 使用统计map
     * @return 数据该更新还是插入(true：插入， false：更新， null: 不处理)
     */
    private Boolean handleUsageStatistics(String account, String day, Map<String, PluginUsageStatisticsEntity> usageStatisticsMap) {
        PluginUsageStatisticsEntity pluginUsageStatisticsEntity;
        Boolean result = null;
        if (usageStatisticsMap.containsKey(account)) {
            // 已经有数据
            pluginUsageStatisticsEntity = usageStatisticsMap.get(account);
        } else {
            // 从数据库获取数据
            pluginUsageStatisticsEntity = pluginUsageStateRepository.getUsageStatisticsByDay(account, day);
            if (Objects.isNull(pluginUsageStatisticsEntity) || CharSequenceUtil.isBlank(pluginUsageStatisticsEntity.getId())) {
                // 插入
                result = Boolean.TRUE;
            } else {
                // 更新
                result = Boolean.FALSE;
            }
        }
        usageStatisticsMap.put(account, pluginUsageStatisticsEntity);
        return result;
    }

    /**
     * 处理模型能力分类统计
     * @param account 账号
     * @param day 日期
     * @param funId 分类，对应ModelFeatureEnum
     * @param functionStatisticsMap 模型能力分类统计map
     * @return 数据该更新还是插入(true：插入， false：更新， null: 不处理)
     */
    private Boolean handleFunctionStatistics(String account, String day, String funId, Map<String, PluginFunStatisticsEntity> functionStatisticsMap) {
        PluginFunStatisticsEntity pluginFunStatisticsEntity;
        Boolean result = null;
        String key = account + StrPool.DOT + funId;
        ModelAbilityEnum modelAbilityEnum = null;
        for (ModelAbilityEnum abilityEnum : ModelAbilityEnum.values()) {
            if (abilityEnum.getAbilityCode().equalsIgnoreCase(funId) || abilityEnum.name().equalsIgnoreCase(funId)) {
                modelAbilityEnum = abilityEnum;
                break;
            }
        }
        if (functionStatisticsMap.containsKey(key)) {
            // 已经有数据
            pluginFunStatisticsEntity = functionStatisticsMap.get(key);
        } else {
            // 从数据库获取数据
            pluginFunStatisticsEntity = pluginFunStateRepository.getFunStatisticsByDay(account, modelAbilityEnum, day);
            if (Objects.isNull(pluginFunStatisticsEntity) || CharSequenceUtil.isBlank(pluginFunStatisticsEntity.getId())) {
                // 插入
                result = Boolean.TRUE;
            } else {
                // 更新
                result = Boolean.FALSE;
            }
        }
        functionStatisticsMap.put(key, pluginFunStatisticsEntity);
        return result;
    }

    /**
     * 计算统计数据
     * @param adoptionInfoEntity 原始数据
     * @param pluginUsageStatisticsEntity 插件使用统计表实体
     * @param pluginFunStatisticsEntity 插件功能统计表实体
     */
    private void computeStatistics(AdoptionInfoEntity adoptionInfoEntity, PluginUsageStatisticsEntity pluginUsageStatisticsEntity,
                                   PluginFunStatisticsEntity pluginFunStatisticsEntity, Map<String, LocalDateTime> activeMap) {
        computeFunctionStatistics(adoptionInfoEntity, pluginFunStatisticsEntity);
        // 计算完成后获取用户当前最新的代码生成调用次数
        Integer acceptCount = pluginFunStatisticsEntity.getAcceptCount();
        computeUsageStatistics(adoptionInfoEntity, pluginUsageStatisticsEntity, activeMap, acceptCount);
    }

    private void computeFunctionStatistics(AdoptionInfoEntity adoptionInfoEntity, PluginFunStatisticsEntity pluginFunStatisticsEntity) {
        String account = adoptionInfoEntity.getUserId();
        String funId = adoptionInfoEntity.getFunId();
        ModelAbilityEnum modelAbilityEnum = null;
        for (ModelAbilityEnum abilityEnum : ModelAbilityEnum.values()) {
            if (abilityEnum.getAbilityCode().equalsIgnoreCase(funId) || abilityEnum.name().equalsIgnoreCase(funId)) {
                modelAbilityEnum = abilityEnum;
                break;
            }
        }
        Integer genNum = adoptionInfoEntity.getGenNum();
        Integer confirmNum = adoptionInfoEntity.getConfirmNum();

        Integer useCount = pluginFunStatisticsEntity.getUseCount();
        Integer generateLines = pluginFunStatisticsEntity.getGenerateLines();
        Integer acceptLines = pluginFunStatisticsEntity.getAcceptLines();
        Integer acceptCount = pluginFunStatisticsEntity.getAcceptCount();
        if (Objects.isNull(useCount)) {
            useCount = 0;
        }
        if (Objects.isNull(generateLines)) {
            generateLines = 0;
        }
        if (Objects.isNull(acceptLines)) {
            acceptLines = 0;
        }
        if (Objects.isNull(acceptCount)) {
            acceptCount = 0;
        }

        if (Objects.nonNull(genNum) && genNum > 0 && (Objects.isNull(confirmNum) || confirmNum == 0)) {
            // 确认时再加的话会重复计算
            generateLines += genNum;
        }
        if (Objects.nonNull(confirmNum) && confirmNum > 0) {
            acceptLines += confirmNum;
            acceptCount++;
        } else {
            // 确认时插件会上报第二次，因此第二次不计入使用次数
            useCount++;
        }

        pluginFunStatisticsEntity.setAccount(account);
        pluginFunStatisticsEntity.setDay(adoptionInfoEntity.getAccessDate());
        pluginFunStatisticsEntity.setModelAbilityEnum(modelAbilityEnum);
        pluginFunStatisticsEntity.setUseCount(useCount);
        pluginFunStatisticsEntity.setGenerateLines(generateLines);
        pluginFunStatisticsEntity.setAcceptLines(acceptLines);
        pluginFunStatisticsEntity.setAcceptCount(acceptCount);
    }

    private void computeUsageStatistics(AdoptionInfoEntity adoptionInfoEntity, PluginUsageStatisticsEntity pluginUsageStatisticsEntity,
                                        Map<String, LocalDateTime> activeMap, Integer acceptCount) {
        String funId = adoptionInfoEntity.getFunId();
        long costTime = Objects.isNull(adoptionInfoEntity.getCostTime()) ? 0L : adoptionInfoEntity.getCostTime();
        BigDecimal complementRate = adoptionInfoEntity.getComplementRate();
        String account = adoptionInfoEntity.getUserId();
        LocalDateTime nowDate = adoptionInfoEntity.getCreateDate();
        Integer confirmNum = adoptionInfoEntity.getConfirmNum();

        // 累计活跃时长（s）
        Long activeTime = pluginUsageStatisticsEntity.getActiveTime();
        // 总代码补全率
        BigDecimal totalCompletion = pluginUsageStatisticsEntity.getTotalCompletion();
        // 总响应时间(ms)
        Long totalResponse = pluginUsageStatisticsEntity.getTotalResponse();
        // 总调用次数
        Long totalCallNumber = pluginUsageStatisticsEntity.getTotalCallNumber();
        // 平均代码补全率
        BigDecimal avgCompletion = pluginUsageStatisticsEntity.getAvgCompletion();

        if (Objects.isNull(activeTime)) {
            activeTime = 0L;
        }
        if (Objects.isNull(totalCompletion)) {
            totalCompletion = BigDecimal.ZERO;
        }
        if (Objects.isNull(totalResponse)) {
            totalResponse = 0L;
        }
        if (Objects.isNull(totalCallNumber)) {
            totalCallNumber = 0L;
        }
        if (Objects.isNull(avgCompletion)) {
            avgCompletion = BigDecimal.ZERO;
        }
        if (activeMap.containsKey(account)) {
            LocalDateTime preTime = activeMap.get(account);

            long diff = ChronoUnit.SECONDS.between(preTime, nowDate);
            if (diff <= ACTIVE_TIME_GAP) {
                activeTime += diff;
            }
        }
        if (Objects.isNull(confirmNum) || confirmNum == 0) {
            // 确认时是重复上报，所以不计入总次数
            totalCallNumber += 1;
        }

        if (ModelAbilityEnum.CODE_HINTING.getAbilityCode().equalsIgnoreCase(funId) && (Objects.nonNull(complementRate) && complementRate.compareTo(BigDecimal.ZERO) > 0)) {
            totalCompletion = totalCompletion.add(complementRate);
        }
        if (Objects.nonNull(acceptCount) && acceptCount > 0 && ModelAbilityEnum.CODE_HINTING.getAbilityCode().equalsIgnoreCase(funId)) {
            avgCompletion = totalCompletion.divide(new BigDecimal(acceptCount), 4, RoundingMode.HALF_UP);
        }
        if (costTime > 0) {
            totalResponse += costTime;
        }
        activeMap.put(account, nowDate);
        // 更新统计数据
        pluginUsageStatisticsEntity.setAccount(adoptionInfoEntity.getUserId());
        pluginUsageStatisticsEntity.setDay(adoptionInfoEntity.getAccessDate());
        pluginUsageStatisticsEntity.setTotalCompletion(totalCompletion);
        pluginUsageStatisticsEntity.setTotalResponse(totalResponse);
        pluginUsageStatisticsEntity.setActiveTime(activeTime);
        pluginUsageStatisticsEntity.setTotalCallNumber(totalCallNumber);
        pluginUsageStatisticsEntity.setAvgCompletion(avgCompletion);
    }

    /**
     * 保存统计数据
     * @param dataIdList 原始数据的id列表
     * @param insertUsageList 插入使用统计数据列表
     * @param updateUsageList 更新使用统计数据列表
     * @param insertFunctionList 插入功能统计数据列表
     * @param updateFunctionList 更新功能统计数据列表
     * @param lastTimeMap 最近使用时间map
     */
    private void saveStatistics(List<String> dataIdList, List<PluginUsageStatisticsEntity> insertUsageList,
                                List<PluginUsageStatisticsEntity> updateUsageList, List<PluginFunStatisticsEntity> insertFunctionList,
                                List<PluginFunStatisticsEntity> updateFunctionList, Map<String, PluginUserEntity> lastTimeMap) {
        List<PluginUserEntity> lastTimeList = new ArrayList<>();
        for (Map.Entry<String, PluginUserEntity> entityEntry : lastTimeMap.entrySet()) {
            lastTimeList.add(entityEntry.getValue());
        }
        adoptionInfoRepository.setDealFlgBatch(dataIdList);
        if (!CollUtil.isEmpty(insertUsageList)) {
            pluginUsageStateRepository.addPluginUsageStatistics(insertUsageList);
        }
        if (!CollUtil.isEmpty(updateUsageList)) {
            pluginUsageStateRepository.refreshPluginUsageStatistics(updateUsageList);
        }
        if (!CollUtil.isEmpty(insertFunctionList)) {
            pluginFunStateRepository.addPluginFunStatistics(insertFunctionList);
        }
        if (!CollUtil.isEmpty(updateFunctionList)) {
            pluginFunStateRepository.refreshPluginFunStatistics(updateFunctionList);
        }
        if (!CollUtil.isEmpty(lastTimeList)) {
            pluginUserRepository.updateLastUsedTime(lastTimeList);
        }
    }
}
