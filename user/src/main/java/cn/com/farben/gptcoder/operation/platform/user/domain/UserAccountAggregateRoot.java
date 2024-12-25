package cn.com.farben.gptcoder.operation.platform.user.domain;

import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.UserAccountEntity;
import cn.com.farben.gptcoder.operation.platform.user.domain.event.UserAccountChangeEvent;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.UserAccountRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.UserAccountPO;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.IdUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.query.QueryColumn;
import lombok.Getter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.table.UserAccountTableDef.USER_ACCOUNT;

/**
 *
 * 用户账号聚合根<br>
 * 创建时间：2023/8/15<br>
 * @author ltg
 *
 */
@Getter
public class UserAccountAggregateRoot {
    private static final Log logger = LogFactory.get();

    /** 用户账号仓储接口 */
    private final UserAccountRepository userAccountRepository;

    /** 事件发布 */
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 新增/修改用户
     * @param userAccountEntity 用户实体
     */
    public Boolean addUserAccount(@NonNull UserAccountEntity userAccountEntity) {
        SecureRandom random = new SecureRandom();
        Snowflake snowflake = IdUtil.getSnowflake(random.nextInt(32), random.nextInt(32));
        userAccountEntity.setId(snowflake.nextIdStr());
        return userAccountRepository.addAccount(userAccountEntity);
    }

    /**
     * 修改用户
     * @param entity 用户实体
     */
    public Boolean updateAccount(@NonNull UserAccountEntity entity) {
        return userAccountRepository.editAccount(entity);
    }

    /**
     * 修改用户密码
     * @param userId 用户id
     * @param newPwd 新密码
     */
    public void updatePwd(String userId, String newPwd, Date lastLoginDate) {
        Map<QueryColumn, Object> valueMap = new HashMap<>();
        Map<String, Object> whereConditions = Map.of(USER_ACCOUNT.USER_ID.getName(), userId);
        valueMap.put(USER_ACCOUNT.PWD, newPwd);
        valueMap.put(USER_ACCOUNT.UPDATE_BY, userId);
        if (Objects.isNull(lastLoginDate)) {
            valueMap.put(USER_ACCOUNT.LAST_LOGIN_DATE, new Date());
        }
        applicationEventPublisher.publishEvent(new UserAccountChangeEvent(new DataChangeRecord<>(UserAccountPO.class, valueMap,
                null, whereConditions, null, null)));
    }

    public boolean resetPwd(String userId, String newPwd, String updater) {
        UserAccountEntity userAccountEntity = userAccountRepository.findByUserid(userId);
        if (userAccountEntity == null) {
            throw new BusinessException(ErrorCodeEnum.USER_ERROR_A0201);
        }

        Map<QueryColumn, Object> valueMap = new HashMap<>();
        Map<String, Object> whereConditions = Map.of(USER_ACCOUNT.USER_ID.getName(), userId);
        valueMap.put(USER_ACCOUNT.PWD, newPwd);
        valueMap.put(USER_ACCOUNT.UPDATE_BY, userId);
        valueMap.put(USER_ACCOUNT.LAST_LOGIN_DATE, null);
        valueMap.put(USER_ACCOUNT.LAST_LOGIN_IP, null);
        applicationEventPublisher.publishEvent(new UserAccountChangeEvent(new DataChangeRecord<>(UserAccountPO.class, valueMap,
                null, whereConditions, null, null)));
        return true;
    }


    /**
     * 删除用户信息
     * @param userIds 用户ID，多个以逗号分割
     * @return 操作结果
     */
    public boolean deleteUser(String userIds) {
        String[] userArr = userIds.split(StrPool.COMMA);
        List<String> userList = Arrays.asList(userArr);
        return userAccountRepository.deleteAccount(userList);
    }

    /**
     * 根据用户ID查询
     * @param userId 用户ID
     * @return 查询结果
     */
    public UserAccountEntity findUserById(String userId) {
        UserAccountEntity entity = userAccountRepository.findByUserid(userId);
        if (entity == null) {
            throw new BusinessException(ErrorCodeEnum.USER_ERROR_A0201);
        }
        return entity;
    }


    public static class Builder {
        /** 用户账号仓储接口 */
        private final UserAccountRepository userAccountRepository;

        /** 事件发布 */
        private ApplicationEventPublisher applicationEventPublisher;

        public Builder(UserAccountRepository userAccountRepository) {
            this.userAccountRepository = userAccountRepository;
        }

        public Builder applicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
            this.applicationEventPublisher = applicationEventPublisher;
            return this;
        }

        public UserAccountAggregateRoot build() {
            return new UserAccountAggregateRoot(this);
        }
    }

    private UserAccountAggregateRoot(Builder builder) {
        this.userAccountRepository = builder.userAccountRepository;
        this.applicationEventPublisher = builder.applicationEventPublisher;
    }
}
