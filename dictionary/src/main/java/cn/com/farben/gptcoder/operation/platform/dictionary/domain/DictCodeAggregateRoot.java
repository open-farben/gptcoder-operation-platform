package cn.com.farben.gptcoder.operation.platform.dictionary.domain;

import cn.com.farben.gptcoder.operation.commons.user.enums.FlagTypeEnum;
import cn.com.farben.gptcoder.operation.platform.dictionary.domain.entity.DictCodeEntity;
import cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.facade.DictCodeRepository;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 *
 * 系统字典映射信息聚合根
 * @author wuanhui
 */
@Getter
public class DictCodeAggregateRoot {
    private static final Log logger = LogFactory.get();

    /** 系统字典仓储接口 */
    private final DictCodeRepository dictCodeRepository;

    /**
     * 新增字典配置信息
     * @param dictEntity 字典信息
     */
    public boolean addDictCode(DictCodeEntity dictEntity) {
        dictEntity.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        dictEntity.setLogicDelete(FlagTypeEnum.ZERO.getCode());
        return dictCodeRepository.addDictCode(dictEntity);
    }

    /**
     * 修改字典配置信息
     * @param dictEntity 字典信息
     */
    public boolean editDictCode(DictCodeEntity dictEntity) {
        return dictCodeRepository.editDictCode(dictEntity);
    }

    /**
     * 修改字典配置信息
     * @param ids 删除的ID
     */
    public boolean deleteDictCode(List<String> ids) {
        return dictCodeRepository.deleteDictCode(ids);
    }

    public static class Builder {

        /** 系统字典仓储接口 */
        private final DictCodeRepository dictCodeRepository;

        public Builder(DictCodeRepository dictCodeRepository) {
            this.dictCodeRepository = dictCodeRepository;
        }

        public DictCodeAggregateRoot build() {
            return new DictCodeAggregateRoot(this);
        }
    }

    private DictCodeAggregateRoot(Builder builder) {
        this.dictCodeRepository = builder.dictCodeRepository;
    }
}
