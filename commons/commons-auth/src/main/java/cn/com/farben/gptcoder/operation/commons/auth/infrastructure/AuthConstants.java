package cn.com.farben.gptcoder.operation.commons.auth.infrastructure;

/**
 * 系统认证相关常量
 */
public class AuthConstants {
    /** 私钥环境变量 */
    public static final String PRIVATE_KEY_ENV = "PRIVATE_KEY";

    /** 公钥 */
    public static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSg220S0hChltQJRNfTvD5+WHTdwsuos716MifkwwFS/t37uYGWwUxQkJWOFv+MV9a1Lnro7CjfKeldFIm/YU9zuhiyJRqbIimBvjdIWdMXkTQPlvCAfkHT725GYSmZaqBaJYUaKNhqwesoKv5PQyKaMuGAXdqywHik04Sl7Sn/wIDAQAB";

    /** license文件名 */
    public static final String LICENSE_FILE = "gptcoder.lic";

    private AuthConstants() {
        throw new IllegalStateException("常量类不允许实例化");
    }
}
