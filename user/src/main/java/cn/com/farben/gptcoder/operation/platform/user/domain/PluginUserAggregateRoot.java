package cn.com.farben.gptcoder.operation.platform.user.domain;

import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.IllegalParameterException;
import cn.com.farben.commons.errorcode.exception.OperationNotAllowedException;
import cn.com.farben.commons.web.utils.RsaUtils;
import cn.com.farben.gptcoder.operation.commons.user.enums.PluginTypesEnum;
import cn.com.farben.gptcoder.operation.platform.plugin.version.domain.entity.PluginVersionEntity;
import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.enums.PluginStatusEnum;
import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.facade.PluginVersionRepository;
import cn.com.farben.gptcoder.operation.platform.user.command.PluginUserLoginCommand;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.PluginUserEntity;
import cn.com.farben.gptcoder.operation.platform.user.domain.event.PluginUserChangeEvent;
import cn.com.farben.gptcoder.operation.platform.user.dto.PluginUserDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.PluginUserLoginDTO;
import cn.com.farben.gptcoder.operation.platform.user.exception.UserExistsException;
import cn.com.farben.gptcoder.operation.platform.user.exception.UserLoginException;
import cn.com.farben.gptcoder.operation.platform.user.exception.UserNotExistsException;
import cn.com.farben.gptcoder.operation.platform.user.exception.UserStatusException;
import cn.com.farben.gptcoder.operation.platform.user.exception.UserUpperLimitException;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.RedisConstants;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.UserSystemConstants;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums.PluginUserStatusEnum;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.PluginUserRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.PluginUserPO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.service.impl.UserCheck;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.query.QueryColumn;
import lombok.Getter;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.table.PluginUserTableDef.PLUGIN_USER;

/**
 *
 * 插件用户聚合根<br>
 * 创建时间：2023/8/24<br>
 * @author ltg
 *
 */
@Getter
public class PluginUserAggregateRoot {
    private static final Log logger = LogFactory.get();

    /** 插件用户仓储接口 */
    private final PluginUserRepository pluginUserRepository;

    /** 事件发布 */
    private final ApplicationEventPublisher applicationEventPublisher;

    /** 插件版本仓储接口 */
    private final PluginVersionRepository pluginVersionRepository;

    private final StringRedisTemplate stringRedisTemplate;
    private final UserCheck userCheck;

    private static final String UPPER_MESSAGE = "启用的插件用户数已达到上限";


    /**
     * 插件用户登陆，登陆成功后将用户信息存储到redis
     * @param loginCommand 登陆命令
     * @param pluginUserExpireDay 用户登陆过期时间
     * @return 登陆成功后返回用户信息
     */
    public List<PluginUserLoginDTO> login(PluginUserLoginCommand loginCommand, Long pluginUserExpireDay) {
        String account = loginCommand.getUserId();
        String password = loginCommand.getPassword();
        String uuid = loginCommand.getUuid();
        String decryptAccount;
        String decryptPassword;
        try {
            decryptAccount = RsaUtils.decryptStr(account, KeyType.PrivateKey);
        } catch (Exception e) {
            logger.error("解密用户账号失败", e);
            throw new UserLoginException("解密用户账号失败");
        }
        try {
            decryptPassword = RsaUtils.decryptStr(password, KeyType.PrivateKey);
        } catch (Exception e) {
            logger.error("解密密码失败", e);
            throw new UserLoginException("解密密码失败");
        }

        if (uuid.equals(decryptAccount)) {
            logger.error("登陆失败，uuid不能和账户相同");
            throw new UserLoginException("uuid不能和账户相同");
        }
        if (!userCheck.preLogin(account)){
            logger.error("系统已被锁定，请30分钟后再试！");
            throw new UserLoginException(ErrorCodeEnum.USER_ERROR_A0205,"系统已被锁定，请30分钟后再试！");
        }
        UserCheck.LoginAttemptInfo loginAttemptInfo=userCheck.getInfo(account);
        PluginUserEntity userEntity = pluginUserRepository.findByAccount(decryptAccount);
        if (Objects.isNull(userEntity) || CharSequenceUtil.isBlank(userEntity.getId())) {
            // 用户不存在
            logger.error("登陆失败，用户不存在");
            throw new UserLoginException("用户名和密码不匹配");
        }
        if (!BCrypt.checkpw(decryptPassword, userEntity.getPassword())) {
            // 密码不正确
            logger.error("密码不正确");
            String errorCode = userCheck.checkUser(account);
            userCheck.postLogin(account,loginAttemptInfo,false,1);
            String errorMessage = "用户名和密码不匹配";
            if (errorCode!=null)
                errorMessage=errorCode;
            throw new UserLoginException(errorMessage);
        }

        PluginUserStatusEnum status = userEntity.getStatus();
        if (PluginUserStatusEnum.ENABLE != status) {
            logger.error("账号[{}]未启用，不能登陆", decryptAccount);
            throw new UserLoginException("账号未启用，请联系管理员");
        }
        String uuidKey = RedisConstants.REDIS_PLUGIN_USER_PREFIX + uuid;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(uuidKey))) {
            logger.warn("标识码[{}]重复", uuid);
            throw new UserLoginException("标识码重复");
        }
        if (userEntity.getLastUsedTime()==null){
            logger.error("用户未修改过初始化密码，禁止登录");
            throw new UserLoginException(ErrorCodeEnum.USER_ERROR_A0123);
        }

        String ideName = loginCommand.getIdeName();
        String ideVersion = loginCommand.getIdeVersion();
        String pluginType = loginCommand.getPluginType();
        String pluginVersion = loginCommand.getPluginVersion();
        String machineId = loginCommand.getMachineId();

        checkIde(ideName, ideVersion);
        checkPluginVersion(pluginType, pluginVersion);

        // 获取用户所有登陆信息
        String redisKey = RedisConstants.REDIS_PLUGIN_USER_PREFIX + decryptAccount;
        JSONObject redisJo = new JSONObject();
        JSONArray loginInfoJa = new JSONArray();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            String redisStr = stringRedisTemplate.opsForValue().get(redisKey);
            if (!JSONUtil.isTypeJSONObject((redisStr))) {
                logger.error("redis缓存信息[{}]不正确，不是json对象", redisStr);
                throw new UserLoginException("用户缓存信息不正确，不是json对象");
            }
            redisJo = JSONUtil.parseObj(redisStr);
            loginInfoJa = redisJo.getJSONArray(RedisConstants.LOGIN_INFO_KEY);
            // 处理重复登陆的情况
            loginInfoJa = handleRepeatLogin(loginInfoJa, machineId, ideName, ideVersion);
            if (Objects.isNull(loginInfoJa)) {
                loginInfoJa = new JSONArray();
            }
        }
        PluginUserLoginDTO userLoginDTO = new PluginUserLoginDTO();
        userLoginDTO.setUuid(uuid);
        userLoginDTO.setMachineId(machineId);
        userLoginDTO.setDevice(loginCommand.getDevice());
        userLoginDTO.setLoginTime(LocalDateTime.now());
        userLoginDTO.setIdeName(ideName);
        userLoginDTO.setIdeVersion(ideVersion);
        userLoginDTO.setPluginType(pluginType);
        userLoginDTO.setPluginVersion(pluginVersion);
        loginInfoJa.put(userLoginDTO);
        redisJo.set(RedisConstants.LOGIN_INFO_KEY, loginInfoJa);
        userCheck.postLogin(account,loginAttemptInfo,true,1);
        stringRedisTemplate.opsForValue().set(redisKey, JSONUtil.toJsonStr(redisJo), Duration.ofDays(pluginUserExpireDay));

        PluginUserDTO pluginUserDTO = new PluginUserDTO();
        BeanUtils.copyProperties(userEntity, pluginUserDTO);
        stringRedisTemplate.opsForValue().set(RedisConstants.REDIS_PLUGIN_USER_PREFIX + uuid,
                JSONUtil.toJsonStr(pluginUserDTO), Duration.ofDays(pluginUserExpireDay));
        return Collections.emptyList();
    }

    /**
     * 新增插件用户
     * @param userEntity 插件用户实体
     * @param operator 操作者账号
     * @param defaultPassword 默认密码
     */
    public void addUser(@NonNull PluginUserEntity userEntity, String operator, String defaultPassword) {
        String account = userEntity.getAccount();
        long count = pluginUserRepository.countUserByAccount(account);
        // 账号已存在
        if (count > 0) throw new UserExistsException("账号已存在");

        userEntity.setId(IdUtil.objectId());
        userEntity.setCreateUser(operator);
        userEntity.setUpdateUser(operator);
        if (Objects.isNull(userEntity.getStatus())) {
            // 默认为未启用
            userEntity.setStatus(PluginUserStatusEnum.DISABLE);
        }
        // 设置默认密码
        userEntity.setPassword(BCrypt.hashpw(defaultPassword));
        pluginUserRepository.addUser(userEntity);
    }

    /**
     * 启用插件用户
     * @param id 用户id
     */
    public void enableUser(String id, String operator, List<Integer> organList) {
        if (CollUtil.isEmpty(organList)) {
            throw new OperationNotAllowedException(UserSystemConstants.NO_DATA_PERMISSION_MESSAGE);
        }
        Objects.requireNonNull(id);
        checkUserExistsWithId(id);
        PluginUserEntity userEntity = pluginUserRepository.findById(id);
        String organization = userEntity.getOrganization();
        checkOrganization(organization, organList);
        if (PluginUserStatusEnum.ENABLE == userEntity.getStatus()) {
            throw new UserStatusException("已经是启用状态");
        }
        long count = pluginUserRepository.countEnabledUser();

        Map<QueryColumn, Object> valueMap = new HashMap<>();
        Map<String, Object> whereConditions = Map.of(PLUGIN_USER.ID.getName(), id);
        valueMap.put(PLUGIN_USER.STATUS, PluginUserStatusEnum.ENABLE);
        valueMap.put(PLUGIN_USER.UPDATE_USER, operator);

        updatePluginUser(valueMap, whereConditions);
    }

    /**
     * 禁用插件用户
     * @param id 用户id
     */
    public void disableUser(String id, String operator, List<Integer> organList) {
        Objects.requireNonNull(id);
        checkUserExistsWithId(id);
        PluginUserEntity userEntity = pluginUserRepository.findById(id);
        String organization = userEntity.getOrganization();
        checkOrganization(organization, organList);
        if (PluginUserStatusEnum.DISABLE == userEntity.getStatus()) {
            throw new UserStatusException("已经是禁用状态");
        }

        Map<QueryColumn, Object> valueMap = new HashMap<>();
        Map<String, Object> whereConditions = Map.of(PLUGIN_USER.ID.getName(), id);
        valueMap.put(PLUGIN_USER.STATUS, PluginUserStatusEnum.DISABLE);
        valueMap.put(PLUGIN_USER.UPDATE_USER, operator);

        updatePluginUser(valueMap, whereConditions);

        // 删除登陆缓存信息
        clearUserCache(userEntity);
    }

    /**
     * 编辑插件用户
     * @param userEntity 插件用户实体
     * @param operator 操作者账号
     */
    public void editUser(@NonNull PluginUserEntity userEntity, String operator) {
        String id = userEntity.getId();
        checkUserExistsWithId(id);
        PluginUserStatusEnum newStatus = userEntity.getStatus();
        if (PluginUserStatusEnum.ENABLE == newStatus) {
            // 新状态是启用，判断旧状态
            PluginUserEntity oldEntity = pluginUserRepository.findById(id);

        }

        Map<QueryColumn, Object> valueMap = new HashMap<>();
        Map<String, Object> whereConditions = Map.of(PLUGIN_USER.ID.getName(), id);
        valueMap.put(PLUGIN_USER.ACCOUNT, userEntity.getAccount());
        valueMap.put(PLUGIN_USER.NAME, userEntity.getName());
        valueMap.put(PLUGIN_USER.JOB_NUMBER, userEntity.getJobNumber());
        valueMap.put(PLUGIN_USER.DUTY, userEntity.getDuty());
        valueMap.put(PLUGIN_USER.ORGANIZATION, userEntity.getOrganization());
        valueMap.put(PLUGIN_USER.FULL_ORGANIZATION, userEntity.getFullOrganization());
        valueMap.put(PLUGIN_USER.MOBILE, userEntity.getMobile());
        valueMap.put(PLUGIN_USER.EMAIL, userEntity.getEmail());
        valueMap.put(PLUGIN_USER.STATUS, newStatus);
        valueMap.put(PLUGIN_USER.UPDATE_USER, operator);

        updatePluginUser(valueMap, whereConditions);
    }

    /**
     * 删除插件用户
     * @param id 用户id
     */
    public void removeUser(String id, List<Integer> organList) {
        logger.info("删除插件用户，id：[{}]", id);
        Objects.requireNonNull(id);
        checkUserExistsWithId(id);
        PluginUserEntity userEntity = pluginUserRepository.findById(id);
        checkOrganization(userEntity.getOrganization(), organList);

        // 物理删除
        pluginUserRepository.deleteUser(id);

        // 删除登陆缓存信息
        clearUserCache(userEntity);
    }

    /**
     * 重置插件用户的密码
     * @param id 用户id
     * @param operator 操作者账号
     * @param password 新密码
     */
    public void resetPassword(String id, String operator, String password, List<Integer> organList) {
        checkUserExistsWithId(id);
        PluginUserEntity userEntity = pluginUserRepository.findById(id);
        checkOrganization(userEntity.getOrganization(), organList);

        Map<QueryColumn, Object> valueMap = new HashMap<>();
        Map<String, Object> whereConditions = Map.of(PLUGIN_USER.ID.getName(), id);
        valueMap.put(PLUGIN_USER.PASSWORD, BCrypt.hashpw(password));
        valueMap.put(PLUGIN_USER.LAST_USED_TIME, null);
        valueMap.put(PLUGIN_USER.UPDATE_USER, operator);

        updatePluginUser(valueMap, whereConditions);
    }

    /**
     * 插件用户修改密码
     * @param account 用户账号
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    public void modifyPassword(String account, String oldPassword, String newPassword) {
        long count = pluginUserRepository.countUserByAccount(account);
        if (count <= 0) {
            // 账号不存在
            throw new UserNotExistsException();
        }
        PluginUserEntity userEntity = pluginUserRepository.findByAccount(account);
        if (!BCrypt.checkpw(oldPassword, userEntity.getPassword())) {
            // 密码不正确
            logger.error("用户[{}]修改密码失败，密码不正确");
            throw new OperationNotAllowedException("原密码不正确");
        }
        if (oldPassword.equals(newPassword)) {
            logger.error("用户[{}]修改密码失败，密码未改变");
            throw new OperationNotAllowedException("密码未改变");
        }

        Map<QueryColumn, Object> valueMap = new HashMap<>();
        Map<String, Object> whereConditions = Map.of(PLUGIN_USER.ACCOUNT.getName(), account);
        valueMap.put(PLUGIN_USER.PASSWORD, BCrypt.hashpw(newPassword));
        valueMap.put(PLUGIN_USER.UPDATE_USER, account);
        if (Objects.isNull(userEntity.getLastUsedTime())) {
            valueMap.put(PLUGIN_USER.LAST_USED_TIME, LocalDateTime.now());
        }

        updatePluginUser(valueMap, whereConditions);
    }

    public static class Builder {
        /** 插件用户仓储接口 */
        private final PluginUserRepository pluginUserRepository;

        /** 事件发布 */
        private ApplicationEventPublisher applicationEventPublisher;

        /** 插件版本仓储接口 */
        private PluginVersionRepository pluginVersionRepository;
        private UserCheck userCheck;

        private StringRedisTemplate stringRedisTemplate;

        public Builder(PluginUserRepository pluginUserRepository) {
            this.pluginUserRepository = pluginUserRepository;
        }

        public PluginUserAggregateRoot.Builder applicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
            this.applicationEventPublisher = applicationEventPublisher;
            return this;
        }

        public PluginUserAggregateRoot.Builder stringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
            this.stringRedisTemplate = stringRedisTemplate;
            return this;
        }

        public PluginUserAggregateRoot.Builder pluginVersionRepository(PluginVersionRepository pluginVersionRepository) {
            this.pluginVersionRepository = pluginVersionRepository;
            return this;
        }
        public PluginUserAggregateRoot.Builder pluginUserCheck(UserCheck userCheck) {
            this.userCheck = userCheck;
            return this;
        }

        public PluginUserAggregateRoot build() {
            return new PluginUserAggregateRoot(this);
        }
    }

    /**
     * 校验机构数据权限
     * @param organization 所属机构
     * @param organList 有权限的机构列表
     */
    private void checkOrganization(String organization, List<Integer> organList) {
        if (CollUtil.isEmpty(organList)) {
            throw new OperationNotAllowedException(UserSystemConstants.NO_DATA_PERMISSION_MESSAGE);
        }
        if (CharSequenceUtil.isBlank(organization)) {
            return;
        }
        try {
            Integer iOrganization = Integer.parseInt(organization);
            if (!organList.contains(iOrganization)) {
                throw new OperationNotAllowedException(UserSystemConstants.NO_DATA_PERMISSION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0400, "机构不正确");
        }
    }

    private void updatePluginUser(Map<QueryColumn, Object> valueMap, Map<String, Object> whereConditions) {
        applicationEventPublisher.publishEvent(new PluginUserChangeEvent(new DataChangeRecord<>(PluginUserPO.class, valueMap,
                null, whereConditions, null, null)));
    }

    private void checkUserExistsWithId(String id) {
        long count = pluginUserRepository.countUserById(id);
        if (count <= 0) {
            // 账号不存在
            logger.error("用户[{}]不存在", id);
            throw new UserNotExistsException("用户不存在");
        }
    }

    /**
     * 登陆时ide名称和ide版本要么都不传，要么都传
     * @param ideName ide名称
     * @param ideVersion ide版本
     */
    private void checkIde(String ideName, String ideVersion) {
        if ((CharSequenceUtil.isBlank(ideName) && CharSequenceUtil.isNotBlank(ideVersion)) ||
                (CharSequenceUtil.isBlank(ideVersion) && CharSequenceUtil.isNotBlank(ideName))) {
            // 必须同时传ide名称和ide版本
            logger.error("ide名称和ide版本必须同时存在");
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0200, "ide名称和ide版本必须同时存在");
        }
    }

    private PluginTypesEnum getPluginType(String pluginType) {
        for (PluginTypesEnum pluginTypesEnum : PluginTypesEnum.values()) {
            if (pluginTypesEnum.getType().equalsIgnoreCase(pluginType)) {
                return pluginTypesEnum;
            }
        }
        return null;
    }

    private void checkPluginVersion(String pluginType, String version) {
        if ((CharSequenceUtil.isBlank(pluginType) && CharSequenceUtil.isNotBlank(version)) ||
                (CharSequenceUtil.isBlank(version) && CharSequenceUtil.isNotBlank(pluginType))) {
            // 必须同时传插件类型和插件版本
            logger.error("插件类型和插件版本必须同时存在");
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0200, "插件类型和插件版本必须同时存在");
        }
        if (CharSequenceUtil.isBlank(pluginType)) return;
        PluginTypesEnum type = getPluginType(pluginType);
        if (Objects.isNull(type)) {
            logger.error("插件类型不存在");
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0200, "插件类型不存在");
        }
        PluginVersionEntity pluginVersionEntity = pluginVersionRepository.getPluginByTypeAndVersion(type, version);
        // 版本已不能使用
        if (PluginStatusEnum.RELEASED != pluginVersionEntity.getStatus()) {
            logger.error("登陆失败，当前版本已不可使用");
            throw new UserLoginException("当前版本已不可使用");
        }
    }

    /**
     * 处理插件用户重复登陆的情况
     * @param loginInfoJa 已登陆的信息
     * @param machineId 机器id
     * @param ideName ide名称
     * @param ideVersion ide版本
     */
    private JSONArray handleRepeatLogin(JSONArray loginInfoJa, String machineId, String ideName, String ideVersion) {
        if (CharSequenceUtil.isBlank(ideName) || Objects.isNull(loginInfoJa) || loginInfoJa.isEmpty()) return loginInfoJa;
        List<PluginUserLoginDTO> loginList = JSONUtil.toList(loginInfoJa, PluginUserLoginDTO.class);
        PluginUserLoginDTO lastSameDto = loginList.stream().filter(
                dto -> ideName.equalsIgnoreCase(dto.getIdeName()) && ideVersion.equalsIgnoreCase(dto.getIdeVersion())
                && machineId.equalsIgnoreCase(dto.getMachineId())).findFirst()
                        .orElse(null);
        if (Objects.nonNull(lastSameDto)) {
            // 重复登录，自动踢出上一次登陆信息
            loginList.remove(lastSameDto);
            stringRedisTemplate.delete(RedisConstants.REDIS_PLUGIN_USER_PREFIX + lastSameDto.getUuid());
            return JSONUtil.parseArray(loginList);
        }
        return loginInfoJa;
    }

    /**
     * 清除插件用户的缓存信息
                .findFirst().orElse(null);
     * @param userEntity 用户实体
     */
    private void clearUserCache(PluginUserEntity userEntity) {
        String redisKey = RedisConstants.REDIS_PLUGIN_USER_PREFIX + userEntity.getAccount();
        JSONArray loginInfoJa;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            loginInfoJa = getLoginCache(redisKey);
            List<PluginUserLoginDTO> list = JSONUtil.toList(loginInfoJa, PluginUserLoginDTO.class);
            for (PluginUserLoginDTO pluginUserLoginDTO : list) {
                // 删除uuid对应的缓存
                stringRedisTemplate.delete(RedisConstants.REDIS_PLUGIN_USER_PREFIX + pluginUserLoginDTO.getUuid());
            }
            // 删除用户缓存
            stringRedisTemplate.delete(redisKey);
        }
    }

    /**
     * 获取用户缓存的登陆信息
     * @param redisKey redis的键
     * @return 缓存的登陆信息
     */
    private JSONArray getLoginCache(String redisKey) {
        String redisStr = stringRedisTemplate.opsForValue().get(redisKey);
        if (!JSONUtil.isTypeJSONObject((redisStr))) {
            logger.error("redis缓存信息[{}]不正确，不是json对象", redisStr);
            throw new UserLoginException("用户缓存信息不正确，不是json对象");
        }
        JSONObject redisJo = JSONUtil.parseObj(redisStr);
        return redisJo.getJSONArray(RedisConstants.LOGIN_INFO_KEY);
    }

    private PluginUserAggregateRoot(PluginUserAggregateRoot.Builder builder) {
        this.pluginUserRepository = builder.pluginUserRepository;
        this.pluginVersionRepository = builder.pluginVersionRepository;
        this.applicationEventPublisher = builder.applicationEventPublisher;
        this.stringRedisTemplate = builder.stringRedisTemplate;
        this.userCheck = builder.userCheck;
    }
}
