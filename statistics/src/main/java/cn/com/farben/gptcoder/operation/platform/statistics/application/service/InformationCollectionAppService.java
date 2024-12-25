package cn.com.farben.gptcoder.operation.platform.statistics.application.service;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.IllegalParameterException;
import cn.com.farben.gptcoder.operation.commons.model.enums.ModelAbilityEnum;
import cn.com.farben.gptcoder.operation.platform.statistics.command.PluginUseCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.AdoptionInfoDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.RedisConstants;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.enums.ModelFeatureEnum;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade.AdoptionInfoRepository;
import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.paginate.Page;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 *
 * 信息采集应用服务<br>
 * 创建时间：2023/8/15<br>
 * @author ltg
 *
 */
@Component
public class InformationCollectionAppService {
    private final AdoptionInfoRepository adoptionInfoRepository;

    private final StringRedisTemplate redisTemplate;

    private static final Log logger = LogFactory.get();

    /**
     * 增加插件使用记录
     * 1.8版本不再建议使用该接口，为了兼容，暂时保留
     * @param clientIp 客户端ip
     * @param useCommand 使用记录
     */
    public boolean addAdoptionInfo(String clientIp, PluginUseCommand useCommand) {
        logger.info("增加插件使用记录");
        //校验插件版本
        useCommand.checkAdoptionData();

        JSONObject streamJo = new JSONObject();
        streamJo.set("account", useCommand.getUserId());
        streamJo.set("pluginType", useCommand.getPluginType());
        streamJo.set("pluginVersion", useCommand.getPluginsVer());
        streamJo.set("line", useCommand.getGenNum());
        streamJo.set("promptCharacterCount", useCommand.getPromptNum());
        streamJo.set("model", useCommand.getModelName());
        streamJo.set("ability", convertFunId(useCommand.getFunId()));
        streamJo.set("interval", useCommand.getCostTime());
        streamJo.set("date", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));

        // 向Stream中生产数据
        redisTemplate.opsForStream().add(StreamRecords.newRecord()
                .in(RedisConstants.GPTCODER_USAGE_STREAM_KEY)
                .ofObject(streamJo.toString())
                .withId(RecordId.autoGenerate()));

        logger.info("增加插件使用记录完成");
        return true;
    }

    /**
     * 转换模型对应的能力，由ModelFeatureEnum变为ModelAbilityEnum
     */
    private String convertFunId(String funId) {
        ModelFeatureEnum modelFeatureEnum = ModelFeatureEnum.convertFeature(funId);
        if (Objects.isNull(modelFeatureEnum)) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0402, "未识别的能力");
        }
        switch (modelFeatureEnum) {
            case CODE -> {
                return ModelAbilityEnum.CODE_HINTING.getAbilityCode();
            }
            case TRANSLATE -> {
                return ModelAbilityEnum.CODE_CONVERSION.getAbilityCode();
            }
            case EXPLAIN -> {
                return ModelAbilityEnum.CODE_EXPLAIN.getAbilityCode();
            }
            case UNIT -> {
                return ModelAbilityEnum.UNIT_TEST.getAbilityCode();
            }
            case COMMENTARY -> {
                return ModelAbilityEnum.CODE_COMMENT.getAbilityCode();
            }
            case CORRECT -> {
                return ModelAbilityEnum.CODE_CORRECTION.getAbilityCode();
            }
            case KNOWLEDGE_QA -> {
                return ModelAbilityEnum.KNOWLEDGE_QA.getAbilityCode();
            }
            case CODE_SEARCH -> {
                return ModelAbilityEnum.CODE_SEARCH.getAbilityCode();
            }
            default -> throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0402, "未识别的能力");
        }
    }

    /**
     * 分页查询插件使用信息
     * @param userId 用户ID
     * @param startTime 开始时间，格式：yyyy-MM-dd
     * @param endTime 结束时间, 格式：yyyy-MM-dd
     * @param pageSize 每页数据量
     * @param pageNo 页码
     * @return 插件使用信息
     */
    public Page<AdoptionInfoDTO> pageAdoptionInfo(String userId, String startTime, String endTime,
                                                  long pageSize, long pageNo) {
        return adoptionInfoRepository.pageAdoptionInfo(userId, startTime, endTime, pageSize, pageNo);
    }

    public InformationCollectionAppService(AdoptionInfoRepository adoptionInfoRepository, StringRedisTemplate redisTemplate) {
        this.adoptionInfoRepository = adoptionInfoRepository;
        this.redisTemplate = redisTemplate;
    }
}
