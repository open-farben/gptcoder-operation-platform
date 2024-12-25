package cn.com.farben.commons.file.job;

import cn.com.farben.commons.file.domain.entity.FileInfoEntity;
import cn.com.farben.commons.file.domain.event.FileInfoEvent;
import cn.com.farben.commons.file.infrastructure.FileInfoConstants;
import cn.com.farben.commons.file.infrastructure.FileStoreControl;
import cn.com.farben.commons.file.infrastructure.FileSyncInfo;
import cn.com.farben.commons.file.infrastructure.enums.FileChangeTypeEnum;
import cn.com.farben.commons.file.infrastructure.enums.FileSyncTypeEnum;
import cn.com.farben.commons.file.infrastructure.enums.FileUseTypeEnum;
import cn.com.farben.commons.file.infrastructure.repository.facade.FileInfoRepository;
import cn.com.farben.commons.file.infrastructure.tools.FileSyncTool;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 文件清理任务<br>
 */
@Component
public class FileClearJob implements ApplicationListener<FileInfoEvent> {
    private static final Log logger = LogFactory.get();

    /** 文件信息仓储接口 */
    private final FileInfoRepository fileInfoRepository;

    private final FileStoreControl fileStoreControl;

    private final FileSyncTool fileSyncTool;

    public FileClearJob(FileInfoRepository fileInfoRepository, FileStoreControl fileStoreControl, FileSyncTool fileSyncTool) {
        this.fileInfoRepository = fileInfoRepository;
        this.fileStoreControl = fileStoreControl;
        this.fileSyncTool = fileSyncTool;
    }

    /**
     * 清除无用文件，每天两点启动。清理25小时以内的没有用的文件<br>
     * 会自动清除文件创建{@link FileInfoConstants#CLEAN_HOUR}后还没有在数据库记录的文件
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void clearUselessFilesTask() {
        logger.info("------清除无用文件任务启动------");
        LocalDateTime now = LocalDateTime.now();
        // 数据库中的路径
        List<FileInfoEntity> fileList = fileInfoRepository.listFiles(now.minusHours(FileInfoConstants.CLEAN_HOUR));
        long count = 0;
        for (FileUseTypeEnum useTypeEnum : FileUseTypeEnum.values()) {
            count += clearFiles(fileStoreControl.getStorePath(null, useTypeEnum), useTypeEnum, fileList);
        }
        // 处理队列中信息
        count += handleSyncMessage();
        // 处理临时队列中信息
        count += handleSyncTmpMessage();
        logger.info("------清除无用文件任务结束，共清理[{}]个文件和目录------", count);
    }

    private long handleSyncMessage() {
        List<FileSyncInfo> dataList = fileSyncTool.listInfo();
        long count = 0L;
        if (CollUtil.isEmpty(dataList)) {
            return count;
        }
        for (FileSyncInfo fileSyncInfo : dataList) {
            String path = fileSyncInfo.path();
            try {
                boolean flg = FileUtil.del(path);
                if (flg) {
                    count++;
                    fileSyncTool.removeInfo(fileSyncInfo);
                }
            } catch (IORuntimeException e) {
                // 删除文件失败
                logger.error(CharSequenceUtil.format("删除文件[{}]失败", path), e);
            }
        }
        count += handleSyncMessage();

        return count;
    }

    private long handleSyncTmpMessage() {
        List<FileSyncInfo> dataList = fileSyncTool.listTmpInfo();
        long count = 0L;
        if (CollUtil.isEmpty(dataList)) {
            return count;
        }
        LocalDateTime compareTime = LocalDateTime.now().minusHours(FileInfoConstants.CLEAN_HOUR);
        for (FileSyncInfo fileSyncInfo : dataList) {
            LocalDateTime eventTime = fileSyncInfo.eventTime();
            if (eventTime.isBefore(compareTime)) {
                // 临时文件超过25小时才删除
                doRemove(fileSyncInfo.folder(), fileSyncInfo.path());
                count++;
                fileSyncTool.removeTmpInfo(fileSyncInfo);
            } else {
                // 按时间顺序进入的队列，因此后面的也还没到25小时，不用处理了
                return count;
            }
        }
        count += handleSyncTmpMessage();

        return count;
    }

    /**
     * 清理目标路径无用的文件，包括子路径
     * @param targetPath 目标路径
     * @param useTypeEnum 使用类型
     * @param inUseList 使用中的文件路径
     * @return 清理文件数
     */
    private long clearFiles(String targetPath, FileUseTypeEnum useTypeEnum, List<FileInfoEntity> inUseList) {
        logger.info("开始清理路径[{}]", targetPath);
        if (!FileUtil.isDirectory(targetPath)) {
            logger.warn("路径[{}]不存在或不是文件夹", targetPath);
            return 0L;
        }
        if (targetPath.equals(fileStoreControl.getStorePath(FileInfoConstants.FILE_TMP_DIR, useTypeEnum))) {
            // 临时目录，特殊处理
            return 0L;
        }
        AtomicLong count = new AtomicLong(0L);
        // 处理25小时以内的文件
        LocalDateTime compareTime = LocalDateTime.now().minusHours(FileInfoConstants.CLEAN_HOUR);
        List<String> pathList = filterPath(inUseList, compareTime);

        File[] files = FileUtil.ls(targetPath);
        for (File file : files) {
            Date date = FileUtil.lastModifiedTime(file);
            LocalDateTime modifiedTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            if (FileUtil.isFile(file) && modifiedTime.isBefore(compareTime)) {
                // 超过时间范围，不处理
                continue;
            }
            String filePath = FileUtil.normalize(targetPath + StrPool.SLASH + file.getName());
            if (FileUtil.isFile(file)) {
                count.addAndGet(tryClean(file, filePath, pathList));
            } else if (modifiedTime.isAfter(compareTime) && FileUtil.isDirEmpty(file)) {
                // 目录为空
                count.addAndGet(tryClean(file, filePath, pathList));
            } else if (!FileUtil.isDirEmpty(file)) {
                // 目录不为空，清理子路径和文件
                count.addAndGet(clearFiles(filePath, useTypeEnum, inUseList));
                if (modifiedTime.isAfter(compareTime) && FileUtil.isDirEmpty(file)) {
                    // 清理子文件和目录后再次检查自己为空
                    count.addAndGet(tryClean(file, filePath, pathList));
                }
            }
        }

        return count.get();
    }

    /**
     * 过滤掉无需处理的路径
     * @param inUseList 使用中路径
     * @param compareTime 处理时限
     * @return 需处理的路径
     */
    private List<String> filterPath(List<FileInfoEntity> inUseList, LocalDateTime compareTime) {
        return inUseList.stream().filter(e -> {
            Date date = FileUtil.lastModifiedTime(e.getPath());
            if (Objects.isNull(date)) {
                // 是其它平台的文件
                return false;
            }
            LocalDateTime modifiedTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            return modifiedTime.isAfter(compareTime);
        }).map(FileInfoEntity::getPath).toList();
    }

    /**
     * 清理文件，会跳过使用中的文件
     * @param file 待清理的文件
     * @param filePath 文件路径
     * @param inUseList 使用的中的文件列表
     * @return 0或1
     */
    private long tryClean(File file, String filePath, List<String> inUseList) {
        // 不在db内且超过指定时间，删除
        // 不能直接用文件绝对路径比较，因为db存储的不一定是绝对路径
        if(!inUseList.contains(filePath)) {
            logger.warn("清理无用文件或目录：[{}]", filePath);
            if (FileUtil.isFile(file)) {
                FileUtil.del(file);
            } else {
                FileUtil.cleanEmpty(file);
            }
            return 1;
        }
        return 0;
    }

    @Override
    public void onApplicationEvent(@NonNull FileInfoEvent event) {
        logger.info("清理文件任务监听到事件:[{}]，事件类型：[{}]", event, event.getChangeType());
        FileInfoEntity source = (FileInfoEntity)event.getSource();
        if (FileChangeTypeEnum.CREATE == event.getChangeType()) {
            syncCreate(source);
        }
        if (FileChangeTypeEnum.REMOVE == event.getChangeType()) {
            syncRemove(source);
        }
        if (FileChangeTypeEnum.FORCE_REMOVE_FOLDER == event.getChangeType()) {
            syncRemoveFolder(source);
        }
    }

    private void syncCreate(FileInfoEntity source) {
        Byte folder = source.getFolder();
        // 创建目录，在一个事务里，因此数据库和实际目录信息是一致的。不一致的情况基本不会发生
        // 上传单个文件，先保存文件再写数据库，可能会文件成功，写入失败
        if (folder == 0) {
            String id = source.getId();
            long count = fileInfoRepository.countById(id);
            if (count == 0) {
                // 写数据库时失败，删除对应文件
                String path = source.getPath();
                logger.warn("数据库未发现对应文件[{}]，将删除", path);
                doRemove((byte) 0, source.getPath());
            }
        }
    }

    private void syncRemove(FileInfoEntity source) {
        // 删除时先保证数据库删除成功，因此此时文件还在
        String id = source.getId();
        long count = fileInfoRepository.countById(id);
        if (count == 0) {
            // 数据已删除，清理掉对应文件
            String path = source.getPath();
            logger.warn("清理无用文件或目录：[{}]", path);
            doRemove((byte) 0, source.getPath());
        }
    }

    private void syncRemoveFolder(FileInfoEntity source) {
        String id = source.getId();
        long count = fileInfoRepository.countById(id);
        if (count == 0) {
            // 数据已删除，清理掉对应目录
            String path = source.getPath();
            logger.warn("强制删除目录：[{}]", path);
            doRemove((byte) 1, source.getPath());
        }
    }

    private void doRemove(byte folder, String path) {
        try {
            boolean flg = FileUtil.del(path);
            if (!flg) {
                // 删除失败，写入redis
                fileSyncTool.writeInfo(new FileSyncInfo(FileSyncTypeEnum.REMOVE, LocalDateTime.now(), folder, path));
            }
        } catch (IORuntimeException e) {
            // 删除文件失败
            logger.error(CharSequenceUtil.format("删除文件[{}]失败", path), e);
            // 写入redis
            fileSyncTool.writeInfo(new FileSyncInfo(FileSyncTypeEnum.REMOVE, LocalDateTime.now(), folder, path));
        }
    }
}
