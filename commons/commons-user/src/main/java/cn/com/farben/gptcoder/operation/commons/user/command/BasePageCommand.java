package cn.com.farben.gptcoder.operation.commons.user.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分页列表查询公共参数
 * @author wuanhui
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BasePageCommand extends BaseAuthOrganCommand {

    private static final long serialVersionUID = 5251291276750512836L;

    /** 当前页 */
    private int pageNo;

    /** 每页显示条数 */
    private int pageSize;

    /** 排序字段 */
    private String orderStr;

    /** 排序方法 */
    private String orderBy = "DESC";
}
