package cn.com.farben.commons.ddd.repository.entity;

import cn.com.farben.commons.ddd.po.IPO;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.SqlOperators;

import java.util.Map;

/**
 * 数据变更记录<br>
 * entityClass: 实体类，对应表<br>
 * valueMap: 直接更新值的列<br>
 * rawMap: 通过setRaw方法更新的列<br>
 * whereConditions: 更新条件<br>
 * operators: 更新条件组合方式<br>
 * queryCondition: 查询条件<br>
 * valueMap和rawMap至少要有一个，whereConditions和queryCondition都是更新条件，设置其一即可。优先判断queryCondition
 */
public record DataChangeRecord<T extends IPO> (
        /* 实体类，对应表 */
        Class<T> entityClass,
        /* 直接更新值的列 */
        Map<QueryColumn, Object> valueMap,
        /* 通过setRaw方法更新的列 */
        Map<QueryColumn, Object> rawMap,
        /* 更新条件 */
        Map<String, Object> whereConditions,
        /* 更新条件组合方式 */
        SqlOperators operators,
        /* 查询条件 */
        QueryCondition queryCondition
) {
}