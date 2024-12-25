package cn.com.farben.gptcoder.authentication.facade;

import cn.com.farben.gptcoder.authentication.application.service.TokenAppService;
import cn.com.farben.gptcoder.authentication.command.GenerateTokenCommand;
import cn.com.farben.gptcoder.authentication.command.LogoutCommand;
import cn.com.farben.gptcoder.authentication.command.RefreshTokenCommand;
import cn.com.farben.gptcoder.authentication.dto.TokenPairDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

/**
 * 令牌相关对外接口
 */
@Validated
@RestController
@RequestMapping("/token")
public class TokenController {
    private final TokenAppService tokenAppService;

    public TokenController(TokenAppService tokenAppService) {
        this.tokenAppService = tokenAppService;
    }

    /**
     * 生成令牌对
     * @param generateTokenCommand 生成令牌命令
     * @return 令牌对
     */
    @PostMapping("generateTokenPair")
    public TokenPairDTO generateTokenPair(@RequestBody @Valid GenerateTokenCommand generateTokenCommand) {
        return tokenAppService.generateTokenPair(generateTokenCommand.getUserName(), generateTokenCommand.getRoles());
    }

    /**
     * 刷新令牌对
     * @param refreshTokenCommand 刷新令牌命令
     * @return 新的令牌对
     */
    @PostMapping("refreshToken")
    public TokenPairDTO refreshToken(@RequestBody @Valid RefreshTokenCommand refreshTokenCommand) {
        return tokenAppService.refreshToken(refreshTokenCommand);
    }

    /**
     * 注销令牌
     * @param logoutCommand 访问令牌
     */
    @PostMapping("logout")
    public void logout(@RequestBody @Valid LogoutCommand logoutCommand) {
        tokenAppService.logout(logoutCommand);
    }
}
