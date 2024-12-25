package cn.com.farben.gptcoder.operation.platform.user.command.organ;

import cn.com.farben.gptcoder.operation.commons.user.command.BasePageCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 修改管理用户命令
 * @author wuanhui
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrganListCommand extends BasePageCommand {

    /** 搜索关键字 */
    private String searchKey;

    /** 如果传此字段，则只查询其下级机构 */
    private String organNo;

}
