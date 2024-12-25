package cn.com.farben.gptcoder.operation.platform.dictionary.command;

import cn.com.farben.gptcoder.operation.commons.user.command.BasePageCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询系统字典列表
 * @author wuanhui
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DictListCommand extends BasePageCommand {

    /** 搜索关键字 */
    private String searchKey;

}
