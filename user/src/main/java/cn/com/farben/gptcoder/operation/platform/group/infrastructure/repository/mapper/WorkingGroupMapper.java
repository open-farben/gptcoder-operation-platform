package cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.mapper;

import cn.com.farben.gptcoder.operation.platform.group.domain.entity.AuthorizedGroupEntity;
import cn.com.farben.gptcoder.operation.platform.group.domain.entity.AuthorizedUserEntity;
import cn.com.farben.gptcoder.operation.platform.group.dto.WorkingGroupDTO;
import cn.com.farben.gptcoder.operation.platform.group.infrastructure.SqlConstants;
import cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.po.WorkingGroupPO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * 工作组表mapper<br>
 *
 */
public interface WorkingGroupMapper extends BaseMapper<WorkingGroupPO> {
    /**
     * 获取可授权的用户信息
     * @param organList 用户有数据权限的机构
     * @param memberList 组成员
     * @return 用户信息
     */
    @Select({SqlConstants.AUTHORIZED_USER_SQL})
    List<AuthorizedUserEntity> listAuthorizedUsers(@Param("organList")List<Integer> organList, @Param("memberList")List<String> memberList);

    /**
     * 删除工作组，将同时删除工作组用户关联、工作组知识库关联
     * @param groupId 工作组id
     */
    @Delete({SqlConstants.DELETE_GROUP_SQL})
    void removeGroup(@Param("groupId")String groupId);

    /**
     * 获取指定知识库的已授权工作组信息
     * @param kid 知识库id
     * @return 已授权的工作组id列表
     */
    @Select({SqlConstants.KNOWLEDGE_GROUP_SQL})
    List<String> listAssociatedAuthorizedKnowledgeGroup(@Param("kid")Long kid);

    /**
     * 获取指定git库的已授权工作组信息
     * @param kid git库id
     * @return 已授权的工作组id列表
     */
    @Select({SqlConstants.GIT_GROUP_SQL})
    List<String> listAssociatedAuthorizedGitGroup(@Param("kid")Long kid);

    /**
     * 获取可授权的工作组信息
     * @param organList 用户有数据权限的机构
     * @param memberList 知识库已授权工作组
     * @return 工作组信息
     */
    @Select({SqlConstants.AUTHORIZED_GROUP_SQL})
    List<AuthorizedGroupEntity> listAuthorizedGroups(@Param("organList")List<Integer> organList, @Param("memberList")List<String> memberList);
}
