package cn.com.farben.commons.file.infrastructure.repository.facade.impl;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.commons.ddd.repository.IDataChangeRepository;
import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import cn.com.farben.commons.errorcode.exception.DataUpdateException;
import cn.com.farben.commons.file.domain.entity.FileInfoEntity;
import cn.com.farben.commons.file.domain.event.FileInfoChangeEvent;
import cn.com.farben.commons.file.infrastructure.repository.facade.FileInfoRepository;
import cn.com.farben.commons.file.infrastructure.repository.mapper.FileInfoMapper;
import cn.com.farben.commons.file.infrastructure.repository.po.FileInfoPO;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static cn.com.farben.commons.file.infrastructure.repository.po.table.FileInfoTableDef.FILE_INFO;

/**
 * 文件信息仓储实现类
 */
@Repository
public class FileInfoRepositoryImpl implements FileInfoRepository, IDataChangeRepository {
    /** 插件版本DB服务 */
    private final FileInfoMapper fileInfoMapper;
    private static final Log logger = LogFactory.get();

    @Override
    public void addFile(@NonNull FileInfoEntity fileInfoEntity) {
        logger.info("增加文件信息：[{}]", fileInfoEntity);
        FileInfoPO po = new FileInfoPO();
        CommonAssemblerUtil.assemblerEntityToPO(fileInfoEntity, po);
        fileInfoMapper.insertSelectiveWithPk(po);
    }

    @Override
    public long countByPath(String path) {
        return QueryChain.of(fileInfoMapper).where(FILE_INFO.PATH.eq(path)).count();
    }

    @Override
    public FileInfoEntity getById(String id) {
        FileInfoPO fileInfoPO = QueryChain.of(fileInfoMapper)
                .select(FILE_INFO.ALL_COLUMNS)
                .where(FILE_INFO.ID.eq(id)).one();
        FileInfoEntity fileInfoEntity = new FileInfoEntity();
        CommonAssemblerUtil.assemblerPOToEntity(fileInfoPO, fileInfoEntity);
        return fileInfoEntity;
    }

    @Override
    public List<FileInfoEntity> listFiles(LocalDateTime time) {
        List<FileInfoPO> dataList = QueryChain.of(fileInfoMapper)
                .select(FILE_INFO.ALL_COLUMNS)
                .where(FILE_INFO.UPDATE_TIME.ge(time))
                .list();

        return CommonAssemblerUtil.assemblerPOListToEntityList(dataList, FileInfoEntity.class);
    }

    @Override
    public void removeById(String id) {
        logger.info("删除文件信息：[{}]", id);
        fileInfoMapper.deleteById(id);
    }

    @Override
    public long countByPath(String id, String path) {
        return QueryChain.of(fileInfoMapper).where(FILE_INFO.PATH.eq(path).and(FILE_INFO.ID.ne(id))).count();
    }

    @Override
    public String getIdByPath(String path) {
        return QueryChain.of(fileInfoMapper).select(FILE_INFO.ID).where(FILE_INFO.PATH.eq(path)).oneAs(String.class);
    }

    @Override
    public void addFiles(List<FileInfoEntity> fileList) {
        if (CollUtil.isEmpty(fileList)) {
            return;
        }
        fileInfoMapper.insertBatch(CommonAssemblerUtil.assemblerEntityListToPOList(fileList, FileInfoPO.class));
    }

    @Override
    public List<String> getParentIds(List<String> idList) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select(QueryMethods.ifNull(FILE_INFO.PARENT_ID.getName(), ""));
        queryWrapper.in(FILE_INFO.ID.getName(), idList);

        return fileInfoMapper.selectListByQueryAs(queryWrapper, String.class);
    }

    @Override
    public long countChildren(String id) {
        return QueryChain.of(fileInfoMapper).where(FILE_INFO.PARENT_ID.eq(id).or(FILE_INFO.ROOT_ID.eq(id))).count();
    }

    @Override
    public Page<FileInfoEntity> pageChildrens(String id, String name, long pageSize, long pageNo) {
        QueryChain<FileInfoPO> queryChain = QueryChain.of(fileInfoMapper);
        queryChain.where(FILE_INFO.PARENT_ID.eq(id));
        if (CharSequenceUtil.isNotBlank(name)) {
            queryChain.where(FILE_INFO.FILE_NAME.like(name));
        }
        Page<FileInfoPO> page = new Page<>(pageNo, pageSize);
        Page<FileInfoPO> poPage = queryChain.orderBy(FILE_INFO.FOLDER.desc()).orderBy(FILE_INFO.UPDATE_TIME.desc())
                .orderBy(FILE_INFO.CREATE_TIME.desc()).page(page);

        return CommonAssemblerUtil.assemblerPOPageToEntityPage(poPage, FileInfoEntity.class);
    }

    @Override
    public long countById(String id) {
        return QueryChain.of(fileInfoMapper).where(FILE_INFO.ID.eq(id)).count();
    }

    @Override
    public void removeChildrens(String id) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(FILE_INFO.PARENT_ID.getName(), id);
        fileInfoMapper.deleteByQuery(queryWrapper);
    }

    @Override
    public List<FileInfoEntity> listChildrens(String id) {
        List<FileInfoPO> dataList = QueryChain.of(fileInfoMapper)
                .select(FILE_INFO.ALL_COLUMNS)
                .where(FILE_INFO.PARENT_ID.eq(id))
                .list();

        return CommonAssemblerUtil.assemblerPOListToEntityList(dataList, FileInfoEntity.class);
    }

    @Override
    public long countAbilityWithIds(List<String> idList) {
        return QueryChain.of(fileInfoMapper).where(FILE_INFO.FILE_SIZE.gt(0).and(FILE_INFO.ID.in(idList))).count();
    }

    @Override
    public List<FileInfoEntity> listFiles(List<String> idList) {
        List<FileInfoPO> dataList = QueryChain.of(fileInfoMapper)
                .select(FILE_INFO.ALL_COLUMNS)
                .where(FILE_INFO.ID.in(idList))
                .orderBy(FILE_INFO.FOLDER, false)
                .orderBy(FILE_INFO.PARENT_ID, true)
                .list();

        return CommonAssemblerUtil.assemblerPOListToEntityList(dataList, FileInfoEntity.class);
    }

    @Override
    public long countByPathAndType(String path, byte type) {
        return QueryChain.of(fileInfoMapper).where(FILE_INFO.PATH.eq(path).and(FILE_INFO.FOLDER.eq(type))).count();
    }

    @Override
    public List<FileInfoEntity> listDescendant(String id) {
        List<FileInfoPO> dataList = QueryChain.of(fileInfoMapper)
                .select(FILE_INFO.ALL_COLUMNS)
                .where(FILE_INFO.PARENT_ID.eq(id).or(FILE_INFO.ROOT_ID.eq(id)))
                .orderBy(FILE_INFO.FOLDER, false)
                .list();

        return CommonAssemblerUtil.assemblerPOListToEntityList(dataList, FileInfoEntity.class);
    }

    @Override
    public long countData() {
        return QueryChain.of(fileInfoMapper).count();
    }

    @Override
    public void batchAdd(List<FileInfoEntity> dataList) {
        fileInfoMapper.insertBatch(CommonAssemblerUtil.assemblerEntityListToPOList(dataList, FileInfoPO.class));
    }

    @Override
    public List<FileInfoEntity> listFilesWithPaths(List<String> pathList) {
        List<FileInfoPO> dataList = QueryChain.of(fileInfoMapper)
                .select(FILE_INFO.ALL_COLUMNS)
                .where(FILE_INFO.PATH.in(pathList))
                .list();

        return CommonAssemblerUtil.assemblerPOListToEntityList(dataList, FileInfoEntity.class);
    }

    @Override
    public List<FileInfoEntity> listDescendantFolders(String id) {
        List<FileInfoPO> dataList = QueryChain.of(fileInfoMapper)
                .select(FILE_INFO.ALL_COLUMNS)
                .where(FILE_INFO.FOLDER.eq(1).and(FILE_INFO.PARENT_ID.eq(id).or(FILE_INFO.ROOT_ID.eq(id))))
                .orderBy(FILE_INFO.PARENT_ID, true)
                .list();

        return CommonAssemblerUtil.assemblerPOListToEntityList(dataList, FileInfoEntity.class);
    }

    @Override
    public long countSearchByName(String id, String fileName) {
        return QueryChain.of(fileInfoMapper).where(FILE_INFO.FILE_NAME.like(fileName)
                .and("path like CONCAT((select path from file_info where id = {0}), '%')", id)).count();
    }

    @Override
    public List<FileInfoEntity> searchByName(String id, String fileName) {
        List<FileInfoPO> dataList = QueryChain.of(fileInfoMapper).where(FILE_INFO.FILE_NAME.like(fileName)
                        .and("path like CONCAT((select path from file_info where id = {0}), '%')", id))
                .list();

        return CommonAssemblerUtil.assemblerPOListToEntityList(dataList, FileInfoEntity.class);
    }

    @Override
    public FileInfoEntity getByPath(String path) {
        FileInfoPO fileInfoPO = QueryChain.of(fileInfoMapper)
                .select(FILE_INFO.ALL_COLUMNS)
                .where(FILE_INFO.PATH.eq(path)).one();
        FileInfoEntity fileInfoEntity = new FileInfoEntity();
        CommonAssemblerUtil.assemblerPOToEntity(fileInfoPO, fileInfoEntity);
        return fileInfoEntity;
    }

    @Override
    public void onApplicationEvent(@NonNull FileInfoChangeEvent event) {
        logger.info("监听到事件：[{}]", event);
        Object source = event.getSource();
        if (Objects.isNull(source)) {
            throw new DataUpdateException("无法更新数据，source为空");
        }
        updateEntity((DataChangeRecord)source);
    }

    public FileInfoRepositoryImpl(FileInfoMapper fileInfoMapper) {
        this.fileInfoMapper = fileInfoMapper;
    }
}
