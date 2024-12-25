package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po;

import cn.com.farben.commons.ddd.po.IPO;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * 系统角色和用户关联表
 * @author wuanhui
 */
@Data
@Table("sys_account_role")
public class SysAccountRolePO implements IPO {

    /** 用户ID */
    private String userId;

    /** 角色ID */
    private String roleId;
}
