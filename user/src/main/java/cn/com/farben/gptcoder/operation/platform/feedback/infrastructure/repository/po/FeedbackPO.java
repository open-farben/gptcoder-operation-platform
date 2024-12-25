package cn.com.farben.gptcoder.operation.platform.feedback.infrastructure.repository.po;

import cn.com.farben.commons.ddd.po.IPO;
import cn.com.farben.gptcoder.operation.commons.model.enums.ModelAbilityEnum;
import cn.com.farben.gptcoder.operation.platform.feedback.infrastructure.enums.FeedbackEnum;
import cn.com.farben.gptcoder.operation.platform.feedback.infrastructure.enums.UserTypeEnum;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户反馈记录表
 */
@Data
@Table("feedback")
public class FeedbackPO implements IPO {
    /** 记录ID */
    @Id(keyType = KeyType.None)
    private String id;

    /** 账号 */
    private String account;

    /** 账号类型 */
    private UserTypeEnum userType;

    /** 反馈类型 */
    private FeedbackEnum feedbackType;

    /** 反馈功能 */
    private ModelAbilityEnum modelAbility;

    /** 反馈建议 */
    private String suggest;

    /** 反馈时间 */
    private LocalDateTime feedbackTime;

    /** 传的参数 */
    private String params;

    /** 请求头 */
    private String headers;

    /** 模型返回结果 */
    private String modelResult;
}
