package cn.com.farben.gptcoder.operation.platform.user.domain;

import cn.com.farben.gptcoder.operation.platform.user.domain.entity.OrganMngEntity;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums.UseFlagEnum;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.OrganMngRepository;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 *
 * 插件用户聚合根
 * @author wuanhui
 *
 */
@Getter
public class OrganAggregateRoot {

    private final OrganMngRepository organMngRepository;

    /**
     * 新增机构
     * @param entity 机构实体
     * @return 操作结果
     */
    public boolean addOrgan(OrganMngEntity entity) {
        entity.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        entity.setLogicDelete(UseFlagEnum.NORMAL.getCode());
        entity.setStatus(UseFlagEnum.NORMAL.getCode());
        return organMngRepository.addOrgan(entity);
    }

    /**
     * 修改机构
     * @param entity 机构实体
     * @return 操作结果
     */
    public boolean editOrgan(OrganMngEntity entity) {
        return organMngRepository.editOrgan(entity);
    }

    /**
     * 删除指定ID机构
     * @param idList 指定ID
     * @return 操作结果
     */
    public boolean deleteOrgan(List<String> idList) {
        return organMngRepository.deleteOrgan(idList);
    }


    public static class Builder {
        private final OrganMngRepository organMngRepository;

        public Builder(OrganMngRepository organMngRepository) {
            this.organMngRepository = organMngRepository;
        }

        public OrganAggregateRoot build() {
            return new OrganAggregateRoot(this);
        }
    }

    private OrganAggregateRoot(OrganAggregateRoot.Builder builder) {
        this.organMngRepository = builder.organMngRepository;
    }
}
