package cn.com.farben.commons.web.bo;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoBO {
    /** 用户账号 */
    private String account;

    /** 用户所属角色 */
    private List<String> roles;

    /** 令牌 */
    private String token;
}
