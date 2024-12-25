package cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.commons.user.enums.PluginTypesEnum;
import cn.com.farben.gptcoder.operation.platform.plugin.version.domain.entity.PluginVersionEntity;
import cn.com.farben.gptcoder.operation.platform.plugin.version.domain.event.PluginVersionChangeEvent;
import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.enums.PluginStatusEnum;
import com.mybatisflex.core.paginate.Page;
import org.springframework.context.ApplicationListener;

import java.util.List;

/**
 *
 * 插件版本仓储接口<br>
 * 创建时间：2023/9/27<br>
 * @author ltg
 *
 */
public interface PluginVersionRepository extends ApplicationListener<PluginVersionChangeEvent> {
    /**
     * 根据插件类型和版本获取数量
     * @param type 插件类型
     * @param version 插件版本
     * @return 插件数量
     */
    long countWithTypeAndVersion(PluginTypesEnum type, String version);

    /**
     * 根据插件id获取数量
     * @param id 插件id
     * @return 插件数量
     */
    long countPluginById(String id);

    /**
     * 新增插件版本
     * @param entity 插件版本实体
     */
    void addPluginVersion(PluginVersionEntity entity);

    /**
     * 查询可供下载的插件信息
     * @return 插件列表
     */
    List<PluginVersionEntity> listDownloadPlugins();

    /**
     * 分页查询版本信息
     * @param status 插件状态
     * @param type 插件类型
     * @param version 版本号
     * @param pageSize 每页数据量
     * @param page 当前页
     * @return 插件版本列表
     */
    Page<PluginVersionEntity> pagePlugin(PluginStatusEnum status, PluginTypesEnum type, String version, long pageSize, long page);

    /**
     * 根据id获取具体插件信息
     * @param id 主键
     * @return 插件版本实体
     */
    PluginVersionEntity getPluginById(String id);

    /**
     * 根据id删除插件
     * @param id 主键
     */
    void removePluginById(String id);

    /**
     * 根据类型和版本获取具体插件信息
     * @param type 插件类型
     * @param version 插件版本
     * @return 插件版本实体
     */
    PluginVersionEntity getPluginByTypeAndVersion(PluginTypesEnum type, String version);

    /**
     * 查询所有查询版本的总数
     * @return 总数量
     */
    long countAllPlugin();

    /**
     * 根据插件id列表获取对应的插件信息
     * @param pluginIdList 插件id列表
     * @return 插件信息列表
     */
    List<PluginVersionEntity> listByIds(List<String> pluginIdList);
}
