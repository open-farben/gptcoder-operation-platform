package cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.facade.impl;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.gptcoder.operation.commons.user.dto.DictCodeDTO;
import cn.com.farben.gptcoder.operation.commons.user.dto.DictCodeListDTO;
import cn.com.farben.gptcoder.operation.platform.dictionary.domain.entity.DictCodeEntity;
import cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.facade.DictCodeRepository;
import cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.mapper.DictCodeMapper;
import cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.po.DictCodePO;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import static cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.po.table.DictCodeTableDef.DICT_CODE;

/**
 * 系统字典映射仓储实现
 * @author wuanhui
 */
@Repository
public class DictCodeRepositoryImpl implements DictCodeRepository {

    private final DictCodeMapper dictCodeMapper;

    public DictCodeRepositoryImpl(DictCodeMapper dictCodeMapper) {
        this.dictCodeMapper = dictCodeMapper;
    }

    /**
     * 根据ID查询
     *
     * @param id 主键ID
     * @return 结果
     */
    @Override
    public DictCodeEntity findDictCodeById(String id) {
        DictCodePO dictCodePO = QueryChain.of(dictCodeMapper)
                .select(DICT_CODE.ALL_COLUMNS)
                .where(DICT_CODE.ID.eq(id)).one();
        DictCodeEntity entity = new DictCodeEntity();
        CommonAssemblerUtil.assemblerPOToEntity(dictCodePO, entity);
        return entity;
    }

    /**
     * 添加字典映射
     *
     * @param entity 实体
     * @return 结果
     */
    @Override
    public boolean addDictCode(DictCodeEntity entity) {
        DictCodePO dictCodePO = new DictCodePO();
        CommonAssemblerUtil.assemblerEntityToPO(entity, dictCodePO);
        return dictCodeMapper.insertSelectiveWithPk(dictCodePO) > 0;
    }

    /**
     * 修改字典映射
     *
     * @param entity 实体
     * @return 结果
     */
    @Override
    public boolean editDictCode(DictCodeEntity entity) {
        DictCodePO dictCodePO = new DictCodePO();
        CommonAssemblerUtil.assemblerEntityToPO(entity, dictCodePO);
        return dictCodeMapper.update(dictCodePO) > 0;
    }

    /**
     * 删除操作
     * @param ids 删除的ID
     * @return 结果
     */
    @Override
    public boolean deleteDictCode(List<String> ids) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.in(DICT_CODE.ID.getName(), ids);
        return dictCodeMapper.deleteByQuery(queryWrapper) > 0;
    }

    /**
     * 查询指定码表字典
     *
     * @param dictCode 码表code
     * @param kind     子code
     * @return 返回值
     */
    @Override
    public List<DictCodeDTO> findDictByKind(String dictCode, String kind) {
        QueryChain<DictCodePO> queryChain = QueryChain.of(dictCodeMapper)
                .select(DICT_CODE.ALL_COLUMNS)
                .from(DICT_CODE);
        queryChain.where(DICT_CODE.DISABLE.eq((byte)0));
        queryChain.where(DICT_CODE.LOGIC_DELETE.eq((byte)0));
        if (CharSequenceUtil.isNotBlank(dictCode)) {
            queryChain.where(DICT_CODE.DICT_CODE_.eq(dictCode));
        }
        if (CharSequenceUtil.isNotBlank(kind)) {
            queryChain.where(DICT_CODE.KIND_CODE.eq(kind));
        }
        List<DictCodePO> dataList = queryChain.orderBy(DICT_CODE.ORDER_NO, true).orderBy(DICT_CODE.CREATE_DATE, false).list();

        return BeanUtil.copyToList(dataList, DictCodeDTO.class);
    }

    /**
     * 查询指定码表字典(包含被禁用的)
     * @param dictCode 码表code
     * @return 返回值
     */
    @Override
    public List<DictCodeDTO> findAllDictByKind(String dictCode) {
        QueryChain<DictCodePO> queryChain = QueryChain.of(dictCodeMapper)
                .select(DICT_CODE.ALL_COLUMNS)
                .from(DICT_CODE);
        queryChain.where(DICT_CODE.DICT_CODE_.eq(dictCode));
        List<DictCodePO> dataList = queryChain.orderBy(DICT_CODE.CREATE_DATE, false).list();

        return BeanUtil.copyToList(dataList, DictCodeDTO.class);
    }

    /**
     * 分页查询系统字典映射信息
     * @param dictCode 字典编码
     * @param pageSize 每页显示数
     * @param pageNo 页码
     * @return 查询结果
     */
    @Override
    public Page<DictCodeListDTO> findDictCodeList(String dictCode, Integer pageSize, Integer pageNo) {
        QueryChain<DictCodePO> queryChain = QueryChain.of(dictCodeMapper);
        queryChain.where(DICT_CODE.LOGIC_DELETE.eq((byte)0));
        if (CharSequenceUtil.isNotBlank(dictCode)) {
            queryChain.where(DICT_CODE.DICT_CODE_.eq(dictCode));
        }
        Page<DictCodePO> page = new Page<>(pageNo, pageSize);
        Page<DictCodePO> poPage = queryChain.orderBy(DICT_CODE.ORDER_NO, true).orderBy(DICT_CODE.CREATE_DATE, false).page(page);

        Page<DictCodeEntity> entityPage = CommonAssemblerUtil.assemblerPOPageToEntityPage(poPage, DictCodeEntity.class);
        return CommonAssemblerUtil.assemblerEntityPageToDTOPage(entityPage, DictCodeListDTO.class);
    }

    /**
     * 根据ID列表查询对应字典码值
     * @param ids 主键ID列表
     * @return 结果
     */
    @Override
    public List<DictCodeEntity> findDictCodeByIdList(List<String> ids) {
        QueryChain<DictCodePO> queryChain = QueryChain.of(dictCodeMapper);
        queryChain.where(DICT_CODE.ID.in(ids));
        List<DictCodePO> poList = queryChain.list();
        return CommonAssemblerUtil.assemblerPOListToEntityList(poList, DictCodeEntity.class);
    }
}
