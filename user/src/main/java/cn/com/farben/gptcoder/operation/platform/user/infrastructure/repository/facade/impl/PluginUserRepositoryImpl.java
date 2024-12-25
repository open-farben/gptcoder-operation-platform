package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.impl;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.commons.ddd.repository.IDataChangeRepository;
import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import cn.com.farben.commons.errorcode.exception.DataDeleteException;
import cn.com.farben.commons.errorcode.exception.DataUpdateException;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.PluginUserEntity;
import cn.com.farben.gptcoder.operation.platform.user.domain.event.PluginUserChangeEvent;
import cn.com.farben.gptcoder.operation.platform.user.dto.PluginUserDTO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums.PluginUserStatusEnum;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.PluginUserRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper.PluginUserMapper;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.PluginUserPO;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.po.table.DictCodeTableDef.DICT_CODE;
import static cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.table.OrganMngTableDef.ORGAN_MNG;
import static cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.table.PluginUserTableDef.PLUGIN_USER;

/**
 *
 * 插件用户仓储实现<br>
 * 创建时间：2023/8/24<br>
 * @author ltg
 *
 */
@Repository
public class PluginUserRepositoryImpl implements PluginUserRepository, IDataChangeRepository {
    /** 插件用户DB服务 */
    private final PluginUserMapper pluginUserMapper;
    private static final Log logger = LogFactory.get();

    @Override
    public PluginUserEntity findByAccount(String account) {
        logger.info("根据用户账号获取用户信息：{}", account);
        PluginUserEntity pluginUserEntity = new PluginUserEntity();
        CommonAssemblerUtil.assemblerPOToEntity(
                QueryChain.of(pluginUserMapper).select(PLUGIN_USER.ALL_COLUMNS)
                        .where(PLUGIN_USER.ACCOUNT.eq(account)).one(),
                pluginUserEntity
        );

        return pluginUserEntity;
    }

    @Override
    public Page<PluginUserDTO> pageUser(String searchText, String organization, PluginUserStatusEnum status, List<Integer> organList, long pageSize, long page) {
        logger.info("分页查询插件用户，searchText: [{}], organization: [{}], status: [{}], pageSize: [{}], page: [{}]",
                searchText, organization, status, pageSize, page);
        QueryChain<PluginUserPO> queryChain = QueryChain.of(pluginUserMapper);
        queryChain.select("u.id", "u.account", "u.job_number as jobNumber", "u.name", "u.organization",
                "u.full_organization as fullOrganization", "u.duty", "u.mobile", "u.email", "u.plugin", "u.version",
                "u.last_used_time as lastUsedTime", "u.status", "u.locked", "o.organ_name as organizationName",
                "d.kind_value as dutyName");
        queryChain.from(PLUGIN_USER).as("u");
        queryChain.leftJoin(ORGAN_MNG).as("o")
                .on(PLUGIN_USER.ORGANIZATION.eq(ORGAN_MNG.ORGAN_NO));
        queryChain.leftJoin(DICT_CODE).as("d")
                .on(PLUGIN_USER.DUTY.eq(DICT_CODE.KIND_CODE).and(DICT_CODE.DICT_CODE_.eq("JOB_INFO")));
        queryChain.where(PLUGIN_USER.ORGANIZATION.in(organList).when(CollUtil.isNotEmpty(organList)));
        queryChain.where(PLUGIN_USER.ACCOUNT.like(searchText)
                .or(PLUGIN_USER.JOB_NUMBER.like(searchText))
                .or(PLUGIN_USER.NAME.like(searchText)).when(CharSequenceUtil.isNotBlank(searchText)));
        queryChain.where(PLUGIN_USER.FULL_ORGANIZATION.likeLeft(organization).when(CharSequenceUtil.isNotBlank(organization)));
        queryChain.where(PLUGIN_USER.STATUS.eq(status).when(Objects.nonNull(status)));
        queryChain.orderBy(PLUGIN_USER.CREATE_TIME.desc());
        return queryChain.pageAs(Page.of(page, pageSize), PluginUserDTO.class);
    }

    @Override
    public long countUserByAccount(String account) {
        return QueryChain.of(pluginUserMapper)
                .where(PLUGIN_USER.ACCOUNT.eq(account))
                .count();
    }

    @Override
    public void addUser(PluginUserEntity userEntity) {
        logger.info("新增插件用户： [{}]", userEntity);
        PluginUserPO userPO = new PluginUserPO();
        CommonAssemblerUtil.assemblerEntityToPO(userEntity, userPO);
        pluginUserMapper.insertSelectiveWithPk(userPO);
    }

    @Override
    public long countUserById(String id) {
        return QueryChain.of(pluginUserMapper)
                .where(PLUGIN_USER.ID.eq(id))
                .count();
    }

    @Override
    public void deleteUser(String id) {
        int count = pluginUserMapper.deleteById(id);
        if (count <= 0) {
            logger.error("删除插件用户失败：[{}]", id);
            throw new DataDeleteException("删除插件用户失败，可重新查询后再操作");
        }
    }

    @Override
    public void updateLastUsedTime(@NonNull List<PluginUserEntity> dataList) {
        logger.info("更新插件用户最后使用时间和版本");
        pluginUserMapper.updateLastUsedTime(CommonAssemblerUtil.assemblerEntityListToPOList(dataList, PluginUserPO.class));
    }

    @Override
    public long countEnabledUser() {
        logger.info("查询已启用的插件用户数量");
        return QueryChain.of(pluginUserMapper).where(PLUGIN_USER.STATUS.eq(PluginUserStatusEnum.ENABLE))
                .count();
    }

    @Override
    public PluginUserEntity findById(String id) {
        logger.info("根据用户id获取用户信息：{}", id);
        PluginUserEntity pluginUserEntity = new PluginUserEntity();
        CommonAssemblerUtil.assemblerPOToEntity(
                QueryChain.of(pluginUserMapper).where(PLUGIN_USER.ID.eq(id))
                        .one(),
                pluginUserEntity
        );

        return pluginUserEntity;
    }

    /**
     * 查询指定账号是否在数据库存在
     * @param accountList 指定账号列表
     * @return 已存在的账号列表
     */
    @Override
    public List<String> findExistAccount(List<String> accountList) {
        return pluginUserMapper.findExistAccount(accountList);
    }

    /**
     * 批量新增插件用户
     * @param userList 用户列表
     * @return 操作结果
     */
    @Override
    public boolean batchAddUser(List<PluginUserEntity> userList) {
        if(CollUtil.isEmpty(userList)) {
            return false;
        }
        PluginUserPO pluginUser;
        List<PluginUserPO> pluginUserList = new ArrayList<>();
        for(PluginUserEntity item : userList) {
            pluginUser = new PluginUserPO();
            BeanUtils.copyProperties(item, pluginUser);
            pluginUserList.add(pluginUser);
        }
        return pluginUserMapper.batchAddUser(pluginUserList) > 0;
    }

    /**
     * 修改机构上下级关系时，由于机构长编码的变化，需要联级更新插件用户的机构
     * @param oldOrgan 旧机构
     * @param newOrgan 新机构
     */
    @Override
    public void updateUserOrgan(String oldOrgan, String newOrgan) {
        pluginUserMapper.updateUserOrgan(oldOrgan, newOrgan);
    }

    @Override
    public List<Integer> listOrgans(List<String> userList) {
        logger.info("获取用户列表的机构：[{}]", userList);
        return QueryChain.of(pluginUserMapper).select(QueryMethods.distinct(
                QueryMethods.column("CAST(COALESCE(organization, '0') as UNSIGNED)")))
                .where(PLUGIN_USER.ID.in(userList)).listAs(Integer.class);
    }

    @Override
    public long countByIds(List<String> userList) {
        logger.info("获取用户列表里的用户数量：[{}]", userList);
        return QueryChain.of(pluginUserMapper)
                .where(PLUGIN_USER.ID.in(userList)).count();
    }

    @Override
    public void onApplicationEvent(@NonNull PluginUserChangeEvent event) {
        logger.info("监听到事件：[{}]", event);
        Object source = event.getSource();
        if (Objects.isNull(source)) {
            throw new DataUpdateException("无法更新数据，source为空");
        }
        updateEntity((DataChangeRecord)source);
    }

    public PluginUserRepositoryImpl(PluginUserMapper pluginUserMapper) {
        this.pluginUserMapper = pluginUserMapper;
    }
}
