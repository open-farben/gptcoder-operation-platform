package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.platform.user.command.organ.OrganListCommand;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.OrganMngEntity;
import cn.com.farben.gptcoder.operation.platform.user.dto.OrganMngDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.OrganMngListDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.OrganMngTreeDTO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

public interface OrganMngRepository {

    /**
     * 新增机构
     * @param entity 机构实体
     */
    boolean addOrgan(OrganMngEntity entity);

    /**
     * 修改机构
     * @param entity 机构实体
     */
    boolean editOrgan(OrganMngEntity entity);

    /**
     * 删除机构信息
     * @param idList 机构ID
     * @return 操作结果
     */
    boolean deleteOrgan(List<String> idList);

    /**
     * 根据ID查询
     * @param id 主键ID
     * @return 机构信息
     */
    OrganMngEntity findOrganById(String id);

    /**
     * 根据架构号查询
     * @param organNo 机构号
     * @return 机构信息
     */
    OrganMngEntity findOrganByNo(Integer organNo);

    /**
     * 根据机构号查询是否存在下级机构
     * @param organNoList 机构号列表
     * @return 机构信息
     */
    Integer countChildrenOrgan(List<Integer> organNoList);

    /**
     * 分页查询机构信息
     * @param command 参数
     * @return 查询结果
     */
    Page<OrganMngListDTO> findOrganList(OrganListCommand command);

    /**
     * 查询当前最大的机构号
     * @return 机构号
     */
    List<Integer> findMaxOrganNo();

    /**
     * 查询所有可用架构信息
     * @return 架构列表
     */
    List<OrganMngTreeDTO> findAllNormalOrgan();

    /**
     * 根据ID查询机构本身及上级机构
     * @param id 主键ID
     * @return 机构信息
     */
    OrganMngDTO findOrganAndParentById(String id);

    /**
     * 查询指定多个架构号对应的架构信息
     * @param organList 架构号列表
     * @return 架构列表
     */
    List<OrganMngListDTO> findOrganByNoList(List<Integer> organList);

    /**
     * 根据多个ID查询机构信息
     * @param idList ID列表
     * @return 机构信息
     */
    List<OrganMngEntity> findOrganByIdList(List<String> idList);

    /**
     * 根据长机构号查询下级机构
     * @param fullOrganNo 机构号（长机构号）
     * @return 机构信息
     */
    List<OrganMngEntity> findChildrenOrganByNo(String fullOrganNo);

    /**
     * 修改替换下级机构的机构号信息
     * @param oldOrganNo 要替换的
     * @param newOrganNo 替换新的
     * @param ids 指定ID
     */
    void updateChildrenOrgan(String oldOrganNo, String newOrganNo, List<String> ids);
}
