package cn.com.farben.gptcoder.operation.platform.feedback.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import cn.com.farben.gptcoder.operation.commons.model.enums.ModelAbilityEnum;
import cn.com.farben.gptcoder.operation.platform.feedback.infrastructure.enums.FeedbackEnum;
import cn.com.farben.gptcoder.operation.platform.feedback.infrastructure.enums.UserTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户反馈记录实体
 */
@Data
public class FeedbackEntity implements IEntity {
    /** 记录ID */
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
