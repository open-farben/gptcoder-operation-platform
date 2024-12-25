package cn.com.farben.gptcoder.operation.platform.plugin.version.facade;

import cn.com.farben.commons.web.ResultData;
import cn.com.farben.gptcoder.operation.commons.user.enums.PluginTypesEnum;
import cn.com.farben.gptcoder.operation.commons.user.utils.UserInfoUtils;
import cn.com.farben.gptcoder.operation.platform.plugin.version.application.service.PluginVersionAppService;
import cn.com.farben.gptcoder.operation.platform.plugin.version.command.AddVersionCommand;
import cn.com.farben.gptcoder.operation.platform.plugin.version.command.ChangePluginStatusCommand;
import cn.com.farben.gptcoder.operation.platform.plugin.version.command.CheckdVersionCommand;
import cn.com.farben.gptcoder.operation.platform.plugin.version.command.ModifyPluginVersionCommand;
import cn.com.farben.gptcoder.operation.platform.plugin.version.dto.PluginAnalysisDTO;
import cn.com.farben.gptcoder.operation.platform.plugin.version.dto.PluginDownloadDTO;
import cn.com.farben.gptcoder.operation.platform.plugin.version.dto.PluginTypesDTO;
import cn.com.farben.gptcoder.operation.platform.plugin.version.dto.PluginVersionDTO;
import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.enums.PluginStatusEnum;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/plugin")
@Validated
public class PluginVersionController {
    private static final Log logger = LogFactory.get();

    private final PluginVersionAppService pluginVersionAppService;

    /**
     * 查看系统支持的IDE类型
     * @return IDE列表
     */
    @GetMapping("listPluginTypes")
    public ResultData<List<PluginTypesDTO>> listPluginTypes() {
        logger.info("查看系统支持的IDE类型");
        return new ResultData.Builder<List<PluginTypesDTO>>().ok().data(pluginVersionAppService.listPluginTypes()).build();
    }

    /**
     * 解析插件文件
     * @param pluginFile 插件文件
     * @param id 主键
     * @return 解析结果
     */
    @PostMapping("/analysisPluginFile")
    public ResultData<PluginAnalysisDTO> analysisPluginFile(@RequestParam("pluginFile") @NotNull(message = "必须上传插件文件") MultipartFile pluginFile,
                                                            @RequestParam(value = "id", required = false) String id) {
        logger.info("解析插件文件:pluginFile -> [{}], id -> [{}]", pluginFile, id);
        return new ResultData.Builder<PluginAnalysisDTO>().ok().data(pluginVersionAppService.analysisPluginFile(pluginFile, id,
                UserInfoUtils.getUserInfo().getAccount())).build();
    }

    /**
     * 新增版本
     * @param addVersionCommand 新增版本命令
     * @return 操作结果
     */
    @PostMapping("/addPlugin")
    public ResultData<Void> addVersion(@RequestBody @Valid AddVersionCommand addVersionCommand) {
        logger.info("新增版本:{}", addVersionCommand);
        pluginVersionAppService.addVersion(addVersionCommand);
        return new ResultData.Builder<Void>().ok().build();
    }

    /**
     * 查询可供下载的插件信息
     * @return 插件列表
     */
    @GetMapping("listDownloadPlugins")
    public ResultData<List<PluginDownloadDTO>> listDownloadPlugins() {
        return new ResultData.Builder<List<PluginDownloadDTO>>().ok().data(pluginVersionAppService.listDownloadPlugins()).build();
    }

    /**
     * 下载插件文件
     */
    @GetMapping("downloadPlugin")
    public void downloadPlugin(@RequestParam("id") String id, HttpServletResponse response) {
        pluginVersionAppService.downloadPlugin(id, response);
    }

    /**
     * 分页查询版本信息
     * @param status 插件状态
     * @param type 插件类型
     * @param version 版本号
     * @param pageSize 每页数据量
     * @param pageNo 当前页
     * @return 插件版本列表
     */
    @GetMapping("pagePlugin")
    public ResultData<Page<PluginVersionDTO>> pagePlugin(@RequestParam(required = false) PluginStatusEnum status,
                                                        @RequestParam(required = false) PluginTypesEnum type,
                                                          @RequestParam(required = false) String version,
                                                        @RequestParam long pageSize, @RequestParam long pageNo) {
        logger.info("分页查询版本信息，status: [{}], type: [{}], version: [{}], pageSize: [{}], page: [{}]", status, type, version, pageSize, pageNo);
        return new ResultData.Builder<Page<PluginVersionDTO>>().ok().data(pluginVersionAppService.pagePlugin(status, type, version, pageSize, pageNo)).build();
    }

    /**
     * 改变插件版本状态
     * @param changePluginStatusCommand 改变插件状态命令
     * @return 操作结果
     */
    @PostMapping("/changeStatus")
    public ResultData<Void> changeStatus(@RequestBody @Valid ChangePluginStatusCommand changePluginStatusCommand) {
        logger.info("改变插件版本状态:{}", changePluginStatusCommand);
        pluginVersionAppService.changeStatus(changePluginStatusCommand);
        return new ResultData.Builder<Void>().ok().build();
    }

    /**
     * 编辑插件版本
     * @param modifyPluginVersionCommand 编辑插件版本命令
     * @return 操作结果
     */
    @PostMapping("/modifyPlugin")
    public ResultData<Void> modifyPlugin(@RequestBody @Valid ModifyPluginVersionCommand modifyPluginVersionCommand) {
        logger.info("编辑插件版本:{}", modifyPluginVersionCommand);
        pluginVersionAppService.modifyPlugin(modifyPluginVersionCommand);
        return new ResultData.Builder<Void>().ok().build();
    }

    /**
     * 删除插件版本
     * @param ids 插件id列表
     * @return 操作结果
     */
    @DeleteMapping("deletePlugin")
    public ResultData<Void> deletePlugin(@RequestParam List<String> ids) {
        pluginVersionAppService.deletePlugin(ids);
        return new ResultData.Builder<Void>().ok().build();
    }

    /**
     * 检查插件版本是否可用
     * @param checkdVersionCommand 检查版本是否可用命令
     * @return 操作结果
     */
    @PostMapping("/checkVersion")
    public ResultData<Boolean> checkVersion(@RequestBody @Valid CheckdVersionCommand checkdVersionCommand) {
        logger.info("检查插件版本是否可用:{}", checkdVersionCommand);
        return new ResultData.Builder<Boolean>().ok().data(pluginVersionAppService.checkVersion(checkdVersionCommand)).build();
    }

    public PluginVersionController(PluginVersionAppService pluginVersionAppService) {
        this.pluginVersionAppService = pluginVersionAppService;
    }
}
