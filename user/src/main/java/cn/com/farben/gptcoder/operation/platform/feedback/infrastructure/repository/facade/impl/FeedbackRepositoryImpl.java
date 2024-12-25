package cn.com.farben.gptcoder.operation.platform.feedback.infrastructure.repository.facade.impl;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.commons.ddd.repository.IDataChangeRepository;
import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import cn.com.farben.commons.errorcode.exception.DataUpdateException;
import cn.com.farben.gptcoder.operation.platform.feedback.command.PageFeedbackCommand;
import cn.com.farben.gptcoder.operation.platform.feedback.domain.entity.FeedbackEntity;
import cn.com.farben.gptcoder.operation.platform.feedback.domain.event.FeedbackChangeEvent;
import cn.com.farben.gptcoder.operation.platform.feedback.dto.FeedbackExportDTO;
import cn.com.farben.gptcoder.operation.platform.feedback.dto.FeedbackQueryDTO;
import cn.com.farben.gptcoder.operation.platform.feedback.infrastructure.repository.facade.FeedbackRepository;
import cn.com.farben.gptcoder.operation.platform.feedback.infrastructure.repository.mapper.FeedbackMapper;
import cn.com.farben.gptcoder.operation.platform.feedback.infrastructure.repository.po.FeedbackPO;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.po.table.DictCodeTableDef.DICT_CODE;
import static cn.com.farben.gptcoder.operation.platform.feedback.infrastructure.repository.po.table.FeedbackTableDef.FEEDBACK;
import static cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.table.OrganMngTableDef.ORGAN_MNG;
import static cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.table.PluginUserTableDef.PLUGIN_USER;
import static cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.table.UserAccountTableDef.USER_ACCOUNT;

/**
 *
 * 用户反馈仓储实现
 *
 */
@Repository
public class FeedbackRepositoryImpl implements FeedbackRepository, IDataChangeRepository {
    /** 用户反馈DB服务 */
    private final FeedbackMapper feedbackMapper;
    private static final Log logger = LogFactory.get();

    public FeedbackRepositoryImpl(FeedbackMapper feedbackMapper) {
        this.feedbackMapper = feedbackMapper;
    }

    @Override
    public FeedbackEntity getFeedbackById(String id) {
        logger.info("根据id获取用户反馈信息：{}", id);
        FeedbackEntity feedbackEntity = new FeedbackEntity();
        CommonAssemblerUtil.assemblerPOToEntity(
                QueryChain.of(feedbackMapper)
                        .select(FEEDBACK.ALL_COLUMNS)
                        .where(FEEDBACK.ID.eq(id))
                        .one(),
                feedbackEntity
        );
        return feedbackEntity;
    }

    @Override
    public void saveFeedback(FeedbackEntity feedbackEntity) {
        logger.info("保存用户反馈：{}", feedbackEntity);
        FeedbackPO feedbackPO = new FeedbackPO();
        CommonAssemblerUtil.assemblerEntityToPO(feedbackEntity, feedbackPO);
        feedbackMapper.insertSelectiveWithPk(feedbackPO);
    }

    @Override
    public void removeFeedback(String id) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.where(FEEDBACK.ID.eq(id));
        feedbackMapper.deleteByQuery(queryWrapper);
    }

    @Override
    public String getParams(String id) {
        return QueryChain.of(feedbackMapper)
                .select(FEEDBACK.PARAMS)
                .where(FEEDBACK.ID.eq(id))
                .objAs(String.class);
    }

    @Override
    public Page<FeedbackQueryDTO> pageFeedback(PageFeedbackCommand command) {
        return feedbackMapper.paginateAs(command.getPageNo(), command.getPageSize(), buildQueryWrapper(command), FeedbackQueryDTO.class);
    }

    @Override
    public FeedbackEntity getFeedbackQAById(String id) {
        FeedbackEntity feedbackEntity = new FeedbackEntity();
        CommonAssemblerUtil.assemblerPOToEntity(
                QueryChain.of(feedbackMapper)
                        .select(FEEDBACK.PARAMS, FEEDBACK.MODEL_RESULT)
                        .where(FEEDBACK.ID.eq(id))
                        .one(),
                feedbackEntity
        );
        return feedbackEntity;
    }

    @Override
    public List<FeedbackExportDTO> exportFeedback(PageFeedbackCommand command) {
        QueryWrapper queryWrapper = buildQueryWrapper(command);
        queryWrapper.limit(command.getStartNo(), command.getExportNo());
        return feedbackMapper.selectListByQueryAs(queryWrapper, FeedbackExportDTO.class);
    }

    @Override
    public void onApplicationEvent(@NonNull FeedbackChangeEvent event) {
        logger.info("监听到事件：[{}]", event);
        Object source = event.getSource();
        if (Objects.isNull(source)) {
            throw new DataUpdateException("无法更新数据，source为空");
        }
        updateEntity((DataChangeRecord)source);
    }

    private QueryWrapper buildQueryWrapper(PageFeedbackCommand command) {
        LocalDate from = command.getFrom();
        LocalDate to = command.getTo();
        String modelAbility = command.getModelAbility();
        String feedbackType = command.getFeedbackType();
        String fullOrganNo = command.getFullOrganNo();
        String searchText = command.getSearchText();
        QueryChain<FeedbackPO> queryChain = QueryChain.of(feedbackMapper);
        queryChain.select("f.account", "pu.job_number as jobNumber", "pu.name", "o.organ_name as organizationName",
                "dc.kind_value as dutyName", "f.feedback_type as feedbackType", "f.model_ability as modelAbility",
                "f.suggest", "f.feedback_time as feedbackTime", "f.id as feedbackId, f.params, f.model_result as modelResult");
        queryChain.from(FEEDBACK).as("f");
        queryChain.innerJoin(PLUGIN_USER).as("pu").on(FEEDBACK.ACCOUNT.eq(PLUGIN_USER.ACCOUNT));
        queryChain.leftJoin(ORGAN_MNG).as("o").on(PLUGIN_USER.ORGANIZATION.eq(ORGAN_MNG.ORGAN_NO));
        queryChain.leftJoin(DICT_CODE).as("dc").on(DICT_CODE.KIND_CODE.eq(PLUGIN_USER.DUTY).and(DICT_CODE.DICT_CODE_.eq("JOB_INFO")));
        queryChain.where(QueryMethods.date(FEEDBACK.FEEDBACK_TIME).ge(from).when(Objects.nonNull(from)));
        queryChain.where(QueryMethods.date(FEEDBACK.FEEDBACK_TIME).le(to).when(Objects.nonNull(to)));
        queryChain.where(FEEDBACK.MODEL_ABILITY.eq(modelAbility).when(CharSequenceUtil.isNotBlank(modelAbility)));
        queryChain.where(FEEDBACK.FEEDBACK_TYPE.eq(feedbackType).when(CharSequenceUtil.isNotBlank(feedbackType)));
        queryChain.where(PLUGIN_USER.FULL_ORGANIZATION.likeLeft(fullOrganNo).when(CharSequenceUtil.isNotBlank(fullOrganNo)));
        queryChain.where(PLUGIN_USER.ACCOUNT.like(searchText).or(PLUGIN_USER.NAME.like(searchText)).or(PLUGIN_USER.JOB_NUMBER.like(searchText)).when(CharSequenceUtil.isNotBlank(searchText)));
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.select("f.account", "a.job_number as jobNumber", "a.user_name as name", "o.organ_name as organizationName",
                "dc.kind_value as dutyName", "f.feedback_type as feedbackType", "f.model_ability as modelAbility",
                "f.suggest", "f.feedback_time as feedbackTime", "f.id as feedbackId, f.params, f.model_result as modelResult");
        queryWrapper.from(FEEDBACK).as("f");
        queryWrapper.innerJoin(USER_ACCOUNT).as("a").on(FEEDBACK.ACCOUNT.eq(USER_ACCOUNT.USER_ID));
        queryWrapper.leftJoin(ORGAN_MNG).as("o").on(USER_ACCOUNT.ORGANIZATION.eq(ORGAN_MNG.ORGAN_NO));
        queryWrapper.leftJoin(DICT_CODE).as("dc").on(DICT_CODE.KIND_CODE.eq(USER_ACCOUNT.DUTY).and(DICT_CODE.DICT_CODE_.eq("JOB_INFO")));
        queryWrapper.where(QueryMethods.date(FEEDBACK.FEEDBACK_TIME).ge(from).when(Objects.nonNull(from)));
        queryWrapper.where(QueryMethods.date(FEEDBACK.FEEDBACK_TIME).le(to).when(Objects.nonNull(to)));
        queryWrapper.where(FEEDBACK.MODEL_ABILITY.eq(modelAbility).when(CharSequenceUtil.isNotBlank(modelAbility)));
        queryWrapper.where(FEEDBACK.FEEDBACK_TYPE.eq(feedbackType).when(CharSequenceUtil.isNotBlank(feedbackType)));
        queryWrapper.where(USER_ACCOUNT.FULL_ORGANIZATION.likeLeft(fullOrganNo).when(CharSequenceUtil.isNotBlank(fullOrganNo)));
        queryWrapper.where(USER_ACCOUNT.USER_ID.like(searchText).or(USER_ACCOUNT.USER_NAME.like(searchText)).or(USER_ACCOUNT.JOB_NUMBER.like(searchText)).when(CharSequenceUtil.isNotBlank(searchText)));
        queryChain.union(queryWrapper);

        QueryWrapper queryWrapper2 = QueryWrapper.create();
        queryWrapper2.select("*");
        queryWrapper2.from(queryChain.toQueryWrapper());
        queryWrapper2.orderBy("feedbackTime desc");
        return queryWrapper2;
    }
}
