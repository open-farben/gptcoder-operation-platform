package cn.com.farben.gptcoder.authentication.facade;

import cn.com.farben.commons.web.constants.AuthenticationConstants;
import cn.com.farben.gptcoder.authentication.application.service.AuthenticationAppService;
import cn.com.farben.gptcoder.authentication.constants.ForwardedConstants;
import cn.com.farben.gptcoder.authentication.constants.PluginConstants;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权限校验接口，校验管理平台用户和插件用户权限
 */
@Validated
@RestController
@RequestMapping("/authentication")
public class AuthenticationController {
    private final AuthenticationAppService authenticationAppService;

    public AuthenticationController(AuthenticationAppService authenticationAppService) {
        this.authenticationAppService = authenticationAppService;
    }

    /**
     * 校验GPTCoder权限
     * @param forwardedUri 原始uri
     * @param forwardedIp 原始ip地址
     * @param authHeader 令牌
     * @param uuid 插件用户uuid
     * @param version 插件版本
     * @param response 响应
     */
    @GetMapping("verifyGPTCoder")
    public void verifyGPTCoder(@RequestHeader(ForwardedConstants.FORWARDED_URI_HEADER) String forwardedUri,
                               @RequestHeader(value = ForwardedConstants.FORWARDED_IP_HEADER, required = false) String forwardedIp,
                               @RequestHeader(value = AuthenticationConstants.TOKEN_REQUEST_HEADER, required = false) String authHeader,
                               @RequestHeader(value = PluginConstants.USER_IDENTIFICATION_HEADER, required = false) String uuid,
                               @RequestHeader(value = PluginConstants.PLUGIN_VERSION_HEADER, required = false) String version,
                               HttpServletResponse response) {
        authenticationAppService.verifyGPTCoder(forwardedUri, forwardedIp, authHeader, uuid, version, response);
    }
}
