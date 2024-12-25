package cn.com.farben.gptcoder.operation.platform.group.domain;

import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import cn.com.farben.commons.errorcode.exception.OperationNotAllowedException;
import cn.com.farben.gptcoder.operation.platform.group.domain.entity.UserGroupRelevanceEntity;
import cn.com.farben.gptcoder.operation.platform.group.domain.entity.WorkingGroupEntity;
import cn.com.farben.gptcoder.operation.platform.group.domain.event.GroupChangeEvent;
import cn.com.farben.gptcoder.operation.platform.group.exception.GroupExistsException;
import cn.com.farben.gptcoder.operation.platform.group.exception.GroupNotExistsException;
import cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.facade.UserGroupRelevanceRepository;
import cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.facade.WorkingGroupRepository;
import cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.po.WorkingGroupPO;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.query.QueryColumn;
import lombok.Getter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.po.table.WorkingGroupTableDef.WORKING_GROUP;

/**
 *
 * 工作组聚合根<br>
 *
 */
@Getter
public class WorkingGroupAggregateRoot {
    private static final Log logger = LogFactory.get();

    /** 工作组仓储接口 */
    private final WorkingGroupRepository workingGroupRepository;

    /** 工作组用户关联仓储接口 */
    private final UserGroupRelevanceRepository userGroupRelevanceRepository;

    /** 事件发布 */
    private final ApplicationEventPublisher applicationEventPublisher;

    private static final String NO_PERMISSION_MESSAGE = "您没有该机构的数据权限";

    /**
     * 新增工作组
     * @param workingGroupEntity 工作组实体
     * @param operator 操作者账号
     * @param organList 数据权限机构列表
     */
    public void addGroup(@NonNull WorkingGroupEntity workingGroupEntity, String operator, List<Integer> organList) {
        String groupName = workingGroupEntity.getGroupName();
        Integer organNo = workingGroupEntity.getOrganNo();
        if (CollUtil.isEmpty(organList) || !organList.contains(organNo)) {
            throw new OperationNotAllowedException(NO_PERMISSION_MESSAGE);
        }
        long count = workingGroupRepository.countByNameAndOrgan(groupName, organNo);
        // 工作组已存在
        if (count > 0) {
            throw new GroupExistsException("工作组已存在");
        }

        workingGroupEntity.setId(IdUtil.objectId());
        workingGroupEntity.setCreateUser(operator);
        workingGroupEntity.setUpdateUser(operator);
        workingGroupRepository.addGroup(workingGroupEntity);
    }

    /**
     * 编辑工作组
     * @param workingGroupEntity 工作组实体
     * @param operator 操作者账号
     * @param organList 数据权限机构列表
     */
    public void editGroup(@NonNull WorkingGroupEntity workingGroupEntity, String operator, List<Integer> organList) {
        String id = workingGroupEntity.getId();
        WorkingGroupEntity oldEntity = workingGroupRepository.getById(id);
        if (Objects.isNull(oldEntity) || CharSequenceUtil.isBlank(oldEntity.getId())) {
            throw new GroupNotExistsException("工作组不存在");
        }
        String groupName = workingGroupEntity.getGroupName();
        Integer organNo = workingGroupEntity.getOrganNo();
        String oldName = oldEntity.getGroupName();
        Integer oldOrganNo = oldEntity.getOrganNo();
        if (CollUtil.isEmpty(organList) || !organList.contains(organNo) || !organList.contains(oldOrganNo)) {
            throw new OperationNotAllowedException(NO_PERMISSION_MESSAGE);
        }
        if (!oldName.equals(groupName) || !oldOrganNo.equals(organNo)) {
            // 改变工作组名称或所属机构
            long count = workingGroupRepository.countByNameAndOrgan(groupName, organNo);
            // 工作组已存在
            if (count > 0) {
                throw new GroupExistsException("工作组已存在");
            }
        }

        Map<QueryColumn, Object> valueMap = new HashMap<>();
        Map<String, Object> whereConditions = Map.of(WORKING_GROUP.ID.getName(), id);
        valueMap.put(WORKING_GROUP.GROUP_NAME, groupName);
        valueMap.put(WORKING_GROUP.ORGAN_NO, organNo);
        valueMap.put(WORKING_GROUP.INTRODUCE, workingGroupEntity.getIntroduce());
        valueMap.put(WORKING_GROUP.EFFECTIVE_DAY, workingGroupEntity.getEffectiveDay());
        valueMap.put(WORKING_GROUP.FAILURE_DAY, workingGroupEntity.getFailureDay());
        valueMap.put(WORKING_GROUP.UPDATE_USER, operator);
        applicationEventPublisher.publishEvent(new GroupChangeEvent(new DataChangeRecord<>(WorkingGroupPO.class, valueMap,
                null, whereConditions, null, null)));
    }

    /**
     * 工作组授权
     * @param groupId 工作组id
     * @param userIds 用户id列表
     * @param organList 数据权限机构列表
     */
    public void accreditGroup(String groupId, List<String> userIds, List<Integer> organList) {
        checkPermission(groupId, organList);
        // 清空工作组的授权
        userGroupRelevanceRepository.clearGroupPermissions(groupId);
        List<UserGroupRelevanceEntity> entityList = new ArrayList<>(userIds.size());
        for (String userId : userIds) {
            UserGroupRelevanceEntity entity = new UserGroupRelevanceEntity();
            entity.setId(IdUtil.objectId());
            entity.setGroupId(groupId);
            entity.setUserId(userId);
            entityList.add(entity);
        }
        userGroupRelevanceRepository.bindPermissions(entityList);
    }

    /**
     * 删除工作组
     * @param groupId 工作组id
     */
    public void removeGroup(String groupId, List<Integer> organList) {
        checkPermission(groupId, organList);
        workingGroupRepository.removeGroup(groupId);
    }

    public static class Builder {
        /** 工作组仓储接口 */
        private final WorkingGroupRepository workingGroupRepository;

        /** 工作组用户关联仓储接口 */
        private UserGroupRelevanceRepository userGroupRelevanceRepository;

        /** 事件发布 */
        private ApplicationEventPublisher applicationEventPublisher;

        public Builder(WorkingGroupRepository workingGroupRepository) {
            this.workingGroupRepository = workingGroupRepository;
        }

        public WorkingGroupAggregateRoot.Builder userGroupRelevanceRepository(UserGroupRelevanceRepository userGroupRelevanceRepository) {
            this.userGroupRelevanceRepository = userGroupRelevanceRepository;
            return this;
        }

        public WorkingGroupAggregateRoot.Builder applicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
            this.applicationEventPublisher = applicationEventPublisher;
            return this;
        }

        public WorkingGroupAggregateRoot build() {
            return new WorkingGroupAggregateRoot(this);
        }
    }

    private WorkingGroupAggregateRoot(WorkingGroupAggregateRoot.Builder builder) {
        this.workingGroupRepository = builder.workingGroupRepository;
        this.userGroupRelevanceRepository = builder.userGroupRelevanceRepository;
        this.applicationEventPublisher = builder.applicationEventPublisher;
    }

    private void checkPermission(String groupId, List<Integer> organList) {
        WorkingGroupEntity workingGroupEntity = workingGroupRepository.getById(groupId);
        if (Objects.isNull(workingGroupEntity) || CharSequenceUtil.isBlank(workingGroupEntity.getId())) {
            throw new GroupNotExistsException("工作组不存在");
        }
        Integer organNo = workingGroupEntity.getOrganNo();
        if (CollUtil.isEmpty(organList) || !organList.contains(organNo)) {
            throw new OperationNotAllowedException(NO_PERMISSION_MESSAGE);
        }
    }
}
