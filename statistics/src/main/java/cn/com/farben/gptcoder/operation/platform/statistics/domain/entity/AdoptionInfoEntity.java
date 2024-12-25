package cn.com.farben.gptcoder.operation.platform.statistics.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.IllegalParameterException;
import cn.com.farben.gptcoder.operation.commons.model.enums.ModelAbilityEnum;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.enums.ModelFeatureEnum;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 *
 * 插件使用记录实体<br>
 * 创建时间：2023/8/15<br>
 * @author ltg
 *
 */
@Data
public class AdoptionInfoEntity implements IEntity {
    private static final Log logger = LogFactory.get();

    /** 流水ID，主键 */
    private String infoId;

    /** 用户ID */
    private String userId;

    /** 方法ID */
    private String funId;

    /** 客户端IP */
    private String clientIp;

    /** 插件版本 */
    private String pluginsVer;

    /** idea名称 */
    private String ideName;

    /** idea版本 */
    private String ideVer;

    /** 系统版本 */
    private String sysVer;

    /** 客户端其他信息 */
    private String macInfo;

    /** 生成代码行数 */
    private Integer genNum;

    /** 确认代码行数 */
    private Integer confirmNum;

    /** 请求访问日期 */
    private LocalDate accessDate;

    /** 请求花费时间 */
    private Integer costTime;

    /** 提示字符数 */
    private Integer promptNum;

    /** 补全字符数 */
    private Integer complementCharNum;

    /** 代码补全率 */
    private BigDecimal complementRate;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private LocalDateTime createDate;

    /** 是否已处理过，1：是，0或null：否 */
    private byte dealFlg;

    /** 当前模型 */
    private String modelName;

    /** 插件类型 */
    private String pluginType;

    /**
     * 转换模型对应的能力，由ModelFeatureEnum变为ModelAbilityEnum
     */
    public void convertFunId() {
        ModelFeatureEnum modelFeatureEnum = ModelFeatureEnum.convertFeature(funId);
        if (Objects.isNull(modelFeatureEnum)) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0402, "未识别的能力");
        }
        switch (modelFeatureEnum) {
            case CODE -> funId = ModelAbilityEnum.CODE_HINTING.getAbilityCode();
            case TRANSLATE -> funId = ModelAbilityEnum.CODE_CONVERSION.getAbilityCode();
            case EXPLAIN -> funId = ModelAbilityEnum.CODE_EXPLAIN.getAbilityCode();
            case UNIT -> funId = ModelAbilityEnum.UNIT_TEST.getAbilityCode();
            case COMMENTARY -> funId = ModelAbilityEnum.CODE_COMMENT.getAbilityCode();
            case CORRECT -> funId = ModelAbilityEnum.CODE_CORRECTION.getAbilityCode();
            case KNOWLEDGE_QA -> funId = ModelAbilityEnum.KNOWLEDGE_QA.getAbilityCode();
            case CODE_SEARCH -> funId = ModelAbilityEnum.CODE_SEARCH.getAbilityCode();
            default -> logger.info("未识别的能力[{}]", funId);
        }
    }
}
