package cn.com.farben.gptcoder.operation.commons.model.enums;

import com.mybatisflex.annotation.EnumValue;
import lombok.Getter;

/**
 * 模型能力枚举
 */
@Getter
public enum ModelAbilityEnum {
    CODE_HINTING("code_hinting", "generate_", "代码提示"),
    CODE_CORRECTION("code_correction", "repair_", "代码纠正"),
    CODE_EXPLAIN("code_explain", "explain_", "代码解释"),
    CODE_CONVERSION("code_conversion", "translate_", "代码转换"),
    UNIT_TEST("unit_test", "testcase_", "单元测试"),
    CODE_QUESTION("code_question", "chat_", "智能问答"),
    CODE_COMMENT("code_comment", "annotation_", "代码注释"),
    KNOWLEDGE_QA("knowledge_qa", "knowledge_", "知识库问答"),
    CODE_SEARCH("code_search", "search_", "代码搜索");

    ModelAbilityEnum(String abilityCode, String redisKey, String describe) {
        this.abilityCode = abilityCode;
        this.redisKey = redisKey;
        this.describe = describe;
    }

    @EnumValue
    private final String abilityCode;
    private final String redisKey;
    private final String describe;

    public static String convert2Desc(String abilityCode) {
        for (ModelAbilityEnum abilityEnum : ModelAbilityEnum.values()) {
            if (abilityEnum.getAbilityCode().equalsIgnoreCase(abilityCode)) {
                return abilityEnum.getDescribe();
            }
        }

        return null;
    }
}
