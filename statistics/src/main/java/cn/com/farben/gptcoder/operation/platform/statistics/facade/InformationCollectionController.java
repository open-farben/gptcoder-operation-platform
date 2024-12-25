package cn.com.farben.gptcoder.operation.platform.statistics.facade;

import cn.com.farben.commons.web.ResultData;
import cn.com.farben.commons.web.constants.MvcConstants;
import cn.com.farben.gptcoder.operation.commons.user.utils.CoderClientUtils;
import cn.com.farben.gptcoder.operation.platform.statistics.application.service.InformationCollectionAppService;
import cn.com.farben.gptcoder.operation.platform.statistics.command.PluginUseCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.AdoptionInfoDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.exception.IncorrectDateException;
import cn.hutool.core.text.CharSequenceUtil;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 信息采集接口，采集插件使用的信息
 */
@RestController
@RequestMapping("/option")
@Validated
public class InformationCollectionController {
    private final InformationCollectionAppService informationCollectionService;

    /**
     * 保存用户代码采样数据接口
     * @param useCommand 插件使用记录命令
     * @param request http请求
     * @return 返回值
     */
    @Deprecated(since = "1.8.0", forRemoval = true)
    @PostMapping("/save")
    public ResultData<Boolean> addAdoptionInfo(@RequestBody @Valid PluginUseCommand useCommand, HttpServletRequest request) {
        String clientIp = CoderClientUtils.getIpAddress(request);
        return new ResultData.Builder<Boolean>().ok().data(informationCollectionService.addAdoptionInfo(clientIp, useCommand)).build();
    }


    /**
     * 分页查询插件使用信息
     * @param userId 用户ID
     * @param startTime 开始时间，格式：yyyy-MM-dd
     * @param endTime 结束时间, 格式：yyyy-MM-dd
     * @param pageSize 每页数据量
     * @param pageNo 页码
     * @return 插件使用信息
     */
    @RequestMapping("list")
    public ResultData<Page<AdoptionInfoDTO>> pageAdoptionInfo(@RequestParam(required = false) String userId,
                                                               @RequestParam(required = false) String startTime,
                                                               @RequestParam(required = false) String endTime,
                                                               @RequestParam
                                                               @Min(value = 1, message = "每页数据量不能小于1")
                                                               @Max(value = MvcConstants.MAX_PAGE_SIZE, message = "每页数据量不能大于" + MvcConstants.MAX_PAGE_SIZE)
                                                               long pageSize,
                                                               @RequestParam
                                                               @Min(value = 1, message = "当前页不能小于1")
                                                               long pageNo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (!CharSequenceUtil.isEmpty(startTime)) {
            // 校验开始时间
            try {
                formatter.parse(startTime,  LocalDate::from);
            } catch (DateTimeParseException e) {
                throw new IncorrectDateException("开始时间不正确，正确格式：yyyy-MM-dd");
            }
        }
        if (!CharSequenceUtil.isEmpty(endTime)) {
            // 校验结束时间
            try {
                formatter.parse(endTime,  LocalDate::from);
            } catch (DateTimeParseException e) {
                throw new IncorrectDateException("结束时间不正确，正确格式：yyyy-MM-dd");
            }
        }
        return new ResultData.Builder<Page<AdoptionInfoDTO>>().ok().data(informationCollectionService
                .pageAdoptionInfo(userId, startTime, endTime, pageSize, pageNo)).build();
    }

    public InformationCollectionController(InformationCollectionAppService informationCollectionService) {
        this.informationCollectionService = informationCollectionService;
    }
}
