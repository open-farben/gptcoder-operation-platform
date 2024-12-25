package cn.com.farben.gptcoder.operation.platform.statistics.facade;

import cn.com.farben.commons.web.ResultData;
import cn.com.farben.gptcoder.operation.commons.user.utils.UserInfoUtils;
import cn.com.farben.gptcoder.operation.platform.statistics.application.service.DashboardStatService;
import cn.com.farben.gptcoder.operation.platform.statistics.command.DashboardStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.FunUseStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.JobUseFunStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.PersonUseStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.SpeedConfigCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.UserDutyCountStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.DashboardStatDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.FunStatDataDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.UseFunctionDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.UseStatDetailDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 *  首页大盘统计http接口类
 * @date 2023-10-27
 * @author wuanhui
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardStatController {

    private final DashboardStatService statService;

    /**
     * 概览页统计指标接口
     * @param query 参数
     * @return 统计结果
     */
    @GetMapping("/stat")
    public ResultData<DashboardStatDTO> dashboardStat(DashboardStatCommand query) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        return new ResultData.Builder<DashboardStatDTO>().ok().data(statService.dashboardStat(query, operator)).build();
    }

    /**
     * 概览页使用插件人数统计图表
     * @param query 参数
     * @return 统计结果
     */
    @GetMapping("/user")
    public ResultData<List<UseStatDetailDTO>> personUseStat(PersonUseStatCommand query) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        return new ResultData.Builder<List<UseStatDetailDTO>>().ok().data(statService.personUseStat(query, operator)).build();
    }

    /**
     * 概览页功能使用情况统计
     * @param query 参数
     * @return 统计结果
     */
    @GetMapping("/fun")
    public ResultData<List<FunStatDataDTO>> funUseStat(FunUseStatCommand query) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        return new ResultData.Builder<List<FunStatDataDTO>>().ok().data(statService.funUseStat(query, operator)).build();
    }


    /**
     * 概览页各职务使用插件人数统计
     * @param query 参数
     * @return 统计结果
     */
    @GetMapping("/duty/count")
    public ResultData<List<UseFunctionDTO>> userDutyCountStat(UserDutyCountStatCommand query) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        return new ResultData.Builder<List<UseFunctionDTO>>().ok().data(statService.userDutyCountStat(query, operator)).build();
    }

    /**
     * 概览页职务使用插件功能次数统计
     * @param query 参数
     * @return 统计结果
     */
    @GetMapping("/fun/count")
    public ResultData<List<FunStatDataDTO>> userFunctionStat(JobUseFunStatCommand query) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        return new ResultData.Builder<List<FunStatDataDTO>>().ok().data(statService.userJobFunctionStat(query, operator)).build();
    }

    /**
     * 响应速率基线设置
     *
     * @param query 参数
     * @return 操作结果
     */
    @PostMapping("/speed/edit")
    public ResultData<Boolean> speedEdit(@RequestBody SpeedConfigCommand query) {
        return new ResultData.Builder<Boolean>().ok().data(statService.speedEdit(query)).build();
    }

    public DashboardStatController(DashboardStatService dashboardStatService) {
        this.statService = dashboardStatService;
    }
}
