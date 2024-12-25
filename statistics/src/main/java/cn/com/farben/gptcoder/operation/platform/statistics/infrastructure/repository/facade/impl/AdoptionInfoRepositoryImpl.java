package cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade.impl;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.gptcoder.operation.platform.statistics.command.DashboardStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.FunUseStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.PersonUseStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.domain.entity.AdoptionInfoEntity;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.FunStatDataDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.AdoptionInfoDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.DashboardStatDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.UseStatDetailDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade.AdoptionInfoRepository;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.mapper.AdoptionInfoMapper;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.po.AdoptionInfoPO;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.po.table.AdoptionInfoTableDef.ADOPTION_INFO;

/**
 *
 * 插件使用记录仓储实现<br>
 * 创建时间：2023/8/15<br>
 * @author ltg
 *
 */
@Repository
public class AdoptionInfoRepositoryImpl implements AdoptionInfoRepository {
    /** 插件使用记录DB服务 */
    private final AdoptionInfoMapper adoptionInfoMapper;
    private static final Log logger = LogFactory.get();

    @Override
    public void addAdoptionInfo(AdoptionInfoEntity adoptionInfoEntity) {
        logger.info("存储插件使用记录{}", adoptionInfoEntity);
        AdoptionInfoPO adoptionInfoPO = new AdoptionInfoPO();
        CommonAssemblerUtil.assemblerEntityToPO(adoptionInfoEntity, adoptionInfoPO);
        adoptionInfoMapper.insertSelectiveWithPk(adoptionInfoPO);
    }

    /**
     * 新增插件使用记录
     * @param adoptions 插件使用记录实体
     */
    @Override
    public boolean batchAddAdoptionInfo(List<AdoptionInfoEntity> adoptions) {
        return adoptionInfoMapper.insertBatch(CommonAssemblerUtil.assemblerEntityListToPOList(adoptions, AdoptionInfoPO.class)) > 0;
    }

    @Override
    public Page<AdoptionInfoDTO> pageAdoptionInfo(String userId, String startTime, String endTime, long pageSize, long pageNo) {
        logger.info(
                """
                分页查询插件使用记录，userId: [{}], startTime: [{}], endTime: [{}], pageSize: [{}], pageNo: [{}]
                """, userId, startTime, endTime, pageSize, pageNo);
        QueryChain<AdoptionInfoPO> queryChain = QueryChain.of(adoptionInfoMapper)
                .select(ADOPTION_INFO.USER_ID, QueryMethods.count().as("useNum"), QueryMethods.sum(ADOPTION_INFO.GEN_NUM).as("genNum"),
                        QueryMethods.sum(ADOPTION_INFO.CONFIRM_NUM).as("confirmNum"));
        if (CharSequenceUtil.isNotBlank(userId)) {
            queryChain.where(ADOPTION_INFO.USER_ID.eq(userId));
        }
        if (CharSequenceUtil.isNotBlank(startTime)) {
            queryChain.where(ADOPTION_INFO.CREATE_DATE.ge(startTime));
        }
        if (CharSequenceUtil.isNotBlank(endTime)) {
            queryChain.where(ADOPTION_INFO.CREATE_DATE.le(endTime));
        }
        queryChain.groupBy(ADOPTION_INFO.USER_ID);
        Page<AdoptionInfoDTO> page = new Page<>(pageNo, pageSize);

        return queryChain.pageAs(page, AdoptionInfoDTO.class);
    }

    /**
     * 使用人数统计记录
     * @param command 参数
     * @return 统计结果
     */
    @Override
    public List<UseStatDetailDTO> personUseStat(PersonUseStatCommand command) {
        QueryChain<AdoptionInfoPO> queryChain = QueryChain.of(adoptionInfoMapper)
                .select(QueryMethods.count(QueryMethods.distinct(ADOPTION_INFO.USER_ID)).as("count"));
        String type = command.getType();
        if ("day".equalsIgnoreCase(type)) {
            queryChain.select(QueryMethods.dateFormat(ADOPTION_INFO.ACCESS_DATE, "%Y-%m-%d").as("date"));
        } else if ("month".equalsIgnoreCase(type)) {
            queryChain.select(QueryMethods.dateFormat(ADOPTION_INFO.ACCESS_DATE, "%Y-%m").as("date"));
        } else {
            queryChain.select(QueryMethods.dateFormat(ADOPTION_INFO.ACCESS_DATE, "%Y").as("date"));
        }
        queryChain.where(ADOPTION_INFO.ACCESS_DATE.between(command.getStartDateTime(), command.getEndDateTime()));
        queryChain.groupBy(ADOPTION_INFO.ACCESS_DATE);
        queryChain.orderBy(ADOPTION_INFO.ACCESS_DATE.asc());
        return queryChain.listAs(UseStatDetailDTO.class);
    }

    /**
     * 功能方法使用人数统计
     * @param command 参数
     * @return 统计结果
     */
    @Override
    public List<FunStatDataDTO> funUseStat(FunUseStatCommand command) {
        QueryChain<AdoptionInfoPO> queryChain = QueryChain.of(adoptionInfoMapper)
                .select(QueryMethods.count(QueryMethods.distinct(ADOPTION_INFO.USER_ID)).as("count"),
                        ADOPTION_INFO.FUN_ID);
        String type = command.getType();
        if ("day".equalsIgnoreCase(type)) {
            queryChain.select(QueryMethods.dateFormat(ADOPTION_INFO.ACCESS_DATE, "%Y-%m-%d").as("date"));
        } else if ("month".equalsIgnoreCase(type)) {
            queryChain.select(QueryMethods.dateFormat(ADOPTION_INFO.ACCESS_DATE, "%Y-%m").as("date"));
        } else {
            queryChain.select(QueryMethods.dateFormat(ADOPTION_INFO.ACCESS_DATE, "%Y").as("date"));
        }
        List<String> funList = command.getFunList();
        if (CollUtil.isNotEmpty(funList)) {
            queryChain.where(ADOPTION_INFO.FUN_ID.in(funList));
        }
        queryChain.where(ADOPTION_INFO.ACCESS_DATE.between(command.getStartDateTime(), command.getEndDateTime()));
        queryChain.groupBy(ADOPTION_INFO.FUN_ID).groupBy(ADOPTION_INFO.ACCESS_DATE);
        queryChain.orderBy(ADOPTION_INFO.ACCESS_DATE.asc());
        return queryChain.listAs(FunStatDataDTO.class);
    }


    /**
     * 概览汇总使用记录信息
     * @param command 参数
     * @return 统计结果
     */
    @Override
    public DashboardStatDTO dashboardStat(DashboardStatCommand command, List<String> codeList) {
        QueryChain<AdoptionInfoPO> queryChain = QueryChain.of(adoptionInfoMapper)
                .select(QueryMethods.count(QueryMethods.distinct(ADOPTION_INFO.USER_ID)).as("userNum"),
                        QueryMethods.count().as("totalNum"), QueryMethods.sum(ADOPTION_INFO.GEN_NUM).as("genNum"),
                        QueryMethods.sum(ADOPTION_INFO.CONFIRM_NUM).as("confirmNum"), QueryMethods.avg(ADOPTION_INFO.COST_TIME).as("avgTime"));
        if (CharSequenceUtil.isNotBlank(command.getStartDate())) {
            queryChain.where(ADOPTION_INFO.ACCESS_DATE.ge(command.getStartDate()));
        }
        if (CharSequenceUtil.isNotBlank(command.getEndDate())) {
            queryChain.where(ADOPTION_INFO.ACCESS_DATE.le(command.getEndDate()));
        }
        if (CollUtil.isNotEmpty(codeList)) {
            queryChain.where(ADOPTION_INFO.FUN_ID.in(codeList));
        }
        return queryChain.objAs(DashboardStatDTO.class);
    }

    @Override
    public Double findCompletionRate(DashboardStatCommand command, String code) {
        QueryChain<AdoptionInfoPO> queryChain = QueryChain.of(adoptionInfoMapper)
                .select(QueryMethods.avg(ADOPTION_INFO.COMPLEMENT_RATE).as("completionRate"));
        queryChain.where(ADOPTION_INFO.FUN_ID.eq(code));
        queryChain.where(ADOPTION_INFO.COMPLEMENT_RATE.gt(0));
        if (CharSequenceUtil.isNotBlank(command.getStartDate())) {
            queryChain.where(ADOPTION_INFO.ACCESS_DATE.ge(command.getStartDate()));
        }
        if (CharSequenceUtil.isNotBlank(command.getEndDate())) {
            queryChain.where(ADOPTION_INFO.ACCESS_DATE.le(command.getEndDate()));
        }
        return queryChain.objAs(Double.class);
    }

    @Override
    public List<AdoptionInfoEntity> listDealData() {
//        String userIdColumn = "user_id";
//        QueryWrapper<AdoptionInfoPO> queryWrapper = new QueryWrapper<>();
//        DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        String dealFlgColumn = "deal_flg";
//        queryWrapper.select("min(access_date) as accessDate");
//        queryWrapper.isNotNull(userIdColumn);
//        queryWrapper.ne(userIdColumn, "");
//        queryWrapper.and(wrapper -> {
//            wrapper.ne(dealFlgColumn, 1);
//            wrapper.or();
//            wrapper.isNull(dealFlgColumn);
//        });
//        AdoptionInfoPO minAdoptionInfoPO = adoptionInfoService.getOne(queryWrapper);
//        if (Objects.isNull(minAdoptionInfoPO) || Objects.isNull(minAdoptionInfoPO.getAccessDate())) {
//            // 没有需要处理的数据
//            return Collections.emptyList();
//        }
//        queryWrapper.clear();
//        queryWrapper.eq("access_date", dayFormat.format(minAdoptionInfoPO.getAccessDate()));
//        queryWrapper.isNotNull(userIdColumn);
//        queryWrapper.ne(userIdColumn, "");
//        queryWrapper.and(wrapper -> {
//            wrapper.ne(dealFlgColumn, 1);
//            wrapper.or();
//            wrapper.isNull(dealFlgColumn);
//        });
//        queryWrapper.orderByAsc("create_date");
//        queryWrapper.last("limit 1000");
//        return CommonAssemblerUtil.assemblerPOListToEntityList(adoptionInfoService.list(queryWrapper), AdoptionInfoEntity.class);
        // TODO 确认没问题后删掉上面的注释
        QueryChain<AdoptionInfoPO> queryChain1 = QueryChain.of(adoptionInfoMapper)
                .select(QueryMethods.min(ADOPTION_INFO.ACCESS_DATE).as("accessDate"));
        queryChain1.where(ADOPTION_INFO.USER_ID.isNotNull().and(ADOPTION_INFO.USER_ID.ne("")));
        queryChain1.where(ADOPTION_INFO.DEAL_FLG.ne(1).or(ADOPTION_INFO.DEAL_FLG.isNull()));
        LocalDate accessDate = queryChain1.objAs(LocalDate.class);
        if (Objects.isNull(accessDate)) {
            // 没有需要处理的数据
            return Collections.emptyList();
        }
        QueryChain<AdoptionInfoPO> queryChain2 = QueryChain.of(adoptionInfoMapper).select(ADOPTION_INFO.ALL_COLUMNS);
        queryChain2.where(ADOPTION_INFO.ACCESS_DATE.eq(accessDate));
        queryChain2.where(ADOPTION_INFO.USER_ID.isNotNull().and(ADOPTION_INFO.USER_ID.ne("")));
        queryChain2.where(ADOPTION_INFO.DEAL_FLG.ne(1).or(ADOPTION_INFO.DEAL_FLG.isNull()));
        queryChain2.orderBy(ADOPTION_INFO.CREATE_DATE.asc());
        queryChain2.limit(1000);
        return queryChain2.listAs(AdoptionInfoEntity.class);
    }

    @Override
    public void setDealFlgBatch(List<String> ids) {
        UpdateChain.of(adoptionInfoMapper).set(ADOPTION_INFO.DEAL_FLG, 1)
                .where(ADOPTION_INFO.INFO_ID.in(ids)).update();
    }

    @Override
    public LocalDateTime getUserLastDealTime(String account, String day) {
        return QueryChain.of(adoptionInfoMapper)
                .select(QueryMethods.max(ADOPTION_INFO.CREATE_DATE))
                .where(ADOPTION_INFO.USER_ID.eq(account).and(ADOPTION_INFO.ACCESS_DATE.eq(day))
                        .and(ADOPTION_INFO.DEAL_FLG.eq(1)))
                .objAs(LocalDateTime.class);
    }

    @Override
    public void removeProcessedData() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.where(ADOPTION_INFO.DEAL_FLG.eq(1));
        adoptionInfoMapper.deleteByQuery(queryWrapper);
    }

    public AdoptionInfoRepositoryImpl(AdoptionInfoMapper adoptionInfoMapper) {
        this.adoptionInfoMapper = adoptionInfoMapper;
    }
}
