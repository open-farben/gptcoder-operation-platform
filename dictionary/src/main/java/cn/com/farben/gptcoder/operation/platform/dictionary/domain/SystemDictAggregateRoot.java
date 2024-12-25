package cn.com.farben.gptcoder.operation.platform.dictionary.domain;

import cn.com.farben.gptcoder.operation.platform.dictionary.domain.entity.DictEntity;
import cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.facade.DictRepository;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import lombok.Getter;

import java.util.UUID;

/**
 *
 * 系统字典信息聚合根
 * @author wuanhui
 */
@Getter
public class SystemDictAggregateRoot {
    private static final Log logger = LogFactory.get();

    /** 系统字典仓储接口 */
    private final DictRepository dictRepository;

    /**
     * 新增字典
     * @param dictEntity 字典信息
     */
    public boolean addDict(DictEntity dictEntity) {
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        dictEntity.setId(id);
        dictEntity.setDisable((byte) 0);
        return dictRepository.addDict(dictEntity);
    }

    /**
     * 修改字典
     * @param dictEntity 字典信息
     */
    public boolean editDict(DictEntity dictEntity) {
        return dictRepository.updateDict(dictEntity);
    }


    public static class Builder {

        /** 系统字典仓储接口 */
        private final DictRepository dictRepository;

        public Builder(DictRepository dictRepository) {
            this.dictRepository = dictRepository;
        }

        public SystemDictAggregateRoot build() {
            return new SystemDictAggregateRoot(this);
        }
    }

    private SystemDictAggregateRoot(Builder builder) {
        this.dictRepository = builder.dictRepository;
    }
}
