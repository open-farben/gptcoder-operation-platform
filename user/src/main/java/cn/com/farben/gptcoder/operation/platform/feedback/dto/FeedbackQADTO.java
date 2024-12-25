package cn.com.farben.gptcoder.operation.platform.feedback.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import lombok.Data;

@Data
public class FeedbackQADTO implements IDTO {
    /** 入参 */
    private String question;

    /** 模型回答 */
    private String answer;
}
