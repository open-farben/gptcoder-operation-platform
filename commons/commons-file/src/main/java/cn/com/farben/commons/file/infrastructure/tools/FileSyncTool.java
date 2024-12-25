package cn.com.farben.commons.file.infrastructure.tools;

import cn.com.farben.commons.file.infrastructure.FileInfoConstants;
import cn.com.farben.commons.file.infrastructure.FileSyncInfo;
import cn.com.farben.commons.file.infrastructure.enums.FileSyncTypeEnum;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 文件同步工具类
 */
@Component
public class FileSyncTool {
    private final StringRedisTemplate redisTemplate;

    public FileSyncTool(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 写入需要同步的文件信息
     * @param syncInfo 文件同步信息
     */
    public void writeInfo(FileSyncInfo syncInfo) {
        redisTemplate.opsForList().leftPush(FileInfoConstants.FILE_SYNC_REDIS_KEY, JSONUtil.toJsonStr(syncInfo));
    }

    /**
     * 写入需要同步的临时文件信息
     * @param syncInfo 文件同步信息
     */
    public void writeTmpInfo(FileSyncInfo syncInfo) {
        redisTemplate.opsForList().leftPush(FileInfoConstants.TMP_FILE_SYNC_REDIS_KEY, JSONUtil.toJsonStr(syncInfo));
    }

    /**
     * 获取需要处理的文件信息，每次最多1000条
     * @return 文件信息列表
     */
    public List<FileSyncInfo> listInfo() {
        List<String> infoList = redisTemplate.opsForList().range(FileInfoConstants.FILE_SYNC_REDIS_KEY, 0, 1000);
        return handleMessage(infoList);
    }

    /**
     * 移除消息，处理成功后再调用该方法，否则会导致数据丢失
     * @param syncInfo 删除的信息
     */
    public void removeInfo(FileSyncInfo syncInfo) {
        redisTemplate.opsForList().remove(FileInfoConstants.FILE_SYNC_REDIS_KEY, 0, JSONUtil.toJsonStr(syncInfo));
    }

    /**
     * 获取需要处理的临时文件信息，每次最多1000条
     * @return 临时文件信息列表
     */
    public List<FileSyncInfo> listTmpInfo() {
        List<String> infoList = redisTemplate.opsForList().range(FileInfoConstants.TMP_FILE_SYNC_REDIS_KEY, 0, 1000);

        return handleMessage(infoList);
    }

    /**
     * 移除临时文件队列中的消息，处理成功后再调用该方法，否则会导致数据丢失
     * @param syncInfo 删除的信息
     */
    public void removeTmpInfo(FileSyncInfo syncInfo) {
        redisTemplate.opsForList().remove(FileInfoConstants.TMP_FILE_SYNC_REDIS_KEY, 0, JSONUtil.toJsonStr(syncInfo));
    }

    private List<FileSyncInfo> handleMessage(List<String> messageList) {
        if (CollUtil.isEmpty(messageList)) {
            return Collections.emptyList();
        }
        List<FileSyncInfo> resultList = new ArrayList<>(messageList.size());
        messageList.forEach(str -> {
            JSONObject jo = JSONUtil.parseObj(str);
            String syncTypeStr = jo.getStr("syncTypeEnum");
            if (CharSequenceUtil.isBlank(syncTypeStr)) {
                return;
            }
            FileSyncTypeEnum syncTypeEnum = FileSyncTypeEnum.valueOf(syncTypeStr);
            LocalDateTime eventTime = LocalDateTime.parse(jo.getStr("eventTime"));
            byte folder = jo.getByte("folder");
            String path = jo.getStr("path");

            FileSyncInfo fileSyncInfo = new FileSyncInfo(syncTypeEnum, eventTime, folder, path);
            resultList.add(fileSyncInfo);
        });

        return resultList;
    }
}
