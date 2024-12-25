package cn.com.farben.gptcoder.operation.platform.feedback.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.platform.feedback.command.PageFeedbackCommand;
import cn.com.farben.gptcoder.operation.platform.feedback.domain.entity.FeedbackEntity;
import cn.com.farben.gptcoder.operation.platform.feedback.domain.event.FeedbackChangeEvent;
import cn.com.farben.gptcoder.operation.platform.feedback.dto.FeedbackExportDTO;
import cn.com.farben.gptcoder.operation.platform.feedback.dto.FeedbackQueryDTO;
import com.mybatisflex.core.paginate.Page;
import org.springframework.context.ApplicationListener;

import java.util.List;

/**
 * 用户反馈仓储接口
 */
public interface FeedbackRepository extends ApplicationListener<FeedbackChangeEvent> {
    /**
     * 获取用户反馈
     * @param id 主键
     * @return 用户反馈实体
     */
    FeedbackEntity getFeedbackById(String id);

    /**FeedbackRepositoryImpl
     * 保存用户反馈
     * @param feedbackEntity 用户反馈实体
     */
    void saveFeedback(FeedbackEntity feedbackEntity);

    /**
     * 取消反馈
     * @param id 主键
     */
    void removeFeedback(String id);

    /**
     * 获取传入的参数
     * @param id 主键
     * @return 入参
     */
    String getParams(String id);

    /**
     * 分页查询用户反馈记录
     * @param command 参数
     * @return 反馈记录列表
     */
    Page<FeedbackQueryDTO> pageFeedback(PageFeedbackCommand command);

    /**
     * 获取用户反馈的参数和回答
     * @param id 主键
     * @return 用户反馈实体
     */
    FeedbackEntity getFeedbackQAById(String id);

    /**
     * 导出用户反馈记录
     * @param command 参数
     * @return 反馈记录列表
     */
    List<FeedbackExportDTO> exportFeedback(PageFeedbackCommand command);
}
