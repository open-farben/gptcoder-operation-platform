package cn.com.farben.gptcoder.authentication.utils;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import jakarta.servlet.http.HttpServletRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPUtils {
    private static final Log logger = LogFactory.get();

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IP = "0:0:0:0:0:0:0:1";
    private static final String LOCALHOST_IP1 = "127.0.0.1";

    /**
     * 获取IP地址
     * <p>
     * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = null;
        try {
            //以下两个获取在k8s中，将真实的客户端IP，放到了x-Original-Forwarded-For。而将WAF的回源地址放到了 x-Forwarded-For了。
            ip = request.getHeader("X-Original-Forwarded-For");
            if (CharSequenceUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Forwarded-For");
            }
            //获取nginx等代理的ip
            if (CharSequenceUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("x-forwarded-for");
            }
            if (CharSequenceUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (CharSequenceUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (CharSequenceUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (CharSequenceUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            //兼容k8s集群获取ip
            if (CharSequenceUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
                if (LOCALHOST_IP1.equalsIgnoreCase(ip) || LOCALHOST_IP.equalsIgnoreCase(ip)) {
                    //根据网卡取本机配置的IP
                    InetAddress iNet;
                    try {
                        iNet = InetAddress.getLocalHost();
                        ip = iNet.getHostAddress();
                    } catch (UnknownHostException e) {
                        logger.error("getClientIp error: {}", e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("IPUtils ERROR ", e);
        }
        //使用代理，则获取第一个IP地址
        if (CharSequenceUtil.isNotBlank(ip) && ip.contains(StrPool.COMMA)) {
            ip = ip.substring(0, ip.indexOf(StrPool.COMMA));
        }

        return ip;
    }

    private IPUtils() {
        throw new IllegalStateException("工具类不允许创建实例");
    }
}
