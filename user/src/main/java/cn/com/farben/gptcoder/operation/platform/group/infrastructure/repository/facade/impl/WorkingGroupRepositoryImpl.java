package cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.facade.impl;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.commons.ddd.repository.IDataChangeRepository;
import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import cn.com.farben.commons.errorcode.exception.DataUpdateException;
import cn.com.farben.gptcoder.operation.platform.group.domain.entity.AuthorizedGroupEntity;
import cn.com.farben.gptcoder.operation.platform.group.domain.entity.AuthorizedUserEntity;
import cn.com.farben.gptcoder.operation.platform.group.domain.entity.WorkingGroupEntity;
import cn.com.farben.gptcoder.operation.platform.group.dto.WorkingGroupDTO;
import cn.com.farben.gptcoder.operation.platform.group.domain.event.GroupChangeEvent;
import cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.facade.WorkingGroupRepository;
import cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.mapper.WorkingGroupMapper;
import cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.po.WorkingGroupPO;
import cn.com.farben.gptcoder.operation.platform.group.vo.OrganWorkingGroupVO;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.po.table.UserGroupRelevanceTableDef.USER_GROUP_RELEVANCE;
import static cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.po.table.WorkingGroupTableDef.WORKING_GROUP;
import static cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.table.OrganMngTableDef.ORGAN_MNG;

/**
 *
 * 工作组仓储实现<br>
 *
 */
@Repository
public class WorkingGroupRepositoryImpl implements WorkingGroupRepository, IDataChangeRepository {
    /** 工作组DB服务 */
    private final WorkingGroupMapper workingGroupMapper;
    private static final Log logger = LogFactory.get();

    @Override
    public Page<WorkingGroupDTO> pageGroup(String groupName, LocalDate effectiveDay, LocalDate failureDay,
                                           List<Integer> organList, long pageSize, long page) {
        logger.info("分页查询工作组，groupName: [{}], effectiveDay: [{}], failureDay: [{}], pageSize: [{}], page: [{}]",
                groupName, effectiveDay, failureDay, pageSize, page);
        if (CollUtil.isEmpty(organList)) {
            return new Page<>();
        }
        QueryChain<WorkingGroupPO> queryChain = QueryChain.of(workingGroupMapper);
        queryChain.from(WORKING_GROUP).as("g");
        queryChain.select("g.id", "g.group_name as groupName", "g.organ_no as organNo", "o.organ_name as organName",
                "g.introduce", "IFNULL(t1.userNumber, 0) as userNumber", "g.effective_day as effectiveDay",
                "g.failure_day as failureDay");
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.select(QueryMethods.count(USER_GROUP_RELEVANCE.ID).as("userNumber"), USER_GROUP_RELEVANCE.GROUP_ID)
                        .from(USER_GROUP_RELEVANCE)
                                .groupBy(USER_GROUP_RELEVANCE.GROUP_ID);
        queryChain.leftJoin(queryWrapper)
                .as("t1")
                .on(WORKING_GROUP.ID.eq("t1.group_id"))
                .leftJoin(ORGAN_MNG).as("o").on(WORKING_GROUP.ORGAN_NO.eq(ORGAN_MNG.ORGAN_NO));
        queryChain.where(WORKING_GROUP.ORGAN_NO.in(organList).when(CollUtil.isNotEmpty(organList)));
        queryChain.where(WORKING_GROUP.GROUP_NAME.like(groupName).when(CharSequenceUtil.isNotBlank(groupName)));
        queryChain.where(WORKING_GROUP.EFFECTIVE_DAY.ge(effectiveDay).when(Objects.nonNull(effectiveDay)));
        queryChain.where(WORKING_GROUP.EFFECTIVE_DAY.le(failureDay).when(Objects.nonNull(failureDay)));
        queryChain.orderBy(WORKING_GROUP.CREATE_TIME.desc());
        return queryChain.pageAs(Page.of(page, pageSize), WorkingGroupDTO.class);
    }

    @Override
    public long countByNameAndOrgan(String groupName, Integer organNo) {
        return QueryChain.of(workingGroupMapper)
                .where(WORKING_GROUP.GROUP_NAME.eq(groupName)
                        .and(WORKING_GROUP.ORGAN_NO.eq(organNo)))
                .count();
    }

    @Override
    public void addGroup(WorkingGroupEntity workingGroupEntity) {
        WorkingGroupPO groupPO = new WorkingGroupPO();
        CommonAssemblerUtil.assemblerEntityToPO(workingGroupEntity, groupPO);
        workingGroupMapper.insertSelectiveWithPk(groupPO);
    }

    @Override
    public WorkingGroupEntity getById(String id) {
        WorkingGroupEntity workingGroupEntity = new WorkingGroupEntity();
        CommonAssemblerUtil.assemblerPOToEntity(
                QueryChain.of(workingGroupMapper)
                        .select()
                        .where(WORKING_GROUP.ID.eq(id))
                        .one(),
                workingGroupEntity
        );

        return workingGroupEntity;
    }

    @Override
    public List<AuthorizedUserEntity> listAuthorizedUsers(List<Integer> organList, List<String> memberList) {
        return workingGroupMapper.listAuthorizedUsers(organList, memberList);
    }

    @Override
    public long countById(String id) {
        return QueryChain.of(workingGroupMapper)
                .where(WORKING_GROUP.ID.eq(id))
                .count();
    }

    @Override
    public void removeGroup(String groupId) {
        workingGroupMapper.removeGroup(groupId);
    }

    @Override
    public List<String> listAssociatedAuthorizedKnowledgeGroup(Long kid) {
        return workingGroupMapper.listAssociatedAuthorizedKnowledgeGroup(kid);
    }

    @Override
    public List<AuthorizedGroupEntity> listAuthorizedGroups(List<Integer> organList, List<String> memberList) {
        return workingGroupMapper.listAuthorizedGroups(organList, memberList);
    }

    @Override
    public List<String> listAssociatedAuthorizedGitGroup(Long kid) {
        return workingGroupMapper.listAssociatedAuthorizedGitGroup(kid);
    }

    @Override
    public void onApplicationEvent(@NonNull GroupChangeEvent event) {
        logger.info("监听到事件：[{}]", event);
        Object source = event.getSource();
        if (Objects.isNull(source)) {
            throw new DataUpdateException("无法更新数据，source为空");
        }
        updateEntity((DataChangeRecord)source);
    }

    public WorkingGroupRepositoryImpl(WorkingGroupMapper workingGroupMapper) {
        this.workingGroupMapper = workingGroupMapper;
    }

    @Override
    public List<OrganWorkingGroupVO> listOrganWorkingGroups(List<Integer> organList) {
        QueryChain<WorkingGroupPO> queryChain = QueryChain.of(workingGroupMapper);
        queryChain.select(ORGAN_MNG.ORGAN_NO, ORGAN_MNG.ORGAN_NAME, ORGAN_MNG.PARENT_ORGAN_NO,
                WORKING_GROUP.ID, WORKING_GROUP.GROUP_NAME);
        queryChain.from(ORGAN_MNG);
        queryChain.leftJoin(WORKING_GROUP).on(WORKING_GROUP.ORGAN_NO.eq(ORGAN_MNG.ORGAN_NO));
        queryChain.where(ORGAN_MNG.ORGAN_NO.in(organList))
                .and(WORKING_GROUP.EFFECTIVE_DAY.isNull().and(WORKING_GROUP.FAILURE_DAY.isNull())
                        .or(WORKING_GROUP.EFFECTIVE_DAY.le(QueryMethods.curDate()).and(WORKING_GROUP.FAILURE_DAY.ge(QueryMethods.curDate()))));

        return queryChain.listAs(OrganWorkingGroupVO.class);
    }
}
