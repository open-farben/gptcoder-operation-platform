package cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.facade.impl;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.gptcoder.operation.platform.group.domain.entity.UserGroupRelevanceEntity;
import cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.facade.UserGroupRelevanceRepository;
import cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.mapper.UserGroupRelevanceMapper;
import cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.po.UserGroupRelevancePO;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.po.table.UserGroupRelevanceTableDef.USER_GROUP_RELEVANCE;

/**
 *
 * 工作组用户关联仓储实现<br>
 *
 */
@Repository
public class UserGroupRelevanceRepositoryImpl implements UserGroupRelevanceRepository {
    /** 工作组用户关联DB服务 */
    private final UserGroupRelevanceMapper userGroupRelevanceMapper;
    private static final Log logger = LogFactory.get();

    @Override
    public List<String> listGroupUsers(String groupId) {
        logger.info("获取工作组[{}]的用户", groupId);
        return QueryChain.of(userGroupRelevanceMapper)
                .select(USER_GROUP_RELEVANCE.USER_ID)
                .where(USER_GROUP_RELEVANCE.GROUP_ID.eq(groupId))
                .listAs(String.class);
    }

    @Override
    public void clearGroupPermissions(String groupId) {
        logger.info("清空工作组[{}]授权", groupId);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.where(USER_GROUP_RELEVANCE.GROUP_ID.eq(groupId));
        userGroupRelevanceMapper.deleteByQuery(queryWrapper);
    }

    @Override
    public void bindPermissions(List<UserGroupRelevanceEntity> entityList) {
        logger.info("绑定用户和工作组[{}]", entityList);
        userGroupRelevanceMapper.insertBatch(CommonAssemblerUtil.assemblerEntityListToPOList(entityList, UserGroupRelevancePO.class));
    }

    public UserGroupRelevanceRepositoryImpl(UserGroupRelevanceMapper userGroupRelevanceMapper) {
        this.userGroupRelevanceMapper = userGroupRelevanceMapper;
    }
}
