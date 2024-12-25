package cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.platform.model.domain.event.ModelParameterChangeEvent;
import org.springframework.context.ApplicationListener;

/**
 *
 * 模型参数详情仓储接口<br>
 * 创建时间：2023/10/24<br>
 * @author ltg
 *
 */
public interface ModelParameterDetailRepository extends ApplicationListener<ModelParameterChangeEvent> {
    /**
     * 根据id和模型id查询数量
     * @param id 参数详情id
     * @param modelId 模型id
     * @return 数量
     */
    long countByIdAndModelId(String id, String modelId);
}
