package cn.com.farben.gptcoder.operation.platform.feedback.infrastructure.enums;

import com.mybatisflex.annotation.EnumValue;
import lombok.Getter;

/**
 * 问答反馈枚举
 */
@Getter
public enum FeedbackEnum {
    BE_OF_USE("be_of_use", "有用"), NO_USE("no_use", "没用"), CANCEL("cancel", "取消");

    FeedbackEnum(String effect, String describe) {
        this.effect = effect;
        this.describe = describe;
    }

    @EnumValue
    private final String effect;
    private final String describe;

    public static String convert2Desc(String effect) {
        for (FeedbackEnum feedbackEnum : FeedbackEnum.values()) {
            if (feedbackEnum.getEffect().equalsIgnoreCase(effect)) {
                return feedbackEnum.getDescribe();
            }
        }

        return null;
    }
}
