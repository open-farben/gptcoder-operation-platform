package cn.com.farben.gptcoder.operation.commons.user.utils;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import jakarta.servlet.http.HttpServletRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * web模块工具类
 * @author wuanhui
 * @version 1.0
 * @title CoderClientUtils
 * @create 2023/8/2 11:21
 */
public class CoderClientUtils {
    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IP = "0:0:0:0:0:0:0:1";
    private static final String LOCALHOST_IP1 = "127.0.0.1";

    /** 手机号校验正则 */
    private static final String PHONE_MATCHER_STR = "^1([3-9])[0-9]{9}$";

    /** 邮箱正则 */
    private static final String EMAIL_MATCHER_STR = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /**
     * 获取客户端IP地址
     * @param request 请求信息
     * @return IP地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = null;
        try {
            //以下两个获取在k8s中，将真实的客户端IP，放到了x-Original-Forwarded-For。而将WAF的回源地址放到了 x-Forwarded-For了。
            ip = request.getHeader("X-Original-Forwarded-For");
            if (CharSequenceUtil.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Forwarded-For");
            }
            //获取nginx等代理的ip
            if (CharSequenceUtil.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("x-forwarded-for");
            }
            if (CharSequenceUtil.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (CharSequenceUtil.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (CharSequenceUtil.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (CharSequenceUtil.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            //兼容k8s集群获取ip
            if (CharSequenceUtil.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
                if (LOCALHOST_IP1.equalsIgnoreCase(ip) || LOCALHOST_IP.equalsIgnoreCase(ip)) {
                    //根据网卡取本机配置的IP
                    InetAddress iNet = null;
                    try {
                        iNet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
//                        logger.error("getClientIp error: {}", e);
                    }
                    ip = iNet != null ? iNet.getHostAddress() : "";
                }
            }
        } catch (Exception ignored) {
        }
        //使用代理，则获取第一个IP地址
        if (!CharSequenceUtil.isEmpty(ip) && ip.contains(StrPool.COMMA)) {
            ip = ip.substring(0, ip.indexOf(StrPool.COMMA));
        }

        return ip;
    }

    /**
     * 校验邮箱格式
     * @param email 邮箱
     * @return 是否符合
     */
    public static boolean checkEmail(String email) {
        if(CharSequenceUtil.isBlank(email)) {
            return false;
        }
        Pattern regex = Pattern.compile(EMAIL_MATCHER_STR);
        Matcher matcher = regex.matcher(email);
        return matcher.matches();
    }

    /**
     * 校验手机号格式
     * @param phone 手机号
     * @return 是否符合
     */
    public static boolean checkPhone(String phone) {
        if(CharSequenceUtil.isBlank(phone)) {
            return false;
        }
        Pattern p = Pattern.compile(PHONE_MATCHER_STR);
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    private CoderClientUtils() {
        throw new IllegalStateException("工具类不允许创建实例");
    }

}
