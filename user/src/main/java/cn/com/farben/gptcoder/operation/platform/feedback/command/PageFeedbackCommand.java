package cn.com.farben.gptcoder.operation.platform.feedback.command;

import cn.com.farben.commons.web.command.BaseFromToDateCommand;
import cn.com.farben.commons.web.constants.MvcConstants;
import cn.com.farben.gptcoder.operation.platform.feedback.validation.FeedbackExport;
import cn.com.farben.gptcoder.operation.platform.feedback.validation.FeedbackQuery;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分页查询用户反馈参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PageFeedbackCommand extends BaseFromToDateCommand {
    /** 反馈功能 */
    private String modelAbility;

    /** 反馈类型 */
    private String feedbackType;

    /** 用户机构 */
    private String fullOrganNo;

    /** 账号/姓名/工号 */
    private String searchText;

    @NotNull(message = "当前页不能为空", groups = FeedbackQuery.class)
    @Min(value = 1, message = "当前页不能小于1", groups = FeedbackQuery.class)
    private Long pageNo;

    @NotNull(message = "每页数据量不能为空", groups = FeedbackQuery.class)
    @Min(value = 1, message = "每页数据量不能小于1", groups = FeedbackQuery.class)
    @Max(value = MvcConstants.MAX_PAGE_SIZE, message = "每页数据量不能大于" + MvcConstants.MAX_PAGE_SIZE, groups = FeedbackQuery.class)
    private Long pageSize;

    /** 导出的起始记录 */
    @Min(value = 1, message = "导出的起始记录不能小于1", groups = FeedbackExport.class)
    private Long startNo;

    /** 导出记录数 */
    @Min(value = 1, message = "导出记录数不能小于1", groups = FeedbackExport.class)
    @Max(value = 1000, message = "导出记录数不能大于1000", groups = FeedbackExport.class)
    private Long exportNo;
}
