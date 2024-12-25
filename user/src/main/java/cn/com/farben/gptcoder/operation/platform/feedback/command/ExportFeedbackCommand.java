package cn.com.farben.gptcoder.operation.platform.feedback.command;

import cn.com.farben.commons.web.command.BaseFromToDateCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 导出用户反馈参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExportFeedbackCommand extends BaseFromToDateCommand {
    /** 反馈功能 */
    private String modelAbility;

    /** 反馈类型 */
    private String feedbackType;

    /** 用户机构 */
    private String fullOrganNo;

    /** 账号/姓名/工号 */
    private String searchText;
}
