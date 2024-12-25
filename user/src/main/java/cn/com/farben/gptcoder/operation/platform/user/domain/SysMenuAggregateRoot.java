package cn.com.farben.gptcoder.operation.platform.user.domain;


import cn.com.farben.gptcoder.operation.platform.user.domain.entity.SysMenuEntity;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums.UseFlagEnum;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.SysMenuRepository;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 *
 * 系统菜单聚合根
 * @author wuanhui
 *
 */
@Getter
public class SysMenuAggregateRoot {

    private final SysMenuRepository sysMenuRepository;

    /**
     * 新增菜单
     * @param entity 机构实体
     * @return 操作结果
     */
    public boolean addMenu(SysMenuEntity entity) {
        entity.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        if(entity.getStatus() == null) {
            entity.setStatus(UseFlagEnum.NORMAL.getCode());
        }
        if(entity.getHidden() == null) {
            entity.setHidden(UseFlagEnum.NORMAL.getCode());
        }
        if(StringUtils.isBlank(entity.getParentId())) {
            entity.setParentId("0");
        }
        return sysMenuRepository.addMenu(entity);
    }

    /**
     * 修改菜单
     * @param entity 机构实体
     * @return 操作结果
     */
    public boolean editMenu(SysMenuEntity entity) {
        if(StringUtils.isBlank(entity.getParentId())) {
            entity.setParentId("0");
        }
        return sysMenuRepository.editMenu(entity);
    }

    /**
     * 删除指定ID菜单
     * @param id 菜单ID
     * @return 操作结果
     */
    public boolean deleteMenu(String id) {
        return sysMenuRepository.deleteMenu(id);
    }


    public static class Builder {

        private final SysMenuRepository sysMenuRepository;

        public Builder(SysMenuRepository sysMenuRepository) {
            this.sysMenuRepository = sysMenuRepository;
        }

        public SysMenuAggregateRoot build() {
            return new SysMenuAggregateRoot(this);
        }
    }

    private SysMenuAggregateRoot(SysMenuAggregateRoot.Builder builder) {
        this.sysMenuRepository = builder.sysMenuRepository;
    }
}
