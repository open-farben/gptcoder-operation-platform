package cn.com.farben.gptcoder.authentication.constants;

public class ForwardedConstants {
    /** 原始的URI请求头 */
    public static final String FORWARDED_URI_HEADER = "X-Forwarded-Uri";

    /** 发起请求的原ip地址请求头 */
    public static final String FORWARDED_IP_HEADER = "X-Forwarded-For";

    private ForwardedConstants() {
        throw new IllegalStateException("常量类不允许实例化");
    }
}
