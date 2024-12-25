package cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.facade.impl;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.commons.ddd.repository.IDataChangeRepository;
import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import cn.com.farben.commons.errorcode.exception.DataUpdateException;
import cn.com.farben.gptcoder.operation.commons.user.enums.PluginTypesEnum;
import cn.com.farben.gptcoder.operation.platform.plugin.version.domain.entity.PluginVersionEntity;
import cn.com.farben.gptcoder.operation.platform.plugin.version.domain.event.PluginVersionChangeEvent;
import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.enums.PluginStatusEnum;
import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.facade.PluginVersionRepository;
import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.mapper.PluginVersionMapper;
import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.po.PluginVersionPO;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.po.table.PluginVersionTableDef.PLUGIN_VERSION;

/**
 *
 * 插件版本仓储实现<br>
 * 创建时间：2023/9/27<br>
 * @author ltg
 *
 */
@Repository
public class PluginVersionRepositoryImpl implements PluginVersionRepository, IDataChangeRepository {
    /** 插件版本DB服务 */
    private final PluginVersionMapper pluginVersionMapper;
    private static final Log logger = LogFactory.get();

    @Override
    public long countWithTypeAndVersion(PluginTypesEnum type, String version) {
        Objects.requireNonNull(type, "类型不能为空");
        Objects.requireNonNull(version, "版本不能为空");
        return QueryChain.of(pluginVersionMapper).from(PLUGIN_VERSION).where(PLUGIN_VERSION.TYPE.eq(type)
                .and(PLUGIN_VERSION.VERSION.eq(version))).count();
    }

    @Override
    public long countPluginById(String id) {
        Objects.requireNonNull(id, "id不能为空");
        return QueryChain.of(pluginVersionMapper).from(PLUGIN_VERSION).where(PLUGIN_VERSION.ID.eq(id)).count();
    }

    @Override
    public void addPluginVersion(PluginVersionEntity entity) {
        PluginVersionPO po = new PluginVersionPO();
        CommonAssemblerUtil.assemblerEntityToPO(entity, po);
        pluginVersionMapper.insertSelectiveWithPk(po);
    }

    @Override
    public List<PluginVersionEntity> listDownloadPlugins() {
        logger.info("查询可供下载的插件信息");
        return CommonAssemblerUtil.assemblerPOListToEntityList(QueryChain.of(pluginVersionMapper).select(PLUGIN_VERSION.ALL_COLUMNS).where(
                PLUGIN_VERSION.STATUS.eq(PluginStatusEnum.RELEASED)
        ).orderBy(PLUGIN_VERSION.CREATE_TIME, false).list(), PluginVersionEntity.class);
    }

    @Override
    public Page<PluginVersionEntity> pagePlugin(PluginStatusEnum status, PluginTypesEnum type, String version, long pageSize, long pageNo) {
        logger.info("分页查询插件信息，status: [{}], type: [{}], version: [{}],pageSize: [{}], pageNo: [{}]", status, type, version, pageSize, pageNo);
        QueryChain<PluginVersionPO> queryChain = QueryChain.of(pluginVersionMapper);
        if (Objects.nonNull(status)) {
            queryChain.where(PLUGIN_VERSION.STATUS.eq(status));
        }
        if (Objects.nonNull(type)) {
            queryChain.where(PLUGIN_VERSION.TYPE.eq(type));
        }
        if (CharSequenceUtil.isNotBlank(version)) {
            queryChain.where(PLUGIN_VERSION.VERSION.like(version));
        }
        Page<PluginVersionPO> page = new Page<>(pageNo, pageSize);
        Page<PluginVersionPO> poPage = queryChain.orderBy(PLUGIN_VERSION.CREATE_TIME.desc()).page(page);

        return CommonAssemblerUtil.assemblerPOPageToEntityPage(poPage, PluginVersionEntity.class);
    }

    @Override
    public PluginVersionEntity getPluginById(String id) {
        logger.info("根据id获取具体插件信息，id: [{}]", id);
        Objects.requireNonNull(id, "id不能为空");
        PluginVersionPO pluginVersionPO = QueryChain.of(pluginVersionMapper)
                .select(PLUGIN_VERSION.ALL_COLUMNS)
                .from(PLUGIN_VERSION)
                .where(PLUGIN_VERSION.ID.eq(id)).one();
        PluginVersionEntity pluginVersionEntity = new PluginVersionEntity();
        CommonAssemblerUtil.assemblerPOToEntity(pluginVersionPO, pluginVersionEntity);
        return pluginVersionEntity;
    }

    @Override
    public void removePluginById(String id) {
        if (CharSequenceUtil.isBlank(id)) {
            return;
        }
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(PLUGIN_VERSION.ID.getName(), id);
        pluginVersionMapper.deleteByQuery(queryWrapper);
    }

    @Override
    public PluginVersionEntity getPluginByTypeAndVersion(PluginTypesEnum type, String version) {
        logger.info("根据类型和版本获取具体插件信息，type: [{}], version: [{}]", type, version);
        Objects.requireNonNull(type, "类型不能为空");
        Objects.requireNonNull(version, "版本不能为空");
        PluginVersionPO pluginVersionPO = QueryChain.of(pluginVersionMapper)
                .select(PLUGIN_VERSION.ALL_COLUMNS)
                .from(PLUGIN_VERSION)
                .where(PLUGIN_VERSION.TYPE.eq(type).and(PLUGIN_VERSION.VERSION.eq(version))).one();
        PluginVersionEntity pluginVersionEntity = new PluginVersionEntity();
        CommonAssemblerUtil.assemblerPOToEntity(pluginVersionPO, pluginVersionEntity);
        return pluginVersionEntity;
    }

    /**
     * 查询所有查询版本的总数
     * @return 总数量
     */
    @Override
    public long countAllPlugin() {
        return QueryChain.of(pluginVersionMapper).from(PLUGIN_VERSION).count();
    }

    @Override
    public List<PluginVersionEntity> listByIds(List<String> pluginIdList) {
        return CommonAssemblerUtil.assemblerPOListToEntityList(QueryChain.of(pluginVersionMapper).select(PLUGIN_VERSION.ALL_COLUMNS).where(
                PLUGIN_VERSION.ID.in(pluginIdList)
        ).list(), PluginVersionEntity.class);
    }

    @Override
    public void onApplicationEvent(@NonNull PluginVersionChangeEvent event) {
        logger.info("监听到事件：[{}]", event);
        Object source = event.getSource();
        if (Objects.isNull(source)) {
            throw new DataUpdateException("无法更新数据，source为空");
        }
        updateEntity((DataChangeRecord)source);
    }

    public PluginVersionRepositoryImpl(PluginVersionMapper pluginVersionMapper) {
        this.pluginVersionMapper = pluginVersionMapper;
    }
}
