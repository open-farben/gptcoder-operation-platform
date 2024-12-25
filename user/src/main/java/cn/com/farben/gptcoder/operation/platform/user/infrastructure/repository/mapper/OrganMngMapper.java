package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper;

import cn.com.farben.gptcoder.operation.platform.user.command.organ.OrganListCommand;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.OrganMngEntity;
import cn.com.farben.gptcoder.operation.platform.user.dto.OrganMngDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.OrganMngListDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.OrganMngTreeDTO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.OrganMngPO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 系统组织机构mapper
 * @author wuanhui
 */
public interface OrganMngMapper extends BaseMapper<OrganMngPO> {

    /**
     * 根据机构号列表查询是否存在下级机构
     * @param organNoList 机构号列表
     * @return 机构数值
     */
    @Select({
        """
        <script>
            select count(*) from t_organ_mng
            <where>
                status = 0 and logic_delete = 0
                <if test='organNoList != null and organNoList.size() > 0'>
                    and parent_organ_no in
                    <foreach collection="organNoList" open="(" close=")" separator="," item="item">
                        #{item}
                    </foreach>
                </if>
            </where>
        </script>
        """
    })
    Integer countChildrenOrgan(@Param("organNoList") List<Integer> organNoList);

    /**
     * 删除指定ID的机构
     * @param idList 指定ID
     * @return 操作影响行数
     */
    @Delete({
        """
        <script>
            delete from t_organ_mng
            <where>
                <if test='idList != null and idList.size() > 0'>
                    and id in
                    <foreach collection="idList" open="(" close=")" separator="," item="item">
                        #{item}
                    </foreach>
                </if>
            </where>
        </script>
        """
    })
    Integer deleteOrgan(List<String> idList);

    /**
     * 分页查询机构信息
     * @param command 参数
     * @param page 分页信息
     * @return 查询结果
     */
    @Select({
        """
        <script>
            select t.`id`, t.organ_no as organNo, t.full_organ_no as fullOrganNo, t.organ_level as organLevel,
           t.organ_name as organName, t.short_name as shortName, t.parent_organ_no as parentOrganNo, t.organ_address as organAddress,
           t.organ_post as organPost, t1.organ_name as parentOrganName
            from t_organ_mng t left join t_organ_mng t1 on t.parent_organ_no = t1.organ_no
            <where>
                t.status = 0
                <if test = 'param.searchKey != null and param.searchKey != ""'>
                    and (t.organ_no like concat('%',#{param.searchKey},'%') or t.organ_name like concat('%',#{param.searchKey},'%'))
                </if>
                <if test = 'param.organNo != null and param.organNo != ""'>
                    and t.full_organ_no like concat(#{param.organNo}, '%')
                </if>
            </where>
            order by organ_no asc
        </script>
        """
    })
    Page<OrganMngListDTO> pageOrganList(@Param("param") OrganListCommand command, Page<OrganMngPO> page);

    /**
     * 查询系统当前最大的架构号
     * @return 架构号列表
     */
    @Select({
        """
        <script>
            select organ_no from t_organ_mng order by organ_no desc
            limit 2
        </script>
        """
    })
    List<Integer> findMaxOrganNo();

    /**
     * 查询当前系统所有架构，构建架构树
     * @return 架构列表
     */
    @Select({
        """
        <script>
            select `id`, organ_no as organNo, full_organ_no as fullOrganNo, organ_name as organName, short_name as shortName,
           parent_organ_no as parentOrganNo, organ_level as organLevel
            from t_organ_mng where status = 0 and logic_delete = 0
            order by organ_no asc
        </script>
        """
    })
    List<OrganMngTreeDTO> findAllNormalOrgan();

    /**
     * 查询当前系统所有架构，构建架构树
     * @return 架构列表
     */
    @Select({
        """
        <script>
            select t.`id`, t.organ_no as organNo, t.full_organ_no as fullOrganNo, t.organ_name as organName,
           t.short_name as shortName, t.parent_organ_no as parentOrganNo,t.organ_level as organLevel, t.organ_address as organAddress,
           t.organ_post as organPost, t.mark, t.status, t1.organ_name as parentOrganName
            from t_organ_mng t left join t_organ_mng t1 on t.parent_organ_no = t1.organ_no
            where t.`id` = #{id}
        </script>
        """
    })
    OrganMngDTO findOrganAndParentById(String id);

    /**
     * 查询指定多个架构号对应的架构信息
     * @return 架构列表
     */
    @Select({
       """
       <script>
           select t.`id`, t.organ_no as organNo, t.full_organ_no as fullOrganNo, t.organ_name as organName,
           t.short_name as shortName, t.parent_organ_no as parentOrganNo, t.organ_address as organAddress,
           t.organ_post as organPost, t.mark, t.status
           from t_organ_mng t
           where t.organ_no in
           <foreach collection="organList" open="(" close=")" separator="," item="organ">
               #{organ}
           </foreach>
       </script>
       """
    })
    List<OrganMngListDTO> findOrganByNoList(@Param("organList") List<Integer> organList);

    /**
     * 根据多个主键ID查询机构信息
     * @param idList 主键ID列表
     * @return 机构列表
     */
    @Select({
        """
        <script>
            select t.`id`, t.organ_no as organNo, t.full_organ_no as fullOrganNo, t.organ_name as organName,
           t.short_name as shortName, t.parent_organ_no as parentOrganNo, t.organ_address as organAddress,
           t.organ_post as organPost, t.mark, t.status
            from t_organ_mng t
            where t.`id` in
            <foreach collection="idList" open="(" close=")" separator="," item="id">
                #{id}
            </foreach>
        </script>
        """
    })
    List<OrganMngEntity> findOrganByIdList(@Param("idList") List<String> idList);

    /**
     * 根据长机构号查询下级机构
     * @param fullOrganNo 机构号长编码
     * @return 机构列表
     */
    @Select({
        """
        <script>
            select t.`id`, t.organ_no as organNo, t.full_organ_no as fullOrganNo, t.organ_name as organName,
           t.short_name as shortName, t.parent_organ_no as parentOrganNo, t.organ_address as organAddress,
           t.organ_post as organPost, t.mark, t.status
            from t_organ_mng t
            where t.full_organ_no like concat(#{fullOrganNo}, '%')
        </script>
        """
    })
    List<OrganMngEntity> findChildrenOrganByNo(@Param("fullOrganNo") String fullOrganNo);

    /**
     * 修改替换下级机构的机构号信息
     * @param oldOrganNo 要替换的
     * @param newOrganNo 替换新的
     * @param ids 指定ID
     * @return 操作结果
     */
    @Update({
        """
        <script>
            update t_organ_mng set full_organ_no = replace(full_organ_no, #{oldOrganNo}, #{newOrganNo})
            where `id` in
            <foreach collection="ids" open="(" close=")" separator="," item="id">
                #{id}
            </foreach>
        </script>
        """
    })
    Integer updateChildrenOrgan(String oldOrganNo, String newOrganNo, List<String> ids);
}
