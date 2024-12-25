package cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.commons.user.dto.SystemDictListDTO;
import cn.com.farben.gptcoder.operation.platform.dictionary.command.DictListCommand;
import cn.com.farben.gptcoder.operation.platform.dictionary.domain.entity.DictEntity;
import com.mybatisflex.core.paginate.Page;

/**
 *
 * 平台用户仓储接口<br>
 * 创建时间：2023/8/29<br>
 * @author ltg
 *
 */
public interface DictRepository {
    /**
     * 根据主键查询字典信息
     * @param id 编码
     * @return 字典信息
     */
    DictEntity findDictById(String id);

    /**
     * 根据编码查询字典信息
     * @param dictCode 编码
     * @return 字典信息
     */
    DictEntity findByCode(String dictCode);

    /**
     * 添加字典
     * @param dictEntity 字典实体
     * @return 用户实体
     */
    boolean addDict(DictEntity dictEntity);

    /**
     * 修改字典
     * @param dictEntity 字典实体
     * @return 用户实体
     */
    boolean updateDict(DictEntity dictEntity);

    /**
     * 分页查询系统字典信息
     * @param command 参数
     * @return 查询结果
     */
    Page<SystemDictListDTO> findDictList(DictListCommand command);
}
