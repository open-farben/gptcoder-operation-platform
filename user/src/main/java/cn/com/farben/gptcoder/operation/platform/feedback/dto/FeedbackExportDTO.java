package cn.com.farben.gptcoder.operation.platform.feedback.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedbackExportDTO implements IDTO {
    /** 序号 */
    private Integer sort;

    /** 用户账号 */
    private String account;

    /** 用户工号 */
    private String jobNumber;

    /** 用户姓名 */
    private String name;

    /** 用户机构名称 */
    private String organizationName;

    /** 用户职务名称 */
    private String dutyName;

    /** 反馈类型 */
    private String feedbackType;

    /** 模型能力 */
    private String modelAbility;

    /** 建议 */
    private String suggest;

    /** 反馈时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime feedbackTime;

    /** 反馈问答 */
    private String qa;

    /** 上文内容 */
    private String history;

    /** 参数 */
    private String params;

    /** 模型回答 */
    private String modelResult;
}
