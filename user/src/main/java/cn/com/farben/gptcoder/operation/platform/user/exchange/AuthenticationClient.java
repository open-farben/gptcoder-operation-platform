package cn.com.farben.gptcoder.operation.platform.user.exchange;

import cn.com.farben.gptcoder.operation.platform.user.dto.LoginTokenDTO;
import cn.hutool.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * 令牌相关接口定义
 */
@HttpExchange("${coder.config.url.authentication}")
public interface AuthenticationClient {
    /**
     * 创建令牌对
     * @param body 参数
     * @return 令牌对
     */
    @PostExchange(value = "/token/generateTokenPair")
    LoginTokenDTO generateTokenPair(@RequestBody JSONObject body);

    /**
     * 刷新令牌对
     * @param body 参数
     * @return 新的令牌对
     */
    @PostExchange(value = "/token/refreshToken")
    LoginTokenDTO refreshToken(@RequestBody JSONObject body);

    /**
     * 注销令牌
     * @param body 参数
     */
    @PostExchange(value = "/token/logout")
    void logout(@RequestBody JSONObject body);
}
