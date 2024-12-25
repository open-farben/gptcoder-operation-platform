package cn.com.farben.gptcoder.operation.platform.statistics.command;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.IllegalParameterException;
import cn.com.farben.gptcoder.operation.commons.user.enums.PluginTypesEnum;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.enums.ModelFeatureEnum;
import cn.hutool.core.text.CharSequenceUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * 插件使用记录命令
 */
@Validated
@Data
public class PluginUseCommand implements Serializable {
    /** 用户ID */
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    /** 用户名称 */
    private String userName;

    /** 方法ID，对应ModelFeatureEnum */
    @NotBlank(message = "插件功能不能为空")
    private String funId;

    /** 客户端IP */
    private String clientIp;

    /** 插件版本 */
    @NotBlank(message = "插件版本不能为空")
    private String pluginsVer;

    /** 插件名称 */
    private String ideName;

    /** 插件版本 */
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
    private Date accessDate;

    /** 请求花费时间 */
    private Integer costTime;

    /** 补全字符数 */
    private Integer complementCharNum;

    /** 提示字符数 */
    private Integer promptNum;

    /** 当前模型 */
    @NotBlank(message = "模型名称不能为空")
    private String modelName;

    /** 插件类型,对应PluginTypesEnum */
    @NotBlank(message = "插件类型不能为空")
    private String pluginType;

    /**
     * 检查上报数据
     */
    public void checkAdoptionData() {
        PluginTypesEnum pluginTypeEnum = PluginTypesEnum.convertType(pluginType);
        if (Objects.isNull(pluginTypeEnum)) {
            throw new IllegalParameterException(ErrorCodeEnum.NOT_EXIST_PLUGIN_TYPE);
        }
        pluginType = pluginTypeEnum.getType();
        ModelFeatureEnum modelFeatureEnum = ModelFeatureEnum.convertFeature(funId);
        if (Objects.isNull(modelFeatureEnum)) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0402, CharSequenceUtil.format("未识别的能力[{}]", funId));
        }
        funId = modelFeatureEnum.getType();
    }

}
