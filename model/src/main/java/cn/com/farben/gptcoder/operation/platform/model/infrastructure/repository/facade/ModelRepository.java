package cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.platform.model.domain.entity.ModelEntity;
import cn.com.farben.gptcoder.operation.platform.model.domain.event.ModelChangeEvent;
import cn.com.farben.gptcoder.operation.platform.model.dto.ModelParameterDetailDTO;
import org.springframework.context.ApplicationListener;

import java.util.List;

/**
 *
 * 模型管理户仓储接口<br>
 * 创建时间：2023/8/28<br>
 * @author ltg
 *
 */
public interface ModelRepository extends ApplicationListener<ModelChangeEvent> {
    /**
     * 查询系统所有模型
     */
    List<ModelEntity> listModels();

    /**
     * 根据id查询模型数量
     * @param id 模型id
     * @return 模型数量
     */
    long countById(String id);

    /**
     * 根据id获取模型
     * @param id 模型id
     * @return 模型实体
     */
    ModelEntity getById(String id);

    /**
     * 查询模型参数详情
     * @param modelId 模型id
     * @return 模型参数详情列表
     */
    List<ModelParameterDetailDTO> listModelParameterDetails(String modelId);
}
