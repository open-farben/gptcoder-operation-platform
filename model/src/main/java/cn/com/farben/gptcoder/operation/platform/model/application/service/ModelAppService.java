package cn.com.farben.gptcoder.operation.platform.model.application.service;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.gptcoder.operation.commons.model.enums.ModelAbilityEnum;
import cn.com.farben.gptcoder.operation.platform.model.command.ChangeModelParamCommand;
import cn.com.farben.gptcoder.operation.platform.model.command.ChangeModelStatusCommand;
import cn.com.farben.gptcoder.operation.platform.model.domain.ModelAggregateRoot;
import cn.com.farben.gptcoder.operation.platform.model.domain.entity.ModelEntity;
import cn.com.farben.gptcoder.operation.platform.model.domain.entity.ModelParameterTypeEntity;
import cn.com.farben.gptcoder.operation.platform.model.dto.ModelAbilityDTO;
import cn.com.farben.gptcoder.operation.platform.model.dto.ModelAbilityGatherDTO;
import cn.com.farben.gptcoder.operation.platform.model.dto.ModelInfoDTO;
import cn.com.farben.gptcoder.operation.platform.model.dto.ModelManagerDTO;
import cn.com.farben.gptcoder.operation.platform.model.dto.ModelParameterTypeDTO;
import cn.com.farben.gptcoder.operation.platform.model.dto.ModelParameterDetailDTO;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.enums.ModelStatusEnum;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.enums.ParamTypeEnum;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.facade.ModelParameterDetailRepository;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.facade.ModelParameterRangeRepository;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.facade.ModelParameterTypeRepository;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.facade.ModelRepository;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * 模型应用服务<br>
 * 创建时间：2023/8/28<br>
 * @author ltg
 *
 */
@Component
public class ModelAppService {
    private static final Log logger = LogFactory.get();

    private final ModelRepository modelRepository;

    private final ModelParameterRangeRepository modelParameterRangeRepository;

    private final ModelParameterTypeRepository modelParameterTypeRepository;

    /** 模型参数详情仓储接口 */
    private final ModelParameterDetailRepository modelParameterDetailRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    /** 事件发布 */
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 查询系统支持的模型
     * @return 系统支持的模型
     */
    public ModelManagerDTO getSupportModels() {
        List<ModelEntity> modelList = modelRepository.listModels();
        if (Objects.isNull(modelList) || CollUtil.isEmpty(modelList)) {
            logger.warn("系统未配置任何模型");
            return new ModelManagerDTO();
        }
        ModelManagerDTO modelDTO = new ModelManagerDTO();

        List<ModelAbilityDTO> abilitys = new ArrayList<>();
        for (ModelAbilityEnum abilityEnum : ModelAbilityEnum.values()) {
            ModelAbilityDTO abilityDTO = new ModelAbilityDTO();
            abilityDTO.setAbilityCode(abilityEnum.getAbilityCode());
            abilityDTO.setDescribe(abilityEnum.getDescribe());
            abilityDTO.setAbilityName(abilityEnum.name());
            abilitys.add(abilityDTO);
        }
        modelDTO.setAbilitys(abilitys);

        List<ModelParameterTypeEntity> parameterTypeEntities = modelParameterTypeRepository.listParameterTypes();
        List<ModelParameterTypeDTO> parameters = CommonAssemblerUtil.assemblerEntityListToDTOList(parameterTypeEntities, ModelParameterTypeDTO.class);
        modelDTO.setParameters(parameters);

        List<ModelInfoDTO> models = new ArrayList<>();

        for (ModelEntity modelEntity : modelList) {
            ModelInfoDTO modelInfoDTO = new ModelInfoDTO();
            List<ModelParameterDetailDTO> parameterDetailList = modelRepository.listModelParameterDetails(modelEntity.getId());
            EnumMap<ModelAbilityEnum, List<ModelParameterDetailDTO>> abilityMap = new EnumMap<>(ModelAbilityEnum.class);
            for (ModelParameterDetailDTO modelParameterDetailDTO : parameterDetailList) {
                ModelAbilityEnum abilityEnum = modelParameterDetailDTO.getModelAbility();
                List<ModelParameterDetailDTO> mapDetailList;
                if (abilityMap.containsKey(abilityEnum)) {
                    mapDetailList = abilityMap.get(abilityEnum);
                } else {
                    mapDetailList = new ArrayList<>();
                }
                mapDetailList.add(modelParameterDetailDTO);
                abilityMap.put(abilityEnum, mapDetailList);
            }

            modelInfoDTO.setId(modelEntity.getId());
            modelInfoDTO.setName(modelEntity.getName());
            modelInfoDTO.setOwn(modelEntity.getOwn());
            modelInfoDTO.setStatus(modelEntity.getStatus());
            List<ModelAbilityGatherDTO> modelAbilitys = new ArrayList<>();
            for (Map.Entry<ModelAbilityEnum, List<ModelParameterDetailDTO>> entry : abilityMap.entrySet()) {
                ModelAbilityGatherDTO abilityGatherDTO = new ModelAbilityGatherDTO();
                ModelAbilityEnum key = entry.getKey();
                abilityGatherDTO.setAbilityName(key.name());
                abilityGatherDTO.setAbilityCode(key.getAbilityCode());
                abilityGatherDTO.setDescribe(key.getDescribe());
                abilityGatherDTO.setParameterDetails(entry.getValue());
                modelAbilitys.add(abilityGatherDTO);
            }
            modelInfoDTO.setModelAbilitys(modelAbilitys);
            models.add(modelInfoDTO);
        }
        modelDTO.setModels(models);

        return modelDTO;
    }

    /**
     * 启用模型
     * @param changeModelStatusCommand 改变模型状态命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void enableModel(ChangeModelStatusCommand changeModelStatusCommand) {
        ModelAggregateRoot modelAggregateRoot = new ModelAggregateRoot.Builder().modelRepository(modelRepository)
                .applicationEventPublisher(applicationEventPublisher).build();
        String modelName = modelAggregateRoot.enableModel(changeModelStatusCommand.getId());

        List<ModelParameterDetailDTO> parameterDetailList = modelRepository.listModelParameterDetails(changeModelStatusCommand.getId());
        // 启用后将模型参数存入redis
        setRedisData(modelName, parameterDetailList);
    }

    /**
     * 禁用模型
     * @param changeModelStatusCommand 改变模型状态命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void disableModel(ChangeModelStatusCommand changeModelStatusCommand) {
        ModelAggregateRoot modelAggregateRoot = new ModelAggregateRoot.Builder().modelRepository(modelRepository)
                .applicationEventPublisher(applicationEventPublisher).build();
        String modelName = modelAggregateRoot.disableModel(changeModelStatusCommand.getId());

        List<ModelAbilityEnum> modelAbilitys = modelParameterRangeRepository.listModelAbilitys(changeModelStatusCommand.getId());
        // 禁用后从redis删除对应的模型参数信息
        for (ModelAbilityEnum modelAbility : modelAbilitys) {
            String redisKey = modelAbility.getRedisKey() + modelName;
            redisTemplate.delete(redisKey.toLowerCase());
        }
    }

    /**
     * 修改模型参数
     * @param changeModelParamCommand 修改模型参数命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void modifyModelParam(ChangeModelParamCommand changeModelParamCommand) {
        ModelAggregateRoot modelAggregateRoot = new ModelAggregateRoot.Builder().modelRepository(modelRepository)
                .modelParameterRepository(modelParameterRangeRepository).modelParameterDetailRepository(modelParameterDetailRepository)
                .applicationEventPublisher(applicationEventPublisher).build();
        String modelName = modelAggregateRoot.modifyModelParam(changeModelParamCommand.getId(), changeModelParamCommand.getParams());

        List<ModelParameterDetailDTO> parameterDetailList = modelRepository.listModelParameterDetails(changeModelParamCommand.getId());
        // 将模型参数存入redis
        setRedisData(modelName, parameterDetailList);
    }

    /**
     * 刷新模型缓存
     */
    public void refreshCache() {
        List<ModelEntity> modelList = modelRepository.listModels();
        if (Objects.isNull(modelList) || CollUtil.isEmpty(modelList)) {
            return;
        }
        for (ModelEntity modelEntity : modelList) {
            String modelName = modelEntity.getName();
            if (Objects.isNull(modelEntity.getOwn()) || modelEntity.getOwn().compareTo((byte) 0) == 0 ||
                    ModelStatusEnum.DISABLE == modelEntity.getStatus()) {
                logger.warn("模型[{}]未启用或未拥有，删除redis缓存", modelName);
                // 没有拥有该模型或者模型未启用
                List<ModelAbilityEnum> modelAbilitys = modelParameterRangeRepository.listModelAbilitys(modelEntity.getId());
                for (ModelAbilityEnum modelAbility : modelAbilitys) {
                    String redisKey = modelAbility.getRedisKey() + modelName;
                    redisTemplate.delete(redisKey.toLowerCase());
                }
                continue;
            }

            List<ModelParameterDetailDTO> parameterDetailList = modelRepository.listModelParameterDetails(modelEntity.getId());
            setRedisData(modelName, parameterDetailList);
        }
    }

    /**
     * 设置某个模型的redis缓存数据
     * @param modelName 模型名称
     * @param paramDetailList 参数
     */
    private void setRedisData(String modelName, List<ModelParameterDetailDTO> paramDetailList) {
        Objects.requireNonNull(modelName, "模型名称不能为空");
        if (CollUtil.isEmpty(paramDetailList)) {
            return;
        }
        Map<String, JSONObject> modelAbilityMap = new HashMap<>();
        for (ModelParameterDetailDTO modelParameterDetailDTO : paramDetailList) {
            ModelAbilityEnum modelAbility = modelParameterDetailDTO.getModelAbility();
            String paramName = modelParameterDetailDTO.getParamName();
            String paramValue = modelParameterDetailDTO.getParamValue();
            String defaultValue = modelParameterDetailDTO.getDefaultValue();
            ParamTypeEnum paramType = modelParameterDetailDTO.getParamType();
            String mapKey = modelAbility.getRedisKey() + modelName;
            JSONObject paramJo;
            if (modelAbilityMap.containsKey(mapKey)) {
                paramJo = modelAbilityMap.get(mapKey);
            } else {
                paramJo = new JSONObject();
            }
            String storeValue = CharSequenceUtil.isBlank(paramValue) ? defaultValue : paramValue;
            BigDecimal decimalValue;
            try {
                decimalValue = new BigDecimal(storeValue);
            } catch (NumberFormatException e) {
                paramJo.set(paramName, storeValue);
                logger.warn("参数值[{}]不是数值，不能以数值形式存储到redis", storeValue, e);
                modelAbilityMap.put(mapKey, paramJo);
                continue;
            }
            if (Objects.requireNonNull(paramType) == ParamTypeEnum.FLOAT) {
                paramJo.set(paramName, decimalValue.doubleValue());
            } else if (paramType == ParamTypeEnum.INT) {
                paramJo.set(paramName, decimalValue.intValue());
            }
            modelAbilityMap.put(mapKey, paramJo);
        }
        for (Map.Entry<String, JSONObject> mapEntry : modelAbilityMap.entrySet()) {
            redisTemplate.opsForValue().set(mapEntry.getKey().toLowerCase(), mapEntry.getValue());
        }
    }

    public ModelAppService(ModelRepository modelRepository, ModelParameterRangeRepository modelParameterRangeRepository,
                           ModelParameterTypeRepository modelParameterTypeRepository, ModelParameterDetailRepository modelParameterDetailRepository,
                           RedisTemplate<String, Object> redisTemplate, ApplicationEventPublisher applicationEventPublisher) {
        this.modelRepository = modelRepository;
        this.modelParameterRangeRepository = modelParameterRangeRepository;
        this.modelParameterTypeRepository = modelParameterTypeRepository;
        this.modelParameterDetailRepository = modelParameterDetailRepository;
        this.redisTemplate = redisTemplate;
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
