package cn.com.farben.commons.web.constants;

/**
 * 鉴权相关常量类
 */
public class AuthenticationConstants {
    /** jwt签名的公钥 */
    public static final String JWT_SIGN_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDH1l8PV4GKlFiPy0UBmYS5WUEfRj8OKVsRramA0WDsa7m1DIk3RlmOFZwCMzvqq9G905F4cysz8qyGeJYYeJYmIpF+bTNxYkSyGUYHbYiBziEu//d5mVJQo+6n425d+wsfIkprOVwKvximJQ/PItVN83xkTsayKfSBZZD/86G3wwIDAQAB";

    /** 刷新令牌中存放访问令牌的key */
    public static final String ACCESS_TOKEN_KEY = "accessToken";

    /** jwt中用户账号的key */
    public static final String JWT_ACCOUNT_KEY = "account";

    /** jwt中用户角色的key */
    public static final String JWT_ROLE_KEY = "groups";

    /** redis中令牌的key */
    public static final String REDIS_TOKEN_KEY = "token:";

    /** token请求头字段 */
    public static final String TOKEN_REQUEST_HEADER = "Authorization";

    /** token字段头信息 */
    public static final String TOKEN_HEAD = "Bearer";

    /** 用户标识头名称 */
    public static final String USER_IDENTIFICATION_HEADER = "Uuid";

    /** 插件版本头名称 */
    public static final String PLUGIN_VERSION_HEADER = "Version";

    private AuthenticationConstants() {
        throw new IllegalStateException("常量类不允许实例化");
    }
}
