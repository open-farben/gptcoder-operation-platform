package cn.com.farben.gptcoder.operation.platform.user.facade;

import cn.com.farben.commons.web.ResultData;
import cn.com.farben.gptcoder.operation.commons.user.utils.UserInfoUtils;
import cn.com.farben.gptcoder.operation.platform.user.application.service.UserAccountAppService;
import cn.com.farben.gptcoder.operation.platform.user.command.AddUserAccountCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.DisableUserCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.EditPasswordCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.MultipleIdCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.PrimaryIdCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.ResetPasswordCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.UserAccountListCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.UserLoginCommand;
import cn.com.farben.gptcoder.operation.platform.user.dto.LoginTokenDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.ManagerAccountDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.UserAccountListDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.UserLoginDTO;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@Validated
public class UserAccountController {
    private static final Log logger = LogFactory.get();

    private final UserAccountAppService userAccountAppService;


    /**
     * 用户登陆
     * @param loginCommand 登陆命令
     * @return token和用户信息
     */
    @PostMapping("/login")
    public ResultData<LoginTokenDTO> login(@RequestBody @Valid UserLoginCommand loginCommand, HttpServletRequest request) {
        return new ResultData.Builder<LoginTokenDTO>().ok().data(userAccountAppService.login(loginCommand, request)).build();
    }
    @GetMapping("/check/{uid}")
    public ResultData<String> checkUser(@PathVariable String uid) {
        return new ResultData.Builder<String>().ok().data(userAccountAppService.checkUser(uid)).build();
    }
    @GetMapping("/unlock/{uid}")
    public ResultData<String> unlockUser(@PathVariable String uid) {
        return new ResultData.Builder<String>().ok().data(userAccountAppService.unlockUser(uid)).build();
    }

    /**
     * 退出登录
     * @return 请求结果
     */
    @PostMapping("/exit")
    public ResultData<Boolean> logout() {
        return new ResultData.Builder<Boolean>().ok().data(userAccountAppService.logout()).build();
    }

    /**
     * 获取登录人的用户信息
     * @return 请求结果
     */
    @PostMapping("/detail")
    public ResultData<UserLoginDTO> findLoginMsg() {
        return new ResultData.Builder<UserLoginDTO>().ok().data(userAccountAppService.findLoginMsg()).build();
    }

    /**
     * 新增用户
     * @param addUserAccountCommand 新增用户命令
     * @return 操作结果
     */
    @PostMapping("/addUser")
    public ResultData<String> addUserAccount(@RequestBody @Valid AddUserAccountCommand addUserAccountCommand) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        logger.info("[{}]新增用户[{}]", operator, addUserAccountCommand);
        return new ResultData.Builder<String>().ok().data(userAccountAppService.addUserAccount(addUserAccountCommand)).build();
    }

    /**
     * 删除用户
     * @param command 删除用户信息参数
     * @return 操作结果
     */
    @DeleteMapping("/deleteUser")
    public ResultData<Boolean> deleteUserAccount(@RequestBody MultipleIdCommand command) {
        return new ResultData.Builder<Boolean>().ok().data(userAccountAppService.deleteUser(command)).build();
    }

    /**
     * 修改登录密码
     * @param editPasswordCommand 修改密码命令
     * @return 操作结果
     */
    @PostMapping("/pwd/edit")
    public ResultData<Boolean> updatePwd(@RequestBody @Valid EditPasswordCommand editPasswordCommand) {
//        String operator = UserInfoUtils.getUserInfo().getAccount();
        logger.info("[{}]修改密码[{}]", editPasswordCommand.getUserId(), editPasswordCommand);
        return new ResultData.Builder<Boolean>().ok().data(userAccountAppService.updatePwd(editPasswordCommand)).build();
    }

    /**
     * 重置登录密码
     * @param command 参数
     * @return 请求结果
     */
    @PostMapping("/pwd/reset")
    public ResultData<String> resetPwd(@RequestBody ResetPasswordCommand command) {
        return new ResultData.Builder<String>().ok().data(userAccountAppService.resetPassword(command.getId())).build();
    }

    /**
     * 分页查询用户列表
     * @param command 参数
     * @return 查询结果
     */
    @GetMapping("/pageUser")
    public ResultData<Page<UserAccountListDTO>> pageUserList(UserAccountListCommand command) {
        logger.info("=查询用户列表参数：：[{}]", command);
        String operator = UserInfoUtils.getUserInfo().getAccount();
        return new ResultData.Builder<Page<UserAccountListDTO>>().ok().data(userAccountAppService.pageUserList(command, operator)).build();
    }

    /**
     * 查询指定管理用户详情
     * @param param 参数
     * @return 请求结果
     */
    @GetMapping("/info")
    public ResultData<ManagerAccountDTO> findAccountInfo(PrimaryIdCommand param) {
        return new ResultData.Builder<ManagerAccountDTO>().ok().data(userAccountAppService.findAccountInfo(param)).build();
    }


    /**
     * 启用/禁用用户信息
     * @return 请求结果
     */
    @PostMapping("/disable")
    public ResultData<Boolean> disableUser(@RequestBody DisableUserCommand command) {
        String operator = UserInfoUtils.getUserInfo().getAccount();
        return new ResultData.Builder<Boolean>().ok().data(userAccountAppService.disableUser(command, operator)).build();
    }


    public UserAccountController(UserAccountAppService userAccountAppService) {
        this.userAccountAppService = userAccountAppService;
    }
}
