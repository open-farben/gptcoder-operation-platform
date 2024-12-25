package cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.platform.group.domain.entity.AuthorizedGroupEntity;
import cn.com.farben.gptcoder.operation.platform.group.domain.entity.AuthorizedUserEntity;
import cn.com.farben.gptcoder.operation.platform.group.domain.entity.WorkingGroupEntity;
import cn.com.farben.gptcoder.operation.platform.group.dto.WorkingGroupDTO;
import cn.com.farben.gptcoder.operation.platform.group.domain.event.GroupChangeEvent;
import com.mybatisflex.core.paginate.Page;
import org.springframework.context.ApplicationListener;
import cn.com.farben.gptcoder.operation.platform.group.vo.OrganWorkingGroupVO;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * 工作组仓储接口<br>
 *
 */
public interface WorkingGroupRepository extends ApplicationListener<GroupChangeEvent> {
    /**
     * 分页查询工作组
     * @param groupName 工作组名称
     * @param effectiveDay 生效时间
     * @param failureDay 失效时间
     * @param organList 用户有数据权限的机构
     * @param pageSize 每页数据量
     * @param page 当前页
     * @return 工作组信息
     */
    Page<WorkingGroupDTO> pageGroup(String groupName, LocalDate effectiveDay, LocalDate failureDay,
                                    List<Integer> organList, long pageSize, long page);

    /**
     * 根据工作组名和机构获取数量
     * @param groupName 工作组名称
     * @param organNo 机构号
     * @return 数量
     */
    long countByNameAndOrgan(String groupName, Integer organNo);

    /** 新增工作组 */
    void addGroup(WorkingGroupEntity workingGroupEntity);

    /** 根据id获取工作组 */
    WorkingGroupEntity getById(String id);

    /**
     * 获取可授权的用户信息
     * @param organList 用户有数据权限的机构
     * @param memberList 组成员信息
     * @return 用户信息
     */
    List<AuthorizedUserEntity> listAuthorizedUsers(List<Integer> organList, List<String> memberList);

    /**
     * 根据id获取工作组数量
     * @param id 工作组id
     * @return 工作组数量
     */
    long countById(String id);

    /**
     * 删除工作组，将同时删除工作组用户关联、工作组知识库关联
     * @param groupId 工作组id
     */
    void removeGroup(String groupId);

    /**
     * 获取指定知识库的已授权工作组信息
     *      * @param kid 知识库id
     * @return 已授权的工作组id列表
     */
    List<String> listAssociatedAuthorizedKnowledgeGroup(Long kid);

    /**
     * 获取可授权的工作组信息
     * @param organList 用户有数据权限的机构
     * @param memberList 知识库已授权工作组
     * @return 工作组信息
     */
    List<AuthorizedGroupEntity> listAuthorizedGroups(List<Integer> organList, List<String> memberList);

    /**
     * 获取指定git库的已授权工作组信息
     * @param kid git库id
     * @return 已授权的工作组id列表
     */
    List<String> listAssociatedAuthorizedGitGroup(Long kid);

    /**
     * 获取指定机构下的工作组信息
     * @param organList 机构列表
     * @return 机构工作组信息
     */
    List<OrganWorkingGroupVO> listOrganWorkingGroups(List<Integer> organList);
}
