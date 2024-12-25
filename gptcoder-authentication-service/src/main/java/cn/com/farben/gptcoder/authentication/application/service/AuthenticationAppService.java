package cn.com.farben.gptcoder.authentication.application.service;

import cn.com.farben.commons.web.constants.AuthenticationConstants;
import cn.com.farben.gptcoder.authentication.constants.PluginConstants;
import cn.com.farben.gptcoder.authentication.utils.CompareUtils;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.jwt.JWTException;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class AuthenticationAppService {
    private static final Log logger = LogFactory.get();

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${authentication.config.uri-white-list}")
    private String uriWhiteList;

    @Value("${authentication.config.platform-uris}")
    private String platformUris;

    @Value("${authentication.config.plugin-uris}")
    private String pluginUris;

    @Value("${authentication.config.api-key}")
    private String apiKey;

    @Value("${authentication.config.api-uris}")
    private String apiUris;

    private static final List<String> whiteList = null;

    private static final List<String> platformUriList = null;

    private static final List<String> pluginUriList = null;

    private static final List<String> apiUriList = null;

    public AuthenticationAppService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
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
    public void verifyGPTCoder(String forwardedUri, String forwardedIp, String authHeader, String uuid, String version,
                               HttpServletResponse response) {
        logger.info("校验权限，forwardedUri:[{}], forwardedIp:[{}], authHeader:[{}], uuid:[{}], version:[{}]",
                forwardedUri, forwardedIp, authHeader, uuid, version);
        if (inWhiteList(forwardedUri)) {
            logger.info("uri[{}]在白名单中，校验通过", forwardedUri);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        boolean pluginFlg = false;
        boolean platformFlg = false;
        boolean apiFlg = false;

        if (isApiUri(forwardedUri)) {
            apiFlg = verifyApi(authHeader, forwardedUri);
        }
        if (apiFlg) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        if (isPluginUri(forwardedUri)) {
            pluginFlg = verifyPlugin(uuid, version, forwardedUri);
        }
        if (pluginFlg) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        if (isPlatformUri(forwardedUri)) {
            platformFlg = verifyPlatform(authHeader, forwardedUri);
        }
        if (platformFlg) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        logger.error("权限校验未通过，forwardedUri:[{}], forwardedIp:[{}], authHeader:[{}], uuid:[{}], version:[{}]",
                forwardedUri, forwardedIp, authHeader, uuid, version);
    }

    /**
     * 判断是否在白名单中
     * @param forwardedUri 原始访问ip
     * @return 是或否
     */
    private boolean inWhiteList(String forwardedUri) {
        return CompareUtils.isContain(whiteList, uriWhiteList, forwardedUri);
    }

    /**
     * 判断是否是平台用户uri
     * @param forwardedUri 原始访问ip
     * @return 是或否
     */
    private boolean isPlatformUri(String forwardedUri) {
        return CompareUtils.isContain(platformUriList, platformUris, forwardedUri);
    }

    /**
     * 判断是否是插件用户uri
     * @param forwardedUri 原始访问ip
     * @return 是或否
     */
    private boolean isPluginUri(String forwardedUri) {
        return CompareUtils.isContain(pluginUriList, pluginUris, forwardedUri);
    }

    /**
     * 判断是否是开放出去的api的uri
     * @param forwardedUri 原始访问ip
     * @return 是或否
     */
    private boolean isApiUri(String forwardedUri) {
        return CompareUtils.isContain(apiUriList, apiUris, forwardedUri);
    }

    private boolean verifyPlatform(String authHeader, String forwardedUri) {
        logger.info("校验平台用户，uri:[{}],token:[{}]", forwardedUri, authHeader);
        if (CharSequenceUtil.isBlank(authHeader) || !authHeader.startsWith(AuthenticationConstants.TOKEN_HEAD)) {
            logger.warn("没有令牌信息");
            return false;
        }
        final String authToken = authHeader.replace(AuthenticationConstants.TOKEN_HEAD, "").trim();
        if (CharSequenceUtil.isBlank(authToken)) {
            logger.warn("令牌为空");
            return false;
        }
        RSA rsa = new RSA(null, AuthenticationConstants.JWT_SIGN_PUBLIC_KEY);
        final JWTSigner signer = JWTSignerUtil.rs256(rsa.getPublicKey());
        try {
            if (!JWTUtil.verify(authToken, signer)) {
                logger.warn("非法令牌[{}]", authToken);
                return false;
            }
        } catch (JWTException e) {
            logger.warn("非法令牌[{}]", authToken);
            return false;
        }
        String accessRedisKey = AuthenticationConstants.REDIS_TOKEN_KEY + authToken;
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(accessRedisKey))) {
            logger.warn("令牌[{}]已过期", authToken);
            return false;
        }
        logger.info("平台用户，uri:[{}],token:[{}]校验通过", forwardedUri, authHeader);
        return true;
    }

    private boolean verifyPlugin(String uuid, String version, String forwardedUri) {
        logger.info("校验插件用户，uri:[{}], uuid:[{}], version:[{}]", forwardedUri, uuid, version);
        if (CharSequenceUtil.isBlank(uuid) || CharSequenceUtil.isBlank(version)) {
            logger.warn("没有uuid或version");
            return false;
        }
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(PluginConstants.REDIS_PLUGIN_USER_PREFIX + uuid))) {
            // uuid不正确或登陆已过期
            logger.warn("uuid[{}]不正确或登陆已过期", uuid);
            return false;
        }
        String[] versionArr = version.split(StrPool.COLON);
        if (versionArr.length != 2) {
            logger.warn("插件版本信息[{}]不正确或登陆已过期", version);
            return false;
        }
        String versionKey = PluginConstants.VERSION_REDIS_KEY;
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(versionKey))) {
            // 没有版本信息
            logger.warn("缓存中没有版本信息");
            return false;
        }
        String pluginType = versionArr[0];
        String pluginVersion = versionArr[1];
        List<String> versionList = stringRedisTemplate.opsForList().range(versionKey, 0, -1);
        if (Objects.nonNull(versionList) && CollUtil.isNotEmpty(versionList)) {
            long count = versionList.stream().filter(str -> str.equalsIgnoreCase(pluginType + StrPool.UNDERLINE + pluginVersion)).count();
            if (count < 1) {
                logger.warn("插件[{}]，版本[{}]已不可用", pluginType, pluginVersion);
                return false;
            }
        }
        logger.info("插件用户，uri:[{}], uuid:[{}], version:[{}]校验通过", forwardedUri, uuid, version);
        return true;
    }

    private boolean verifyApi(String authHeader, String forwardedUri) {
        logger.info("校验开放出去的api，uri:[{}],token:[{}]", forwardedUri, authHeader);
        if (CharSequenceUtil.isBlank(authHeader)) {
            logger.warn("没有令牌信息");
            return false;
        }
        if (!apiKey.equals(authHeader)) {
            logger.warn("非法的api key");
            return false;
        }
        logger.info("开放出去的api，uri:[{}],token:[{}]校验通过", forwardedUri, authHeader);
        return true;
    }
}
