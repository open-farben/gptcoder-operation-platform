package cn.com.farben.commons.file.domain;

import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.DataUpdateException;
import cn.com.farben.commons.errorcode.exception.IllegalParameterException;
import cn.com.farben.commons.file.domain.entity.FileInfoEntity;
import cn.com.farben.commons.file.domain.event.FileInfoChangeEvent;
import cn.com.farben.commons.file.domain.event.FileInfoEvent;
import cn.com.farben.commons.file.exception.FileUploadException;
import cn.com.farben.commons.file.infrastructure.FileInfoConstants;
import cn.com.farben.commons.file.infrastructure.enums.FileChangeTypeEnum;
import cn.com.farben.commons.file.infrastructure.enums.FileUseTypeEnum;
import cn.com.farben.commons.file.infrastructure.repository.facade.FileInfoRepository;
import cn.com.farben.commons.file.infrastructure.repository.po.FileInfoPO;
import cn.com.farben.commons.file.vo.DescendantVO;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.SqlOperators;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.com.farben.commons.file.infrastructure.FileInfoConstants.CODE_LIST;
import static cn.com.farben.commons.file.infrastructure.repository.po.table.FileInfoTableDef.FILE_INFO;

/**
 * 文件信息聚合根
 */
@Getter
public class FileInfoAggregateRoot {
    private static final Log logger = LogFactory.get();

    /** 文件信息仓储接口 */
    private final FileInfoRepository fileInfoRepository;

    /** 事件发布 */
    private final ApplicationEventPublisher applicationEventPublisher;

    private static final String FILE_EXISTS_MESSAGE = "文件已存在！";

    /**
     * 创建文件夹
     * @param folderName 文件夹名
     * @param useType 使用类型
     * @param parentPath 上级目录
     * @param isRoot 是否是根目录
     * @param operator 操作人
     * @return 文件id
     */
    public String createFolder(String folderName, FileUseTypeEnum useType, String parentPath, boolean isRoot, String operator) {
        checkFileName(folderName);
        checkPath(parentPath);
        checkOperator(operator);
        // 存储文件
        parentPath = parentPath.replace(StrPool.BACKSLASH, StrPool.SLASH);
        parentPath = FileUtil.normalize(parentPath);
        String totalPath = parentPath + StrPool.SLASH + folderName;
        totalPath = FileUtil.normalize(totalPath);
        checkPath(totalPath);
        long count = fileInfoRepository.countByPath(totalPath);
        if (count > 0){
            logger.error(FileInfoConstants.FILE_EXISTS_MESSAGE, totalPath);
            throw new FileUploadException(FILE_EXISTS_MESSAGE);
        }
        String parentId = fileInfoRepository.getIdByPath(parentPath);
        if (!isRoot && (CharSequenceUtil.isBlank(parentId) || !FileUtil.isDirectory(parentPath))) {
            logger.error("上级目录[{}]不存在", parentPath);
            throw new FileUploadException("上级目录不存在！");
        }
        String id = IdUtil.objectId();
        // 入库
        FileInfoEntity fileInfoEntity = new FileInfoEntity();
        fileInfoEntity.setId(id);
        fileInfoEntity.setFileName(folderName);
        fileInfoEntity.setFileSize(0L);
        fileInfoEntity.setFolder((byte) 1);
        fileInfoEntity.setUseType(useType);
        fileInfoEntity.setPath(totalPath);
        if (CharSequenceUtil.isNotBlank(parentId)) {
            fileInfoEntity.setParentId(parentId);
            FileInfoEntity parentEntity = fileInfoRepository.getById(parentId);
            // 有root id，跟随父目录的root id，否则将父目录的id也存储为自己的root id
            String rootId = CharSequenceUtil.isBlank(parentEntity.getRootId()) ? parentId : parentEntity.getRootId();
            fileInfoEntity.setRootId(rootId);
        }
        fileInfoEntity.setCreateUser(operator);
        fileInfoEntity.setUpdateUser(operator);
        fileInfoRepository.addFile(fileInfoEntity);

        File file = new File(totalPath);
        if (FileUtil.exist(file) && FileUtil.isFile(file)) {
            logger.error("无法创建目录，已有相同文件[{}]", totalPath);
            throw new FileUploadException(CharSequenceUtil.format("无法创建目录，已有相同文件[{}]", folderName));
        }
        boolean flg = FileUtil.mkdirsSafely(file, FileInfoConstants.FILE_HANDLE_TRY_COUNT, FileInfoConstants.FILE_HANDLE_TRY_TIME);
        if (!flg) {
            logger.error("创建目录[{}]失败", totalPath);
            throw new FileUploadException("创建目录失败！");
        }
        // 发布事件
        FileInfoEvent fileInfoEvent = new FileInfoEvent(fileInfoEntity, FileChangeTypeEnum.CREATE);
        applicationEventPublisher.publishEvent(fileInfoEvent);

        return id;
    }

    /**
     * 新增文件信息
     * @param multipartFile 上传的文件
     * @param fileName 文件名
     * @param useType 使用类型
     * @param path 存储路径
     * @param operator 操作员
     * @return 文件id
     */
    public String addFileInfo(@NonNull MultipartFile multipartFile, String fileName, FileUseTypeEnum useType, String path, String operator) {
        checkFileName(fileName);
        checkPath(path);
        checkOperator(operator);
        String parentId = getParentId(path);
        String storePath = getStorePath(fileName, path);
        File storeFile = new File(storePath);

        try {
            FileUtil.mkParentDirs(storeFile);
            multipartFile.transferTo(storeFile);
        } catch (Exception e) {
            logger.error(FileInfoConstants.STORE_FILE_FAIL_MESSAGE, e);
            throw new FileUploadException(FileInfoConstants.STORE_FILE_FAIL_MESSAGE);
        }
        return addFile(storeFile, storePath, useType, parentId, operator);
    }

    /**
     * 新增文件信息
     * @param fileName 文件名
     * @param useType 使用类型
     * @param path 存储路径
     * @param operator 操作员
     * @return 文件id
     */
    public String addFileInfo(String fileName, FileUseTypeEnum useType, String path, String operator) {
        checkFileName(fileName);
        checkPath(path);
        checkOperator(operator);
        String parentId = getParentId(path);
        String storePath = getStorePath(fileName, path);
        File storeFile = new File(storePath);
        return addFile(storeFile, storePath, useType, parentId, operator);
    }

    /**
     * 根据id删除文件信息
     * @param id 文件id
     */
    public DescendantVO removeById(String id) {
        DescendantVO descendantVO = new DescendantVO();
        FileInfoEntity infoEntity = fileInfoRepository.getById(id);
        if (Objects.isNull(infoEntity) || CharSequenceUtil.isBlank(infoEntity.getId())) {
            return descendantVO;
        }
        if (1 == infoEntity.getFolder()) {
            return forceRemoveFolder(id);
        } else {
            descendantVO.setEntity(infoEntity);
            fileInfoRepository.removeById(id);
            if (CharSequenceUtil.isNotBlank(infoEntity.getParentId())) {
                addParentSize(infoEntity.getParentId(), -infoEntity.getFileSize());
            }
            return descendantVO;
        }
    }

    /**
     * 替换指定id的文件，并指定文件名
     * @param multipartFile 用户上传的文件
     * @param fileId 文件id
     * @param fileName 文件名
     * @param operator 操作人员
     */
    public void replaceFile(@NonNull MultipartFile multipartFile, String fileId, String fileName, String operator) {
        checkFileName(fileName);
        if (CharSequenceUtil.isBlank(fileId)) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0410, "文件id不能为空");
        }
        FileInfoEntity fileInfoEntity = fileInfoRepository.getById(fileId);
        if (Objects.isNull(fileInfoEntity) || CharSequenceUtil.isBlank(fileInfoEntity.getId())) {
            throw new DataUpdateException("数据不存在");
        }
        checkOperator(operator);
        String path = fileInfoEntity.getPath();
        String oldName = fileInfoEntity.getFileName();
        String newPath = path.replace(oldName, fileName);
        if (!path.endsWith(newPath)) {
            if (newPath.length() > FileInfoConstants.FILE_PATH_MAX_LENGTH) {
                throw new FileUploadException(CharSequenceUtil.format("新存储路径超过长度{}", FileInfoConstants.FILE_PATH_MAX_LENGTH));
            }
            // 更改了文件名，检查新的文件名是否被使用
            long count = fileInfoRepository.countByPath(fileId, newPath);
            if (count > 0){
                logger.error(FileInfoConstants.FILE_EXISTS_MESSAGE, newPath);
                throw new FileUploadException(CharSequenceUtil.format(FileInfoConstants.FILE_EXISTS_MESSAGE, fileName));
            }
        }
        File storeFile = new File(newPath);
        try {
            multipartFile.transferTo(storeFile);
        } catch (IOException e) {
            logger.error(FileInfoConstants.STORE_FILE_FAIL_MESSAGE, e);
            throw new FileUploadException(FileInfoConstants.STORE_FILE_FAIL_MESSAGE);
        }
        String type = FileUtil.getType(storeFile);
        checkType(type);
        long newSize = FileUtil.size(storeFile);
        long changeSize = newSize - fileInfoEntity.getFileSize();

        // 更新字段map
        Map<QueryColumn, Object> valueMap = new HashMap<>();
        valueMap.put(FILE_INFO.TYPE, type);
        valueMap.put(FILE_INFO.FILE_NAME, fileName);
        valueMap.put(FILE_INFO.FILE_SIZE, newSize);
        valueMap.put(FILE_INFO.PATH, newPath);
        valueMap.put(FILE_INFO.UPDATE_USER, operator);
        // 更新条件map
        Map<String, Object> whereConditions = new HashMap<>();
        whereConditions.put(FILE_INFO.ID.getName(), fileId);

        applicationEventPublisher.publishEvent(new FileInfoChangeEvent(new DataChangeRecord<>(FileInfoPO.class, valueMap,
                null, whereConditions, SqlOperators.empty(), null)));
        if (CharSequenceUtil.isNotBlank(fileInfoEntity.getParentId()) && changeSize != 0L) {
            addParentSize(fileInfoEntity.getParentId(), changeSize);
        }
        if (!oldName.equals(fileName)) {
            // 更改了文件名，删除旧文件
            FileUtil.del(path);
        }
        // 发布事件
        FileInfoEvent fileInfoEvent = new FileInfoEvent(fileInfoRepository.getById(fileId), FileChangeTypeEnum.CHANGE);
        applicationEventPublisher.publishEvent(fileInfoEvent);
    }

    /**
     * 上传多个文件到指定路径
     * @param files 用户上传的文件数组
     * @param useType 使用类型
     * @param path 存储路径
     * @param operator 操作员
     * @return 文件id
     */
    public Map<String, String> addFileList(@NonNull MultipartFile[] files, FileUseTypeEnum useType, String path, String operator) {
        checkPath(path);
        checkOperator(operator);
        path = path.replace(StrPool.BACKSLASH, StrPool.SLASH);
        path = FileUtil.normalize(path);
        String parentId = fileInfoRepository.getIdByPath(path);
        if (!path.endsWith(StrPool.SLASH)) {
            path += StrPool.SLASH;
        }
        if (!FileUtil.isDirectory(path)) {
            String message = "指定目录有误";
            logger.error(message);
            throw new FileUploadException(message);
        }
        FileInfoEntity parentEntity = fileInfoRepository.getById(parentId);
        // 有root id，跟随父目录的root id，否则将父目录的id也存储为自己的root id
        String rootId = CharSequenceUtil.isBlank(parentEntity.getRootId()) ? parentId : parentEntity.getRootId();
        Map<String, String> idMap = new HashMap<>();
        List<FileInfoEntity> entityList = new ArrayList<>(files.length);
        long totalSize = 0L;
        for (MultipartFile file : files) {
            FileInfoEntity fileInfoEntity = new FileInfoEntity();

            String id = IdUtil.objectId();
            String fileName = file.getOriginalFilename();
            if (idMap.containsKey(fileName)) {
                String message = CharSequenceUtil.format("文件[{}]重复", fileName);
                logger.error(message);
                throw new FileUploadException(message);
            }
            String storePath = path + fileName;
            File storeFile = new File(storePath);
            // 数据库已有记录，不允许直接通过新增覆盖
            long count = fileInfoRepository.countByPath(storePath);
            if (count > 0){
                logger.error(FileInfoConstants.FILE_EXISTS_MESSAGE, storePath);
                throw new FileUploadException(CharSequenceUtil.format("文件[{}]已存在！", fileName));
            }
            try {
                file.transferTo(storeFile);
            } catch (IOException e) {
                logger.error(FileInfoConstants.STORE_FILE_FAIL_MESSAGE, e);
                throw new FileUploadException(FileInfoConstants.STORE_FILE_FAIL_MESSAGE);
            }
            long size = FileUtil.size(storeFile);
            totalSize += size;
            String type = FileUtil.getType(storeFile);
            checkType(type);

            idMap.put(fileName, id);
            fileInfoEntity.setId(id);
            fileInfoEntity.setFileName(fileName);
            fileInfoEntity.setFileSize(size);
            fileInfoEntity.setFolder((byte) 0);
            fileInfoEntity.setUseType(useType);
            fileInfoEntity.setPath(storePath);
            fileInfoEntity.setType(type);
            if (CharSequenceUtil.isNotBlank(parentId)) {
                // 父id存在
                fileInfoEntity.setParentId(parentId);
                fileInfoEntity.setRootId(rootId);
            }
            fileInfoEntity.setCreateUser(operator);
            fileInfoEntity.setUpdateUser(operator);

            entityList.add(fileInfoEntity);
        }

        // 入库
        fileInfoRepository.addFiles(entityList);
        if (CharSequenceUtil.isNotBlank(parentId)) {
            // 增加父id记录的存储容量
            addParentSize(parentId, totalSize);
        }
        for (FileInfoEntity fileInfoEntity : entityList) {
            // 发布事件
            FileInfoEvent fileInfoEvent = new FileInfoEvent(fileInfoEntity, FileChangeTypeEnum.CREATE);
            applicationEventPublisher.publishEvent(fileInfoEvent);
        }

        return idMap;
    }

    /**
     * 修改文件夹名称
     * @param id 文件信息id
     * @param name 新名称
     * @param operator 操作人员
     */
    public void changeFolderName(String id, String name, String operator) {
        checkFileName(name);
        checkOperator(operator);
        if (CharSequenceUtil.isBlank(id)) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0410, "文件id不能为空");
        }
        FileInfoEntity fileInfoEntity = fileInfoRepository.getById(id);
        if (Objects.isNull(fileInfoEntity) || CharSequenceUtil.isBlank(fileInfoEntity.getId())) {
            throw new DataUpdateException("数据不存在");
        }
        if (fileInfoEntity.getFolder() != 1) {
            throw new DataUpdateException("不是文件夹，不允许更改名称");
        }
        if (name.equals(fileInfoEntity.getFileName())) {
            throw new DataUpdateException("名称未改变");
        }
        String path = fileInfoEntity.getPath();
        File file = new File(path);
        if (!FileUtil.isDirectory(file)) {
            throw new DataUpdateException("不是文件夹，不允许更改名称");
        }
        String newPath = path.replace(fileInfoEntity.getFileName(), name);
        long count = fileInfoRepository.countByPath(newPath);
        if (count > 0 || FileUtil.exist(newPath)) {
            throw new DataUpdateException("新路径已存在");
        }

        // 更新字段map
        Map<QueryColumn, Object> valueMap = new HashMap<>();
        valueMap.put(FILE_INFO.FILE_NAME, name);
        valueMap.put(FILE_INFO.PATH, newPath);
        valueMap.put(FILE_INFO.UPDATE_USER, operator);
        // 更新条件map
        Map<String, Object> whereConditions = new HashMap<>();
        whereConditions.put(FILE_INFO.ID.getName(), id);

        applicationEventPublisher.publishEvent(new FileInfoChangeEvent(new DataChangeRecord<>(FileInfoPO.class, valueMap,
                null, whereConditions, SqlOperators.empty(), null)));
        count = fileInfoRepository.countChildren(id);
        if (count > 0) {
            // 所有子文件、子目录的路径都要更新
            Map<QueryColumn, Object> rawValueMap = new HashMap<>();
            rawValueMap.put(FILE_INFO.PATH, CharSequenceUtil.format("REPLACE({},'{}','{}')",
                    FILE_INFO.PATH.getName(), path, newPath));
            QueryCondition condition = FILE_INFO.ID.ne(id).and("SUBSTR(path,1,{}) = {}", path.length() + 1, path);

            applicationEventPublisher.publishEvent(new FileInfoChangeEvent(new DataChangeRecord<>(FileInfoPO.class, null, rawValueMap,
                    null, null, condition)));
        }

        // 修改物理路径
        FileUtil.rename(file, name, false);

        // 发布事件
        FileInfoEvent fileInfoEvent = new FileInfoEvent(fileInfoEntity, FileChangeTypeEnum.CHANGE);
        applicationEventPublisher.publishEvent(fileInfoEvent);
    }

    /**
     * 将临时文件正式转储
     * @param tmpFilePath 临时文件路径
     * @param officialName 正式文件名
     * @param useType 使用类型
     * @param path 存储路径
     * @param operator 操作员
     * @return 文件id
     */
    public String transferTmpFile(String tmpFilePath, String officialName, FileUseTypeEnum useType, String path, String operator) {
        checkFileName(officialName);
        checkPath(path);
        checkOperator(operator);
        String parentId = getParentId(path);
        String storePath = getStorePath(officialName, path);
        File storeFile = new File(storePath);
        if (FileUtil.exist(storeFile)) {
            logger.error(FileInfoConstants.FILE_EXISTS_MESSAGE, FileUtil.getAbsolutePath(storeFile));
            throw new FileUploadException(FILE_EXISTS_MESSAGE);
        }

        try {
            FileUtil.mkParentDirs(storeFile);
            FileUtil.move(new File(tmpFilePath), storeFile, false);
        } catch (Exception e) {
            logger.error(FileInfoConstants.STORE_FILE_FAIL_MESSAGE, e);
            throw new FileUploadException(FileInfoConstants.STORE_FILE_FAIL_MESSAGE);
        }
        return addFileNoParent(storeFile, storePath, useType, parentId, operator);
    }

    /**
     * 以压缩包形式上传文件夹，文件名就是目录名
     * @param multipartFile 压缩包文件
     * @param fileName 文件名
     * @param path 指定解压的目录
     * @param useType 使用类型
     * @param operator 操作员
     * @return 文件信息列表
     */
    public List<FileInfoEntity> uploadZipFile(@NonNull MultipartFile multipartFile, String fileName, String path,
                                              FileUseTypeEnum useType, String operator) {
        checkFileName(fileName);
        checkPath(path);
        checkOperator(operator);
        String parentId = getParentId(path);
        String storePath = getStorePath(fileName, path);
        File temFile = new File(storePath);
        // 只校验顶层目录
        long count = fileInfoRepository.countByPath(storePath.replace(StrPool.DOT + FileInfoConstants.UPLOAD_FOLDER_TYPE, ""));
        if (count > 0) {
            String message = CharSequenceUtil.format("目录[{}]已存在", fileName.replace(StrPool.DOT + FileInfoConstants.UPLOAD_FOLDER_TYPE, ""));
            logger.error(message);
            throw new FileUploadException(message);
        }
        String unZipPath = FileUtil.normalize(path + StrPool.SLASH + IdUtil.objectId());
        if (FileUtil.exist(temFile)) {
            logger.error(FileInfoConstants.FILE_EXISTS_MESSAGE, FileUtil.getAbsolutePath(temFile));
            throw new FileUploadException("同名文件或目录已存在！");
        }
        if (CharSequenceUtil.isBlank(parentId)) {
            logger.error("指定的父目录[{}]不存在", path);
            throw new FileUploadException("指定的父目录不存在！");
        }
        String rootId;
        FileInfoEntity parentEntity = fileInfoRepository.getById(parentId);
        // 有root id，跟随父目录的root id，否则将父目录的id也存储为自己的root id
        rootId = CharSequenceUtil.isBlank(parentEntity.getRootId()) ? parentId : parentEntity.getRootId();

        List<FileInfoEntity> entityList;
        long topSize;
        try {
            multipartFile.transferTo(temFile);
            if (!FileTypeUtil.getType(temFile).equals(FileInfoConstants.UPLOAD_FOLDER_TYPE)) {
                throw new FileUploadException(CharSequenceUtil.format("只能上传[{}]格式的文件", FileInfoConstants.UPLOAD_FOLDER_TYPE));
            }
            File zipFolder = unzipFile(storePath, unZipPath);

            // 复制临时解压目录的文件到指定路径
            entityList = copyTmpUnzipFolder(path, unZipPath, parentId, rootId, useType, operator);
            topSize = FileUtil.size(zipFolder);
        } catch (FileUploadException e) {
            FileUtil.del(temFile.getAbsolutePath().replace(StrPool.DOT + FileInfoConstants.UPLOAD_FOLDER_TYPE, ""));
            throw e;
        } catch (Exception e) {
            FileUtil.del(temFile.getAbsolutePath().replace(StrPool.DOT + FileInfoConstants.UPLOAD_FOLDER_TYPE, ""));
            logger.error(FileInfoConstants.STORE_FILE_FAIL_MESSAGE, e);
            throw new FileUploadException(FileInfoConstants.STORE_FILE_FAIL_MESSAGE);
        } finally {
            logger.info("删除临时文件[{}]和[{}]", temFile.getAbsolutePath(), unZipPath);
            FileUtil.del(temFile);
            FileUtil.del(unZipPath);
        }
        fileInfoRepository.addFiles(entityList);
        addParentSize(parentId, topSize);

        for (FileInfoEntity fileInfoEntity : entityList) {
            // 发布事件
            FileInfoEvent fileInfoEvent = new FileInfoEvent(fileInfoEntity, FileChangeTypeEnum.CREATE);
            applicationEventPublisher.publishEvent(fileInfoEvent);
        }
        return entityList;
    }

    /**
     * 强制删除一个目录
     * @param id 目录id
     */
    public DescendantVO forceRemoveFolder(String id) {
        FileInfoEntity infoEntity = fileInfoRepository.getById(id);
        if (Objects.isNull(infoEntity) || CharSequenceUtil.isBlank(infoEntity.getId())) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0402, CharSequenceUtil.format("目录[{}]不存在", id));
        }
        String path = infoEntity.getPath();
        if (!FileUtil.exist(path)) {
            DescendantVO descendantVO = new DescendantVO();
            descendantVO.setEntity(infoEntity);
            return descendantVO;
        }
        if (1 != infoEntity.getFolder() || !FileUtil.isDirectory(path)) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0402, CharSequenceUtil.format("[{}]不是目录，不允许删除", infoEntity.getFileName()));
        }
        List<FileInfoEntity> descendantList = fileInfoRepository.listDescendant(id);
        removeChildrens(id);
        fileInfoRepository.removeById(id);
        if (CharSequenceUtil.isNotBlank(infoEntity.getParentId())) {
            addParentSize(infoEntity.getParentId(), -infoEntity.getFileSize());
        }
        DescendantVO descendantVO = new DescendantVO();
        descendantVO.setEntity(infoEntity);
        descendantVO.setDescendantList(descendantList);
        return descendantVO;
    }

    /**
     * 以压缩包形式上传代码文件夹，文件名就是目录名
     * @param multipartFile 压缩包文件
     * @param fileName 文件名
     * @param path 指定解压的目录
     * @param useType 使用类型
     * @param operator 操作员
     * @return 文件信息列表
     */
    public List<FileInfoEntity> uploadCodeZip(@NonNull MultipartFile multipartFile, String fileName, String path,
                                              FileUseTypeEnum useType, String operator) {
        checkFileName(fileName);
        checkPath(path);
        checkOperator(operator);
        String parentId = getParentId(path);
        String storePath = getStorePath(fileName, path);
        // 只校验顶层目录
        long count = fileInfoRepository.countByPath(storePath.replace(StrPool.DOT + FileInfoConstants.UPLOAD_FOLDER_TYPE, ""));
        if (count > 0) {
            String message = CharSequenceUtil.format("目录[{}]已存在", fileName.replace(StrPool.DOT + FileInfoConstants.UPLOAD_FOLDER_TYPE, ""));
            logger.error(message);
            throw new FileUploadException(message);
        }
        File temFile = new File(storePath);
        String unZipPath = FileUtil.normalize(path + StrPool.SLASH + IdUtil.objectId());
        if (FileUtil.exist(temFile)) {
            logger.error(FileInfoConstants.FILE_EXISTS_MESSAGE, FileUtil.getAbsolutePath(temFile));
            throw new FileUploadException("同名文件或目录已存在！");
        }
        if (CharSequenceUtil.isBlank(parentId)) {
            logger.error("指定的父目录[{}]不存在", path);
            throw new FileUploadException("指定的父目录不存在！");
        }
        String rootId;
        FileInfoEntity parentEntity = fileInfoRepository.getById(parentId);
        // 有root id，跟随父目录的root id，否则将父目录的id也存储为自己的root id
        rootId = CharSequenceUtil.isBlank(parentEntity.getRootId()) ? parentId : parentEntity.getRootId();

        List<FileInfoEntity> entityList;
        long topSize;
        try {
            multipartFile.transferTo(temFile);
            if (!FileTypeUtil.getType(temFile).equals(FileInfoConstants.UPLOAD_FOLDER_TYPE)) {
                throw new FileUploadException(CharSequenceUtil.format("只能上传[{}]格式的文件", FileInfoConstants.UPLOAD_FOLDER_TYPE));
            }
            unzipFile(storePath, unZipPath);

            // 复制临时解压目录的文件到指定路径
            entityList = copyTmpUnZipCode(path, unZipPath, parentId, rootId, useType, operator);
            if (entityList.stream().noneMatch(entity -> 0 == entity.getFolder())) {
                logger.error("未包含任何代码文件");
                throw new FileUploadException("未包含任何代码文件");
            }
            topSize = FileUtil.size(new File(storePath));
        } catch (FileUploadException e) {
            FileUtil.del(temFile.getAbsolutePath().replace(StrPool.DOT + FileInfoConstants.UPLOAD_FOLDER_TYPE, ""));
            throw e;
        } catch (Exception e) {
            FileUtil.del(temFile.getAbsolutePath().replace(StrPool.DOT + FileInfoConstants.UPLOAD_FOLDER_TYPE, ""));
            logger.error(FileInfoConstants.STORE_FILE_FAIL_MESSAGE, e);
            throw new FileUploadException(FileInfoConstants.STORE_FILE_FAIL_MESSAGE);
        } finally {
            logger.info("删除临时文件[{}]和[{}]", temFile.getAbsolutePath(), unZipPath);
            FileUtil.del(temFile);
            FileUtil.del(unZipPath);
        }
        fileInfoRepository.addFiles(entityList);
        addParentSize(parentId, topSize);

        for (FileInfoEntity fileInfoEntity : entityList) {
            // 发布事件
            FileInfoEvent fileInfoEvent = new FileInfoEvent(fileInfoEntity, FileChangeTypeEnum.CREATE);
            applicationEventPublisher.publishEvent(fileInfoEvent);
        }
        return entityList;
    }

    /**
     * 解压文件
     * @param zipFilePath 压缩文件的路径
     * @param outFileDir 解压到的目录
     */
    private File unzipFile(String zipFilePath, String outFileDir) {
        File zipFolder;
        try {
            zipFolder = ZipUtil.unzip(zipFilePath, outFileDir, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("utf8解压文件出错", e);
            try {
                zipFolder = ZipUtil.unzip(zipFilePath, outFileDir, Charset.forName("gbk"));
            } catch (Exception e2) {
                logger.error("gbk解压文件出错", e);
                throw new FileUploadException("无法解压文件，请确认压缩文件正确，为utf8或gbk编码");
            }
        }

        return zipFolder;
    }

    /**
     * 拷贝临时解压目录的文件
     * @param path 路径
     * @param unZipPath 临时解压路径
     * @param parentId 父目录id
     * @param rootId 根目录id
     * @param useType 使用类型
     * @param operator 操作员
     * @return 文件信息实体
     */
    private List<FileInfoEntity> copyTmpUnzipFolder(String path, String unZipPath, String parentId, String rootId,
                                                    FileUseTypeEnum useType, String operator) {
        List<FileInfoEntity> entityList = new ArrayList<>();
        logger.info("拷贝目录[{}]到目标[{}]", unZipPath, path);
        File[] files = FileUtil.ls(unZipPath);
        for (File file : files) {
            FileUtil.copy(file.getPath(), path, false);
        }
        addTmpUnzipFile(path, unZipPath, parentId, rootId, useType, operator, entityList);
        return entityList;
    }

    /**
     * 将临时解压的目录增加到文件信息实体
     * @param path 路径
     * @param unZipPath 临时解压路径
     * @param parentId 父目录id
     * @param rootId 根目录id
     * @param useType 使用类型
     * @param operator 操作员
     * @param entityList 文件信息实体
     */
    private void addTmpUnzipFile(String path, String unZipPath, String parentId, String rootId,
                                 FileUseTypeEnum useType, String operator, List<FileInfoEntity> entityList) {
        File[] files = FileUtil.ls(unZipPath);
        for (File file : files) {
            byte type = (byte) (FileUtil.isFile(file) ? 0 : 1);
            String name = FileNameUtil.getName(file);
            String filePath = FileUtil.normalize(path + StrPool.SLASH + name);
            String id = IdUtil.objectId();
            FileInfoEntity fileEntity = new FileInfoEntity();
            fileEntity.setId(id);
            fileEntity.setFileName(FileUtil.getName(file));
            fileEntity.setPath(path);
            fileEntity.setParentId(parentId);
            fileEntity.setRootId(rootId);
            fileEntity.setCreateUser(operator);
            fileEntity.setUpdateUser(operator);
            fileEntity.setUseType(useType);
            fileEntity.setPath(filePath);
            fileEntity.setFileSize(FileUtil.size(file));
            if (type == 1) {
                fileEntity.setFolder((byte) 1);
                entityList.add(fileEntity);
                addTmpUnzipFile(filePath, FileUtil.normalize(unZipPath + StrPool.SLASH + name), id, rootId,
                        useType, operator, entityList);
            } else {
                fileEntity.setFolder((byte) 0);
                if (name.contains(StrPool.DOT)) {
                    fileEntity.setType(name.substring(name.lastIndexOf(StrPool.DOT) + 1));
                }
                entityList.add(fileEntity);
            }
        }
    }

    private String getStorePath(String fileName, String path) {
        path = FileUtil.normalize(path);
        if (!path.endsWith(StrPool.SLASH)) {
            path += StrPool.SLASH;
        }
        String storePath = path + fileName;
        // 数据库已有记录，不允许覆盖
        long count = fileInfoRepository.countByPath(storePath);
        if (count > 0){
            logger.error(FileInfoConstants.FILE_EXISTS_MESSAGE, storePath);
            throw new FileUploadException(FILE_EXISTS_MESSAGE);
        }

        return storePath;
    }

    private String getParentId(String path) {
        return fileInfoRepository.getIdByPath(FileUtil.normalize(path));
    }


    private String addFileNoParent(File storeFile, String storePath, FileUseTypeEnum useType, String parentId, String operator) {
        String type = FileUtil.getType(storeFile);
        checkType(type);
        String id = IdUtil.objectId();
        long size = FileUtil.size(storeFile);
        // 入库
        FileInfoEntity fileInfoEntity = new FileInfoEntity();
        fileInfoEntity.setId(id);
        fileInfoEntity.setFileName(FileUtil.getName(storeFile));
        fileInfoEntity.setFileSize(size);
        fileInfoEntity.setFolder((byte) 0);
        fileInfoEntity.setUseType(useType);
        fileInfoEntity.setPath(storePath);
        fileInfoEntity.setType(type);
        fileInfoEntity.setParentId(parentId);
        if (CharSequenceUtil.isNotBlank(parentId)) {
            // 增加父id记录的存储容量
            addParentSize(parentId, size);
            FileInfoEntity parentEntity = fileInfoRepository.getById(parentId);
            // 有root id，跟随父目录的root id，否则将父目录的id也存储为自己的root id
            String rootId = CharSequenceUtil.isBlank(parentEntity.getRootId()) ? parentId : parentEntity.getRootId();
            fileInfoEntity.setRootId(rootId);
        }
        fileInfoEntity.setCreateUser(operator);
        fileInfoEntity.setUpdateUser(operator);
        fileInfoRepository.addFile(fileInfoEntity);
        // 发布事件
        FileInfoEvent fileInfoEvent = new FileInfoEvent(fileInfoEntity, FileChangeTypeEnum.CREATE);
        applicationEventPublisher.publishEvent(fileInfoEvent);

        return id;
    }

    private String addFile(File storeFile, String storePath, FileUseTypeEnum useType, String parentId, String operator) {
        if (CharSequenceUtil.isBlank(parentId)) {
            String message = "父目录不存在";
            logger.error(message);
            throw new FileUploadException(message);
        }

        return addFileNoParent(storeFile, storePath, useType, parentId, operator);
    }

    private void checkFileName(String fileName) {
        if (CharSequenceUtil.isBlank(fileName)) {
            throw new FileUploadException("文件名不能为空");
        }
        if (fileName.length() > FileInfoConstants.FILE_NAME_MAX_LENGTH) {
            throw new FileUploadException(CharSequenceUtil.format("文件名超过长度{}", FileInfoConstants.FILE_NAME_MAX_LENGTH));
        }
        if (!isValidFileName(fileName)) {
            logger.error("非法文件名[{}]", fileName);
            throw new FileUploadException("非法文件名");
        }
    }

    private void checkPath(String path) {
        if (CharSequenceUtil.isBlank(path)) {
            throw new FileUploadException("存储路径不能为空");
        }
        if (path.getBytes(StandardCharsets.UTF_8).length > FileInfoConstants.FILE_PATH_MAX_LENGTH) {
            throw new FileUploadException(CharSequenceUtil.format("存储路径字节超过长度{}", FileInfoConstants.FILE_PATH_MAX_LENGTH));
        }
    }

    private void checkOperator(String operator) {
        if (CharSequenceUtil.isBlank(operator)) {
            throw new FileUploadException("操作员不能为空");
        }
    }

    private void checkType(String type) {
        if (CharSequenceUtil.isBlank(type)) {
            throw new FileUploadException("文件类型不能为空");
        }
        if (type.length() > FileInfoConstants.FILE_TYPE_MAX_LENGTH) {
            throw new FileUploadException(CharSequenceUtil.format("文件类型超过长度{}", FileInfoConstants.FILE_TYPE_MAX_LENGTH));
        }
    }

    /**
     * 更新父文件夹的存储容量
     * @param parentId 父id
     * @param size 增加的容量，为负数则表示减少
     */
    private void addParentSize(String parentId, long size) {
        if (size == 0L || CharSequenceUtil.isBlank(parentId)) {
            return;
        }
        FileInfoEntity infoEntity = fileInfoRepository.getById(parentId);
        if (Objects.isNull(infoEntity) || CharSequenceUtil.isBlank(infoEntity.getId()) || infoEntity.getFolder() == 0) {
            return;
        }
        // 更新数据
        Map<QueryColumn, Object> rawValueMap = new HashMap<>();
        rawValueMap.put(FILE_INFO.FILE_SIZE, CharSequenceUtil.format("file_size = file_size + {}", size));
        // 更新条件map
        Map<String, Object> whereConditions = new HashMap<>();
        whereConditions.put(FILE_INFO.ID.getName(), parentId);
        applicationEventPublisher.publishEvent(new FileInfoChangeEvent(new DataChangeRecord<>(FileInfoPO.class, null,
                rawValueMap, whereConditions, SqlOperators.empty(), null)));

        // 继续更新其父容量
        addParentSize(infoEntity.getParentId(), size);
    }

    /**
     * 删除子文件和子目录
     * @param parentId 父id
     */
    private void removeChildrens(String parentId) {
        List<FileInfoEntity> fileInfoEntityList = fileInfoRepository.listChildrens(parentId);
        if (CharSequenceUtil.isBlank(parentId) || CollUtil.isEmpty(fileInfoEntityList)) {
            return;
        }
        // 删除所有子目录和子文件，不递归
        fileInfoRepository.removeChildrens(parentId);
        fileInfoEntityList = fileInfoEntityList.stream().filter(e -> 1 == e.getFolder()).toList();

        for (FileInfoEntity fileInfoEntity : fileInfoEntityList) {
            // 继续删除子目录
            removeChildrens(fileInfoEntity.getId());
        }
    }

    /**
     * 拷贝临时解压目录的代码文件
     * @param path 路径
     * @param unZipPath 临时解压路径
     * @param parentId 父目录id
     * @param rootId 根目录id
     * @param useType 使用类型
     * @param operator 操作员
     * @return 文件信息实体
     */
    private List<FileInfoEntity> copyTmpUnZipCode(String path, String unZipPath, String parentId, String rootId,
                                                    FileUseTypeEnum useType, String operator) {
        List<FileInfoEntity> entityList = new ArrayList<>();
        logger.info("拷贝代码目录[{}]到目标[{}]", unZipPath, path);
        File[] files = FileUtil.ls(unZipPath);
        for (File file : files) {
            byte isFile = (byte) (FileUtil.isFile(file) ? 0 : 1);
            if (0 == isFile) {
                String fileName = file.getName();
                if (fileName.indexOf(StrPool.DOT) <= 0) {
                    // 没有文件类型
                    logger.warn(FileInfoConstants.NOT_CODE_FILE_MESSAGE, fileName);
                    continue;
                }
                String type = FileUtil.getType(file);
                if (!CODE_LIST.contains(type)) {
                    logger.warn(FileInfoConstants.NOT_CODE_FILE_MESSAGE, fileName);
                    continue;
                }
            }
            FileUtil.copy(file.getPath(), path, false);
        }
        addTmpCodezipFile(path, unZipPath, parentId, rootId, useType, operator, entityList);
        return entityList;
    }

    /**
     * 将临时解压的代码目录增加到文件信息实体
     * @param path 路径
     * @param unZipPath 临时解压路径
     * @param parentId 父目录id
     * @param rootId 根目录id
     * @param useType 使用类型
     * @param operator 操作员
     * @param entityList 文件信息实体
     */
    private void addTmpCodezipFile(String path, String unZipPath, String parentId, String rootId,
                                 FileUseTypeEnum useType, String operator, List<FileInfoEntity> entityList) {
        File[] files = FileUtil.ls(unZipPath);
        for (File file : files) {
            byte type = (byte) (FileUtil.isFile(file) ? 0 : 1);
            String name = FileNameUtil.getName(file);
            String filePath = FileUtil.normalize(path + StrPool.SLASH + name);
            String id = IdUtil.objectId();
            FileInfoEntity fileEntity = new FileInfoEntity();
            fileEntity.setId(id);
            fileEntity.setFileName(FileUtil.getName(file));
            fileEntity.setPath(path);
            fileEntity.setParentId(parentId);
            fileEntity.setRootId(rootId);
            fileEntity.setCreateUser(operator);
            fileEntity.setUpdateUser(operator);
            fileEntity.setUseType(useType);
            fileEntity.setPath(filePath);
            fileEntity.setFileSize(FileUtil.size(file));
            if (type == 1) {
                fileEntity.setFolder((byte) 1);
                entityList.add(fileEntity);
                addTmpCodezipFile(filePath, FileUtil.normalize(unZipPath + StrPool.SLASH + name), id, rootId,
                        useType, operator, entityList);
            } else {
                fileEntity.setFolder((byte) 0);
                if (name.indexOf(StrPool.DOT) > 0) {
                    String fileType = name.substring(name.lastIndexOf(StrPool.DOT) + 1);
                    if (!CODE_LIST.contains(fileType)) {
                        logger.warn(FileInfoConstants.NOT_CODE_FILE_MESSAGE, name);
                        continue;
                    }
                    fileEntity.setType(fileType);
                } else {
                    // 没有文件类型
                    logger.warn(FileInfoConstants.NOT_CODE_FILE_MESSAGE, name);
                    continue;
                }
                entityList.add(fileEntity);
            }
        }
    }

    private static boolean isValidFileName(String fileName) {
        return fileName != null && fileName.length() <= 255;
    }

    public static class Builder {
        /** 文件信息仓储接口 */
        private final FileInfoRepository fileInfoRepository;

        /** 事件发布 */
        private final ApplicationEventPublisher applicationEventPublisher;

        public Builder(FileInfoRepository fileInfoRepository, ApplicationEventPublisher applicationEventPublisher) {
            this.fileInfoRepository = fileInfoRepository;
            this.applicationEventPublisher = applicationEventPublisher;
        }

        public FileInfoAggregateRoot build() {
            return new FileInfoAggregateRoot(this);
        }
    }

    private FileInfoAggregateRoot(Builder builder) {
        this.fileInfoRepository = builder.fileInfoRepository;
        this.applicationEventPublisher = builder.applicationEventPublisher;
    }
}
