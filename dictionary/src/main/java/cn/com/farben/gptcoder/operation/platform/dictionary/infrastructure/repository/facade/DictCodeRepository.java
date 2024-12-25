package cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.commons.user.dto.DictCodeDTO;
import cn.com.farben.gptcoder.operation.commons.user.dto.DictCodeListDTO;
import cn.com.farben.gptcoder.operation.platform.dictionary.domain.entity.DictCodeEntity;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

public interface DictCodeRepository {
    /**
     * 根据ID查询
     * @param id 主键ID
     * @return 结果
     */
    DictCodeEntity findDictCodeById(String id);

    /**
     * 添加字典映射
     * @param entity 实体
     * @return 结果
     */
    boolean addDictCode(DictCodeEntity entity);

    /**
     * 修改字典映射
     * @param entity 实体
     * @return 结果
     */
    boolean editDictCode(DictCodeEntity entity);

    /**
     * 删除操作
     * @param ids 删除的ID
     * @return 结果
     */
    boolean deleteDictCode(List<String> ids);

    /**
     * 查询指定码表字典
     * @param dictCode 码表code
     * @param kind 子code
     * @return 返回值
     */
    List<DictCodeDTO> findDictByKind(String dictCode, String kind);

    /**
     * 查询指定码表字典(包含被禁用的)
     * @param dictCode 码表code
     * @return 返回值
     */
    List<DictCodeDTO> findAllDictByKind(String dictCode);

    /**
     * 分页查询系统字典映射信息
     * @param dictCode 字典编码
     * @param pageSize 每页显示数
     * @param pageNo 页码
     * @return 查询结果
     */
    Page<DictCodeListDTO> findDictCodeList(String dictCode, Integer pageSize, Integer pageNo);

    /**
     * 根据ID列表查询对应字典码值
     * @param ids 主键ID列表
     * @return 结果
     */
    List<DictCodeEntity> findDictCodeByIdList(List<String> ids);

}
