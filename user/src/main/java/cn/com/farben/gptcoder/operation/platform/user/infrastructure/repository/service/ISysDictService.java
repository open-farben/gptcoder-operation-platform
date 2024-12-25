package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.service;

import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.SysDict;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Gary
 * @since 2024-04-09
 */
public interface ISysDictService /*extends IService<SysDict>*/ {
    public SysDict getDict(String code,String defaultValue);
}
