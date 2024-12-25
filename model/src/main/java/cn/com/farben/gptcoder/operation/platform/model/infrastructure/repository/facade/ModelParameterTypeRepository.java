package cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.platform.model.domain.entity.ModelParameterTypeEntity;

import java.util.List;

/**
 *
 * 模型参数类型仓储接口<br>
 * 创建时间：2023/10/24<br>
 * @author ltg
 *
 */
public interface ModelParameterTypeRepository {
    /**
     * 查询模型参数类型
     * @return 模型参数类型
     */
    List<ModelParameterTypeEntity> listParameterTypes();
}
