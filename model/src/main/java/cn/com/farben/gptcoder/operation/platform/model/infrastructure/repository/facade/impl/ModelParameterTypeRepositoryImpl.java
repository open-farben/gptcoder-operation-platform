package cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.facade.impl;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.gptcoder.operation.platform.model.domain.entity.ModelParameterTypeEntity;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.facade.ModelParameterTypeRepository;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.mapper.ModelParameterTypeMapper;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.query.QueryChain;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * 模型参数类型仓储实现<br>
 * 创建时间：2023/10/24<br>
 * @author ltg
 *
 */
@Repository
public class ModelParameterTypeRepositoryImpl implements ModelParameterTypeRepository {
    /** 模型参数类型DB服务 */
    private final ModelParameterTypeMapper modelParameterTypeMapper;
    private static final Log logger = LogFactory.get();

    @Override
    public List<ModelParameterTypeEntity> listParameterTypes() {
        logger.info("查询所有参数类型");
        return CommonAssemblerUtil.assemblerPOListToEntityList(QueryChain.of(modelParameterTypeMapper).list(), ModelParameterTypeEntity.class);
    }

    public ModelParameterTypeRepositoryImpl(ModelParameterTypeMapper modelParameterTypeMapper) {
        this.modelParameterTypeMapper = modelParameterTypeMapper;
    }
}
