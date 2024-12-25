package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.impl;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.commons.ddd.repository.IDataChangeRepository;
import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import cn.com.farben.commons.errorcode.exception.DataInsertException;
import cn.com.farben.commons.errorcode.exception.DataUpdateException;
import cn.com.farben.gptcoder.operation.platform.user.command.UserAccountListCommand;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.UserAccountEntity;
import cn.com.farben.gptcoder.operation.platform.user.domain.event.UserAccountChangeEvent;
import cn.com.farben.gptcoder.operation.platform.user.dto.UserAccountListDTO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.UserAccountRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper.UserAccountMapper;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.UserAccountPO;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.table.UserAccountTableDef.USER_ACCOUNT;

/**
 *
 * 用户账号仓储实现<br>
 * 创建时间：2023/8/15<br>
 * @author ltg
 *
 */
@Repository
public class UserAccountRepositoryImpl implements UserAccountRepository, IDataChangeRepository {
    /** 用户账号DB服务 */
    private final UserAccountMapper userAccountMapper;
    private static final Log logger = LogFactory.get();

    @Override
    public UserAccountEntity findByUserid(String userId) {
        logger.info("根据用户id查找用户：{}", userId);
        UserAccountEntity userAccountEntity = new UserAccountEntity();
        UserAccountPO userAccountPO = QueryChain.of(userAccountMapper).select(USER_ACCOUNT.ALL_COLUMNS, USER_ACCOUNT.PWD)
                .where(USER_ACCOUNT.USER_ID.eq(userId)).one();
        if (Objects.nonNull(userAccountPO) && CharSequenceUtil.isNotBlank(userAccountPO.getId())) {
            CommonAssemblerUtil.assemblerPOToEntity(userAccountPO, userAccountEntity);
        } else {
            userAccountEntity = null;
        }

        return userAccountEntity;
    }

    /**
     * 根据主键查询
     * @param id 主键
     * @return 用户
     */
    @Override
    public UserAccountEntity findById(String id) {
        UserAccountEntity userAccountEntity = new UserAccountEntity();
        CommonAssemblerUtil.assemblerPOToEntity(
                QueryChain.of(userAccountMapper).select()
                        .where(USER_ACCOUNT.ID.eq(id)).one(),
                userAccountEntity
        );
        return userAccountEntity;
    }

    @Override
    public long countByUserId(String userId) {
        logger.info("根据用户id查询用户数量：{}", userId);
        return QueryChain.of(userAccountMapper).select()
                .where(USER_ACCOUNT.USER_ID.eq(userId)).count();
    }

    @Override
    public boolean addAccount(UserAccountEntity userAccountEntity) {
        logger.info("新增账号：{}", userAccountEntity);
        UserAccountPO userAccountPO = new UserAccountPO();
        CommonAssemblerUtil.assemblerEntityToPO(userAccountEntity, userAccountPO);
        boolean flg = userAccountMapper.insertSelectiveWithPk(userAccountPO) > 0;
        if (!flg) {
            throw new DataInsertException("新增账号失败");
        }
        return true;
    }

    @Override
    public boolean editAccount(UserAccountEntity userAccountEntity) {
        UserAccountPO userAccountPO = new UserAccountPO();
        CommonAssemblerUtil.assemblerEntityToPO(userAccountEntity, userAccountPO);
        return userAccountMapper.update(userAccountPO) > 0;
    }

    /**
     * 分页查询用户信息
     * @param command 参数
     * @return 查询结果
     */
    @Override
    public Page<UserAccountListDTO> findAccountList(UserAccountListCommand command) {
        QueryChain<UserAccountPO> queryChain = QueryChain.of(userAccountMapper);
        String searchKey = command.getSearchKey();
        if (StringUtils.isNotBlank(searchKey)) {
            queryChain.where(USER_ACCOUNT.USER_ID.like(searchKey)
                    .or(USER_ACCOUNT.USER_NAME.like(searchKey))
                    .or(USER_ACCOUNT.JOB_NUMBER.like(searchKey)));
        }
        //职务
        if(StringUtils.isNotBlank(command.getDuty())) {
            queryChain.where(USER_ACCOUNT.DUTY.eq(command.getDuty()));
        }
        //组织机构
        if(StringUtils.isNotBlank(command.getFullOrganization())) {
            queryChain.where(USER_ACCOUNT.FULL_ORGANIZATION.likeLeft(command.getFullOrganization()));
        }
        if(StringUtils.isNotBlank(command.getOrganization())) {
            queryChain.where(USER_ACCOUNT.FULL_ORGANIZATION.likeLeft(command.getOrganization()));
        }
        if(command.getStatus() != null){
            queryChain.where(USER_ACCOUNT.IN_USE.eq(command.getStatus()));
        }
        //过滤角色权限
        filterRole(queryChain, command);
        queryChain.where(USER_ACCOUNT.LOGIC_DELETE.eq(0));
        queryChain.orderBy(USER_ACCOUNT.LAST_LOGIN_DATE.desc());

        return queryChain.pageAs(new Page<>(command.getPageNo(), command.getPageSize()), UserAccountListDTO.class);
    }

    /**
     * 过滤角色机构权限
     * @param queryChain 链式查询
     * @param command 前端参数
     */
    private void filterRole(QueryChain queryChain, UserAccountListCommand command) {
        //全部数据
        if(StringUtils.isNotBlank(command.getIsAll())) {
            return;
        }
        //只能看本人
        if(StringUtils.isNotBlank(command.getIsOnly())) {
            queryChain.where(USER_ACCOUNT.USER_ID.eq(command.getUserId()));
            return;
        }
        //指定机构范围
        if(!command.getOrganNoList().isEmpty()) {
            queryChain.where(USER_ACCOUNT.ORGANIZATION.in(command.getOrganNoList()));
        }
    }

    @Override
    public boolean deleteAccount(List<String> userIdList) {
        return userAccountMapper.deleteAccount(userIdList) > 0;
    }

    /**
     * 修改机构上下级关系时，由于机构长编码的变化，需要联级更新插件用户的机构
     * @param oldOrgan 旧机构
     * @param newOrgan 新机构
     */
    @Override
    public void updateSysUserOrgan(String oldOrgan, String newOrgan) {
        userAccountMapper.updateSysUserOrgan(oldOrgan, newOrgan);
    }

    /**
     * 查询指定角色知否存在用户使用
     * @param roleIdList 角色ID列表
     * @return 查询结果
     */
    @Override
    public Integer countUserByRoleId(List<String> roleIdList) {
        return userAccountMapper.countUserByRoleId(roleIdList);
    }

    public UserAccountRepositoryImpl(UserAccountMapper userAccountMapper) {
        this.userAccountMapper = userAccountMapper;
    }

    @Override
    public void onApplicationEvent(@NonNull UserAccountChangeEvent event) {
        logger.info("监听到事件：[{}]", event);
        Object source = event.getSource();
        if (Objects.isNull(source)) {
            throw new DataUpdateException("无法更新数据，source为空");
        }
        updateEntity((DataChangeRecord)source);
    }
}
