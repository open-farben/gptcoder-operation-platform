package cn.com.farben.gptcoder.operation.platform.user.command.role;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 修改菜单信息参数
 * @author wuanhui
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EditMenuCommand extends AddMenuCommand{

    /**
     * 主键ID
     */
    private String id;
}
