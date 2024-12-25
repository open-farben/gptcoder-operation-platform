package cn.com.farben.gptcoder.operation.platform.group.facade;

import cn.com.farben.commons.web.ResultData;
import cn.com.farben.commons.web.constants.MvcConstants;
import cn.com.farben.gptcoder.operation.commons.user.utils.UserInfoUtils;
import cn.com.farben.gptcoder.operation.platform.group.application.service.WorkingGroupAppService;
import cn.com.farben.gptcoder.operation.platform.group.command.AccreditGroupCommand;
import cn.com.farben.gptcoder.operation.platform.group.command.AddGroupCommand;
import cn.com.farben.gptcoder.operation.platform.group.command.EditGroupCommand;
import cn.com.farben.gptcoder.operation.platform.group.dto.AccreditChoseDTO;
import cn.com.farben.gptcoder.operation.platform.group.dto.TreeGroupDTO;
import cn.com.farben.gptcoder.operation.platform.group.dto.WorkingGroupDTO;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/group")
@Validated
public class WorkingGroupController {
    private static final Log logger = LogFactory.get();

    private final WorkingGroupAppService workingGroupAppService;

    /**
     * 分页查询工作组
     * @param groupName 工作组名称
     * @param effectiveDay 生效时间
     * @param failureDay 失效时间
     * @param pageSize 每页数据量
     * @param pageNo 当前页
     * @return 工作组信息
     */
    @GetMapping("pageGroup")
    public ResultData<Page<WorkingGroupDTO>> pageGroup(@RequestParam(required = false) String groupName,
                                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate effectiveDay,
                                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate failureDay,
                                                       @RequestParam
                                                        @Min(value = 1, message = "每页数据量不能小于1")
                                                        @Max(value = MvcConstants.MAX_PAGE_SIZE, message = "每页数据量不能大于" + MvcConstants.MAX_PAGE_SIZE)
                                                        long pageSize,
                                                       @RequestParam
                                                        @Min(value = 1, message = "当前页不能小于1")
                                                        long pageNo) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        logger.info("用户[{}]分页查询工作组，groupName:[{}], effectiveDay:[{}], failureDay:[{}], pageSize:[{}], pageNo:[{}]",
                operator, groupName, effectiveDay, failureDay, pageSize, pageNo);
        return new ResultData.Builder<Page<WorkingGroupDTO>>().ok().data(workingGroupAppService.pageGroup(
                groupName, effectiveDay, failureDay, pageSize, pageNo, operator)).build();
    }

    /**
     * 新增工作组
     * @param addGroupCommand 新增工作组命令
     * @return 操作结果
     */
    @PostMapping("/addGroup")
    public ResultData<Void> addGroup(@RequestBody @Valid AddGroupCommand addGroupCommand) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        logger.info("用户[{}]新增工作组：[{}]", operator, addGroupCommand);
        workingGroupAppService.addGroup(addGroupCommand, operator);
        return new ResultData.Builder<Void>().ok().build();
    }

    /**
     * 编辑工作组
     * @param editGroupCommand 编辑工作组命令
     * @return 操作结果
     */
    @PostMapping("/editGroup")
    public ResultData<Void> editGroup(@RequestBody @Valid EditGroupCommand editGroupCommand) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        logger.info("用户[{}]编辑工作组：[{}]", operator, editGroupCommand);
        workingGroupAppService.editGroup(editGroupCommand, operator);
        return new ResultData.Builder<Void>().ok().build();
    }

    /**
     * 获取可授权的插件用户信息
     * @param groupId 工作组id
     * @return 可授权的插件用户
     */
    @GetMapping("authorizedUser")
    public ResultData<AccreditChoseDTO> authorizedUser(@RequestParam String groupId) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        logger.info("获取可授权的插件用户信息");
        return new ResultData.Builder<AccreditChoseDTO>().ok().data(workingGroupAppService.authorizedUser(groupId, operator)).build();
    }

//    /**
//     * 获取知识库可授权的工作组信息
//     * @param knowledgeId 知识库id
//     * @return 可授权的工作组
//     */
//    @GetMapping("authorizedGroup")
//    public ResultData<TreeGroupDTO> authorizedGroup(@RequestParam Long knowledgeId) {
//        String operator = UserInfoUtils.getUserInfo().getAccount();
//        logger.info("获取知识库可授权的工作组信息");
//        return new ResultData.Builder<TreeGroupDTO>().ok().data(workingGroupAppService.authorizedGroup(knowledgeId, operator)).build();
//    }

    /**
     * 工作组授权
     * @param accreditGroupCommand 授权工作组命令
     * @return 操作结果
     */
    @PostMapping("/accreditGroup")
    public ResultData<Void> accreditGroup(@RequestBody @Valid AccreditGroupCommand accreditGroupCommand) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        logger.info("用户[{}]对工作组进行授权：[{}]", operator, accreditGroupCommand);
        workingGroupAppService.accreditGroup(accreditGroupCommand, operator);
        return new ResultData.Builder<Void>().ok().build();
    }

    /**
     * 删除工作组
     * @param ids 工作组id列表
     * @return 操作结果
     */
    @DeleteMapping("removeGroup")
    public ResultData<Void> removeGroup(@RequestParam @NotNull(message = "工作组id不能为空") List<String> ids) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        logger.info("用户[{}]删除工作组：[{}]", operator, ids);
        workingGroupAppService.removeGroup(ids, operator);
        return new ResultData.Builder<Void>().ok().build();
    }

//    /**
//     * 获取git库可授权的工作组信息
//     * @param gitId git库id
//     * @return 可授权的工作组
//     */
//    @GetMapping("authorizedGitGroup")
//    public ResultData<TreeGroupDTO> authorizedGitGroup(@RequestParam Long gitId) {
//        String operator = UserInfoUtils.getUserInfo().getAccount();
//        logger.info("获取git库可授权的工作组信息");
//        return new ResultData.Builder<TreeGroupDTO>().ok().data(workingGroupAppService.authorizedGitGroup(gitId, operator)).build();
//    }

    /**
     * 获取有权限的组织机构的工作组信息
     * @return 树状返回组织机构下的工作组信息
     */
    @GetMapping("treeOrganGroup")
    public ResultData<List<Tree<String>>> treeOrganGroup() {
        return new ResultData.Builder<List<Tree<String>>>().ok().data(workingGroupAppService.treeOrganGroup()).build();
    }

    public WorkingGroupController(WorkingGroupAppService workingGroupAppService) {
        this.workingGroupAppService = workingGroupAppService;
    }
}
