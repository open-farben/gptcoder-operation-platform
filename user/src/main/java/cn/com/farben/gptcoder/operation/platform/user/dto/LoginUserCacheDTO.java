package cn.com.farben.gptcoder.operation.platform.user.dto;

import cn.com.farben.gptcoder.operation.commons.user.dto.SysRoleCacheDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * 用户登录信息缓存
 * @author wuanhui
 *
 */
@Data
public class LoginUserCacheDTO implements Serializable {

    /** 记录ID */
    private String id;

    /** 用户账号 */
    private String userId;

    /** 用户姓名 */
    private String userName;

    /** 用户密码 */
    private String pwd;

    /** 邮箱 */
    private String email;

    /** 用户工号 */
    private String jobNumber;

    /** 联系手机号 */
    private String phone;

    /** 角色ID */
    private String roleId;

    /** 职位ID */
    private String duty;

    /** 职位名称 */
    private String dutyName;

    /** 状态（0 启用，1 禁用） */
    private Integer inUse;

    /** 组织架构号 */
    private String organization;

    /** 长机构编码 */
    private String fullOrganization;

    /** 组织架构名称 */
    private String organizationName;

    /*** 账户类型 0 系统账户 1 用户账户 */
    private Byte accountType;

    /**
     * 当前用户的刷新token
     */
    private String refreshToken;

    /** 角色列表 */
    private List<SysRoleCacheDTO> roleList;

}
