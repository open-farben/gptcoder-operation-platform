package cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.facade.impl;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.commons.ddd.repository.IDataChangeRepository;
import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import cn.com.farben.commons.errorcode.exception.DataUpdateException;
import cn.com.farben.gptcoder.operation.platform.model.domain.entity.ModelEntity;
import cn.com.farben.gptcoder.operation.platform.model.domain.event.ModelChangeEvent;
import cn.com.farben.gptcoder.operation.platform.model.dto.ModelParameterDetailDTO;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.facade.ModelRepository;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.mapper.ModelMapper;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.po.ModelPO;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.query.QueryChain;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.po.table.ModelParameterDetailTableDef.MODEL_PARAMETER_DETAIL;
import static cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.po.table.ModelParameterRangeTableDef.MODEL_PARAMETER_RANGE;
import static cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.po.table.ModelParameterTypeTableDef.MODEL_PARAMETER_TYPE;
import static cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.po.table.ModelTableDef.MODEL;

/**
 *
 * 模型仓储实现<br>
 * 创建时间：2023/8/28<br>
 * @author ltg
 *
 */
@Repository
public class ModelRepositoryImpl implements ModelRepository, IDataChangeRepository {
    /** 模型DB服务 */
    private final ModelMapper modelMapper;
    private static final Log logger = LogFactory.get();

    @Override
    public List<ModelEntity> listModels() {
        logger.info("查询系统所有模型");

        return CommonAssemblerUtil.assemblerPOListToEntityList(QueryChain.of(modelMapper).list(), ModelEntity.class);
    }

    @Override
    public long countById(String id) {
        return QueryChain.of(modelMapper).from(MODEL).where(MODEL.ID.eq(id)).count();
    }

    @Override
    public ModelEntity getById(String id) {
        ModelPO modelPO = QueryChain.of(modelMapper)
                .select(MODEL.ALL_COLUMNS)
                .from(MODEL)
                .where(MODEL.ID.eq(id)).one();
        ModelEntity modelEntity = new ModelEntity();
        CommonAssemblerUtil.assemblerPOToEntity(modelPO, modelEntity);
        return modelEntity;
    }

    @Override
    public List<ModelParameterDetailDTO> listModelParameterDetails(String modelId) {
        return QueryChain.of(modelMapper)
                .select(MODEL.NAME, MODEL_PARAMETER_DETAIL.ID, MODEL_PARAMETER_RANGE.MODEL_ABILITY, MODEL_PARAMETER_DETAIL.DEFAULT_VALUE,
                        MODEL_PARAMETER_DETAIL.PARAM_VALUE, MODEL_PARAMETER_TYPE.PARAM_TYPE, MODEL_PARAMETER_TYPE.PARAM_NAME,
                        MODEL_PARAMETER_TYPE.PARAM_DESC, MODEL_PARAMETER_RANGE.MIN_VALUE, MODEL_PARAMETER_RANGE.MIN_CONTAIN,
                        MODEL_PARAMETER_RANGE.MAX_VALUE, MODEL_PARAMETER_RANGE.MAX_CONTAIN)
                .from(MODEL)
                .leftJoin(MODEL_PARAMETER_DETAIL).on(MODEL.ID.eq(MODEL_PARAMETER_DETAIL.MODEL_ID))
                .leftJoin(MODEL_PARAMETER_RANGE).on(MODEL_PARAMETER_DETAIL.PARAM_RANGE_ID.eq(MODEL_PARAMETER_RANGE.ID))
                .leftJoin(MODEL_PARAMETER_TYPE).on(MODEL_PARAMETER_RANGE.PARAMETER_ID.eq(MODEL_PARAMETER_TYPE.ID))
                .where(MODEL.ID.eq(modelId))
                .listAs(ModelParameterDetailDTO.class);
    }

    @Override
    public void onApplicationEvent(@NonNull ModelChangeEvent event) {
        logger.info("监听到事件：[{}]", event);
        Object source = event.getSource();
        if (Objects.isNull(source)) {
            throw new DataUpdateException("无法更新数据，source为空");
        }
        updateEntity((DataChangeRecord)source);
    }

    public ModelRepositoryImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
