package cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.enums;

import cn.hutool.core.text.CharSequenceUtil;
import com.mybatisflex.annotation.EnumValue;
import lombok.Getter;

/**
 * 模型功能枚举
 */
public enum ModelFeatureEnum {
    CODE("code", "代码生成"),
    TRANSLATE("translate", "代码转换"),
    EXPLAIN("explain", "代码解释"),
    UNIT("unit", "单元测试"),
    COMMENTARY("commentary", "代码注释"),
    CORRECT("correct", "代码纠正"),
    KNOWLEDGE_QA("knowledge_qa", "知识库问答"),
    CODE_SEARCH("code_search", "代码搜索");

    ModelFeatureEnum(String type, String describe) {
        this.type = type;
        this.describe = describe;
    }

    @EnumValue
    @Getter
    private final String type;

    @Getter
    private final String describe;

    public static String getMsgByCode(String type) {
        if(CharSequenceUtil.isBlank(type)) {
            return "";
        }
        for(ModelFeatureEnum item : ModelFeatureEnum.values()) {
            if(item.getType().equals(type)) {
                return item.getDescribe();
            }
        }
        return "";
    }

    public static ModelFeatureEnum convertFeature(String feature) {
        for(ModelFeatureEnum item : ModelFeatureEnum.values()) {
            if(item.getType().equalsIgnoreCase(feature) || item.name().equalsIgnoreCase(feature)) {
                return item;
            }
        }
        return null;
    }
}
