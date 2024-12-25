package cn.com.farben.gptcoder.operation.platform.model.domain;

import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.DataUpdateException;
import cn.com.farben.commons.errorcode.exception.IllegalParameterException;
import cn.com.farben.gptcoder.operation.platform.model.command.ModelParam;
import cn.com.farben.gptcoder.operation.platform.model.domain.entity.ModelEntity;
import cn.com.farben.gptcoder.operation.platform.model.domain.entity.ModelParameterRangeEntity;
import cn.com.farben.gptcoder.operation.platform.model.domain.event.ModelChangeEvent;
import cn.com.farben.gptcoder.operation.platform.model.domain.event.ModelParameterChangeEvent;
import cn.com.farben.gptcoder.operation.platform.model.exception.ModelNotExistsException;
import cn.com.farben.gptcoder.operation.platform.model.exception.ModelNotOwnException;
import cn.com.farben.gptcoder.operation.platform.model.exception.ModelStatusException;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.enums.ModelStatusEnum;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.enums.ParamContainEnum;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.facade.ModelParameterDetailRepository;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.facade.ModelParameterRangeRepository;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.facade.ModelRepository;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.po.ModelPO;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.po.ModelParameterDetailPO;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.query.QueryColumn;
import lombok.Getter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.po.table.ModelParameterDetailTableDef.MODEL_PARAMETER_DETAIL;
import static cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.po.table.ModelTableDef.MODEL;

/**
 *
 * 模型聚合根<br>
 * 创建时间：2023/8/28<br>
 * @author ltg
 *
 */
@Getter
public class ModelAggregateRoot {
    private static final Log logger = LogFactory.get();

    /** 模型管理仓储接口 */
    private final ModelRepository modelRepository;

    /** 模型参数范围仓储接口 */
    private final ModelParameterRangeRepository modelParameterRangeRepository;

    /** 模型参数详情仓储接口 */
    private final ModelParameterDetailRepository modelParameterDetailRepository;

    /** 事件发布 */
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 启用模型
     * @param id 模型id
     */
    public String enableModel(@NonNull String id) {
        ModelEntity modelEntity = checkModel(id);
        if (ModelStatusEnum.ENABLE == modelEntity.getStatus()) {
            // 已经是启用状态
            logger.error("模型[{}]已经是启用状态", id);
            throw new ModelStatusException("模型已经是启用状态");
        }

        Map<QueryColumn, Object> valueMap = new HashMap<>();
        Map<String, Object> whereConditions = Map.of(MODEL.ID.getName(), id);
        valueMap.put(MODEL.STATUS, ModelStatusEnum.ENABLE);

        updateModel(valueMap, whereConditions);
        return modelEntity.getName();
    }

    /**
     * 禁用模型
     * @param id 模型id
     */
    public String disableModel(@NonNull String id) {
        ModelEntity modelEntity = checkModel(id);
        if (ModelStatusEnum.DISABLE == modelEntity.getStatus()) {
            // 已经是禁用状态
            logger.error("模型[{}]已经是禁用状态", id);
            throw new ModelStatusException("模型已经是禁用状态");
        }

        Map<QueryColumn, Object> valueMap = new HashMap<>();
        Map<String, Object> whereConditions = Map.of(MODEL.ID.getName(), id);
        valueMap.put(MODEL.STATUS, ModelStatusEnum.DISABLE);

        updateModel(valueMap, whereConditions);
        return modelEntity.getName();
    }

    /**
     * 修改模型参数
     * @param id 模型id
     * @param params 模型参数
     * @return 模型名称
     */
    public String modifyModelParam(String id, List<ModelParam> params) {
        ModelEntity modelEntity = checkModel(id);
        if (ModelStatusEnum.DISABLE == modelEntity.getStatus()) {
            // 禁用状态
            logger.error("模型[{}]禁用中，不允许修改参数", id);
            throw new ModelStatusException("模型禁用中，不允许修改参数");
        }

        String modelName = modelEntity.getName();
        // 参数不会很多，因此可以不用批量处理
        for (ModelParam param : params) {
            String paramId = param.getParamId();
            String paramValue = param.getParamValue();
            ModelParameterRangeEntity parameterRangeEntity = modelParameterRangeRepository.getRangeByDetailId(paramId);
            if (Objects.isNull(parameterRangeEntity) || CharSequenceUtil.isBlank(parameterRangeEntity.getId())) {
                logger.error("未获取到对应参数的取值范围", paramId);
                throw new DataUpdateException("未获取到对应参数的取值范围");
            }
            verifyModelParams(paramValue, parameterRangeEntity);
            long count = modelParameterDetailRepository.countByIdAndModelId(paramId, id);
            if (count <= 0) {
                logger.error("参数[{}]和模型[{}]不匹配", paramId, id);
                throw new DataUpdateException("参数和模型不匹配");
            }

            Map<QueryColumn, Object> valueMap = new HashMap<>();
            Map<String, Object> whereConditions = Map.of(MODEL_PARAMETER_DETAIL.ID.getName(), paramId);
            valueMap.put(MODEL_PARAMETER_DETAIL.PARAM_VALUE, paramValue);

            applicationEventPublisher.publishEvent(new ModelParameterChangeEvent(new DataChangeRecord<>(ModelParameterDetailPO.class, valueMap,
                    null, whereConditions, null, null)));
        }
        return modelName;
    }

    private void updateModel(Map<QueryColumn, Object> valueMap, Map<String, Object> whereConditions) {
        applicationEventPublisher.publishEvent(new ModelChangeEvent(new DataChangeRecord<>(ModelPO.class, valueMap,
                null, whereConditions, null, null)));
    }

    /**
     * 检查模型
     * @param id 模型id
     */
    private ModelEntity checkModel(String id) {
        long count = modelRepository.countById(id);
        if (count <= 0) {
            // 模型不存在
            logger.error("模型[{}]不存在", id);
            throw new ModelNotExistsException("模型不存在");
        }

        ModelEntity modelEntity = modelRepository.getById(id);
        if (Objects.isNull(modelEntity.getOwn()) || modelEntity.getOwn().compareTo((byte) 0) == 0) {
            // 没有拥有该模型
            logger.error("没有拥有模型[{}]", id);
            throw new ModelNotOwnException("您还未拥有该模型能力");
        }
        return modelEntity;
    }

    /**
     * 校验模型参数值
     * @param value 参数值
     * @param parameterRangeEntity 模型参数实体
     */
    private void verifyModelParams(@NonNull String value, @NonNull ModelParameterRangeEntity parameterRangeEntity) {
        String minValue = parameterRangeEntity.getMinValue();
        ParamContainEnum minContain = parameterRangeEntity.getMinContain();
        String maxValue = parameterRangeEntity.getMaxValue();
        ParamContainEnum maxContain = parameterRangeEntity.getMaxContain();
        if (CharSequenceUtil.isBlank(value)) {
            return;
        }
        BigDecimal decimalValue;
        try {
            decimalValue = new BigDecimal(value);
        } catch (NumberFormatException e) {
            logger.error("模型参数值[{}]不正确", value, e);
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0402, String.format("模型参数值%s不正确，应该是数值", value));
        }
        BigDecimal decimalMinValue = new BigDecimal(minValue);
        BigDecimal decimalMaxValue = new BigDecimal(maxValue);
        if (decimalValue.compareTo(decimalMinValue) < 0) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0402, String.format("模型参数值%s不正确，不应该小于%s", value, minValue));
        }
        if (decimalValue.compareTo(decimalMaxValue) > 0) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0402, String.format("模型参数值%s不正确，不应该大于%s", value, maxValue));
        }
        if (minContain == ParamContainEnum.EXCLUDE && decimalValue.compareTo(decimalMinValue) == 0) {
            // 不应该包含最小值
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0402, String.format("模型参数值%s不正确，不应该小于等于%s", value, minValue));
        }
        if (maxContain == ParamContainEnum.EXCLUDE && decimalValue.compareTo(decimalMaxValue) == 0) {
            // 不应该包含最大值
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0402, String.format("模型参数值%s不正确，不应该大于等于%s", value, maxValue));
        }
    }

    public static class Builder {

        /** 模型管理仓储接口 */
        private ModelRepository modelRepository;

        /** 模型参数范围仓储接口 */
        private ModelParameterRangeRepository modelParameterRangeRepository;

        /** 模型参数详情仓储接口 */
        private ModelParameterDetailRepository modelParameterDetailRepository;

        /** 事件发布 */
        private ApplicationEventPublisher applicationEventPublisher;

        public ModelAggregateRoot.Builder applicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
            this.applicationEventPublisher = applicationEventPublisher;
            return this;
        }

        public ModelAggregateRoot.Builder modelRepository(ModelRepository modelRepository) {
            this.modelRepository = modelRepository;
            return this;
        }

        public ModelAggregateRoot.Builder modelParameterRepository(ModelParameterRangeRepository modelParameterRangeRepository) {
            this.modelParameterRangeRepository = modelParameterRangeRepository;
            return this;
        }

        public ModelAggregateRoot.Builder modelParameterDetailRepository(ModelParameterDetailRepository modelParameterDetailRepository) {
            this.modelParameterDetailRepository = modelParameterDetailRepository;
            return this;
        }

        public ModelAggregateRoot build() {
            return new ModelAggregateRoot(this);
        }
    }

    private ModelAggregateRoot(ModelAggregateRoot.Builder builder) {
        this.modelRepository = builder.modelRepository;
        this.modelParameterRangeRepository = builder.modelParameterRangeRepository;
        this.modelParameterDetailRepository = builder.modelParameterDetailRepository;
        this.applicationEventPublisher = builder.applicationEventPublisher;
    }
}
