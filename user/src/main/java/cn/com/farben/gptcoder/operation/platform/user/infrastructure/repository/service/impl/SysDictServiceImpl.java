package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.service.impl;


import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper.SysDictMapper;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.SysDict;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.service.ISysDictService;
import com.mybatisflex.core.query.QueryChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.table.SysDictTableDef.SYS_DICT;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Gary
 * @since 2024-04-09
 */
@Service
public class SysDictServiceImpl implements ISysDictService {
    @Autowired
    SysDictMapper sysDictMapper;
    public SysDict getDict(String code,String defaultValue){
        SysDict sysDict= QueryChain.of(sysDictMapper)
                .select(SYS_DICT.ALL_COLUMNS)
                .where(SYS_DICT.CODE.eq(code)).one();
        if (sysDict==null){
            sysDict=new SysDict();
            sysDict.setCode(code);
            sysDict.setValue(defaultValue);
            sysDictMapper.insert(sysDict);
        }
        return sysDict;
    }

}
