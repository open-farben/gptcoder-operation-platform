package cn.com.farben.gptcoder.operation.platform.statistics.facade;

import cn.com.farben.commons.web.ResultData;
import cn.com.farben.commons.web.constants.MvcConstants;
import cn.com.farben.gptcoder.operation.commons.user.utils.UserInfoUtils;
import cn.com.farben.gptcoder.operation.platform.statistics.application.service.PluginStatisticsAppService;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.PluginFunctionStatisticsDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.PluginUsageStatisticsDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.enums.OrderEnum;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 插件使用记录接口
 */
@RestController
@RequestMapping("/need_delete_plugin")//此处为删除标记
@Validated
public class PluginStatisticsController {
    private static final Log logger = LogFactory.get();
    private final PluginStatisticsAppService pluginStatisticsAppService;

    /**
     * 分页查询插件使用信息
     * @param startDay 查询起始日期，格式：yyyy-MM-dd
     * @param endDay 查询截止日期，格式：yyyy-MM-dd
     * @param fullOrganNo 用户机构
     * @param orderStr 排序字段
     * @param orderBy 排序类型(ASC、DESC)
     * @param searchText 查询文本，可根据账号、姓名、工号进行模糊查询
     * @param pageSize 每页数据量
     * @param pageNo 当前页
     * @return 插件使用信息
     */
    @GetMapping("/statistics/pluginUsage")
    public ResultData<Page<PluginUsageStatisticsDTO>> pagePluginUsageStatistics(@RequestParam String startDay,
                                                                                @RequestParam String endDay,
                                                                                @RequestParam(required = false) String fullOrganNo,
                                                                                @RequestParam(required = false) String orderStr,
                                                                                @RequestParam(required = false) OrderEnum orderBy,
                                                                                @RequestParam(required = false) String searchText,
                                                                                @RequestParam
                                                                                     @Min(value = 1, message = "每页数据量不能小于1")
                                                                                     @Max(value = MvcConstants.MAX_PAGE_SIZE, message = "每页数据量不能大于" + MvcConstants.MAX_PAGE_SIZE)
                                                                                     long pageSize,
                                                                                @RequestParam
                                                                                     @Min(value = 1, message = "当前页不能小于1")
                                                                                     long pageNo) {
        logger.info("分页查询插件使用信息，startDay: [{}], endDay: [{}], orderStr: [{}], orderBy: [{}], searchText: [{}], pageSize: [{}], pageNo: [{}]",
                startDay, endDay, orderStr, orderBy, searchText, pageSize, pageNo);
        String operator = UserInfoUtils.getUserInfo().getAccount();
        return new ResultData.Builder<Page<PluginUsageStatisticsDTO>>().ok().data(pluginStatisticsAppService.pagePluginUsageStatistics(startDay, endDay, fullOrganNo,
                orderStr, orderBy, searchText, pageSize, pageNo, operator)).build();
    }

    /**
     * 导出插件使用信息
     * @param startDay 查询起始日期，格式：yyyy-MM-dd
     * @param endDay 查询截止日期，格式：yyyy-MM-dd
     * @param fullOrganNo 用户机构
     * @param orderStr 排序字段
     * @param orderBy 排序类型(ASC、DESC)
     * @param searchText 查询文本，可根据账号、姓名、工号进行模糊查询
     */
    @GetMapping("/statistics/exportPluginUsage")
    public void exportPluginUsage(@RequestParam String startDay, @RequestParam String endDay,
                                  @RequestParam(required = false) String fullOrganNo,
                                  @RequestParam(required = false) String orderStr,
                                  @RequestParam(required = false) OrderEnum orderBy,
                                  @RequestParam(required = false) String searchText,
                                  HttpServletResponse response) {
        logger.info("导出插件使用信息，startDay: [{}], endDay: [{}], orderStr: [{}], orderBy: [{}], searchText: [{}]",
                startDay, endDay, orderStr, orderBy, searchText);
        String operator = UserInfoUtils.getUserInfo().getAccount();
        pluginStatisticsAppService.exportPluginUsage(startDay, endDay, fullOrganNo, orderStr, orderBy, searchText, response, operator);
    }

    /**
     * 分页查询插件功能使用统计
     * @param startDay 查询起始日期，格式：yyyy-MM-dd
     * @param endDay 查询截止日期，格式：yyyy-MM-dd
     * @param fullOrganNo 用户机构
     * @param orderStr 排序字段
     * @param orderBy 排序类型(ASC、DESC)
     * @param searchText 查询文本，可根据账号、姓名、工号进行模糊查询
     * @param pageSize 每页数据量
     * @param pageNo 当前页
     * @return 插件使用信息
     */
    @GetMapping("/statistics/pluginFunction")
    public ResultData<Page<PluginFunctionStatisticsDTO>> pagePluginFunctionStatistics(@RequestParam String startDay, @RequestParam String endDay,
                                                                                       @RequestParam(required = false) String fullOrganNo,
                                                                                       @RequestParam(required = false) String orderStr,
                                                                                       @RequestParam(required = false) OrderEnum orderBy,
                                                                                       @RequestParam(required = false) String searchText,
                                                                                       @RequestParam
                                                                                           @Min(value = 1, message = "每页数据量不能小于1")
                                                                                           @Max(value = MvcConstants.MAX_PAGE_SIZE, message = "每页数据量不能大于" + MvcConstants.MAX_PAGE_SIZE)
                                                                                           long pageSize,
                                                                                       @RequestParam
                                                                                           @Min(value = 1, message = "当前页不能小于1")
                                                                                           long pageNo) {
        logger.info("分页查询插件功能使用统计，startDay: [{}], endDay: [{}], orderStr: [{}], orderBy: [{}], searchText: [{}], pageSize: [{}], pageNo: [{}]",
                startDay, endDay, orderStr, orderBy, searchText, pageSize, pageNo);
        String operator = UserInfoUtils.getUserInfo().getAccount();
        return new ResultData.Builder<Page<PluginFunctionStatisticsDTO>>().ok().data(pluginStatisticsAppService.pagePluginFunctionStatistics(startDay, endDay, fullOrganNo,
                orderStr, orderBy, searchText, pageSize, pageNo, operator)).build();
    }

    /**
     * 导出插件功能使用统计
     * @param startDay 查询起始日期，格式：yyyy-MM-dd
     * @param endDay 查询截止日期，格式：yyyy-MM-dd
     * @param fullOrganNo 用户机构
     * @param orderStr 排序字段
     * @param orderBy 排序类型(ASC、DESC)
     * @param searchText 查询文本，可根据账号、姓名、工号进行模糊查询
     */
    @GetMapping("/statistics/exportPluginFunction")
    public void exportPluginFunction(@RequestParam String startDay, @RequestParam String endDay,
                                     @RequestParam(required = false) String fullOrganNo,
                                     @RequestParam(required = false) String orderStr,
                                     @RequestParam(required = false) OrderEnum orderBy,
                                     @RequestParam(required = false) String searchText,
                                     HttpServletResponse response) {
        logger.info("导出插件功能使用统计，startDay: [{}], endDay: [{}], orderStr: [{}], orderBy: [{}], searchText: [{}]",
                startDay, endDay, orderStr, orderBy, searchText);
        String operator = UserInfoUtils.getUserInfo().getAccount();
        pluginStatisticsAppService.exportPluginFunction(startDay, endDay, orderStr, fullOrganNo, orderBy, searchText, response, operator);
    }

    public PluginStatisticsController(PluginStatisticsAppService pluginStatisticsAppService) {
        this.pluginStatisticsAppService = pluginStatisticsAppService;
    }
}
