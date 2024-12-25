package cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.facade.impl;

import cn.com.farben.commons.ddd.repository.IDataChangeRepository;
import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import cn.com.farben.commons.errorcode.exception.DataUpdateException;
import cn.com.farben.gptcoder.operation.platform.model.domain.event.ModelParameterChangeEvent;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.facade.ModelParameterDetailRepository;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.mapper.ModelParameterDetailMapper;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.query.QueryChain;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Objects;

import static cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.po.table.ModelParameterDetailTableDef.MODEL_PARAMETER_DETAIL;

/**
 *
 * 模型参数详情仓储实现<br>
 * 创建时间：2023/10/24<br>
 * @author ltg
 *
 */
@Repository
public class ModelParameterDetailRepositoryImpl implements ModelParameterDetailRepository, IDataChangeRepository {
    /** 模型参数类型DB服务 */
    private final ModelParameterDetailMapper modelParameterDetailMapper;
    private static final Log logger = LogFactory.get();

    @Override
    public long countByIdAndModelId(String id, String modelId) {
        return QueryChain.of(modelParameterDetailMapper).from(MODEL_PARAMETER_DETAIL)
                .where(MODEL_PARAMETER_DETAIL.ID.eq(id).and(MODEL_PARAMETER_DETAIL.MODEL_ID.eq(modelId))).count();
    }

    @Override
    public void onApplicationEvent(@NonNull ModelParameterChangeEvent event) {
        logger.info("监听到事件：[{}]", event);
        Object source = event.getSource();
        if (Objects.isNull(source)) {
            throw new DataUpdateException("无法更新数据，source为空");
        }
        updateEntity((DataChangeRecord)source);
    }

    public ModelParameterDetailRepositoryImpl(ModelParameterDetailMapper modelParameterDetailMapper) {
        this.modelParameterDetailMapper = modelParameterDetailMapper;
    }
}
