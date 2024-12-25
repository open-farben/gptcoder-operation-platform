package cn.com.farben.gptcoder.operation.platform.feedback.application.service;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;
import cn.com.farben.commons.errorcode.exception.IllegalParameterException;
import cn.com.farben.commons.errorcode.exception.OperationNotAllowedException;
import cn.com.farben.commons.web.bo.UserInfoBO;
import cn.com.farben.commons.web.utils.UserThreadLocal;
import cn.com.farben.gptcoder.operation.commons.model.enums.ModelAbilityEnum;
import cn.com.farben.gptcoder.operation.platform.feedback.command.FeedbackCommand;
import cn.com.farben.gptcoder.operation.platform.feedback.command.PageFeedbackCommand;
import cn.com.farben.gptcoder.operation.platform.feedback.domain.FeedbackAggregateRoot;
import cn.com.farben.gptcoder.operation.platform.feedback.domain.entity.FeedbackEntity;
import cn.com.farben.gptcoder.operation.platform.feedback.dto.FeedbackExportDTO;
import cn.com.farben.gptcoder.operation.platform.feedback.dto.FeedbackQADTO;
import cn.com.farben.gptcoder.operation.platform.feedback.dto.FeedbackQueryDTO;
import cn.com.farben.gptcoder.operation.platform.feedback.infrastructure.enums.FeedbackEnum;
import cn.com.farben.gptcoder.operation.platform.feedback.infrastructure.enums.UserTypeEnum;
import cn.com.farben.gptcoder.operation.platform.feedback.infrastructure.repository.facade.FeedbackRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.ColumnConstants;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static cn.com.farben.gptcoder.operation.platform.user.infrastructure.RedisConstants.REDIS_PLUGIN_USER_PREFIX;

/**
 *
 * 用户反馈应用服务
 *
 */
@Component
public class FeedbackAppService {
    private final StringRedisTemplate stringRedisTemplate;

    /** 用户反馈仓储接口 */
    private final FeedbackRepository feedbackRepository;

    /** 事件发布 */
    private final ApplicationEventPublisher applicationEventPublisher;

    /** 记录流水号用于反馈的前缀 */
    private static final String REDIS_FEEDBACK_PREFIX = "feedback:";

    private static final Log logger = LogFactory.get();

    public FeedbackAppService(StringRedisTemplate stringRedisTemplate, FeedbackRepository feedbackRepository,
                              ApplicationEventPublisher applicationEventPublisher) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.feedbackRepository = feedbackRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * 效果反馈
     * @param feedbackCommand 用户反馈命令
     * @param uuid 用户uuid
     * @param authorization 用户令牌
     */
    public void feedbackEffect(FeedbackCommand feedbackCommand, String uuid, String authorization) {
        JSONObject feedbackJo = checkParam(feedbackCommand, uuid, authorization);

        String ability = feedbackJo.getStr("ability");
        String params = feedbackJo.getStr("params");
        String headers = feedbackJo.getStr("headers");
        String modelResult = feedbackJo.getStr("modelResult");

        if (CharSequenceUtil.isBlank(feedbackCommand.getType())) {
            throw new OperationNotAllowedException("反馈类型不能为空");
        }
        FeedbackEnum feedbackEnum = feedbackCommand.getFeedbackType();
        FeedbackEntity feedbackEntity = new FeedbackEntity();
        feedbackEntity.setId(feedbackCommand.getSerialNumber());
        feedbackEntity.setAccount(feedbackJo.getStr(ColumnConstants.USER_ACCOUNT_COLUMN));
        feedbackEntity.setFeedbackType(feedbackEnum);
        if (CharSequenceUtil.isNotBlank(uuid)) {
            feedbackEntity.setUserType(UserTypeEnum.PLUG_IN);
        } else {
            feedbackEntity.setUserType(UserTypeEnum.PLATFORM);
        }
        feedbackEntity.setModelAbility(changeAbility(ability));
        feedbackEntity.setParams(params);
        feedbackEntity.setHeaders(headers);
        feedbackEntity.setModelResult(modelResult);

        FeedbackAggregateRoot feedbackAggregateRoot = new FeedbackAggregateRoot.Builder(feedbackRepository,
                applicationEventPublisher).build();
        feedbackAggregateRoot.feedbackEffect(feedbackEntity);
    }

    /**
     * 反馈建议
     * @param feedbackCommand 用户反馈命令
     * @param uuid 用户uuid
     * @param authorization 用户令牌
     */
    public void feedbackSuggest(FeedbackCommand feedbackCommand, String uuid, String authorization) {
        String suggest = feedbackCommand.getSuggest();
        if (CharSequenceUtil.isBlank(suggest)) {
            throw new OperationNotAllowedException("反馈建议不能为空");
        }
        JSONObject feedbackJo = checkParam(feedbackCommand, uuid, authorization);

        String ability = feedbackJo.getStr("ability");
        String params = feedbackJo.getStr("params");
        String headers = feedbackJo.getStr("headers");
        String modelResult = feedbackJo.getStr("modelResult");

        FeedbackEntity feedbackEntity = new FeedbackEntity();
        feedbackEntity.setId(feedbackCommand.getSerialNumber());
        feedbackEntity.setAccount(feedbackJo.getStr(ColumnConstants.USER_ACCOUNT_COLUMN));
        if (CharSequenceUtil.isNotBlank(uuid)) {
            feedbackEntity.setUserType(UserTypeEnum.PLUG_IN);
        } else {
            feedbackEntity.setUserType(UserTypeEnum.PLATFORM);
        }
        feedbackEntity.setSuggest(suggest);
        feedbackEntity.setModelAbility(changeAbility(ability));
        feedbackEntity.setParams(params);
        feedbackEntity.setHeaders(headers);
        feedbackEntity.setModelResult(modelResult);

        FeedbackAggregateRoot feedbackAggregateRoot = new FeedbackAggregateRoot.Builder(feedbackRepository,
                applicationEventPublisher).build();
        feedbackAggregateRoot.feedbackSuggest(feedbackEntity);
    }

    /**
     * 分页查询用户反馈
     * @param pageFeedbackCommand 查询参数
     * @return 分页数据
     */
    public Page<FeedbackQueryDTO> pageFeedback(PageFeedbackCommand pageFeedbackCommand) {
        Page<FeedbackQueryDTO> data = feedbackRepository.pageFeedback(pageFeedbackCommand);
        if (CollUtil.isNotEmpty(data.getRecords())) {
            data.getRecords().forEach(rec -> {
                rec.setFeedbackType(FeedbackEnum.convert2Desc(rec.getFeedbackType()));
                rec.setModelAbility(ModelAbilityEnum.convert2Desc(rec.getModelAbility()));
            });
        }

        return data;
    }

    /**
     * 查看用户反馈的提问和回答
     * @param id 反馈id
     * @return 查询结果
     */
    public FeedbackQADTO getFeedbackQA(String id) {
        FeedbackEntity entity = feedbackRepository.getFeedbackQAById(id);
        FeedbackQADTO feedbackQADTO = new FeedbackQADTO();
        if (Objects.nonNull(entity)) {
            feedbackQADTO.setQuestion(entity.getParams());
            feedbackQADTO.setAnswer(entity.getModelResult());
        }

        return feedbackQADTO;
    }

    /**
     * 查看用户反馈上文
     * @param id 反馈id
     * @return 查询结果
     */
    public String getFeedbackHistory(String id) {
        String param = feedbackRepository.getParams(id);
        if (CharSequenceUtil.isBlank(param) || !JSONUtil.isTypeJSONObject(param)) {
            return null;
        }
        JSONObject paramJo = JSONUtil.parseObj(param);
        return paramJo.getStr("history");
    }

    /**
     * 导出用户反馈
     * @param pageFeedbackCommand 查询参数
     */
    public void exportFeedback(PageFeedbackCommand pageFeedbackCommand, HttpServletResponse response) {
        if (Objects.isNull(pageFeedbackCommand.getStartNo())) {
            pageFeedbackCommand.setStartNo(0L);
            // 最多导出1000条数据
            pageFeedbackCommand.setExportNo(1000L);
        } else {
            Long exportNo = pageFeedbackCommand.getExportNo();
            if (Objects.isNull(exportNo)) {
                pageFeedbackCommand.setExportNo(1000L);
            }
        }
        List<FeedbackExportDTO> data = feedbackRepository.exportFeedback(pageFeedbackCommand);
        if (CollUtil.isEmpty(data)) {
            throw new OperationNotAllowedException("没有数据导出");
        }

        int index = 1;
        for (FeedbackExportDTO item : data) {
            item.setSort(index++);
            item.setFeedbackType(FeedbackEnum.convert2Desc(item.getFeedbackType()));
            item.setModelAbility(ModelAbilityEnum.convert2Desc(item.getModelAbility()));
            String params = item.getParams();
            String modelResult = item.getModelResult();

            JSONObject qaJo = new JSONObject();
            qaJo.set("question", params);
            qaJo.set("answer", modelResult);
            item.setQa(qaJo.toString());
            if (CharSequenceUtil.isNotBlank(params) && JSONUtil.isTypeJSONObject(params)) {
                JSONObject paramJo = JSONUtil.parseObj(params);
                item.setHistory(paramJo.getStr("history"));
            }
        }
        String resourceLocation = "classpath:templates/exportFeedbackTemplate.xlsx";
        ExcelReader reader = ExcelUtil.getReader(ResourceUtil.getStream(resourceLocation));
        ExcelWriter writer = reader.getWriter();
        ServletOutputStream out = null;
        try (writer) {
            writer.addHeaderAlias("sort", "序号");
            writer.addHeaderAlias("account", "账号");
            writer.addHeaderAlias("jobNumber", "工号");
            writer.addHeaderAlias("name", "姓名");
            writer.addHeaderAlias("organizationName", "组织机构");
            writer.addHeaderAlias("dutyName", "职位");
            writer.addHeaderAlias("feedbackType", "反馈类型");
            writer.addHeaderAlias("modelAbility", "反馈功能");
            writer.addHeaderAlias("suggest", "反馈建议");
            writer.addHeaderAlias("feedbackTime", "反馈时间");
            writer.addHeaderAlias("qa", "反馈问答");
            writer.addHeaderAlias("history", "上文内容");

            CellStyle datetimeStyle = writer.getWorkbook().createCellStyle();
            CreationHelper createHelper = writer.getWorkbook().getCreationHelper();
            short dateFormat = createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss");
            datetimeStyle.setDataFormat(dateFormat);
            datetimeStyle.setBorderBottom(BorderStyle.THIN);

            writer.setOnlyAlias(true);
            writer.passRows(1);
            writer.write(data, false);
            for (int i = 1; i < index; i++) {
                writer.getCell(9, i).setCellStyle(datetimeStyle);
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=exportFeedback.xlsx");
            out = response.getOutputStream();
            writer.flush(out, true);
        } catch (Exception e) {
            logger.error("导出用户反馈信息出错", e);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR_B0004, "导出用户反馈信息出错");
        } finally {
            if (Objects.nonNull(out)) {
                IoUtil.close(out);
            }
        }
    }

    /**
     * 校验反馈参数
     * @param feedbackCommand 用户参数
     * @param uuid 插件用户uuid
     * @param authorization 平台用户令牌
     * @return 缓存中的反馈信息
     */
    private JSONObject checkParam(FeedbackCommand feedbackCommand, String uuid, String authorization) {
        String serialNumber = feedbackCommand.getSerialNumber();
        String redisFeedbackKey = REDIS_FEEDBACK_PREFIX + serialNumber;
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(redisFeedbackKey))) {
            throw new OperationNotAllowedException("未找到对应记录，可能是时间过长，记录已经删除");
        }
        if (CharSequenceUtil.isBlank(uuid) && CharSequenceUtil.isBlank(authorization)) {
            throw new OperationNotAllowedException("请求头没有相关用户信息");
        }

        // 从流水号获取信息
        String feedbackStr = stringRedisTemplate.opsForValue().get(redisFeedbackKey);
        JSONObject feedbackJo = JSONUtil.parseObj(feedbackStr);
        String feedbackAccount = feedbackJo.getStr(ColumnConstants.USER_ACCOUNT_COLUMN);

        String account = null;
        if (CharSequenceUtil.isNotBlank(uuid)) {
            // 判断uuid和流水号获取的用户账号是否一致
            String userInfo = stringRedisTemplate.opsForValue().get(REDIS_PLUGIN_USER_PREFIX + uuid);
            if (CharSequenceUtil.isBlank(userInfo)) {
                throw new OperationNotAllowedException("没有获取到用户信息");
            }
            account = JSONUtil.parseObj(userInfo).getStr(ColumnConstants.USER_ACCOUNT_COLUMN);
        } else {
            // 平台用户，还要考虑开放出去的api-key
            UserInfoBO userInfoBO = UserThreadLocal.get();
            if (Objects.isNull(userInfoBO) || CharSequenceUtil.isBlank(userInfoBO.getAccount())) {
                throw new OperationNotAllowedException("不是平台用户，不允许反馈");
            }
            account = userInfoBO.getAccount();
        }

        if (CharSequenceUtil.isBlank(account)) {
            throw new OperationNotAllowedException("没有获取到用户信息");
        }
        if (!CharSequenceUtil.equals(feedbackAccount, account)) {
            throw new OperationNotAllowedException("只能反馈当前用户的调用记录");
        }

        return feedbackJo;
    }

    /**
     * 获取模型对应能力的枚举类型
     * @param ability 模型能力
     * @return 模型能力枚举
     */
    private ModelAbilityEnum changeAbility(String ability) {
        ModelAbilityEnum abilityEnum = null;
        for (ModelAbilityEnum aenum: ModelAbilityEnum.values()) {
            if (aenum.name().equalsIgnoreCase(ability) || aenum.getAbilityCode().equalsIgnoreCase(ability)) {
                abilityEnum = aenum;
                break;
            }
        }
        if (Objects.isNull(abilityEnum)) {
            throw new IllegalParameterException("模型能力不正确");
        }

        return abilityEnum;
    }
}
