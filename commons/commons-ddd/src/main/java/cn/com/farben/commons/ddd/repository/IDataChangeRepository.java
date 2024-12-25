package cn.com.farben.commons.ddd.repository;

import cn.com.farben.commons.ddd.po.IPO;
import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import cn.com.farben.commons.errorcode.exception.DataUpdateException;
import cn.hutool.core.collection.CollUtil;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.SqlOperators;
import com.mybatisflex.core.update.UpdateChain;

import java.util.Map;
import java.util.Objects;

/**
 * 数据变更仓储接口
 */
public interface IDataChangeRepository {
    /**
     * 更新数据库
     * @param updateRecord 数据变更记录
     */
    default void updateEntity(DataChangeRecord<IPO> updateRecord) {
        Class<IPO> entityClass = updateRecord.entityClass();
        Map<QueryColumn, Object> valueMap = updateRecord.valueMap();
        Map<QueryColumn, Object> rawMap = updateRecord.rawMap();
        Map<String, Object> whereConditions = updateRecord.whereConditions();
        SqlOperators operators = updateRecord.operators();
        QueryCondition queryCondition = updateRecord.queryCondition();

        UpdateChain<IPO> updateChain = UpdateChain.of(entityClass);
        if (CollUtil.isEmpty(valueMap) && CollUtil.isEmpty(rawMap)) {
            throw new DataUpdateException("无法更新数据，未设置要更新的值");
        }
        if (CollUtil.isEmpty(whereConditions)) {
            throw new DataUpdateException("无法更新数据，where条件不正确");
        }
        if (CollUtil.isNotEmpty(valueMap)) {
            valueMap.forEach(updateChain::set);
        }
        if (CollUtil.isNotEmpty(rawMap)) {
            rawMap.forEach(updateChain::setRaw);
        }
        if (Objects.nonNull(queryCondition)) {
            updateChain.where(queryCondition).update();
        } else {
            updateChain.where(whereConditions, operators).update();
        }
    }
}
