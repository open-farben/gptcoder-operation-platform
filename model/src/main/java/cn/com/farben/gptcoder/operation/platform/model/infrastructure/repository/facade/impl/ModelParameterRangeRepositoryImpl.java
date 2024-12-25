package cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.facade.impl;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.gptcoder.operation.commons.model.enums.ModelAbilityEnum;
import cn.com.farben.gptcoder.operation.platform.model.domain.entity.ModelParameterRangeEntity;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.facade.ModelParameterRangeRepository;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.mapper.ModelParameterRangeMapper;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.po.ModelParameterRangePO;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.query.QueryChain;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.po.table.ModelParameterDetailTableDef.MODEL_PARAMETER_DETAIL;
import static cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.po.table.ModelParameterRangeTableDef.MODEL_PARAMETER_RANGE;
import static cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.po.table.ModelParameterTypeTableDef.MODEL_PARAMETER_TYPE;
import static cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.po.table.ModelTableDef.MODEL;

/**
 *
 * 模型参数范围仓储实现<br>
 * 创建时间：2023/8/28<br>
 * @author ltg
 *
 */
@Repository
public class ModelParameterRangeRepositoryImpl implements ModelParameterRangeRepository {
    /** 模型参数范围DB服务 */
    private final ModelParameterRangeMapper modelParameterRangeMapper;
    private static final Log logger = LogFactory.get();

    @Override
    public List<ModelAbilityEnum> listModelAbilitys(String modelId) {
        logger.info("查询模型[{}]所有能力", modelId);
        return QueryChain.of(modelParameterRangeMapper).select(MODEL_PARAMETER_RANGE.MODEL_ABILITY)
                .from(MODEL)
                .leftJoin(MODEL_PARAMETER_DETAIL).on(MODEL.ID.eq(MODEL_PARAMETER_DETAIL.MODEL_ID))
                .leftJoin(MODEL_PARAMETER_RANGE).on(MODEL_PARAMETER_DETAIL.PARAM_RANGE_ID.eq(MODEL_PARAMETER_RANGE.ID))
                .leftJoin(MODEL_PARAMETER_TYPE).on(MODEL_PARAMETER_RANGE.PARAMETER_ID.eq(MODEL_PARAMETER_TYPE.ID))
                .where(MODEL.ID.eq(modelId)).listAs(ModelAbilityEnum.class);
    }

    @Override
    public ModelParameterRangeEntity getRangeByDetailId(String detailId) {
        logger.info("根据参数详情id[{}]获取参数范围", detailId);
        ModelParameterRangePO modelParameterRangePO = QueryChain.of(modelParameterRangeMapper)
                .select(MODEL_PARAMETER_RANGE.ALL_COLUMNS)
                .from(MODEL_PARAMETER_RANGE)
                .innerJoin(MODEL_PARAMETER_DETAIL).on(MODEL_PARAMETER_DETAIL.ID.eq(detailId)
                        .and(MODEL_PARAMETER_DETAIL.PARAM_RANGE_ID.eq(MODEL_PARAMETER_RANGE.ID)))
                .one();
        ModelParameterRangeEntity modelParameterRangeEntity = new ModelParameterRangeEntity();
        CommonAssemblerUtil.assemblerPOToEntity(modelParameterRangePO, modelParameterRangeEntity);
        return modelParameterRangeEntity;
    }

    public ModelParameterRangeRepositoryImpl(ModelParameterRangeMapper modelParameterRangeMapper) {
        this.modelParameterRangeMapper = modelParameterRangeMapper;
    }
}
