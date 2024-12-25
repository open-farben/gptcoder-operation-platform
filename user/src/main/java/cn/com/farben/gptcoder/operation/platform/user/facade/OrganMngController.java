package cn.com.farben.gptcoder.operation.platform.user.facade;

import cn.com.farben.commons.web.ResultData;
import cn.com.farben.gptcoder.operation.commons.user.utils.UserInfoUtils;
import cn.com.farben.gptcoder.operation.platform.user.application.service.OrganMngAppService;
import cn.com.farben.gptcoder.operation.platform.user.command.MultipleIdCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.PrimaryIdCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.organ.AddOrganCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.organ.EditOrganCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.organ.OrganListCommand;
import cn.com.farben.gptcoder.operation.platform.user.dto.OrganKnowTreeRetDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.OrganMngDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.OrganMngListDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.OrganMngTreeDTO;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 系统组织架构操作接口类
 * @author wuanhui
 */
@RestController
@RequestMapping("/organ")
public class OrganMngController {

    private static final Log logger = LogFactory.get();

    private final OrganMngAppService organMngAppService;

    public OrganMngController(OrganMngAppService organMngAppService) {
        this.organMngAppService = organMngAppService;
    }

    /**
     * 查询返回组织机构树
     * @return 请求结果
     */
    @GetMapping("/tree")
    public ResultData<List<OrganMngTreeDTO>> organTree() {
        return new ResultData.Builder<List<OrganMngTreeDTO>>().ok().data(organMngAppService.buildOrganTree()).build();
    }

    /**
     * 分页查询机构的下级机构信息
     * @param command 参数
     * @return 请求结果
     */
    @GetMapping("/page/list")
    public ResultData<Page<OrganMngListDTO>> organList(OrganListCommand command) {
        logger.info("机构列表参数:{}", command);
        return new ResultData.Builder<Page<OrganMngListDTO>>().ok().data(organMngAppService.organList(command)).build();
    }

    /**
     * 分页查询机构的下级机构信息
     * @param request http请求
     * @return 请求结果
     */
    @GetMapping("/detail")
    public ResultData<OrganMngDTO> organDetail(PrimaryIdCommand request) {
        return new ResultData.Builder<OrganMngDTO>().ok().data(organMngAppService.organDetail(request.getId())).build();
    }

    /**
     * 新增机构信息
     * @param command 参数
     * @return 请求结果
     */
    @PostMapping("/add")
    public ResultData<Boolean> addOrgan(@RequestBody AddOrganCommand command) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        logger.info("---------- 新增系统机构信息 -------------:{}", command);
        return new ResultData.Builder<Boolean>().ok().data(organMngAppService.addOrgan(command, operator)).build();
    }

    /**
     * 修改机构信息
     * @param command 参数
     * @return 请求结果
     */
    @PostMapping("/update")
    public ResultData<Boolean> editOrgan(@RequestBody EditOrganCommand command) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        logger.info("---------- 修改系统机构信息 -------------:{}", command);
        return new ResultData.Builder<Boolean>().ok().data(organMngAppService.editOrgan(command, operator)).build();
    }

    /**
     * 删除机构信息
     * @param command 参数
     * @return 请求结果
     */
    @DeleteMapping("/delete")
    public ResultData<Boolean> deleteOrgan(@RequestBody MultipleIdCommand command) {
        return new ResultData.Builder<Boolean>().ok().data(organMngAppService.deleteOrgan(command)).build();
    }
}
