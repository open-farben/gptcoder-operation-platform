package cn.com.farben.gptcoder.operation.platform.dictionary.facade;

import cn.com.farben.commons.web.ResultData;
import cn.com.farben.gptcoder.operation.commons.user.dto.SystemDictInfoDTO;
import cn.com.farben.gptcoder.operation.commons.user.dto.SystemDictListDTO;
import cn.com.farben.gptcoder.operation.commons.user.utils.UserInfoUtils;
import cn.com.farben.gptcoder.operation.platform.dictionary.application.service.SystemDictAppService;
import cn.com.farben.gptcoder.operation.platform.dictionary.command.AddDictCommand;
import cn.com.farben.gptcoder.operation.platform.dictionary.command.DictDetailCommand;
import cn.com.farben.gptcoder.operation.platform.dictionary.command.DictListCommand;
import cn.com.farben.gptcoder.operation.platform.dictionary.command.EditDictCommand;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统字典配置
 * @author wuanhui
 */
@RestController
@RequestMapping("/dict")
public class SystemDictController {

    private static final Log logger = LogFactory.get();

    private final SystemDictAppService systemDictAppService;

    public SystemDictController(SystemDictAppService systemDictAppService) {
        this.systemDictAppService = systemDictAppService;
    }


    /**
     * 查询根字典列表
     * @param command 参数
     * @return 查询结果
     */
    @GetMapping("/list")
    public ResultData<Page<SystemDictListDTO>> dictList(DictListCommand command) {
        logger.info("字典列表查询:{}", command);
        return new ResultData.Builder<Page<SystemDictListDTO>>().ok().data(systemDictAppService.dictList(command)).build();
    }

    /**
     * 查询字典配置明细
     * @param command 参数
     * @return 请求结果
     */
    @GetMapping("/detail")
    public ResultData<SystemDictInfoDTO> findDictDetail(@Valid DictDetailCommand command) {
        return new ResultData.Builder<SystemDictInfoDTO>().ok().data(systemDictAppService.findDictDetail(command)).build();
    }

    /**
     * 新增字典
     * @param command 新增字典命令
     * @return 操作结果
     */
    @PostMapping("/add")
    public ResultData<Boolean> addDict(@RequestBody AddDictCommand command) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        logger.info("---------- 新增字典参数 -----------：{}", command);
        return new ResultData.Builder<Boolean>().ok().data(systemDictAppService.addDict(command, operator)).build();
    }

    /**
     * 修改字典
     * @param command 修改字典命令
     * @return 操作结果
     */
    @PostMapping("/edit")
    public ResultData<Boolean> editDict(@RequestBody EditDictCommand command) {
        return new ResultData.Builder<Boolean>().ok().data(systemDictAppService.editDict(command)).build();
    }

}
