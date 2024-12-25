package cn.com.farben.gptcoder.authentication.application.service;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.IllegalParameterException;
import cn.com.farben.gptcoder.authentication.command.LogoutCommand;
import cn.com.farben.gptcoder.authentication.command.RefreshTokenCommand;
import cn.com.farben.gptcoder.authentication.constants.TokenConstants;
import cn.com.farben.gptcoder.authentication.dto.TokenPairDTO;
import cn.com.farben.commons.web.constants.AuthenticationConstants;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTException;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class TokenAppService {
    private static final Log logger = LogFactory.get();

    private final StringRedisTemplate stringRedisTemplate;

    public TokenAppService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 生成令牌对
     * @param userName 用户名
     * @param roles 用户角色
     * @return 令牌对
     */
    public TokenPairDTO generateTokenPair(String userName, List<String> roles) {
        String accessToken = createAccessToken(userName, roles);
        String refreshToken = createRefreshToken(accessToken);
        logger.info("根据用户名[{}]和角色[{}]创建令牌对", userName, roles);

        return cacheToken(accessToken, refreshToken);
    }

    /**
     * 用户刷新令牌
     * @param refreshTokenCommand 刷新令牌命令
     * @return 新的令牌对
     */
    public TokenPairDTO refreshToken(RefreshTokenCommand refreshTokenCommand) {
        String accessToken = refreshTokenCommand.getAccessToken();
        String refreshToken = refreshTokenCommand.getRefreshToken();
        if (illegalToken(refreshToken)) {
            String invalidRefreshTokenMessage = "无效的刷新令牌或刷新令牌已失效";
            logger.error(invalidRefreshTokenMessage, refreshToken);
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0220, invalidRefreshTokenMessage);
        }
        JWT accessJwt;
        JWT refreshJwt;
        try {
            accessJwt = JWTUtil.parseToken(accessToken);
            refreshJwt = JWTUtil.parseToken(refreshToken);
        } catch (Exception e) {
            logger.error("解析令牌出错", e);
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0220, "错误的令牌");
        }
        if (!accessToken.equals(refreshJwt.getPayload(AuthenticationConstants.ACCESS_TOKEN_KEY))) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0220, "令牌不匹配");
        }
        Object accountObj = accessJwt.getPayload(AuthenticationConstants.JWT_ACCOUNT_KEY);
        Object roleObj = accessJwt.getPayload(AuthenticationConstants.JWT_ROLE_KEY);
        if (Objects.isNull(accountObj)) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0220, "令牌未包含账户信息");
        }
        if (Objects.isNull(roleObj)) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0220, "令牌未包含角色信息");
        }
        Object payloadAccessObj = refreshJwt.getPayload(AuthenticationConstants.ACCESS_TOKEN_KEY);
        if (Objects.isNull(payloadAccessObj)) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0220, "刷新令牌未包含访问令牌的信息");
        }
        if (!accessToken.equals(payloadAccessObj.toString())) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0220, "刷新令牌中的访问令牌与传入的访问令牌不一致");
        }
        String accessRedisKey = AuthenticationConstants.REDIS_TOKEN_KEY + accessToken;
        String refreshRedisKey = AuthenticationConstants.REDIS_TOKEN_KEY + refreshToken;
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(refreshRedisKey))) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0220, "刷新令牌已失效");
        }

        // 令牌校验通过，发放新的令牌对
        JSONArray roleJa = JSONUtil.parseArray(roleObj);
        String newAccessToken = createAccessToken(accountObj.toString(), JSONUtil.toList(roleJa, String.class));
        String newRefreshToken = createRefreshToken(newAccessToken);
        TokenPairDTO tokenPairDTO = cacheToken(newAccessToken, newRefreshToken);

        // 刷新令牌只用一次，刷新后删除
        stringRedisTemplate.delete(accessRedisKey);
        stringRedisTemplate.delete(refreshRedisKey);

        return tokenPairDTO;
    }

    /**
     * 注销令牌
     * @param logoutCommand 访问令牌
     */
    public void logout(LogoutCommand logoutCommand) {
        String accessRedisKey = AuthenticationConstants.REDIS_TOKEN_KEY + logoutCommand.getAccessToken();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(accessRedisKey))) {
            String refreshToken = stringRedisTemplate.opsForValue().get(accessRedisKey);
            stringRedisTemplate.delete(accessRedisKey);
            // 同步删除对应的刷新令牌
            stringRedisTemplate.delete(AuthenticationConstants.REDIS_TOKEN_KEY + refreshToken);
        }
    }

    /**
     * 缓存令牌
     * @param accessToken 访问令牌
     * @param refreshToken 刷新令牌
     * @return 返回前端的令牌数据
     */
    private TokenPairDTO cacheToken(String accessToken, String refreshToken) {
        TokenPairDTO tokenPairDTO = new TokenPairDTO();
        tokenPairDTO.setAccessToken(accessToken);
        tokenPairDTO.setRefreshToken(refreshToken);

        // 存入缓存
        stringRedisTemplate.opsForValue().set(AuthenticationConstants.REDIS_TOKEN_KEY + accessToken, refreshToken, TokenConstants.ACCESS_TOKEN_PERIOD, TimeUnit.MINUTES);
        stringRedisTemplate.opsForValue().set(AuthenticationConstants.REDIS_TOKEN_KEY + refreshToken, "1", TokenConstants.REFRESH_TOKEN_PERIOD, TimeUnit.MINUTES);

        return tokenPairDTO;
    }

    private boolean illegalToken(String token) {
        RSA rsa = new RSA(null, AuthenticationConstants.JWT_SIGN_PUBLIC_KEY);
        final JWTSigner signer = JWTSignerUtil.rs256(rsa.getPublicKey());

        try {
            return !JWTUtil.verify(token, signer);
        } catch (JWTException e) {
            return true;
        }
    }

    /**
     * 创建访问令牌
     * @param account 用户账号
     * @param roleList 角色列表
     * @return 访问令牌
     */
    private String createAccessToken(String account, List<String> roleList) {
        RSA rsa = new RSA(TokenConstants.JWT_SIGN_PRIVATE_KEY, null);
        final JWTSigner signer = JWTSignerUtil.rs256(rsa.getPrivateKey());
        return JWT.create()
                .setPayload(AuthenticationConstants.JWT_ACCOUNT_KEY, account)
                .setPayload(AuthenticationConstants.JWT_ROLE_KEY, JSONUtil.parseArray(roleList))
                .setIssuedAt(DateUtil.date())
                // 访问令牌的有效期
                .setExpiresAt(DateUtil.date().offset(DateField.MINUTE, TokenConstants.ACCESS_TOKEN_PERIOD))
                .sign(signer);
    }

    /**
     * 创建刷新令牌
     * @param accessToken 访问令牌
     * @return 刷新令牌
     */
    private String createRefreshToken(String accessToken) {
        RSA rsa = new RSA(TokenConstants.JWT_SIGN_PRIVATE_KEY, null);
        final JWTSigner signer = JWTSignerUtil.rs256(rsa.getPrivateKey());
        return JWT.create()
                .setPayload(AuthenticationConstants.ACCESS_TOKEN_KEY, accessToken)
                .setIssuedAt(DateUtil.date())
                // 刷新令牌的有效期
                .setExpiresAt(DateUtil.date().offset(DateField.MINUTE, TokenConstants.REFRESH_TOKEN_PERIOD))
                .sign(signer);
    }
}
