package cn.com.farben.gptcoder.operation.platform.user.facade;

import cn.com.farben.commons.web.ResultData;
import cn.com.farben.gptcoder.operation.platform.user.application.service.SysRoleAppService;
import cn.com.farben.gptcoder.operation.platform.user.command.MultipleIdCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.PrimaryIdCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.role.AddRoleCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.role.EditRoleCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.role.QueryRoleListCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.role.RoleAuthorizeCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.role.RoleRangeAuthCommand;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.AuthRangeListDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysRoleDetailDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysRoleListDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysRoleTreeDTO;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统角色操作接口类
 * @author wuanhui
 */
@RestController
@RequestMapping("/role")
public class SysRoleController {

    private static final Log logger = LogFactory.get();

    private final SysRoleAppService sysRoleAppService;

    public SysRoleController(SysRoleAppService sysRoleAppService) {
        this.sysRoleAppService = sysRoleAppService;
    }

    /**
     * 新增角色
     * @param param 查询参数
     */
    @PostMapping("/add")
    public ResultData<String> addRole(@RequestBody AddRoleCommand param) {
        logger.info("新增角色信息参数：{}", JSONUtil.toJsonStr(param));
        return new ResultData.Builder<String>().ok().data(sysRoleAppService.addRole(param)).build();
    }

    /**
     * 修改角色
     * @param param 查询参数
     */
    @PostMapping("/edit")
    public ResultData<Boolean> editRole(@RequestBody EditRoleCommand param) {
        logger.info("修改角色信息参数：{}", JSONUtil.toJsonStr(param));
        return new ResultData.Builder<Boolean>().ok().data(sysRoleAppService.editRole(param)).build();
    }

    /**
     * 分页查询角色列表
     * @param param 查询参数
     */
    @GetMapping("/list")
    public ResultData<Page<SysRoleListDTO>> roleList(QueryRoleListCommand param) {
        logger.info("查询角色列表参数：{}", JSONUtil.toJsonStr(param));
        return new ResultData.Builder<Page<SysRoleListDTO>>().ok().data(sysRoleAppService.roleList(param)).build();
    }

    /**
     * 删除角色
     * @param param 查询参数
     */
    @DeleteMapping("/delete")
    public ResultData<Boolean> deleteRole(@RequestBody MultipleIdCommand param) {
        logger.info("删除角色列表参数：{}", JSONUtil.toJsonStr(param));
        return new ResultData.Builder<Boolean>().ok().data(sysRoleAppService.deleteRole(param)).build();
    }

    /**
     * 可选角色下拉框
     */
    @GetMapping("/select")
    public ResultData<List<SysRoleTreeDTO>> selectRoleTree() {
        return new ResultData.Builder<List<SysRoleTreeDTO>>().ok().data(sysRoleAppService.selectRoleTree()).build();
    }

    /**
     * 角色授权
     * @param param 参数
     */
    @PostMapping("/authorize")
    public ResultData<Boolean> authorize(@RequestBody RoleAuthorizeCommand param) {
        return new ResultData.Builder<Boolean>().ok().data(sysRoleAppService.authorize(param)).build();
    }

    /**
     * 分页查询角色列表
     * @param param 查询参数
     */
    @GetMapping("/detail")
    public ResultData<SysRoleDetailDTO> roleDetail(PrimaryIdCommand param) {
        return new ResultData.Builder<SysRoleDetailDTO>().ok().data(sysRoleAppService.roleDetail(param)).build();
    }

    /**
     * 角色返回可选下拉框
     */
    @GetMapping("/range/list")
    public ResultData<List<AuthRangeListDTO>> authRangeList() {
        return new ResultData.Builder<List<AuthRangeListDTO>>().ok().data(sysRoleAppService.authRangeList()).build();
    }

    /**
     * 数据范围授权
     * @param param 参数
     */
    @PostMapping("/range/authorize")
    public ResultData<Boolean> rangeAuthorize(@RequestBody RoleRangeAuthCommand param) {
        return new ResultData.Builder<Boolean>().ok().data(sysRoleAppService.rangeAuthorize(param)).build();
    }

}
