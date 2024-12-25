package cn.com.farben.gptcoder.operation.platform.user.application.service;

import cn.com.farben.gptcoder.operation.commons.user.utils.UserInfoUtils;
import cn.com.farben.gptcoder.operation.platform.user.dto.LoginTokenDTO;
import cn.com.farben.gptcoder.operation.platform.user.exception.IllegalUserException;
import cn.com.farben.gptcoder.operation.platform.user.exchange.AuthenticationClient;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 *
 * 管理平台用户token应用服务
 * 创建时间：2023/12/26
 * @author wuanhui
 *
 */
@Component
public class AccessTokenAppService {
    private final AuthenticationClient authenticationClient;

    public AccessTokenAppService(AuthenticationClient authenticationClient) {
        this.authenticationClient = authenticationClient;
    }

    /**
     * 刷新验证token操作
     * @param refreshToken 刷新token
     * @return 新token对
     */
    public LoginTokenDTO refreshToken(String refreshToken) {
        String token = Objects.isNull(UserInfoUtils.getUserInfo()) ? null : UserInfoUtils.getUserInfo().getToken();
        if (CharSequenceUtil.isBlank(token)) {
            return null;
        }
        JSONObject body = new JSONObject();
        body.set("accessToken", token);
        body.set("refreshToken", refreshToken);
        LoginTokenDTO dto = authenticationClient.refreshToken(body);
        if (Objects.isNull(dto) || CharSequenceUtil.isBlank(dto.getAccessToken()) || CharSequenceUtil.isBlank(dto.getRefreshToken())) {
            throw new IllegalUserException("未获取到新的令牌对，请确认令牌有效");
        }
        return dto;
    }
}
