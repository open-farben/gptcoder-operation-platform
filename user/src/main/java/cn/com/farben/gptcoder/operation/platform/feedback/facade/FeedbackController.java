package cn.com.farben.gptcoder.operation.platform.feedback.facade;

import cn.com.farben.commons.errorcode.exception.OperationNotAllowedException;
import cn.com.farben.commons.web.ResultData;
import cn.com.farben.commons.web.constants.AuthenticationConstants;
import cn.com.farben.gptcoder.operation.platform.feedback.application.service.FeedbackAppService;
import cn.com.farben.gptcoder.operation.platform.feedback.command.FeedbackCommand;
import cn.com.farben.gptcoder.operation.platform.feedback.command.PageFeedbackCommand;
import cn.com.farben.gptcoder.operation.platform.feedback.dto.FeedbackQADTO;
import cn.com.farben.gptcoder.operation.platform.feedback.dto.FeedbackQueryDTO;
import cn.com.farben.gptcoder.operation.platform.feedback.validation.FeedbackExport;
import cn.com.farben.gptcoder.operation.platform.feedback.validation.FeedbackQuery;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户反馈对外接口
 */
@RestController
@RequestMapping("/feedback")
@Validated
public class FeedbackController {
    private static final Log logger = LogFactory.get();

    private final FeedbackAppService feedbackAppService;

    public FeedbackController(FeedbackAppService feedbackAppService) {
        this.feedbackAppService = feedbackAppService;
    }

    /**
     * 效果反馈
     * @param feedbackCommand 用户反馈命令
     */
    @PostMapping("/effect")
    public ResultData<Void> feedbackEffect(@RequestBody @Valid FeedbackCommand feedbackCommand,
                                           @RequestHeader(name = AuthenticationConstants.USER_IDENTIFICATION_HEADER, required = false) String uuid,
                                           @RequestHeader(name = AuthenticationConstants.TOKEN_REQUEST_HEADER, required = false) String authorization) {
        if (CharSequenceUtil.isBlank(uuid) && CharSequenceUtil.isBlank(authorization)) {
            throw new OperationNotAllowedException("没有获取到用户信息");
        }
        logger.info("效果反馈: [{}]", feedbackCommand);
        feedbackAppService.feedbackEffect(feedbackCommand, uuid, authorization);
        return new ResultData.Builder<Void>().ok().build();
    }

    /**
     * 反馈建议
     * @param feedbackCommand 用户反馈命令
     */
    @PostMapping("/suggest")
    public ResultData<Void> feedbackSuggest(@RequestBody @Valid FeedbackCommand feedbackCommand,
                                           @RequestHeader(name = AuthenticationConstants.USER_IDENTIFICATION_HEADER, required = false) String uuid,
                                            @RequestHeader(name = AuthenticationConstants.TOKEN_REQUEST_HEADER, required = false) String authorization) {
        if (CharSequenceUtil.isBlank(uuid) && CharSequenceUtil.isBlank(authorization)) {
            throw new OperationNotAllowedException("没有获取到用户信息");
        }
        logger.info("反馈建议: [{}]", feedbackCommand);
        feedbackAppService.feedbackSuggest(feedbackCommand, uuid, authorization);
        return new ResultData.Builder<Void>().ok().build();
    }

    /**
     * 分页查询用户反馈
     * @param pageFeedbackCommand 查询参数
     * @return 分页数据
     */
    @GetMapping("pageFeedback")
    public ResultData<Page<FeedbackQueryDTO>> pageFeedback(@Validated(value = FeedbackQuery.class) PageFeedbackCommand pageFeedbackCommand) {
        return new ResultData.Builder<Page<FeedbackQueryDTO>>().ok().data(feedbackAppService.pageFeedback(pageFeedbackCommand)).build();
    }

    /**
     * 查询用户反馈问答
     * @param id 主键
     * @return 查询结果
     */
    @GetMapping("getFeedbackQA")
    public ResultData<FeedbackQADTO> getFeedbackQA(@RequestParam(name = "id") @NotBlank(message = "不能为空") String id) {
        return new ResultData.Builder<FeedbackQADTO>().ok().data(feedbackAppService.getFeedbackQA(id)).build();
    }

    /**
     * 查询用户反馈上文
     * @param id 主键
     * @return 查询结果
     */
    @GetMapping("getFeedbackHistory")
    public ResultData<String> getFeedbackHistory(@RequestParam(name = "id") @NotBlank(message = "不能为空") String id) {
        return new ResultData.Builder<String>().ok().data(feedbackAppService.getFeedbackHistory(id)).build();
    }

    /**
     * 导出用户反馈
     * @param pageFeedbackCommand 查询参数
     */
    @GetMapping("exportFeedback")
    public void exportFeedback(@Validated(value = FeedbackExport.class) PageFeedbackCommand pageFeedbackCommand, HttpServletResponse response) {
        feedbackAppService.exportFeedback(pageFeedbackCommand, response);
    }
}
