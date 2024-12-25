package cn.com.farben.gptcoder.operation.platform.user.facade;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;
import cn.com.farben.commons.web.ResultData;
import cn.com.farben.gptcoder.operation.commons.user.utils.UserInfoUtils;
import cn.com.farben.gptcoder.operation.platform.user.application.service.PluginUserAppService;
import cn.com.farben.gptcoder.operation.platform.user.command.AddPluginUserCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.ChangePluginUserStatusCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.ModifyPluginUserCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.ModifyPluginUserPasswordCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.PluginUserLoginCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.PluginUserLogoutCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.ResetPasswordCommand;
import cn.com.farben.gptcoder.operation.platform.user.dto.PluginUserDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.PluginUserImportDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.PluginUserLoginDTO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums.PluginUserStatusEnum;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/plugin")
@Validated
public class PluginUserController {
    private static final Log logger = LogFactory.get();

    private final PluginUserAppService pluginUserAppService;

    @Value("${coder.user.plugin-user-default-password}")
    private String defaultPassword;

    @GetMapping("/check/{uid}")
    public ResultData<String> checkUser(@PathVariable String uid) {
        return new ResultData.Builder<String>().ok().data(pluginUserAppService.checkUser(uid)).build();
    }

    @GetMapping("/unlock/{uid}")
    public ResultData<String> unlockUser(@PathVariable String uid) {
        return new ResultData.Builder<String>().ok().data(pluginUserAppService.unlockUser(uid)).build();
    }

    /**
     * 插件用户登陆
     * @param loginCommand 登陆命令
     * @return 操作结果
     */
    @PostMapping("/login")
    public ResultData<List<PluginUserLoginDTO>> login(@RequestBody @Valid PluginUserLoginCommand loginCommand) {
        logger.info("插件用户登陆:{}", loginCommand);
        List<PluginUserLoginDTO> loginList = pluginUserAppService.login(loginCommand);
        if (CollUtil.isEmpty(loginList)) {
            return new ResultData.Builder<List<PluginUserLoginDTO>>().ok().build();
        } else {
            return new ResultData.Builder<List<PluginUserLoginDTO>>().error(ErrorCodeEnum.USER_ERROR_99999).data(loginList).message("用户已登陆设备数已达到上限").build();
        }
    }

    /**
     * 根据标识码查询插件用户信息
     * @param uuid 标识码
     * @return 插件用户信息
     */
    @GetMapping("/getUserInfo")
    public ResultData<PluginUserDTO> getUserInfoByUuid(@RequestParam(name = "uuid") String uuid) {
        logger.info("根据标识码查询插件用户信息:{}", uuid);
        PluginUserDTO pluginUserDTO = pluginUserAppService.getUserInfoByUuid(uuid);
        if (Objects.isNull(pluginUserDTO)) {
            return new ResultData.Builder<PluginUserDTO>().error(ErrorCodeEnum.USER_ERROR_A0201).message("用户未登陆").build();
        }
        return new ResultData.Builder<PluginUserDTO>().ok().data(pluginUserDTO).build();
    }

    /**
     * 插件用户登出，从缓存中删除数据
     * @param userLogoutCommand 用户登出命令
     * @return 操作结果
     */
    @PostMapping("/logout")
    public ResultData<Void> logout(@RequestBody @Valid PluginUserLogoutCommand userLogoutCommand) {
        logger.info("插件用户登出:{}", userLogoutCommand);
        pluginUserAppService.logout(userLogoutCommand.getUuid(), userLogoutCommand.getUserId());
        return new ResultData.Builder<Void>().ok().build();
    }

    /**
     * 分页查询插件用户信息
     * @param userName 用户账号/姓名/或工号的查询
     * @param organization 用户机构
     * @param status 用户状态
     * @param pageSize 每页数据量
     * @param pageNo 当前页
     * @return 系统参数
     */
    @RequestMapping("/pageUser")
    public ResultData<Page<PluginUserDTO>> pageUser(@RequestParam(required = false) String userName,
                                                     @RequestParam(required = false) String organization,
                                                     @RequestParam(required = false) PluginUserStatusEnum status,
                                                     @RequestParam long pageSize, @RequestParam long pageNo) {
        String user = UserInfoUtils.getUserInfo().getAccount();
        logger.info("用户[{}]分页查询插件用户，userName: [{}], organization: [{}], pageSize: [{}], page: [{}]",
                user, userName, organization, pageSize, pageNo);
        return new ResultData.Builder<Page<PluginUserDTO>>().ok().data(pluginUserAppService.pageUser(
                userName, organization, status, pageSize, pageNo, user)).build();
    }

    /**
     * 新增插件用户
     * @param addPluginUserCommand 新增插件用户命令
     * @return 操作结果
     */
    @PostMapping("/addUser")
    public ResultData<Void> addUser(@RequestBody @Valid AddPluginUserCommand addPluginUserCommand) {
        logger.info("新增插件用户：[{}]", addPluginUserCommand);
        pluginUserAppService.addUser(addPluginUserCommand);
        return new ResultData.Builder<Void>().ok().message(StrFormatter.format("用户创建成功，默认密码为{}", defaultPassword)).build();
    }

    /**
     * 修改插件用户
     * @param modifyPluginUserCommand 修改插件用户命令
     * @return 操作结果
     */
    @PostMapping("/editUser")
    public ResultData<Void> editUser(@RequestBody @Valid ModifyPluginUserCommand modifyPluginUserCommand) {
        logger.info("修改插件用户：[{}]", modifyPluginUserCommand);
        pluginUserAppService.editUser(modifyPluginUserCommand);
        return new ResultData.Builder<Void>().ok().build();
    }

    /**
     * 删除插件用户
     * @param ids 用户id
     * @return 操作结果
     */
    @DeleteMapping("deleteUser")
    public ResultData<Void> deleteUser(@RequestParam List<String> ids) {
        pluginUserAppService.deleteUser(ids);
        return new ResultData.Builder<Void>().ok().build();
    }

    /**
     * 插件用户重置密码
     * @param resetPasswordCommand 插件用户重置密码命令
     * @return 操作结果
     */
    @PostMapping("/resetPassword")
    public ResultData<Void> resetPassword(@RequestBody @Valid ResetPasswordCommand resetPasswordCommand) {
        pluginUserAppService.resetPassword(resetPasswordCommand.getId());
        return new ResultData.Builder<Void>().ok().message(StrFormatter.format("密码重置成功，默认密码为{}", defaultPassword)).build();
    }

    /**
     * 插件用户修改密码
     * @param modifyPluginUserPasswordCommand 插件用户修改密码命令
     * @return 操作结果
     */
    @PostMapping("/modifyPassword")
    public ResultData<Void> modifyPassword(@RequestBody @Valid ModifyPluginUserPasswordCommand modifyPluginUserPasswordCommand) {
        pluginUserAppService.modifyPassword(modifyPluginUserPasswordCommand.getAccount(), modifyPluginUserPasswordCommand.getPassword(),
                modifyPluginUserPasswordCommand.getNewPassword());
        return new ResultData.Builder<Void>().ok().build();
    }

    /**
     * 启用插件用户
     * @param changePluginUserStatusCommand 更改插件用户状态命令
     * @return 操作结果
     */
    @PostMapping("/enableUser")
    public ResultData<Void> enableUser(@RequestBody @Valid ChangePluginUserStatusCommand changePluginUserStatusCommand) {
        logger.info("启用插件用户：[{}]", changePluginUserStatusCommand);
        pluginUserAppService.enableUser(changePluginUserStatusCommand);

        return new ResultData.Builder<Void>().ok().build();
    }

    /**
     * 禁用插件用户
     * @param changePluginUserStatusCommand 更改插件用户状态命令
     * @return 操作结果
     */
    @PostMapping("/disableUser")
    public ResultData<Void> disableUser(@RequestBody @Valid ChangePluginUserStatusCommand changePluginUserStatusCommand) {
        logger.info("禁用插件用户：[{}]", changePluginUserStatusCommand);
        pluginUserAppService.disableUser(changePluginUserStatusCommand);

        return new ResultData.Builder<Void>().ok().build();
    }

    /**
     * 导入插件用户信息
     * @param file 文件
     * @return 操作结果
     */
    @PostMapping("/import")
    public ResultData<PluginUserImportDTO> importUser(@RequestParam(value = "file") MultipartFile file) {
        return new ResultData.Builder<PluginUserImportDTO>().ok().data(pluginUserAppService.importUser(file)).build();
    }

    /**
     * 用户批导模板下载
     * @param response 响应体
     */
    @GetMapping("/template/export")
    public void templateExport(HttpServletResponse response) {
        try {
            pluginUserAppService.templateExport(response);
        }catch (IOException e) {
            throw new BusinessException(ErrorCodeEnum.ERROR_DOWNLOAD_TEMPLATE);
        }
    }

    /**
     * 下载导入错误文件
     * @param key 响应体
     */
    @GetMapping("/import/error")
    public void exportErrorData(HttpServletResponse response, @RequestParam("key") String key) {
        try {
            pluginUserAppService.exportErrorData(response, key);
        }catch (IOException e) {
            throw new BusinessException(ErrorCodeEnum.ERROR_OUTPUT_FILE_TIP);
        }
    }

    /**
     * 连接测试
     * @return 操作结果
     */
    @PostMapping("/connection_test")
    public ResultData<Void> connectionTest() {
        return new ResultData.Builder<Void>().ok().build();
    }

    public PluginUserController(PluginUserAppService pluginUserAppService) {
        this.pluginUserAppService = pluginUserAppService;
    }
}
