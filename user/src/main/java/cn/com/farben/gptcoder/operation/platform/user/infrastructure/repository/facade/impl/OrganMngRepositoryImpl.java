package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.impl;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.gptcoder.operation.platform.user.command.organ.OrganListCommand;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.OrganMngEntity;
import cn.com.farben.gptcoder.operation.platform.user.dto.OrganMngDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.OrganMngListDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.OrganMngTreeDTO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.OrganMngRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper.OrganMngMapper;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.OrganMngPO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.table.OrganMngTableDef;
import cn.hutool.core.text.CharSequenceUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.table.OrganMngTableDef.ORGAN_MNG;

/**
 *
 * 系统组织机构仓储实现
 * @author wuanhui
 *
 */
@Repository
public class OrganMngRepositoryImpl implements OrganMngRepository {

    private final OrganMngMapper organMngMapper;

    public OrganMngRepositoryImpl(OrganMngMapper organMngMapper) {
        this.organMngMapper = organMngMapper;
    }

    /**
     * 新增机构
     *
     * @param entity 机构实体
     */
    @Override
    public boolean addOrgan(OrganMngEntity entity) {
        OrganMngPO organ = new OrganMngPO();
        BeanUtils.copyProperties(entity, organ);
        return organMngMapper.insertSelectiveWithPk(organ) > 0;
    }

    /**
     * 修改机构
     *
     * @param entity 机构实体
     */
    @Override
    public boolean editOrgan(OrganMngEntity entity) {
        OrganMngPO organ = new OrganMngPO();
        BeanUtils.copyProperties(entity, organ);
        return organMngMapper.update(organ) > 0;
    }

    /**
     * 删除机构信息
     * @param idList 机构ID
     * @return 操作结果
     */
    @Override
    public boolean deleteOrgan(List<String> idList) {
        return organMngMapper.deleteOrgan(idList) > 0;
    }

    /**
     * 根据ID查询
     *
     * @param id 主键ID
     * @return 机构信息
     */
    @Override
    public OrganMngEntity findOrganById(String id) {
        OrganMngEntity organMngEntity = new OrganMngEntity();
        CommonAssemblerUtil.assemblerPOToEntity(
                QueryChain.of(organMngMapper).select(ORGAN_MNG.ALL_COLUMNS)
                        .where(ORGAN_MNG.ID.eq(id)).one(),
                organMngEntity
        );
        return organMngEntity;
    }

    /**
     * 根据架构号查询
     * @param organNo 机构号
     * @return 机构信息
     */
    @Override
    public OrganMngEntity findOrganByNo(Integer organNo) {
        OrganMngEntity organMngEntity = new OrganMngEntity();
        CommonAssemblerUtil.assemblerPOToEntity(
                QueryChain.of(organMngMapper).select(ORGAN_MNG.ALL_COLUMNS)
                .where(ORGAN_MNG.ORGAN_NO.eq(organNo)).one(),
                organMngEntity
        );
        return organMngEntity;
    }

    /**
     * 根据机构号查询是否存在下级机构
     *
     * @param organNoList 机构号列表
     * @return 机构信息
     */
    @Override
    public Integer countChildrenOrgan(List<Integer> organNoList) {
        return organMngMapper.countChildrenOrgan(organNoList);
    }

    /**
     * 分页查询机构信息
     * @param command 参数
     * @return 查询结果
     */
    @Override
    public Page<OrganMngListDTO> findOrganList(OrganListCommand command) {
        OrganMngTableDef t = ORGAN_MNG.as("t");
        OrganMngTableDef t1 = ORGAN_MNG.as("t1");
        QueryChain<OrganMngPO> queryChain = QueryChain.of(organMngMapper);
        String searchKey = command.getSearchKey();
        String organNo = command.getOrganNo();
        queryChain.from(t)
                .select("t.`id`", "t.organ_no as organNo", "t.full_organ_no as fullOrganNo", "t.organ_level as organLevel",
                        "t.organ_name as organName", "t.short_name as shortName", "t.parent_organ_no as parentOrganNo",
                        "t.organ_address as organAddress", "t.organ_post as organPost", "t1.organ_name as parentOrganName")
                .innerJoin(t1).on(
                        t.PARENT_ORGAN_NO.eq(t1.ORGAN_NO)
                )
                .where(t.STATUS.eq(0))
                .where(t.ORGAN_NO.like(searchKey).or(t.ORGAN_NAME.like(searchKey)).when(CharSequenceUtil.isNotBlank(searchKey)))
                .where(t.FULL_ORGAN_NO.likeLeft(organNo).when(CharSequenceUtil.isNotBlank(organNo)))
                .orderBy(t.ORGAN_NO.asc());
        return queryChain.pageAs(Page.of(command.getPageNo(), command.getPageSize()), OrganMngListDTO.class);
    }

    /**
     * 查询当前最大的机构号
     * @return 机构号
     */
    @Override
    public List<Integer> findMaxOrganNo() {
        return organMngMapper.findMaxOrganNo();
    }

    /**
     * 查询所有可用架构信息
     * @return 架构列表
     */
    @Override
    public List<OrganMngTreeDTO> findAllNormalOrgan() {
        return organMngMapper.findAllNormalOrgan();
    }

    /**
     * 根据ID查询机构本身及上级机构
     * @param id 主键ID
     * @return 机构信息
     */
    @Override
    public OrganMngDTO findOrganAndParentById(String id) {
        return organMngMapper.findOrganAndParentById(id);
    }

    /**
     * 查询指定多个架构号对应的架构信息
     * @param organList 架构号列表
     * @return 架构列表
     */
    @Override
    public List<OrganMngListDTO> findOrganByNoList(List<Integer> organList) {
        return organMngMapper.findOrganByNoList(organList);
    }

    /**
     * 根据多个ID查询机构信息
     * @param idList ID列表
     * @return 机构信息
     */
    @Override
    public List<OrganMngEntity> findOrganByIdList(List<String> idList) {
        return organMngMapper.findOrganByIdList(idList);
    }

    /**
     * 根据长机构号查询下级机构
     * @param fullOrganNo 机构号（长机构号）
     * @return 机构信息
     */
    @Override
    public List<OrganMngEntity> findChildrenOrganByNo(String fullOrganNo) {
        return organMngMapper.findChildrenOrganByNo(fullOrganNo);
    }

    /**
     * 修改替换下级机构的机构号信息
     * @param oldOrganNo 要替换的
     * @param newOrganNo 替换新的
     * @param ids 指定ID
     */
    @Override
    public void updateChildrenOrgan(String oldOrganNo, String newOrganNo, List<String> ids) {
        organMngMapper.updateChildrenOrgan(oldOrganNo, newOrganNo, ids);
    }
}
