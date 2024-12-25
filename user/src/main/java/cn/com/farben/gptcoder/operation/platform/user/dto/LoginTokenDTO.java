package cn.com.farben.gptcoder.operation.platform.user.dto;

import lombok.Data;

/**
 *
 * 用户登录和token刷新返回双token
 * @date 2020-12-26
 * @author wuanhui
 *
 */
@Data
public class LoginTokenDTO {

    /** 访问token */
    private String accessToken;

    /** 刷新token */
    private String refreshToken;
}
