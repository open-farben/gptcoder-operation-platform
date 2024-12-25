package cn.com.farben.commons.file.infrastructure.repository.facade;

import cn.com.farben.commons.file.domain.entity.FileInfoEntity;
import cn.com.farben.commons.file.domain.event.FileInfoChangeEvent;
import com.mybatisflex.core.paginate.Page;
import lombok.NonNull;
import org.springframework.context.ApplicationListener;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * 文件信息仓储接口
 *
 */
public interface FileInfoRepository extends ApplicationListener<FileInfoChangeEvent> {
    /**
     * 增加文件信息
     * @param fileInfoEntity 文件信息实体
     */
    void addFile(@NonNull FileInfoEntity fileInfoEntity);

    /**
     * 根据路径查看文件是否存在
     * @param path 文件全路径
     * @return 文件数量
     */
    long countByPath(String path);

    /**
     * 根据id获取文件信息
     * @param id 文件id
     * @return 文件实体
     */
    FileInfoEntity getById(String id);

    /**
     * 查询更新时间大于等于指定时间的文件路径
     * @param time 查询时间
     * @return 文件路径列表
     */
    List<FileInfoEntity> listFiles(LocalDateTime time);

    /**
     * 根据id删除文件信息
     * @param id 文件id
     */
    void removeById(String id);

    /**
     * 根据路径查看文件是否存在，排除自己
     * @param id 文件id
     * @param path 文件全路径
     * @return 文件数量
     */
    long countByPath(String id, String path);

    /**
     * 根据路径获取文件信息id
     * @param path 文件全路径
     * @return 文件信息id
     */
    String getIdByPath(String path);

    /**
     * 增加多个文件信息
     * @param fileList 文件列表
     */
    void addFiles(List<FileInfoEntity> fileList);

    /**
     * 获取指定id列表的父id，去重
     * @param idList id列表
     * @return 父id列表
     */
    List<String> getParentIds(List<String> idList);

    /**
     * 获取目录下的子文件和目录的数量
     * @param id 目录id
     * @return 子目录和文件的数量
     */
    long countChildren(String id);

    /**
     * 根据id获取下级目录、文件的信息
     * @param id 目录id
     * @param name 子文件或目录名
     * @param pageSize 每页数据量
     * @param pageNo 当前页
     * @return 文件信息列表
     */
    Page<FileInfoEntity> pageChildrens(String id, String name, long pageSize, long pageNo);

    /**
     * 根据id获取数量
     * @param id 文件信息id
     * @return 数量
     */
    long countById(String id);

    /**
     * 删除所有子目录和子文件
     * @param id 目录id
     */
    void removeChildrens(String id);

    /**
     * 根据id获取下级目录、文件的信息
     * @param id 目录id
     * @return 文件信息列表
     */
    List<FileInfoEntity> listChildrens(String id);

    /**
     * 获取指定id列表文件尺寸大于0的数量
     * @param idList id列表
     * @return 尺寸大于0的文件数量
     */
    long countAbilityWithIds(List<String> idList);

    /**
     * 获取指定id列表的文件信息
     * @param idList id列表
     * @return 文件信息列表
     */
    List<FileInfoEntity> listFiles(List<String> idList);

    /**
     * 根据路径和类型获取文件数量
     * @param path 文件路径
     * @param type 类型
     * @return 数量
     */
    long countByPathAndType(String path, byte type);

    /**
     * 查询所有子孙的文件信息
     * @param id 目录id
     * @return 文件信息列表
     */
    List<FileInfoEntity> listDescendant(String id);

    /**
     * 查询文件信息数据
     * @return 数据量
     */
    long countData();

    /**
     * 批量增加文件信息
     * @param dataList 文件信息列表
     */
    void batchAdd(List<FileInfoEntity> dataList);

    /**
     * 根据路径列表获取文件信息
     * @param pathList 路径列表
     * @return 文件信息列表
     */
    List<FileInfoEntity> listFilesWithPaths(List<String> pathList);

    /**
     * 获取所有子孙目录的信息
     * @param id 目录id
     * @return 子孙目录列表
     */
    List<FileInfoEntity> listDescendantFolders(String id);

    /**
     * 计算搜索结果的数量
     * @param id 目录id
     * @param fileName 目录名或文件名
     * @return 匹配的数量
     */
    long countSearchByName(String id, String fileName);

    /**
     * 根据文件名搜索指定目录下的文件信息
     * @param id 目录id
     * @param fileName 目录名或文件名
     * @return 匹配的文件实体
     */
    List<FileInfoEntity> searchByName(String id, String fileName);

    /**
     * 根据路径获取文件信息
     * @param path 文件路径
     * @return 文件实体
     */
    FileInfoEntity getByPath(String path);
}
