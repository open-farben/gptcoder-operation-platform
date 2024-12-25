package cn.com.farben.commons.file.application.service;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.IllegalParameterException;
import cn.com.farben.commons.errorcode.exception.OperationNotAllowedException;
import cn.com.farben.commons.file.domain.FileInfoAggregateRoot;
import cn.com.farben.commons.file.domain.entity.FileInfoEntity;
import cn.com.farben.commons.file.domain.event.FileInfoEvent;
import cn.com.farben.commons.file.dto.FileInfoDTO;
import cn.com.farben.commons.file.exception.FileDownloadException;
import cn.com.farben.commons.file.exception.FileUploadException;
import cn.com.farben.commons.file.infrastructure.FileInfoConstants;
import cn.com.farben.commons.file.infrastructure.FileStoreControl;
import cn.com.farben.commons.file.infrastructure.FileSyncInfo;
import cn.com.farben.commons.file.infrastructure.enums.FileChangeTypeEnum;
import cn.com.farben.commons.file.infrastructure.enums.FileSyncTypeEnum;
import cn.com.farben.commons.file.infrastructure.enums.FileUseTypeEnum;
import cn.com.farben.commons.file.infrastructure.repository.facade.FileInfoRepository;
import cn.com.farben.commons.file.infrastructure.tools.FileSyncTool;
import cn.com.farben.commons.file.vo.DescendantVO;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import cn.hutool.core.text.CharSequenceUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipOutputStream;

/**
 * 文件信息应用服务<br>
 * 可以上传文件、查看文件、下载文件、修改文件等<br>
 * 事务要保持一致时，请在调用的方法上加上@Transactional(rollbackFor = Exception.class)
 */
@Component
public class FileInfoAppService implements IFileInfoService {
    private static final Log logger = LogFactory.get();

    /** 文件信息仓储接口 */
    private final FileInfoRepository fileInfoRepository;

    /** 事件发布 */
    private final ApplicationEventPublisher applicationEventPublisher;

    private final FileStoreControl fileStoreControl;

    private final FileSyncTool fileSyncTool;

    private static final String FILE_NAME_EMPTY_MESSAGE = "未获取到文件名";

    private static final String FILE_EMPTY_MESSAGE = "文件不能为空";

    public FileInfoAppService(FileInfoRepository fileInfoRepository, ApplicationEventPublisher applicationEventPublisher, FileStoreControl fileStoreControl, FileSyncTool fileSyncTool) {
        this.fileInfoRepository = fileInfoRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.fileStoreControl = fileStoreControl;
        this.fileSyncTool = fileSyncTool;
    }

        /**
     * 上传文件到指定路径，会根据使用类型自动加上前缀路径
     * @param multipartFile 用户上传的文件
     * @param useType 使用类型
     * @param parentPath 上级目录
     * @param operator 操作人员
     * @return 文件id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String storeFile(@NonNull MultipartFile multipartFile, @NonNull FileUseTypeEnum useType, String parentPath,
                            @NotBlank String operator) {
        checkFile(multipartFile);
        String originalFilename = multipartFile.getOriginalFilename();
        if (CharSequenceUtil.isBlank(originalFilename)) {
            throw new FileUploadException(FILE_NAME_EMPTY_MESSAGE);
        }
        return doStoreFile(multipartFile, originalFilename, useType, parentPath, operator);
    }

    /**
     * 上传文件到指定路径，并指定文件名，会根据使用类型自动加上前缀路径
     * @param multipartFile 用户上传的文件
     * @param fileName 文件名
     * @param useType 使用类型
     * @param parentPath 上级目录
     * @param operator 操作人员
     * @return 文件id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String storeFile(@NonNull MultipartFile multipartFile, @NotBlank String fileName, @NonNull FileUseTypeEnum useType,
                            String parentPath, @NotBlank String operator) {
        logger.info("操作员[{}]上传文件[{}]到指定目录[{}]，用途[{}]", operator, fileName, parentPath, useType);
        return doStoreFile(multipartFile, fileName, useType, parentPath, operator);
    }

    /**
     * 存储文件
     * @param fileName 文件名
     * @param useType 使用类型
     * @param parentPath 上级目录
     * @param operator 操作人员
     * @return 文件id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String storeFile(@NotBlank String fileName, @NonNull FileUseTypeEnum useType,
                            String parentPath, @NotBlank String operator) {
        logger.info("操作员[{}]上传文件[{}]到指定目录[{}]，用途[{}]", operator, fileName, parentPath, useType);
        return doStoreFile1(fileName, useType, parentPath, operator);
    }

    /**
     * 根据id下载文件
     * @param id 主键
     */
    @Override
    public void downloadFile(String id, HttpServletResponse response) {
        if (CharSequenceUtil.isBlank(id)) {
            throw new FileDownloadException("文件id为空");
        }
        FileInfoEntity fileInfoEntity = fileInfoRepository.getById(id);
        if (Objects.isNull(fileInfoEntity) || CharSequenceUtil.isEmpty(fileInfoEntity.getId())) {
            logger.error("下载文件失败，文件[{}]不存在", id);
            throw new FileDownloadException("文件不存在");
        }
        if (1 == fileInfoEntity.getFolder()) {
            logger.error("下载文件失败，指定id[{}]是目录", id);
            throw new FileDownloadException("不能下载目录");
        }
        String path = fileInfoEntity.getPath();
        File file = new File(path);
        if(!file.exists()){
            logger.error("下载文件失败，文件[{}]不存在", path);
            throw new FileDownloadException("下载文件不存在");
        }
        try (OutputStream outputStream = response.getOutputStream();
             //获取输出流通道
             WritableByteChannel writableByteChannel = Channels.newChannel(outputStream);
             //获取文件输入流
             FileInputStream fileInputStream = new FileInputStream(file);
             FileChannel fileChannel = fileInputStream.getChannel();
             ) {
            String contentType = Files.probeContentType(Paths.get(file.getAbsolutePath()));
            //设置响应头
            response.setHeader("Content-Type", contentType);
            response.setHeader("Content-Disposition", "attachment;filename="+new String(file.getName().getBytes(StandardCharsets.UTF_8),StandardCharsets.ISO_8859_1));
            response.setContentLength((int) file.length());

            //采用零拷贝的方式实现文件的下载
            fileChannel.transferTo(0,fileChannel.size(),writableByteChannel);
            outputStream.flush();
        } catch (IOException e) {
            logger.error("下载文件失败",e);
        }
    }

    /**
     * 根据id获取文件信息
     * @param id 文件id
     * @return 文件信息实体
     */
    @Override
    public FileInfoEntity getById(String id) {
        logger.info("根据id[{}]获取文件信息", id);
        return fileInfoRepository.getById(id);
    }

    /**
     * 根据id删除指定文件信息
     * @param idList 要删除的文件id列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByIds(List<String> idList, String operator) {
        logger.info("操作员[{}]删除文件[{}]", operator, idList);
        FileInfoAggregateRoot fileInfoAggregateRoot = new FileInfoAggregateRoot.Builder(fileInfoRepository, applicationEventPublisher).build();
        List<DescendantVO> voList = new ArrayList<>(idList.size());
        for (String id : idList) {
            voList.add(fileInfoAggregateRoot.removeById(id));
        }
        // 由于有事务，因此都删除成功后再发布事件
        for (DescendantVO descendantVO : voList) {
            // 发布事件
            FileInfoEntity voEntity = descendantVO.getEntity();
            List<FileInfoEntity> descendantList = descendantVO.getDescendantList();
            if (Objects.isNull(voEntity) || CharSequenceUtil.isBlank(voEntity.getId())) {
                continue;
            }
            if (1 == voEntity.getFolder()) {
                FileInfoEvent fileInfoEvent = new FileInfoEvent(descendantVO.getEntity(), FileChangeTypeEnum.FORCE_REMOVE_FOLDER);
                applicationEventPublisher.publishEvent(fileInfoEvent);
                if (CollUtil.isNotEmpty(descendantList)) {
                    for (FileInfoEntity entity : descendantList) {
                        applicationEventPublisher.publishEvent(new FileInfoEvent(entity, FileChangeTypeEnum.FORCE_REMOVE_FOLDER_DESCENDANT));
                    }
                }
            } else {
                FileInfoEvent fileInfoEvent = new FileInfoEvent(voEntity, FileChangeTypeEnum.REMOVE);
                applicationEventPublisher.publishEvent(fileInfoEvent);
            }
        }
    }


    /**
     * 上传多个文件到指定路径，会根据使用类型自动加上前缀路径
     * @param files 用户上传的文件数组
     * @param useType 使用类型
     * @param parentPath 上级目录
     * @param operator 操作人员
     * @return 文件id map，key：文件名，value：文件id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> storeFiles(@NonNull MultipartFile[] files, @NonNull FileUseTypeEnum useType, @NotBlank String parentPath,
                                          @NotBlank String operator) {
        logger.info("操作员[{}]上传多个文件到指定目录[{}]，用途[{}]", operator, parentPath, useType);
        if (files.length == 0) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0410, "文件列表不能为空");
        }
        if (files.length > FileInfoConstants.FILE_EACH_UPLOAD_LIMIT) {
            String message = CharSequenceUtil.format("一次最多只能上传[{}]个文件", FileInfoConstants.FILE_EACH_UPLOAD_LIMIT);
            logger.error(message);
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0425, message);
        }
        for (MultipartFile multipartFile : files) {
            checkFile(multipartFile);
        }
        parentPath = fileStoreControl.getStorePath(parentPath, useType);
        FileInfoAggregateRoot fileInfoAggregateRoot = new FileInfoAggregateRoot.Builder(fileInfoRepository, applicationEventPublisher)
                .build();
        return fileInfoAggregateRoot.addFileList(files, useType, parentPath, operator);
    }


    @Override
    public String storeTmpFile(@NonNull MultipartFile multipartFile, @NotBlank String fileName, @NonNull FileUseTypeEnum useType, @NotBlank String operator) {
        logger.info("操作员[{}]上传临时文件[{}]，用途[{}]", operator, fileName, useType);
        checkFile(multipartFile);
        String originalFilename = getFileName(multipartFile);
        String fileType = originalFilename.substring(originalFilename.lastIndexOf(StrPool.DOT));
        fileName = checkType(fileName, fileType);
        String parentPath = fileStoreControl.getStorePath(FileInfoConstants.FILE_TMP_DIR, useType);
        // 存储文件
        parentPath = FileUtil.normalize(parentPath);
        String storePath = parentPath + StrPool.SLASH + fileName;
        storePath = FileUtil.normalize(storePath);
        if (FileUtil.exist(storePath)) {
            throw new FileUploadException("文件已存在");
        }
        File storeFile = new File(storePath);
        try {
            FileUtil.mkParentDirs(storeFile);
            multipartFile.transferTo(storeFile);
        } catch (IOException e) {
            logger.error(FileInfoConstants.STORE_FILE_FAIL_MESSAGE, e);
            throw new FileUploadException(FileInfoConstants.STORE_FILE_FAIL_MESSAGE);
        }
        return FileUtil.getName(storePath);
    }

    @Override
    public FileInputStream getTmpFileStream(@NotBlank String fileName, @NonNull FileUseTypeEnum useType) {
        String parentPath = fileStoreControl.getStorePath(FileInfoConstants.FILE_TMP_DIR, useType);
        String storePath = parentPath + StrPool.SLASH + fileName;
        storePath = FileUtil.normalize(storePath);
        if (!FileUtil.exist(storePath) || !FileUtil.isFile(storePath)) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0440, CharSequenceUtil.format("文件[{}]不存在", storePath));
        }
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(storePath);
        } catch (FileNotFoundException e) {
            logger.error("读取文件失败", e);
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0440, CharSequenceUtil.format("读取文件[{}]失败", storePath));
        }
        return inputStream;
    }

    @Override
    public void removeTmpFile(@NotBlank String fileName, @NonNull FileUseTypeEnum useType, @NotBlank String operator) {
        String parentPath = fileStoreControl.getStorePath(FileInfoConstants.FILE_TMP_DIR, useType);
        String storePath = parentPath + StrPool.SLASH + fileName;
        storePath = FileUtil.normalize(storePath);
        if (FileUtil.isFile(storePath)) {
            FileUtil.del(storePath);
        }
    }

    @Override
    public long countFile(@NotBlank String fileName, @NonNull FileUseTypeEnum useType, String parentPath) {
        parentPath = fileStoreControl.getStorePath(parentPath, useType);
        String storePath = parentPath + StrPool.SLASH + fileName;
        storePath = FileUtil.normalize(storePath);
        if (FileUtil.exist(storePath)) {
            return 1L;
        }
        return 0L;
    }

    @Override
    public String transferTmpFile( String tmpFileName, @NotBlank String officialName, String parentPath,
                                @NonNull FileUseTypeEnum useType, @NotBlank String operator) {
        logger.info("用户[{}]将临时文件[{}]转储到路径[{}]/[{}]，使用类型[{}]", operator, tmpFileName,
                parentPath, officialName, useType);
        String tmpPath = fileStoreControl.getStorePath(FileInfoConstants.FILE_TMP_DIR, useType);
        String tmpFilePath = tmpPath + StrPool.SLASH + tmpFileName;
        tmpFilePath = FileUtil.normalize(tmpFilePath);
        if (!FileUtil.isFile(tmpFilePath)) {
            logger.error("临时文件[{}]不存在", tmpFilePath);
            throw new FileUploadException("文件不存在");
        }
        parentPath = fileStoreControl.getStorePath(parentPath, useType);
        FileInfoAggregateRoot fileInfoAggregateRoot = new FileInfoAggregateRoot.Builder(fileInfoRepository, applicationEventPublisher)
                .build();
        return fileInfoAggregateRoot.transferTmpFile(tmpFilePath, officialName, useType, parentPath, operator);
    }


    private String doStoreFile(MultipartFile multipartFile, String fileName, FileUseTypeEnum useType, String parentPath,
                               String operator) {
        checkFile(multipartFile);
        String originalFilename = getFileName(multipartFile);
        String fileType = originalFilename.substring(originalFilename.lastIndexOf(StrPool.DOT));
        fileName = checkType(fileName, fileType);
        parentPath = fileStoreControl.getStorePath(parentPath, useType);
        if (!FileUtil.isDirectory(parentPath)) {
            throw new FileUploadException("存储路径不存在或不是目录");
        }
        FileInfoAggregateRoot fileInfoAggregateRoot = new FileInfoAggregateRoot.Builder(fileInfoRepository, applicationEventPublisher)
                .build();
        return fileInfoAggregateRoot.addFileInfo(multipartFile, fileName, useType, parentPath, operator);
    }

    private String doStoreFile1(String fileName, FileUseTypeEnum useType, String parentPath,
                                String operator) {
        parentPath = fileStoreControl.getStorePath(parentPath, useType);
        if (!FileUtil.isDirectory(parentPath)) {
            throw new FileUploadException("存储路径不存在或不是目录："+parentPath);
        }
        FileInfoAggregateRoot fileInfoAggregateRoot = new FileInfoAggregateRoot.Builder(fileInfoRepository, applicationEventPublisher)
                .build();
        return fileInfoAggregateRoot.addFileInfo(fileName, useType, parentPath, operator);
    }

    private void checkFile(@NonNull MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0410, FILE_EMPTY_MESSAGE);
        }
    }

    private String getFileName(@NonNull MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        if (CharSequenceUtil.isBlank(originalFilename)) {
            throw new FileUploadException(FILE_NAME_EMPTY_MESSAGE);
        }

        return originalFilename;
    }

    private String checkType(String fileName, String fileType) {
        if (!fileName.contains(StrPool.DOT) || fileName.endsWith(StrPool.DOT)) {
            fileName = fileName + fileType;
        } else if (!fileType.equals(fileName.substring(fileName.lastIndexOf(StrPool.DOT)))) {
            throw new FileUploadException("指定的文件名与实际文件类型不一致");
        }

        return fileName;
    }

}
