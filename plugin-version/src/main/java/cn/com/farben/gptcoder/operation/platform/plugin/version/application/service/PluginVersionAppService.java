package cn.com.farben.gptcoder.operation.platform.plugin.version.application.service;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.commons.errorcode.exception.DataInsertException;
import cn.com.farben.commons.errorcode.exception.DataSelectException;
import cn.com.farben.commons.errorcode.exception.DataUpdateException;
import cn.com.farben.commons.file.application.service.IFileInfoService;
import cn.com.farben.commons.file.domain.entity.FileInfoEntity;
import cn.com.farben.commons.file.infrastructure.enums.FileUseTypeEnum;
import cn.com.farben.gptcoder.operation.commons.user.enums.PluginTypesEnum;
import cn.com.farben.gptcoder.operation.commons.user.utils.UserInfoUtils;
import cn.com.farben.gptcoder.operation.platform.plugin.version.command.AddVersionCommand;
import cn.com.farben.gptcoder.operation.platform.plugin.version.command.ChangePluginStatusCommand;
import cn.com.farben.gptcoder.operation.platform.plugin.version.command.CheckdVersionCommand;
import cn.com.farben.gptcoder.operation.platform.plugin.version.command.ModifyPluginVersionCommand;
import cn.com.farben.gptcoder.operation.platform.plugin.version.domain.PluginVersionAggregateRoot;
import cn.com.farben.gptcoder.operation.platform.plugin.version.domain.entity.PluginAnalysisEntity;
import cn.com.farben.gptcoder.operation.platform.plugin.version.domain.entity.PluginVersionEntity;
import cn.com.farben.gptcoder.operation.platform.plugin.version.dto.PluginAnalysisDTO;
import cn.com.farben.gptcoder.operation.platform.plugin.version.dto.PluginDownloadDTO;
import cn.com.farben.gptcoder.operation.platform.plugin.version.dto.PluginDownloadItemDTO;
import cn.com.farben.gptcoder.operation.platform.plugin.version.dto.PluginTypesDTO;
import cn.com.farben.gptcoder.operation.platform.plugin.version.dto.PluginVersionDTO;
import cn.com.farben.gptcoder.operation.platform.plugin.version.exception.DownloadException;
import cn.com.farben.gptcoder.operation.platform.plugin.version.exception.PluginFileException;
import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.PluginConstants;
import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.PluginFileDecode;
import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.enums.PluginStatusEnum;
import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.facade.PluginVersionRepository;
import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.utils.PluginFileDecodeUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 * 插件版本应用服务<br>
 * 创建时间：2023/9/27<br>
 * @author ltg
 *
 */
@Component
public class PluginVersionAppService {
    private static final Log logger = LogFactory.get();

    private final PluginVersionRepository pluginVersionRepository;

    /** 事件发布 */
    private final ApplicationEventPublisher applicationEventPublisher;

    private final RedisTemplate<String, String> redisTemplate;

    private final PluginCacheService pluginCacheService;

    private final IFileInfoService fileInfoService;

    /**
     * 查看系统支持的IDE类型
     * @return IDE列表
     */
    public List<PluginTypesDTO> listPluginTypes() {
        List<PluginTypesDTO> resultList = new ArrayList<>();
        for (PluginTypesEnum typesEnum : PluginTypesEnum.values()) {
            PluginTypesDTO pluginTypesDTO = new PluginTypesDTO();
            BeanUtils.copyProperties(typesEnum, pluginTypesDTO);
            resultList.add(pluginTypesDTO);
        }
        return resultList;
    }

    /**
     * 解析插件文件
     * @param pluginFile 插件文件
     * @param id 主键
     * @param operator 操作员
     * @return 解析结果
     */
    public PluginAnalysisDTO analysisPluginFile(MultipartFile pluginFile, String id, String operator) {
        //校验当前版本数量
        if(CharSequenceUtil.isBlank(id) && pluginVersionRepository.countAllPlugin() >= PluginConstants.PLUGIN_VERSION_LIMIT){
            throw new PluginFileException("版本数量超过限制");
        }

        if (pluginFile.isEmpty()) {
            throw new PluginFileException("插件文件不能为空");
        }

        String originalFilename = pluginFile.getOriginalFilename();
        Objects.requireNonNull(originalFilename, "未获取到文件名");
        if (CharSequenceUtil.isBlank(originalFilename)) {
            throw new PluginFileException("未获取到文件名");
        }
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        fileType = CharSequenceUtil.removePrefix(fileType, ".");
        PluginTypesEnum pluginTypesEnum = null;
        for (PluginTypesEnum typeEnum : PluginTypesEnum.values()) {
            if (typeEnum.getFileType().equals(fileType)) {
                pluginTypesEnum = typeEnum;
                break;
            }
        }
        if (Objects.isNull(pluginTypesEnum)) {
            throw new PluginFileException(CharSequenceUtil.format("不支持的文件类型{}", fileType));
        }

        //获取文件的hash值
        String hashValue = calculateFileHash(pluginFile);

        String tmpName = PluginConstants.TMP_STORE_PRIFIX + IdUtil.objectId();
        // 存储临时文件
        String tmpFileName = fileInfoService.storeTmpFile(pluginFile, tmpName, FileUseTypeEnum.PLUGIN, operator);

        FileInputStream inputStream = null;
        PluginFileDecode pluginFileDecode = null;
        try {
            inputStream = fileInfoService.getTmpFileStream(tmpFileName, FileUseTypeEnum.PLUGIN);
            if (PluginTypesEnum.VSCODE == pluginTypesEnum) {
                pluginFileDecode = PluginFileDecodeUtil.decodeVscode(inputStream);
            } else if (PluginTypesEnum.JETBRAINS == pluginTypesEnum) {
                pluginFileDecode = PluginFileDecodeUtil.decodeJetBrains(inputStream);
            }
        } catch (Exception e) {
            logger.error("解析插件文件失败", e);
            fileInfoService.removeTmpFile(tmpFileName, FileUseTypeEnum.PLUGIN, operator);
            throw new PluginFileException("解析插件文件失败");
        } finally {
            if (Objects.nonNull(inputStream)) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error("关闭文件流", e);
                }
            }
        }
        Objects.requireNonNull(pluginFileDecode, "未获取到版本号和变更日志");
        String version = pluginFileDecode.version();
        String changeNotes = pluginFileDecode.changeNotes();
        if (CharSequenceUtil.isBlank(version)) {
            fileInfoService.removeTmpFile(tmpFileName, FileUseTypeEnum.PLUGIN, operator);
            throw new PluginFileException("未获取到版本号");
        }
        checkPluginTypeAndVersion(tmpFileName, fileType, pluginTypesEnum, version, id, operator);
        // 检查通过
        String key = PluginConstants.TMP_STORE_PRIFIX + IdUtil.objectId();

        // 将解析结果暂存redis
        PluginAnalysisEntity pluginAnalysisEntity = new PluginAnalysisEntity();
        pluginAnalysisEntity.setPluginType(pluginTypesEnum.getType());
        pluginAnalysisEntity.setFileName(tmpFileName);
        pluginAnalysisEntity.setVersion(version);

        //设置hash工具类型及hash值
        pluginAnalysisEntity.setHashToolName("SHA-256");
        pluginAnalysisEntity.setHashValue(hashValue);

        redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(pluginAnalysisEntity), 5L, TimeUnit.MINUTES);
        PluginAnalysisDTO pluginAnalysisDTO = new PluginAnalysisDTO();
        pluginAnalysisDTO.setType(pluginTypesEnum);
        pluginAnalysisDTO.setVersion(version);
        pluginAnalysisDTO.setDescription(changeNotes);
        pluginAnalysisDTO.setAnalysisKey(key);

        return pluginAnalysisDTO;
    }

    /**
     * 新增版本
     * @param addVersionCommand 新增版本命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void addVersion(AddVersionCommand addVersionCommand) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        logger.info("[{}]新增插件版本[{}]", operator, addVersionCommand);
        //校验当前版本数量
        if(pluginVersionRepository.countAllPlugin() >= PluginConstants.PLUGIN_VERSION_LIMIT){
            throw new PluginFileException("版本数量超过限制");
        }
        Objects.requireNonNull(addVersionCommand);
        PluginVersionEntity entity = new PluginVersionEntity();
        BeanUtils.copyProperties(addVersionCommand, entity);
        entity.setCreateUser(operator);
        entity.setUpdateUser(operator);
        String analysisKey = addVersionCommand.getAnalysisKey();
        Boolean publish = addVersionCommand.getPublish();
        if (Boolean.TRUE.equals(publish)) {
            entity.setStatus(PluginStatusEnum.RELEASED);
        } else {
            entity.setStatus(PluginStatusEnum.UNPUBLISHED);
        }
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(analysisKey))) {
            throw new PluginFileException("分析结果已丢失，请重新上传并分析");
        }
        String analysisStr = redisTemplate.opsForValue().get(analysisKey);
        if (CharSequenceUtil.isBlank(analysisStr)) {
            throw new PluginFileException("分析结果已丢失，请重新上传并分析");
        }
        PluginAnalysisEntity pluginAnalysisEntity = JSONUtil.toBean(analysisStr, PluginAnalysisEntity.class);
        String fileName = pluginAnalysisEntity.getFileName();
        String strPluginType = pluginAnalysisEntity.getPluginType();
        String version = pluginAnalysisEntity.getVersion();
        PluginTypesEnum pluginType = null;
        for (PluginTypesEnum value : PluginTypesEnum.values()) {
            if (value.getType().equalsIgnoreCase(strPluginType)) {
                pluginType = value;
                break;
            }
        }

        if (!entity.getVersion().equals(version)) {
            throw new DataInsertException("版本与解析结果不一致");
        }
        if (Objects.isNull(pluginType) || entity.getType() != pluginType) {
            throw new DataInsertException("插件类型与解析结果不一致");
        }
        String officialName = PluginConstants.PLUGIN_FILE_PRIFIX + StrPool.DASHED + pluginType.getType() + StrPool.DASHED
                + version + StrPool.DOT + pluginType.getFileType();
        String fileId = fileInfoService.transferTmpFile(fileName, officialName, StrPool.SLASH + pluginType.getType(),
                FileUseTypeEnum.PLUGIN, operator);
        FileInfoEntity fileInfoEntity = fileInfoService.getById(fileId);
        String id = IdUtil.objectId();
        entity.setId(id);
        entity.setFileId(fileId);
        entity.setFileSize(fileInfoEntity.getFileSize());

        //设置文件hash相关信息
        entity.setHashToolName(pluginAnalysisEntity.getHashToolName());
        entity.setHashValue(pluginAnalysisEntity.getHashValue());

        PluginVersionAggregateRoot pluginVersionAggregateRoot = new PluginVersionAggregateRoot.Builder(pluginVersionRepository).build();
        pluginVersionAggregateRoot.addVersion(entity);
        //如果是要发布插件，添加缓存
        if (Boolean.TRUE.equals(publish)) {
            pluginCacheService.addPluginCache(pluginType.getType(), version);
        }
    }

    /**
     * 查询可供下载的插件信息
     * @return 插件列表
     */
    public List<PluginDownloadDTO> listDownloadPlugins() {
        List<PluginVersionEntity> entityList = pluginVersionRepository.listDownloadPlugins();
        Map<PluginTypesEnum, List<PluginVersionEntity>> entityMap = entityList.stream().collect(Collectors.groupingBy(PluginVersionEntity::getType));
        List<PluginDownloadDTO> resultList = new ArrayList<>();
        for (Map.Entry<PluginTypesEnum, List<PluginVersionEntity>> pluginEntry : entityMap.entrySet()) {
            PluginTypesEnum type = pluginEntry.getKey();
            List<PluginVersionEntity> subEntityList = pluginEntry.getValue();
            PluginDownloadDTO dto = new PluginDownloadDTO();
            dto.setType(type.getType());
            dto.setItemList(BeanUtil.copyToList(subEntityList, PluginDownloadItemDTO.class));
            resultList.add(dto);
        }
        return resultList;
    }

    /**
     * 根据id下载插件文件
     * @param id 主键
     */
    public void downloadPlugin(String id, HttpServletResponse response) {
        PluginVersionEntity entity = pluginVersionRepository.getPluginById(id);
        if (Objects.isNull(entity) || CharSequenceUtil.isEmpty(entity.getId())) {
            logger.error("下载插件文件失败，数据[{}]不存在", id);
            throw new DownloadException("插件不存在");
        }
        if (PluginStatusEnum.RELEASED != entity.getStatus()) {
            logger.error("下载插件文件失败，插件[{}]未发布", id);
            throw new DownloadException("插件还未发布，不允许下载");
        }
        fileInfoService.downloadFile(entity.getFileId(), response);
    }

    /**
     * 分页查询版本信息
     * @param status 插件状态
     * @param type 插件类型
     * @param version 版本号
     * @param pageSize 每页数据量
     * @param page 当前页
     * @return 插件版本列表
     */
    public Page<PluginVersionDTO> pagePlugin(PluginStatusEnum status, PluginTypesEnum type, String version, long pageSize, long page) {
        if (CharSequenceUtil.isNotBlank(version) && version.length() < 3) {
            throw new DataSelectException("版本号最低长度为3");
        }
        Page<PluginVersionEntity> data = pluginVersionRepository.pagePlugin(status, type, version, pageSize, page);

        return CommonAssemblerUtil.assemblerEntityPageToDTOPage(data, PluginVersionDTO.class);
    }

    /**
     * 改变插件版本状态
     * @param changePluginStatusCommand 改变插件状态命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(ChangePluginStatusCommand changePluginStatusCommand) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        PluginStatusEnum status = changePluginStatusCommand.getStatus();
        PluginVersionEntity entity = pluginVersionRepository.getPluginById(changePluginStatusCommand.getId());
        // 判断是否可更新
        PluginStatusEnum entityStatus = entity.getStatus();
        String errorMessage = "操作员[{}]更新插件状态失败，原因[{}]";
        if (status == entityStatus) {
            String message = CharSequenceUtil.format("状态已经是{}", status.getDescribe());
            logger.error(errorMessage, operator, message);
            throw new DataUpdateException(message);
        }
        if (status == PluginStatusEnum.UNPUBLISHED) {
            String message = "不允许将状态改为未发布";
            logger.error(errorMessage, operator, message);
            throw new DataUpdateException(message);
        }
        if (status == PluginStatusEnum.REPEAL && entityStatus == PluginStatusEnum.UNPUBLISHED) {
            String message = "未发布的版本不能撤销";
            logger.error(errorMessage, operator, message);
            throw new DataUpdateException(message);
        }
        PluginVersionAggregateRoot pluginVersionAggregateRoot = new PluginVersionAggregateRoot.Builder(pluginVersionRepository)
                .applicationEventPublisher(applicationEventPublisher).build();
        pluginVersionAggregateRoot.changeStatus(changePluginStatusCommand.getId(), status, operator);

        //发布新版本，添加缓存，反之删除缓存
        if(status == PluginStatusEnum.REPEAL) {
            pluginCacheService.deletePluginCache(entity.getType().getType(), entity.getVersion());
        }else {
            pluginCacheService.addPluginCache(entity.getType().getType(), entity.getVersion());
        }

    }

    /**
     * 编辑插件版本
     * @param modifyPluginVersionCommand 编辑插件版本命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void modifyPlugin(ModifyPluginVersionCommand modifyPluginVersionCommand) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        logger.info("[{}]编辑插件版本[{}]", operator, modifyPluginVersionCommand);
        Objects.requireNonNull(modifyPluginVersionCommand);
        PluginVersionEntity entity = new PluginVersionEntity();
        BeanUtils.copyProperties(modifyPluginVersionCommand, entity);
        entity.setUpdateUser(operator);
        PluginVersionAggregateRoot pluginVersionAggregateRoot = new PluginVersionAggregateRoot.Builder(pluginVersionRepository)
                .applicationEventPublisher(applicationEventPublisher).build();
        pluginVersionAggregateRoot.modifyPlugin(entity);
    }

    /**
     * 检查插件版本是否可用
     * @param checkdVersionCommand 检查版本是否可用命令
     */
    public boolean checkVersion(CheckdVersionCommand checkdVersionCommand) {
        Objects.requireNonNull(checkdVersionCommand);
        PluginTypesEnum type = checkdVersionCommand.getType();
        String version = checkdVersionCommand.getVersion();
        //先从缓存获取
        if(StringUtils.isNotBlank(pluginCacheService.findPluginCache(type.getType(), version))) {
            return true;
        }
        long count = pluginVersionRepository.countWithTypeAndVersion(type, version);
        if (count <= 0) {
            return false;
        }
        PluginVersionEntity entity = pluginVersionRepository.getPluginByTypeAndVersion(type, version);
        //如果缓存失效了，数据库有，则重新放到缓存
        boolean exist = PluginStatusEnum.RELEASED == entity.getStatus();
        if(exist) {
            pluginCacheService.addPluginCache(type.getType(), version);
        }
        return exist;
    }

    /**
     * 删除插件版本
     * @param ids 插件id
     */
    @Transactional(rollbackFor = Exception.class)
    public void deletePlugin(List<String> ids) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        logger.info("操作员[{}]删除插件[{}]", operator, ids);
        PluginVersionAggregateRoot pluginVersionAggregateRoot = new PluginVersionAggregateRoot.Builder(pluginVersionRepository).build();
        List<PluginVersionEntity> entityList = pluginVersionRepository.listByIds(ids);
        for (String id : ids) {
            pluginVersionAggregateRoot.removePluginById(id);
        }
        for (PluginVersionEntity pluginVersionEntity : entityList) {
            pluginCacheService.deletePluginCache(pluginVersionEntity.getType().getType(), pluginVersionEntity.getVersion());
        }
        // 同步删除文件信息
        fileInfoService.removeByIds(entityList.stream().map(PluginVersionEntity::getFileId).toList(), operator);
    }

    /**
     * 检查插件类型和版本
     * @param tmpFileName 临时文件名称
     * @param fileType 文件类型
     * @param pluginType 插件类型
     * @param version 插件版本
     * @param id 主键
     * @param operator 操作员
     */
    private void checkPluginTypeAndVersion(String tmpFileName, String fileType, PluginTypesEnum pluginType,
                                           String version, String id, String operator) {
        long count = pluginVersionRepository.countWithTypeAndVersion(pluginType, version);
        String officialName = PluginConstants.PLUGIN_FILE_PRIFIX + StrPool.DASHED + pluginType.getType() + StrPool.DASHED
                + version + StrPool.DOT + fileType;
        if (CharSequenceUtil.isBlank(id)) {
            if (fileInfoService.countFile(officialName, FileUseTypeEnum.PLUGIN, StrPool.SLASH + pluginType.getType()) > 0) {
                // 已有对应文件
                fileInfoService.removeTmpFile(tmpFileName, FileUseTypeEnum.PLUGIN, operator);
                throw new PluginFileException(CharSequenceUtil.format("插件类型{}已有对应版本{}的文件", pluginType.getType(), version));
            }
            if (count > 0) {
                // 不是编辑，数据库已有数据，不允许覆盖
                throw new PluginFileException(CharSequenceUtil.format("插件类型{}已有对应版本{}的记录", pluginType.getType(), version));
            }
        }
        if (CharSequenceUtil.isNotBlank(id)) {
            // 是编辑，上传的文件版本和类型必须一致
            PluginVersionEntity entity = pluginVersionRepository.getPluginById(id);
            if (Objects.isNull(entity) || CharSequenceUtil.isBlank(entity.getId())) {
                fileInfoService.removeTmpFile(tmpFileName, FileUseTypeEnum.PLUGIN, operator);
                throw new PluginFileException("根据id未获取到数据");
            }
            PluginTypesEnum entityType = entity.getType();
            String entityVersion = entity.getVersion();
            if (entityType != pluginType || !entityVersion.equals(version)) {
                fileInfoService.removeTmpFile(tmpFileName, FileUseTypeEnum.PLUGIN, operator);
                throw new PluginFileException("新文件的类型或版本与原数据不一致");
            }
        }
    }

    public PluginVersionAppService(PluginVersionRepository pluginVersionRepository, ApplicationEventPublisher applicationEventPublisher,
                                   RedisTemplate<String, String> redisTemplate, PluginCacheService pluginCacheService,
                                   IFileInfoService fileInfoService) {
        this.pluginVersionRepository = pluginVersionRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.redisTemplate = redisTemplate;
        this.pluginCacheService = pluginCacheService;
        this.fileInfoService = fileInfoService;
    }

    private  String calculateFileHash(MultipartFile file) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = file.getBytes();
            md.update(fileBytes);
            byte[] digestBytes = md.digest();
            BigInteger no = new BigInteger(1, digestBytes);
            String hashtext = no.toString(16);
            // Add leading zeros to make it 64 bit long
            while (hashtext.length() < 64) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error calculating file hash", e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file content", e);
        }
    }

}
