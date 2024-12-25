package cn.com.farben.gptcoder.operation.platform.user.facade;

import cn.com.farben.commons.web.ResultData;
import cn.com.farben.gptcoder.operation.platform.user.application.service.SysMenuAppService;
import cn.com.farben.gptcoder.operation.platform.user.command.PrimaryIdCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.role.AddMenuCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.role.DisableMenuCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.role.EditMenuCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.role.QueryMenuCommand;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysMenuDetailDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysMenuListDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysMenuTreeDTO;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统菜单配置操作接口类
 * @author wuanhui
 */
@RestController
@RequestMapping("/menu")
public class SysMenuController {

    private static final Log logger = LogFactory.get();

    private final SysMenuAppService sysMenuAppService;

    public SysMenuController(SysMenuAppService sysMenuAppService) {
        this.sysMenuAppService = sysMenuAppService;
    }

    /**
     * 分页查询菜单列表（前端根据数据构建结构树）
     * @param param 查询参数
     */
    @GetMapping("/list")
    public ResultData<List<SysMenuListDTO>> list(QueryMenuCommand param) {
        logger.info("查询菜单列表参数：{}", JSONUtil.toJsonStr(param));
        return new ResultData.Builder<List<SysMenuListDTO>>().ok().data(sysMenuAppService.list(param)).build();
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @GetMapping(value = "/detail")
    public ResultData<SysMenuDetailDTO> getInfo(PrimaryIdCommand param) {
        return new ResultData.Builder<SysMenuDetailDTO>().ok().data(sysMenuAppService.getInfo(param)).build();
    }

    /**
     * 新建、编辑菜单时，可选择的下拉树列表
     */
    @GetMapping("/tree")
    public ResultData<List<SysMenuTreeDTO>> selectMenuTree(@RequestParam(required = false) String type,
                                                           @RequestParam(required = false) String auth) {
        return new ResultData.Builder<List<SysMenuTreeDTO>>().ok().data(sysMenuAppService.selectMenuTree(type, auth)).build();
    }

    /**
     * 新增菜单
     */
    @PostMapping("/add")
    public ResultData<Boolean> addMenu(@RequestBody AddMenuCommand param) {
        logger.info("新增菜单参数：{}", JSONUtil.toJsonStr(param));
        return new ResultData.Builder<Boolean>().ok().data(sysMenuAppService.addMenu(param)).build();
    }

    /**
     * 修改菜单
     */
    @PostMapping("/edit")
    public ResultData<Boolean> editMenu(@RequestBody EditMenuCommand param) {
        logger.info("修改菜单参数：{}", JSONUtil.toJsonStr(param));
        return new ResultData.Builder<Boolean>().ok().data(sysMenuAppService.editMenu(param)).build();
    }

    /**
     * 获取登录用户的菜单列表（后台返回列表数据，前端去构建菜单树）
     */
    @GetMapping("/router/list")
    public ResultData<List<SysMenuListDTO>> loginRouterList() {
        return new ResultData.Builder<List<SysMenuListDTO>>().ok().data(sysMenuAppService.findLoginMenu()).build();
    }


    /**
     * 删除菜单
     * @param param 参数
     */
    @DeleteMapping("/delete")
    public ResultData<Boolean> deleteMenu(@RequestBody PrimaryIdCommand param) {
        return new ResultData.Builder<Boolean>().ok().data(sysMenuAppService.deleteMenu(param)).build();
    }

    /**
     * 菜单启用、禁用
     * @param param 参数
     */
    @PostMapping("/disable")
    public ResultData<Boolean> disableMenu(@RequestBody DisableMenuCommand param) {
        return new ResultData.Builder<Boolean>().ok().data(sysMenuAppService.disableMenu(param)).build();
    }
}
