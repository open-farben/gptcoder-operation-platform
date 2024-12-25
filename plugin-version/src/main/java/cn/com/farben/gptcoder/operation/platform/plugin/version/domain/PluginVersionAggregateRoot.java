package cn.com.farben.gptcoder.operation.platform.plugin.version.domain;

import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import cn.com.farben.gptcoder.operation.platform.plugin.version.domain.entity.PluginVersionEntity;
import cn.com.farben.gptcoder.operation.platform.plugin.version.domain.event.PluginVersionChangeEvent;
import cn.com.farben.gptcoder.operation.platform.plugin.version.exception.PluginNotExistsException;
import cn.com.farben.gptcoder.operation.platform.plugin.version.exception.VersionExistsException;
import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.enums.PluginStatusEnum;
import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.facade.PluginVersionRepository;
import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.po.PluginVersionPO;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.query.QueryColumn;
import lombok.Getter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

import static cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.po.table.PluginVersionTableDef.PLUGIN_VERSION;

/**
 *
 * 插件版本聚合根<br>
 * 创建时间：2023/10/7<br>
 * @author ltg
 *
 */
@Getter
public class PluginVersionAggregateRoot {
    private static final Log logger = LogFactory.get();

    /** 插件版本仓储接口 */
    private final PluginVersionRepository pluginVersionRepository;

    /** 事件发布 */
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 新增插件版本
     * @param entity 插件版本实体
     */
    public void addVersion(@NonNull PluginVersionEntity entity) {
        long count = pluginVersionRepository.countWithTypeAndVersion(entity.getType(), entity.getVersion());
        // 版本已存在
        if (count > 0) throw new VersionExistsException("版本已存在");
        pluginVersionRepository.addPluginVersion(entity);
    }

    /**
     * 改变插件版本状态
     * @param id 插件id
     * @param status 状态
     * @param operator 操作者账号
     */
    public void changeStatus(@NonNull String id, @NonNull PluginStatusEnum status, @NonNull String operator) {
        checkPluginExistsWithId(id);

        Map<QueryColumn, Object> valueMap = new HashMap<>();
        Map<String, Object> whereConditions = Map.of(PLUGIN_VERSION.ID.getName(), id);
        valueMap.put(PLUGIN_VERSION.STATUS, status);
        valueMap.put(PLUGIN_VERSION.UPDATE_USER, operator);
        updatePluginVersion(valueMap, whereConditions);
    }

    /**
     * 编辑插件版本
     * @param entity 插件版本实体
     */
    public void modifyPlugin(@NonNull PluginVersionEntity entity) {
        String id = entity.getId();
        checkPluginExistsWithId(id);

        Map<QueryColumn, Object> valueMap = new HashMap<>();
        Map<String, Object> whereConditions = Map.of(PLUGIN_VERSION.ID.getName(), id);
        valueMap.put(PLUGIN_VERSION.DESCRIPTION, CharSequenceUtil.isBlank(entity.getDescription()) ? null : entity.getDescription());
        valueMap.put(PLUGIN_VERSION.IDE_VERSION, entity.getUpdateUser());
        valueMap.put(PLUGIN_VERSION.UPDATE_USER, entity.getIdeVersion());
        updatePluginVersion(valueMap, whereConditions);
    }

    /**
     * 删除插件
     * @param id 插件id
     */
    public void removePluginById(String id) {
        logger.info("删除插件，id：[{}]", id);
        // 物理删除
        pluginVersionRepository.removePluginById(id);
    }

    private void updatePluginVersion(Map<QueryColumn, Object> valueMap, Map<String, Object> whereConditions) {
        applicationEventPublisher.publishEvent(new PluginVersionChangeEvent(new DataChangeRecord<>(PluginVersionPO.class, valueMap,
                null, whereConditions, null, null)));
    }

    private void checkPluginExistsWithId(String id) {
        long count = pluginVersionRepository.countPluginById(id);
        if (count <= 0) {
            // 数据不存在
            logger.error("数据[{}]不存在", id);
            throw new PluginNotExistsException("数据不存在");
        }
    }

    public static class Builder {
        /** 插件版本仓储接口 */
        private final PluginVersionRepository pluginVersionRepository;

        /** 事件发布 */
        private ApplicationEventPublisher applicationEventPublisher;

        public Builder(PluginVersionRepository pluginVersionRepository) {
            this.pluginVersionRepository = pluginVersionRepository;
        }

        public PluginVersionAggregateRoot.Builder applicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
            this.applicationEventPublisher = applicationEventPublisher;
            return this;
        }

        public PluginVersionAggregateRoot build() {
            return new PluginVersionAggregateRoot(this);
        }
    }

    private PluginVersionAggregateRoot(PluginVersionAggregateRoot.Builder builder) {
        this.pluginVersionRepository = builder.pluginVersionRepository;
        this.applicationEventPublisher = builder.applicationEventPublisher;
    }
}
