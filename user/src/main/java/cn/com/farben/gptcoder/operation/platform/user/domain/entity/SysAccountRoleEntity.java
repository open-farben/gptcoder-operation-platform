package cn.com.farben.gptcoder.operation.platform.user.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * 系统角色与用户关联实体
 * @author wuanhui
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysAccountRoleEntity implements IEntity {

    /** 用户账号 */
    private String userId;

    /** 角色ID */
    private String roleId;
}
