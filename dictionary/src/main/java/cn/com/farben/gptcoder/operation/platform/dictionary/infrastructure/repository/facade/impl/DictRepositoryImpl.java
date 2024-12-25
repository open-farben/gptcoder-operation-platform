package cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.facade.impl;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.gptcoder.operation.commons.user.dto.SystemDictListDTO;
import cn.com.farben.gptcoder.operation.platform.dictionary.command.DictListCommand;
import cn.com.farben.gptcoder.operation.platform.dictionary.domain.entity.DictEntity;
import cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.facade.DictRepository;
import cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.mapper.DictMapper;
import cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.po.DictPO;
import cn.hutool.core.text.CharSequenceUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import org.springframework.stereotype.Repository;

import static cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.po.table.DictTableDef.DICT;

/**
 *
 * 系统字典仓储实现
 * @author wuanhui
 *
 */
@Repository
public class DictRepositoryImpl implements DictRepository {
    private final DictMapper dictMapper;

    public DictRepositoryImpl(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    /**
     * 根据主键查询字典信息
     * @param id 编码
     * @return 字典信息
     */
    @Override
    public DictEntity findDictById(String id) {
        DictPO dictPO = QueryChain.of(dictMapper)
                .select(DICT.ALL_COLUMNS)
                .where(DICT.ID.eq(id)).one();
        DictEntity entity = new DictEntity();
        CommonAssemblerUtil.assemblerPOToEntity(dictPO, entity);
        return entity;
    }


    /**
     * 根据编码查询字典信息
     * @param dictCode 编码
     * @return 字典信息
     */
    @Override
    public DictEntity findByCode(String dictCode) {
        DictPO dictPO = QueryChain.of(dictMapper)
                .select(DICT.ALL_COLUMNS)
                .where(DICT.DICT_CODE.eq(dictCode)).one();
        DictEntity entity = new DictEntity();
        CommonAssemblerUtil.assemblerPOToEntity(dictPO, entity);
        return entity;
    }

    /**
     * 添加字典
     *
     * @param dictEntity 字典实体
     * @return 用户实体
     */
    @Override
    public boolean addDict(DictEntity dictEntity) {
        DictPO dictPO = new DictPO();
        CommonAssemblerUtil.assemblerEntityToPO(dictEntity, dictPO);
        return dictMapper.insertSelectiveWithPk(dictPO) > 0;
    }

    /**
     * 修改字典
     * @param dictEntity 字典实体
     * @return 用户实体
     */
    @Override
    public boolean updateDict(DictEntity dictEntity) {
        DictPO dictPO = new DictPO();
        CommonAssemblerUtil.assemblerEntityToPO(dictEntity, dictPO);
        return dictMapper.update(dictPO) > 0;
    }

    /**
     * 分页查询系统字典信息
     * @param command 参数
     * @return 查询结果
     */
    @Override
    public Page<SystemDictListDTO> findDictList(DictListCommand command) {
        QueryChain<DictPO> queryChain = QueryChain.of(dictMapper);
        queryChain.where(DICT.DICT_LEVEL.eq((byte)0));
        if (CharSequenceUtil.isNotBlank(command.getSearchKey())) {
            queryChain.where(DICT.DICT_CODE.like(command.getSearchKey()).or(DICT.DICT_NAME.like(command.getSearchKey())));
        }
        Page<DictPO> page = new Page<>(command.getPageNo(), command.getPageSize());
        Page<DictPO> poPage = queryChain.orderBy(DICT.CREATE_DATE, false).page(page);

        Page<DictEntity> entityPage = CommonAssemblerUtil.assemblerPOPageToEntityPage(poPage, DictEntity.class);
        return CommonAssemblerUtil.assemblerEntityPageToDTOPage(entityPage, SystemDictListDTO.class);
    }
}
