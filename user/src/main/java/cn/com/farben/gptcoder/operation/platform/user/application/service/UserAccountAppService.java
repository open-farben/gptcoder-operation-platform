package cn.com.farben.gptcoder.operation.platform.user.application.service;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;
import cn.com.farben.commons.errorcode.exception.IllegalParameterException;
import cn.com.farben.commons.errorcode.exception.OperationNotAllowedException;
import cn.com.farben.commons.web.constants.MvcConstants;
import cn.com.farben.commons.web.utils.RsaUtils;
import cn.com.farben.gptcoder.operation.commons.user.dto.SysRoleCacheDTO;
import cn.com.farben.gptcoder.operation.commons.user.enums.SystemDictCodeTypeEnum;
import cn.com.farben.gptcoder.operation.commons.user.utils.CoderClientUtils;
import cn.com.farben.gptcoder.operation.commons.user.utils.UserInfoUtils;
import cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.utils.LocalCacheUtils;
import cn.com.farben.gptcoder.operation.platform.user.application.component.AccountOrganComponent;
import cn.com.farben.gptcoder.operation.platform.user.command.AddUserAccountCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.DisableUserCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.EditPasswordCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.MultipleIdCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.PrimaryIdCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.UserAccountListCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.UserLoginCommand;
import cn.com.farben.gptcoder.operation.platform.user.domain.UserAccountAggregateRoot;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.SysAccountRoleEntity;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.SysRoleEntity;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.UserAccountEntity;
import cn.com.farben.gptcoder.operation.platform.user.dto.LoginTokenDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.ManagerAccountDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.UserAccountListDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.UserLoginDTO;
import cn.com.farben.gptcoder.operation.platform.user.exception.UserLoginException;
import cn.com.farben.gptcoder.operation.platform.user.exchange.AuthenticationClient;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.SysAccountRoleRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.SysRoleRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.UserAccountRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.service.impl.UserCheck;
import cn.com.farben.gptcoder.operation.platform.user.util.PasswordChecker;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.google.common.collect.Lists;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 *
 * 用户账号应用服务<br>
 * 创建时间：2023/8/15<br>
 * @author ltg
 *
 */
@Component
public class UserAccountAppService {
    private static final Log logger = LogFactory.get();
    private final UserAccountRepository userAccountRepository;

    private final LocalCacheUtils localCache;

    /** 事件发布 */
    private final ApplicationEventPublisher applicationEventPublisher;

    /** 组织机构 */
    private final OrganMngAppService organMngAppService;

    private final SysRoleRepository sysRoleRepository;

    private final SysAccountRoleRepository accountRoleRepository;

    private final AccountOrganComponent accountOrganComponent;

    @Value("${coder.user.plugin-user-default-password:FB123456}")
    private String defPwd;
    @Autowired
    UserCheck userCheck;

    private final AuthenticationClient authenticationClient;
    public String checkUser(String name) {
        return userCheck.checkUser(name);
    }
    public String unlockUser(String name) {
        userCheck.unlockUser(name,0);
        return "ok";
    }

    /**
     * 用户登陆
     * @param loginCommand 登陆命令
     * @return 操作结构
     */
    @Transactional(rollbackFor = Exception.class)
    public LoginTokenDTO login(UserLoginCommand loginCommand, HttpServletRequest request) {
        logger.info("用户登录，参数：[{}]", loginCommand);
        String account = loginCommand.getUserId();
        String password = loginCommand.getPassword();
        try {
            account = RsaUtils.decryptStr(account, KeyType.PrivateKey);
            password = RsaUtils.decryptStr(password, KeyType.PrivateKey);
        } catch (Exception e) {
            logger.error("解析登录信息失败", e);
            throw new UserLoginException("解析登录信息失败");
        }
        if (!userCheck.preLogin(account)){
            logger.error("系统已被锁定，请30分钟后再试！");
            throw new UserLoginException(ErrorCodeEnum.USER_ERROR_A0205,"系统已被锁定，请30分钟后再试！");
        }
        UserCheck.LoginAttemptInfo loginAttemptInfo=userCheck.getInfo(account);

        String errorMessage = "用户名和密码不匹配";
        UserAccountEntity userEntity = userAccountRepository.findByUserid(account);
        if (Objects.isNull(userEntity) || CharSequenceUtil.isBlank(userEntity.getId())) {
            throw new UserLoginException(errorMessage);
        }
        if (!BCrypt.checkpw(password, userEntity.getPwd())) {
            String errorCode = userCheck.checkUser(account);
            userCheck.postLogin(account,loginAttemptInfo,false,0);
            if (errorCode!=null)
                errorMessage=errorCode;
            throw new UserLoginException(errorMessage);
        }
        if (0 != userEntity.getInUse()) {
            throw new UserLoginException("账号已禁用");
        }
        if (0 != userEntity.getLogicDelete()) {
            throw new UserLoginException("账号已删除");
        }
        List<SysRoleCacheDTO> roleList = accountRoleRepository.findRoleList(account);
        if(CharSequenceUtil.isBlank(userEntity.getRoleId()) && CollUtil.isEmpty(roleList)) {
            logger.error("用户未分配角色，禁止登录");
            throw new UserLoginException("该用户暂时没有分配任何菜单权限，请联系管理员。");
        }
        if (userEntity.getLastLoginDate()==null){
            logger.error("用户未修改过初始化密码，禁止登录");
            throw new UserLoginException(ErrorCodeEnum.USER_ERROR_A0123);
        }
        //修改最近登录时间
        userEntity.setLastLoginDate(new Date());
        userEntity.setLastLoginIp(CoderClientUtils.getIpAddress(request));
        userAccountRepository.editAccount(userEntity);
        JSONArray jsonArray = JSONUtil.parseArray(roleList.stream().map(SysRoleCacheDTO::getRoleKey).toList());
        if (jsonArray.isEmpty()) {
            jsonArray.add(userEntity.getRoleId());
        }
        // 获取令牌
        JSONObject body = new JSONObject();
        body.set("userName", account);
        body.set("roles", jsonArray);
        logger.info("登录参数：[{}]", body);

        userCheck.postLogin(account,loginAttemptInfo,true,0);
        return authenticationClient.generateTokenPair(body);
    }

    /**
     * 用户退出登陆
     */
    public boolean logout() {
        String token = Objects.isNull(UserInfoUtils.getUserInfo()) ? null : UserInfoUtils.getUserInfo().getToken();
        if (CharSequenceUtil.isNotBlank(token)) {
            JSONObject body = new JSONObject();
            body.set("accessToken", UserInfoUtils.getUserInfo().getToken());
            authenticationClient.logout(body);
        }
        return true;
    }

    /**
     * 查询登录用户的基本信息、权限、角色等
     * @return 用户信息
     */
    public UserLoginDTO findLoginMsg() {
        UserLoginDTO login = new UserLoginDTO();
        String userId = UserInfoUtils.getUserInfo().getAccount();
        UserAccountEntity entity = userAccountRepository.findByUserid(userId);
        if(entity == null) {
            throw new BusinessException(ErrorCodeEnum.USER_ERROR_A0201);
        }
        BeanUtils.copyProperties(entity, login);
        //职务
        if(StringUtils.isNotBlank(entity.getDuty())) {
            login.setDutyName(localCache.getDictValue(SystemDictCodeTypeEnum.JOB_INFO.getCode(), entity.getDuty()));
        }
        //组织机构
        if(StringUtils.isNotBlank(entity.getOrganization())) {
            login.setOrganizationName(localCache.getOrganValue(login.getOrganization(), () -> organMngAppService.loadOrganMngByNo(login.getOrganization())));
        }
        return login;
    }

    /**
     * 新增用户和修改用户操作
     * @param command 新增用户命令
     */
    @Transactional(rollbackFor = Exception.class)
    public String addUserAccount(AddUserAccountCommand command) {
        //校验角色
        checkRole(command.getRoleId());

        UserAccountAggregateRoot userAccountAggregateRoot = new UserAccountAggregateRoot.Builder(userAccountRepository).build();
        UserAccountEntity entity = userAccountRepository.findByUserid(command.getUserId());
        if(StringUtils.isNotBlank(command.getId()) && entity == null) {
            throw new BusinessException(ErrorCodeEnum.USER_ERROR_A0201);
        }
        if(StringUtils.isBlank(command.getId()) && entity != null) {
            throw new BusinessException(ErrorCodeEnum.USER_ERROR_A0111);
        }
        String createUserId = UserInfoUtils.getUserInfo().getAccount();
        //验证组织机构和职位
        checkOrganAndPos(command);

        // 账号已存在，修改账号信息
        if(StringUtils.isNotBlank(command.getId())) {
            checkAccount(command, false);
            String id = entity.getId();
            BeanUtils.copyProperties(command, entity);
            //2024-01-12，需求1.5版本将角色挪到sys_account_role表
            entity.setRoleId("");
            entity.setId(id);
            if(entity.getLogicDelete() != 0) {
                entity.setLogicDelete(0);
            }
            if(command.getOrganization() != null) {
                entity.setOrganization(command.getOrganization() + "");
            }
            entity.setUpdateBy(createUserId);
            userAccountAggregateRoot.updateAccount(entity);

            //用户角色关联表
            accountRoleRepository.deleteByUser(command.getUserId());
            addAccountRole(command.getRoleId(), command.getUserId());
            return "";
        }

        checkAccount(command, true);
        //新增角色关联
        addAccountRole(command.getRoleId(), command.getUserId());
        UserAccountEntity userAccountEntity = new UserAccountEntity();
        BeanUtils.copyProperties(command, userAccountEntity);
        //2024-01-12，需求1.5版本将角色挪到sys_account_role表
        userAccountEntity.setRoleId("");
        userAccountEntity.setCreateBy(createUserId);
        if(command.getOrganization() != null) {
            userAccountEntity.setOrganization(command.getOrganization() + "");
        }
        userAccountEntity.setPwd(BCrypt.hashpw(defPwd));
        userAccountAggregateRoot.addUserAccount(userAccountEntity);
        return defPwd;
    }

    /**
     * 新增用户与角色关联
     * @param roleId 前端传的角色
     * @param userId 用户账号
     */
    private void addAccountRole(String roleId, String userId) {
        String[] idList = roleId.split(StrPool.COMMA);
        List<SysAccountRoleEntity> list = new ArrayList<>(idList.length);
        for(String item : idList) {
            list.add(new SysAccountRoleEntity(userId, item));
        }
        accountRoleRepository.batchAddUserRole(list);
    }

    /**
     * 修改登录密码
     * @param command 修改登录密码命令
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePwd(EditPasswordCommand command) {
        String userId = command.getUserId();
        UserAccountAggregateRoot userAccountAggregateRoot = new UserAccountAggregateRoot.Builder(userAccountRepository)
                .applicationEventPublisher(applicationEventPublisher).build();

        String password;
        String newPassword;
        try {
            password = RsaUtils.decryptStr(command.getPassword(), KeyType.PrivateKey);
            newPassword = RsaUtils.decryptStr(command.getNewPassword(), KeyType.PrivateKey);
        } catch (Exception e) {
            logger.error("解析密码失败：", e);
            throw new UserLoginException("密码解析失败");
        }

        UserAccountEntity userEntity = userAccountAggregateRoot.findUserById(userId);
        // 校验密码
        if(!BCrypt.checkpw(password, userEntity.getPwd())) {
            throw new BusinessException(ErrorCodeEnum.USER_ERROR_A0130);
        }

        if (userEntity.getPwd().equals(newPassword)) {
            throw new OperationNotAllowedException("密码未改变");
        }
        if (PasswordChecker.isValidPassword(newPassword, userEntity.getUserId())<2){
            logger.error("检查平台用户密码强度失败");
            throw new UserLoginException(ErrorCodeEnum.USER_ERROR_A0122);
        }

        String newPwd = BCrypt.hashpw(newPassword);
        userAccountAggregateRoot.updatePwd(userId, newPwd, userEntity.getLastLoginDate());
        return true;
    }

    /**
     * 删除用户操作
     * @param command 参数
     * @return 成功与否
     */
    public Boolean deleteUser(MultipleIdCommand command) {
        if(StringUtils.isBlank(command.getIds()) ) {
            throw new BusinessException(ErrorCodeEnum.USER_ERROR_0000);
        }
        UserAccountAggregateRoot aggregateRoot = new UserAccountAggregateRoot.Builder(userAccountRepository).build();
        return aggregateRoot.deleteUser(command.getIds());
    }

    /**
     * 重置用户密码
     * @param id 参数
     * @return 是否成功
     */
    public String resetPassword(String id) {
        if(StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCodeEnum.USER_ERROR_0000);
        }
        logger.info("默认密码配置：{}", defPwd);
        String pwd = BCrypt.hashpw(defPwd);
        String updater = UserInfoUtils.getUserInfo().getAccount();
        UserAccountAggregateRoot aggregateRoot = new UserAccountAggregateRoot.Builder(userAccountRepository)
                .applicationEventPublisher(applicationEventPublisher).build();
        aggregateRoot.resetPwd(id, pwd, updater);
        return defPwd;
    }

    /**
     * 分页查询管理用户信息
     * @param command 参数
     * @param token 登录用户token
     * @return 查询结果
     */
    public Page<UserAccountListDTO> pageUserList(UserAccountListCommand command, String token) {
        accountOrganComponent.filterUserAuth(command, token);
        logger.info("分页查询权限：{}", JSONUtil.toJsonStr(command));
        if(command.getPageNo() < 1) {
            command.setPageNo(1);
        }
        if(command.getPageSize() > MvcConstants.MAX_PAGE_SIZE) {
            throw new IllegalParameterException(CharSequenceUtil.format("每页数据量不能大于{}", MvcConstants.MAX_PAGE_SIZE));
        }
        if(command.getPageSize() < 1) {
            command.setPageSize(20);
        }
        
        Page<UserAccountListDTO> pageInfo = userAccountRepository.findAccountList(command);
        if(CollUtil.isEmpty(pageInfo.getRecords())) {
            return pageInfo;
        }

        List<String> userIds = new ArrayList<>(command.getPageSize());
        for(UserAccountListDTO item : pageInfo.getRecords()) {
            userIds.add(item.getUserId());
            //职务
            if(StringUtils.isNotBlank(item.getDuty())) {
                item.setDutyName(localCache.getDictValue(SystemDictCodeTypeEnum.JOB_INFO.getCode(), item.getDuty()));
            }
            //组织机构
            if(StringUtils.isNotBlank(item.getOrganization())) {
                item.setOrganizationName(localCache.getOrganValue(item.getOrganization(), () -> organMngAppService.loadOrganMngByNo(item.getOrganization())));
            }
        }
        List<SysAccountRoleEntity> roleList = accountRoleRepository.findRoleIdsByUser(userIds);
        List<String> userRoles;
        for(UserAccountListDTO item : pageInfo.getRecords()) {
            //角色名称
            userRoles = roleList.stream().filter(e -> e.getUserId().equals(item.getUserId())).map(SysAccountRoleEntity :: getRoleId).toList();
            item.setRoleName(buildShowRoleName(item.getRoleId(), userRoles));
        }
        return pageInfo;
    }

    /**
     * 根据用户主键ID查询管理用户详情
     * @param param 参数
     * @return 用户详情
     */
    public ManagerAccountDTO findAccountInfo(PrimaryIdCommand param) {
        UserAccountEntity entity = userAccountRepository.findById(param.getId());
        ManagerAccountDTO account = new ManagerAccountDTO();
        BeanUtils.copyProperties(entity, account);
        //职务
        if(StringUtils.isNotBlank(account.getDuty())) {
            account.setDutyName(localCache.getDictValue(SystemDictCodeTypeEnum.JOB_INFO.getCode(), account.getDuty()));
        }
        //组装角色列表
        String roleName;
        if(StringUtils.isNotBlank(entity.getRoleId())) {
            roleName = buildShowRoleName(entity.getRoleId(), null);
            account.setRoleList(Arrays.asList(entity.getRoleId().split(StrPool.COMMA)));
        }else {
            List<SysAccountRoleEntity> roleList = accountRoleRepository.findRoleIdsByUser(Lists.newArrayList(entity.getUserId()));
            List<String> roleIds = roleList.stream().filter(e -> e.getUserId().equals(entity.getUserId())).map(SysAccountRoleEntity :: getRoleId).toList();
            account.setRoleList(roleIds);
            roleName = buildShowRoleName("", roleIds);
        }
        account.setRoleName(roleName);
        //组织机构
        if(StringUtils.isNotBlank(account.getOrganization())) {
            account.setOrganizationName(localCache.getOrganValue(account.getOrganization(), () -> organMngAppService.loadOrganMngByNo(account.getOrganization())));
        }
        return account;
    }

    /**
     * 启用、禁用用户信息
     * @param command 参数
     * @param token token信息
     * @return 成功true
     */
    public boolean disableUser(DisableUserCommand command, String token) {
        String updater = UserInfoUtils.getUserInfo().getAccount();
        UserAccountEntity entity = userAccountRepository.findById(command.getId());
        entity.setUpdateBy(updater);
        entity.setInUse(command.getStatus().intValue());
        UserAccountAggregateRoot accountAggregate = new UserAccountAggregateRoot.Builder(userAccountRepository).build();
        return accountAggregate.updateAccount(entity);
    }

    public UserAccountAppService(UserAccountRepository userAccountRepository,
                                 ApplicationEventPublisher applicationEventPublisher,
                                 OrganMngAppService organMngAppService,
                                 LocalCacheUtils localCache, SysRoleRepository sysRoleRepository,
                                 SysAccountRoleRepository accountRoleRepository, AccountOrganComponent accountOrganComponent, AuthenticationClient authenticationClient) {
        this.userAccountRepository = userAccountRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.organMngAppService = organMngAppService;
        this.localCache = localCache;
        this.sysRoleRepository = sysRoleRepository;
        this.accountRoleRepository = accountRoleRepository;
        this.accountOrganComponent = accountOrganComponent;
        this.authenticationClient = authenticationClient;
    }

    /**
     * 校验用户提交数据
     * @param command 参数
     * @param isNew 是否新增
     */
    private void checkAccount(AddUserAccountCommand command, boolean isNew) {
        //校验长度
        if(command.getUserName().length() > 50 || command.getUserName().length() < 2) {
            throw new BusinessException(ErrorCodeEnum.USER_ERROR_A0113);
        }
        if(StringUtils.isNotBlank(command.getJobNumber()) && command.getJobNumber().length() > 20) {
            throw new BusinessException(ErrorCodeEnum.USER_ERROR_A0114);
        }
        //邮箱格式
        if(!CoderClientUtils.checkEmail(command.getEmail())) {
            throw new BusinessException(ErrorCodeEnum.USER_ERROR_A0153);
        }

        //电话格式
        if(StringUtils.isNotBlank(command.getPhone()) && !CoderClientUtils.checkPhone(command.getPhone())) {
            throw new BusinessException(ErrorCodeEnum.USER_ERROR_A0154);
        }

        //职务必填
        if(StringUtils.isBlank(command.getDuty())) {
            throw new BusinessException(ErrorCodeEnum.EMPTY_JOB_INFO);
        }

        //机构必填
        if(StringUtils.isBlank(command.getFullOrganization()) || command.getOrganization() == null) {
            throw new BusinessException(ErrorCodeEnum.EMPTY_ORGAN_MNG);
        }
        if(!isNew) {
            return;
        }
        //校验长度
        if(command.getUserId().length() > 18 || command.getUserId().length() < 3) {
            throw new BusinessException(ErrorCodeEnum.USER_ERROR_A0112);
        }
    }

    /**
     * 校验用户选择的职务和组织机构
     * @param command 参数
     */
    private void checkOrganAndPos(AddUserAccountCommand command) {
        //机构
        if(command.getOrganization() != null && organMngAppService.loadOrganMngByNo(command.getOrganization().toString()) == null) {
            throw new BusinessException(ErrorCodeEnum.NOT_EXIST_ORGAN);
        }
        //职位
        if(StringUtils.isNotBlank(command.getDuty()) && localCache.getDictValue(SystemDictCodeTypeEnum.JOB_INFO.getCode(), command.getDuty()) == null) {
            throw new BusinessException(ErrorCodeEnum.NOT_EXIST_JOB_INFO);
        }
    }

    /**
     * 校验用户角色
     * @param roleId 角色ID
     */
    private void checkRole(String roleId) {
        if(StringUtils.isBlank(roleId)) {
            throw new IllegalParameterException("用户角色不能为空");
        }
        String[] idList = roleId.split(StrPool.COMMA);
        SysRoleCacheDTO roleCache;
        for(String item : idList) {
            roleCache = localCache.getRoleCache(item, () -> buildRoleCache(item));
            if(roleCache == null || StringUtils.isBlank(roleCache.getId())) {
                throw new IllegalParameterException("选择角色不存在");
            }
        }
    }

    /**
     * 用户列表查询显示角色名称
     * @param roleId 角色ID
     * @param userRoles 多个角色ID
     * @return 角色名称，多个以,分割
     */
    private String buildShowRoleName(String roleId, List<String> userRoles) {
        StringBuilder showRoleName = new StringBuilder();
        if(StringUtils.isNotBlank(roleId)) {
            userRoles = Arrays.asList(roleId.split(StrPool.COMMA));
        }
        SysRoleCacheDTO roleCache;
        for(String item : userRoles) {
            roleCache = localCache.getRoleCache(item, () -> buildRoleCache(item));
            if(roleCache == null || StringUtils.isBlank(roleCache.getId())) {
                continue;
            }
            showRoleName.append(roleCache.getRoleName()).append("、");
        }
        return showRoleName.isEmpty() ? "" : showRoleName.substring(0, showRoleName.length() - 1);
    }

    private SysRoleCacheDTO buildRoleCache(String roleId) {
        SysRoleCacheDTO cache = new SysRoleCacheDTO();
        try {
            SysRoleEntity entity = sysRoleRepository.findRoleById(roleId);
            BeanUtils.copyProperties(entity, cache);
        }catch (Exception ignored) {
        }
        return cache;
    }
}
