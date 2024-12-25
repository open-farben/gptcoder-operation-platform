package cn.com.farben.gptcoder.operation.platform.user.facade;

import cn.com.farben.commons.web.ResultData;
import cn.com.farben.gptcoder.operation.platform.user.application.service.AccessTokenAppService;
import cn.com.farben.gptcoder.operation.platform.user.dto.LoginTokenDTO;
import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * token续期相关操作接口
 * @author wuanhui
 */
@RestController
@RequestMapping("/token")
public class AccessTokenController {
    private final AccessTokenAppService accessTokenAppService;

    /**
     * 刷新用户令牌
     * @param refreshToken 刷新令牌
     * @return 请求结果
     */
    @PostMapping("/refresh")
    public ResultData<LoginTokenDTO> refreshToken(@RequestHeader("RefreshToken")String refreshToken) {
        if (CharSequenceUtil.isBlank(refreshToken)) {
            throw new IllegalArgumentException("未传入刷新令牌");
        }
        return new ResultData.Builder<LoginTokenDTO>().ok().data(accessTokenAppService.refreshToken(refreshToken)).build();
    }

    public AccessTokenController(AccessTokenAppService accessTokenAppService) {
        this.accessTokenAppService = accessTokenAppService;
    }
}
