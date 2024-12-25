package cn.com.farben.gptcoder.operation.platform.feedback.command;

import cn.com.farben.commons.errorcode.exception.IllegalParameterException;
import cn.com.farben.gptcoder.operation.platform.feedback.infrastructure.enums.FeedbackEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

/**
 * 用户反馈命令
 */
@Validated
@Data
public class FeedbackCommand {
    /** 流水号 */
    @NotBlank(message = "流水号不能为空")
    private String serialNumber;

    /** 反馈类型 */
    private String type;

    /** 反馈建议 */
    @Size(max = 200, message = "反馈建议不能超过200字")
    private String suggest;

    public FeedbackEnum getFeedbackType() {
        FeedbackEnum feedbackEnum = null;
        for (FeedbackEnum fenum: FeedbackEnum.values()) {
            if (fenum.name().equalsIgnoreCase(type) || fenum.getEffect().equalsIgnoreCase(type)) {
                feedbackEnum = fenum;
                break;
            }
        }
        if (Objects.isNull(feedbackEnum)) {
            throw new IllegalParameterException("反馈类型不正确");
        }

        return feedbackEnum;
    }
}
