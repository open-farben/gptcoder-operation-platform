package cn.com.farben.gptcoder.operation.platform.group.application.service;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.AuthenticationFailException;
import cn.com.farben.commons.errorcode.exception.IllegalParameterException;
import cn.com.farben.commons.errorcode.exception.OperationNotAllowedException;
import cn.com.farben.commons.web.bo.UserInfoBO;
import cn.com.farben.commons.web.utils.UserThreadLocal;
import cn.com.farben.gptcoder.operation.platform.group.command.AccreditGroupCommand;
import cn.com.farben.gptcoder.operation.platform.group.command.AddGroupCommand;
import cn.com.farben.gptcoder.operation.platform.group.command.EditGroupCommand;
import cn.com.farben.gptcoder.operation.platform.group.domain.WorkingGroupAggregateRoot;
import cn.com.farben.gptcoder.operation.platform.group.domain.entity.AuthorizedGroupEntity;
import cn.com.farben.gptcoder.operation.platform.group.domain.entity.AuthorizedUserEntity;
import cn.com.farben.gptcoder.operation.platform.group.domain.entity.WorkingGroupEntity;
import cn.com.farben.gptcoder.operation.platform.group.dto.*;
import cn.com.farben.gptcoder.operation.platform.group.exception.GroupNotExistsException;
import cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.facade.UserGroupRelevanceRepository;
import cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.facade.WorkingGroupRepository;
import cn.com.farben.gptcoder.operation.platform.group.vo.OrganWorkingGroupVO;
import cn.com.farben.gptcoder.operation.platform.user.application.component.AccountOrganComponent;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.PluginUserRepository;
//import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper.GitMapper;
//import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper.KnowledgeMapper;
//import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.Git;
//import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.Knowledge;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


/**
 *
 * 工作组应用服务<br>
 *
 */
@Component
public class WorkingGroupAppService {
    private static final Log logger = LogFactory.get();

    private final WorkingGroupRepository workingGroupRepository;

    private final UserGroupRelevanceRepository userGroupRelevanceRepository;

    private final AccountOrganComponent accountOrganComponent;

    private final PluginUserRepository pluginUserRepository;

//    private final KnowledgeMapper knowledgeMapper;

//    private final GitMapper gitMapper;

    /** 事件发布 */
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 分页查询工作组
     * @param groupName 工作组名称
     * @param effectiveDay 生效时间
     * @param failureDay 失效时间
     * @param pageSize 每页数据量
     * @param pageNo 当前页
     * @param operator 操作员
     * @return 工作组信息
     */
    public Page<WorkingGroupDTO> pageGroup(String groupName, LocalDate effectiveDay, LocalDate failureDay,
                                           long pageSize, long pageNo, String operator) {
        checkEffectiveDay(effectiveDay, failureDay);
        List<Integer> organList = listUserAuthorityOrgan(operator);
        if (CollUtil.isEmpty(organList)) {
            return new Page<>();
        }
        return workingGroupRepository.pageGroup(groupName, effectiveDay, failureDay, organList, pageSize, pageNo);
    }

    /**
     * 新增工作组
     * @param addGroupCommand 新增工作组命令
     * @param user 操作员
     */
    @Transactional(rollbackFor = Exception.class)
    public void addGroup(AddGroupCommand addGroupCommand, String user) {
        LocalDate effectiveDay = addGroupCommand.getEffectiveDay();
        LocalDate failureDay = addGroupCommand.getFailureDay();
        checkEffectiveDay(effectiveDay, failureDay);

        WorkingGroupAggregateRoot workingGroupAggregateRoot = new WorkingGroupAggregateRoot.Builder(workingGroupRepository)
                .build();
        WorkingGroupEntity workingGroupEntity = new WorkingGroupEntity();
        BeanUtils.copyProperties(addGroupCommand, workingGroupEntity);
        workingGroupAggregateRoot.addGroup(workingGroupEntity, user, listUserAuthorityOrgan(user));
    }

    /**
     * 编辑工作组
     * @param editGroupCommand 编辑工作组命令
     * @param user 操作员
     */
    @Transactional(rollbackFor = Exception.class)
    public void editGroup(EditGroupCommand editGroupCommand, String user) {
        LocalDate effectiveDay = editGroupCommand.getEffectiveDay();
        LocalDate failureDay = editGroupCommand.getFailureDay();
        checkEffectiveDay(effectiveDay, failureDay);

        WorkingGroupAggregateRoot workingGroupAggregateRoot = new WorkingGroupAggregateRoot.Builder(workingGroupRepository)
                .applicationEventPublisher(applicationEventPublisher).build();
        WorkingGroupEntity workingGroupEntity = new WorkingGroupEntity();
        BeanUtils.copyProperties(editGroupCommand, workingGroupEntity);
        workingGroupAggregateRoot.editGroup(workingGroupEntity, user, listUserAuthorityOrgan(user));
    }

    /**
     * 获取可授权的插件用户信息
     * @param groupId 工作组id
     * @param operator 操作员
     * @return 可授权的插件用户
     */
    public AccreditChoseDTO authorizedUser(String groupId, String operator) {
        long count = workingGroupRepository.countById(groupId);
        if (count < 1) {
            throw new GroupNotExistsException("工作组不存在");
        }
        List<Integer> organList = listUserAuthorityOrgan(operator);
        // 工作组当前已有成员
        List<String> memberList = userGroupRelevanceRepository.listGroupUsers(groupId);
        // 可授权的用户列表
        List<AuthorizedUserEntity> userList = workingGroupRepository.listAuthorizedUsers(organList, memberList);
        if (CollUtil.isEmpty(userList)) {
            return new AccreditChoseDTO();
        }
        // 可选择的用户机构
        List<OrganDTO> optionalOrganList = new ArrayList<>();
        Integer optionalRootId = -1;
        LinkedHashMap<Integer, List<AuthorizedUserEntity>> collectMap = userList.stream().collect(
                Collectors.groupingBy(AuthorizedUserEntity::getOrganNo, LinkedHashMap::new, Collectors.toList()));
        for (Map.Entry<Integer, List<AuthorizedUserEntity>> listEntry : collectMap.entrySet()) {
            Integer organNo = listEntry.getKey();
            List<AuthorizedUserEntity> entityList = listEntry.getValue();
            if (CollUtil.isEmpty(entityList)) {
                continue;
            }
            AuthorizedUserEntity first = entityList.getFirst();
            String organName = first.getOrganName();
            Integer parentOrganNo = first.getParentOrganNo();
            List<MemberDTO> optionalUsers = new ArrayList<>();

            for (AuthorizedUserEntity authorizedUserEntity : entityList) {
                handleMember(authorizedUserEntity, optionalUsers);
                Integer parentOrganNo1 = authorizedUserEntity.getParentOrganNo();
                if (optionalRootId.equals(-1)) {
                    optionalRootId = parentOrganNo1;
                }
            }
            if (!organList.contains(organNo)) {
                // 不在自己数据权限的，只能只读
                optionalUsers.forEach(member -> member.setReadonly(true));
            }
            handleUserToOrgan(optionalUsers, optionalOrganList, organNo, organName, parentOrganNo);
        }

        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        treeNodeConfig.setIdKey("organNo");
        treeNodeConfig.setParentIdKey("parentOrganNo");
        treeNodeConfig.setNameKey("organName");
        AccreditChoseDTO dto = new AccreditChoseDTO();
        dto.setOptionalOrgan(buildOrganTree(optionalOrganList, optionalRootId, treeNodeConfig));
        dto.setMemberList(memberList);

        return dto;
    }

//    /**
//     * 获取知识库可授权的工作组信息
//     * @param knowledgeId 知识库id
//     * @param operator 操作员
//     * @return 可授权的工作组
//     */
//    public TreeGroupDTO authorizedGroup(Long knowledgeId, String operator) {
//        Knowledge knowledge = QueryChain.of(knowledgeMapper).select()
//                .where(KNOWLEDGE.ID.eq(knowledgeId)).one();
//        if (Objects.isNull(knowledge) || knowledge.getId() <= 0) {
//            throw new GroupNotExistsException("知识库不存在");
//        }
//        // 知识库已授权工作组
//        List<String> memberList = workingGroupRepository.listAssociatedAuthorizedKnowledgeGroup(knowledgeId);
//
//        return buildAuthorizedGroup(operator, memberList);
//    }

    /**
     * 工作组授权
     * @param accreditGroupCommand 授权工作组命令
     * @param user 操作员
     */
    @Transactional(rollbackFor = Exception.class)
    public void accreditGroup(AccreditGroupCommand accreditGroupCommand, String user) {
        accreditGroupCommand.verify();

        WorkingGroupAggregateRoot workingGroupAggregateRoot = new WorkingGroupAggregateRoot.Builder(workingGroupRepository)
                .userGroupRelevanceRepository(userGroupRelevanceRepository).build();
        String groupId = accreditGroupCommand.getId();
        String userIds = accreditGroupCommand.getUserIds();
        JSONArray userJa = JSONUtil.parseArray(userIds);
        List<String> userList = JSONUtil.toList(userJa, String.class);
        userList = userList.stream().distinct().toList();
        List<Integer> organList = listUserAuthorityOrgan(user);
        if (CollUtil.isEmpty(organList)) {
            logger.error("用户[{}]没有对应的数据权限", user);
            throw new OperationNotAllowedException("您没有对应的数据权限");
        }
        long userCount = pluginUserRepository.countByIds(userList);
        if (userCount != userList.size()) {
            String message = CharSequenceUtil.format("传入的用户数量为[{}]，实际只存在[{}]个用户，请查询后重新操作", userList.size(), userCount);
            logger.error(message);
            throw new OperationNotAllowedException(message);
        }
        // 工作组当前已有成员
        List<String> memberList = userGroupRelevanceRepository.listGroupUsers(groupId);

        // 新增人员
        List<String> notMemberlist = new ArrayList<>(userList.stream().filter(userId -> !memberList.contains(userId)).toList());
        List<String> finalUserList = userList;
        // 移除的成员
        notMemberlist.addAll(memberList.stream().filter(userId -> !finalUserList.contains(userId)).toList());
        if (CollUtil.isNotEmpty(notMemberlist)) {
            // 没有对应变更的成员机构的数据权限
            List<Integer> userOrganList = pluginUserRepository.listOrgans(notMemberlist);
            if (!new HashSet<>(organList).containsAll(userOrganList)) {
                throw new OperationNotAllowedException("您没有所有用户的数据权限");
            }
        }

        workingGroupAggregateRoot.accreditGroup(groupId, userList, organList);
    }

    /**
     * 删除工作组
     * @param ids 工作组id列表
     * @param operator 操作员
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeGroup(List<String> ids, String operator) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<Integer> organList = listUserAuthorityOrgan(operator);
        WorkingGroupAggregateRoot workingGroupAggregateRoot = new WorkingGroupAggregateRoot.Builder(workingGroupRepository)
                .build();
        for (String groupId : ids) {
            workingGroupAggregateRoot.removeGroup(groupId, organList);
        }
    }

//    /**
//     * 获取git库可授权的工作组信息
//     * @param gitId git库id
//     * @param operator 操作员
//     * @return 可授权的工作组
//     */
//    public TreeGroupDTO authorizedGitGroup(Long gitId, String operator) {
//        Git git = QueryChain.of(gitMapper).select()
//                .where(GIT.ID.eq(gitId)).one();
//        if (Objects.isNull(git) || git.getId() <= 0) {
//            throw new OperationNotAllowedException("git库不存在");
//        }
//        // git库已授权工作组
//        List<String> memberList = workingGroupRepository.listAssociatedAuthorizedGitGroup(gitId);
//
//        return buildAuthorizedGroup(operator, memberList);
//    }

    /**
     * 获取有权限的组织机构的工作组信息
     * @return 树状返回组织机构下的工作组信息
     */
    public List<Tree<String>> treeOrganGroup() {
        UserInfoBO userInfoBO = UserThreadLocal.get();
        if (Objects.isNull(userInfoBO)) {
            throw new AuthenticationFailException("未获取到用户信息，请登录后再操作");
        }
        List<Integer> organList = listUserAuthorityOrgan(userInfoBO.getAccount());
        List<OrganWorkingGroupVO> organGroupList = workingGroupRepository.listOrganWorkingGroups(organList);

        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        treeNodeConfig.setIdKey("organNo");
        treeNodeConfig.setNameKey("organName");

        return TreeUtil.build(organGroupList, "0", treeNodeConfig,
                (treeNode, tree) -> {
                    tree.setId(treeNode.getOrganNo());
                    tree.setParentId(treeNode.getParentOrganNo());
                    tree.setName(treeNode.getOrganName());
                    // 扩展属性 ...
                    tree.putExtra("groups", treeNode.getGroupList());
                });
    }

    public WorkingGroupAppService(WorkingGroupRepository workingGroupRepository, UserGroupRelevanceRepository userGroupRelevanceRepository,
                                  AccountOrganComponent accountOrganComponent, PluginUserRepository pluginUserRepository,
                                  /*KnowledgeMapper knowledgeMapper, GitMapper gitMapper,*/ ApplicationEventPublisher applicationEventPublisher) {
        this.workingGroupRepository = workingGroupRepository;
        this.userGroupRelevanceRepository = userGroupRelevanceRepository;
        this.accountOrganComponent = accountOrganComponent;
        this.pluginUserRepository = pluginUserRepository;
//        this.knowledgeMapper = knowledgeMapper;
//        this.gitMapper = gitMapper;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    private TreeGroupDTO buildAuthorizedGroup(String operator, List<String> memberList) {
        List<Integer> organList = listUserAuthorityOrgan(operator);
        // 可授权的工作组列表
        List<AuthorizedGroupEntity> userList = workingGroupRepository.listAuthorizedGroups(organList, memberList);
        if (CollUtil.isEmpty(userList)) {
            return new TreeGroupDTO();
        }
        // 可选择的用户机构
        List<GroupOrganDTO> optionalOrganList = new ArrayList<>();
        Integer optionalRootId = -1;
        LinkedHashMap<Integer, List<AuthorizedGroupEntity>> collectMap = userList.stream().collect(
                Collectors.groupingBy(AuthorizedGroupEntity::getOrganNo, LinkedHashMap::new, Collectors.toList()));
        for (Map.Entry<Integer, List<AuthorizedGroupEntity>> listEntry : collectMap.entrySet()) {
            Integer organNo = listEntry.getKey();
            List<AuthorizedGroupEntity> entityList = listEntry.getValue();
            if (CollUtil.isEmpty(entityList)) {
                continue;
            }
            AuthorizedGroupEntity first = entityList.getFirst();
            String organName = first.getOrganName();
            Integer parentOrganNo = first.getParentOrganNo();
            List<GroupMemberDTO> optionalGroups = new ArrayList<>();

            for (AuthorizedGroupEntity authorizedGroupEntity : entityList) {
                handleGroupMember(authorizedGroupEntity, optionalGroups);
                Integer parentOrganNo1 = authorizedGroupEntity.getParentOrganNo();
                if (optionalRootId.equals(-1)) {
                    optionalRootId = parentOrganNo1;
                }
            }
            if (!organList.contains(organNo)) {
                // 不在自己数据权限的，只能只读
                optionalGroups.forEach(member -> member.setReadonly(true));
            }
            handleGroupToOrgan(optionalGroups, optionalOrganList, organNo, organName, parentOrganNo);
        }

        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        treeNodeConfig.setIdKey("organNo");
        treeNodeConfig.setParentIdKey("parentOrganNo");
        treeNodeConfig.setNameKey("organName");
        TreeGroupDTO dto = new TreeGroupDTO();
        dto.setOptionalOrgan(buildOrganGroupTree(optionalOrganList, optionalRootId, treeNodeConfig));
        dto.setMemberList(memberList);

        return dto;
    }

    /**
     * 处理成员数据
     * @param authorizedUserEntity 用户实体
     * @param users 用户列表
     */
    private void handleMember(AuthorizedUserEntity authorizedUserEntity, List<MemberDTO> users) {
        String userId = authorizedUserEntity.getUserId();
        if (CharSequenceUtil.isBlank(userId)) {
            return;
        }
        String userName = authorizedUserEntity.getUserName();
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setUserId(userId);
        memberDTO.setUserName(userName);
        users.add(memberDTO);
    }

    /**
     * 将用户处理加工到机构列表中
     * @param users 用户列表
     * @param organList 机构列表
     * @param organNo 机构号
     * @param organName 机构名称
     * @param parentOrganNo 父机构号
     */
    private void handleUserToOrgan(List<MemberDTO> users, List<OrganDTO> organList, Integer organNo,
                                   String organName, Integer parentOrganNo) {
        OrganDTO selectedOrgan = new OrganDTO();
        selectedOrgan.setOrganNo(organNo);
        selectedOrgan.setOrganName(organName);
        selectedOrgan.setParentOrganNo(parentOrganNo);
        if (CollUtil.isNotEmpty(users)) {
            selectedOrgan.setUsers(users);
        }
        organList.add(selectedOrgan);
    }

    /**
     * 构建机构人员的树
     * @param organList 机构列表
     * @param rootId 最顶层父id
     * @param treeNodeConfig 节点配置
     * @return 数结构
     */
    private List<Tree<Integer>> buildOrganTree(List<OrganDTO> organList, Integer rootId, TreeNodeConfig treeNodeConfig) {
        Integer finalRootId = rootId;
        if (rootId != 0 && organList.stream().noneMatch(dto -> Objects.equals(dto.getOrganNo(), finalRootId))) {
            for (OrganDTO organDTO : organList) {
                if (Objects.equals(organDTO.getParentOrganNo(), rootId)) {
                    organDTO.setParentOrganNo(0);
                }
            }
            rootId = 0;
        }
        return TreeUtil.build(organList, rootId, treeNodeConfig,
                (treeNode, tree) -> {
                    tree.setId(treeNode.getOrganNo());
                    tree.setParentId(treeNode.getParentOrganNo());
                    tree.setName(treeNode.getOrganName());
                    // 扩展属性 ...
                    tree.putExtra("users", treeNode.getUsers());
                });
    }

    /**
     * 处理知识库工作组成员数据
     * @param authorizedGroupEntity 工作组实体
     * @param groups 工作组列表
     */
    private void handleGroupMember(AuthorizedGroupEntity authorizedGroupEntity, List<GroupMemberDTO> groups) {
        String groupId = authorizedGroupEntity.getGroupId();
        String groupName = authorizedGroupEntity.getGroupName();
        GroupMemberDTO memberDTO = new GroupMemberDTO();
        memberDTO.setGroupId(groupId);
        memberDTO.setGroupName(groupName);
        groups.add(memberDTO);
    }

    /**
     * 将工作组处理加工到机构列表中
     * @param groups 工作组列表
     * @param organList 机构列表
     * @param organNo 机构号
     * @param organName 机构名称
     * @param parentOrganNo 父机构号
     */
    private void handleGroupToOrgan(List<GroupMemberDTO> groups, List<GroupOrganDTO> organList, Integer organNo,
                                   String organName, Integer parentOrganNo) {
        if (CollUtil.isNotEmpty(groups)) {
            GroupOrganDTO selectedOrgan = new GroupOrganDTO();
            selectedOrgan.setOrganNo(organNo);
            selectedOrgan.setOrganName(organName);
            selectedOrgan.setParentOrganNo(parentOrganNo);
            selectedOrgan.setGroups(groups);
            organList.add(selectedOrgan);
        }
    }

    /**
     * 构建机构工作组的树
     * @param organList 机构列表
     * @param rootId 最顶层父id
     * @param treeNodeConfig 节点配置
     * @return 数结构
     */
    private List<Tree<Integer>> buildOrganGroupTree(List<GroupOrganDTO> organList, Integer rootId, TreeNodeConfig treeNodeConfig) {
        Integer finalRootId = rootId;
        if (rootId != 0 && organList.stream().noneMatch(dto -> Objects.equals(dto.getOrganNo(), finalRootId))) {
            for (GroupOrganDTO organDTO : organList) {
                if (Objects.equals(organDTO.getParentOrganNo(), rootId)) {
                    organDTO.setParentOrganNo(0);
                }
            }
            rootId = 0;
        }
        return TreeUtil.build(organList, rootId, treeNodeConfig,
                (treeNode, tree) -> {
                    tree.setId(treeNode.getOrganNo());
                    tree.setParentId(treeNode.getParentOrganNo());
                    tree.setName(treeNode.getOrganName());
                    // 扩展属性 ...
                    tree.putExtra("groups", treeNode.getGroups());
                });
    }

    /**
     * 日期校验
     * @param effectiveDay 生效日期
     * @param failureDay 失效日期
     */
    private void checkEffectiveDay(LocalDate effectiveDay, LocalDate failureDay) {
        if (Objects.nonNull(effectiveDay) && Objects.nonNull(failureDay) && failureDay.isBefore(effectiveDay)) {
            logger.error("失效时间[[}]不能早于生效时间[{}]", failureDay, effectiveDay);
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0400, "失效时间不能早于生效时间");
        }
    }

    /**
     * 查询用户有数据权限的机构
     * @param operator 当前登录人
     * @return 机构列表
     */
    private List<Integer> listUserAuthorityOrgan(String operator) {
        return accountOrganComponent.findAuthOrganListByUser(operator);
    }
}
