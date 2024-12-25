package cn.com.farben.commons.web.utils;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

/**
 * RSA加密工具
 */
public class RsaUtils {
    public static final String CONFIG_KEY = "rsa:";
    /** 私钥 */
    private static final String PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJNQ8e6W29uxU9d175RLS989gMqngDVB0iRRiptZ37nYWDmMchXV/ZNDyFrd4lAquqvpqfJhvTuplGt11BMnWvaDCIH7KUl+u/aFRP7uEjw0x09rvpgz97uKY25ocOp4qRYepK+RCGlZUhxcFWbdDHlAaDvNla2taoAxAALLNcUzAgMBAAECgYAo8gUkewLF185FYy7BaYI/nMEY0qCUbOSDGrTrQ/omlgbDjBFDl3RBA8SC2QwnmxDCapJFX7wwgoxRp/DXmAc0cogR8XIS7Pm7IoKScBHBryJysqGxf1mjP9Fw93ql4OPfev3J1QRJVTudW4d5eujE6ybq8CwRBcnKWZD/CZ5caQJBAOe0SZzYkZfnYIl8KSglFwuIVpgdxvzcd/vgtUkWD+GYtonGptEu2Xxi9YdhpsV/sO8hX+YatWHeDbFe0iu8ZTkCQQCiw2bGPn/MLMQTwCob5+DeDi+dbG+xth25WfoIJnLZp7ceWLFQvpvLaN1iAMl7Tvm/E2DIf5Hg/nx4y2gV1onLAkA3Bq3kvdn1xVE71WzAx+4tIvfNsGStcH5eJwNmlAK5hNb06WR8kS+9RpMgz6QXizet0rM/pmAlGeMOmII5OMihAkB2Hy1VLkJ9IPfkBZAJ1IyjWtQ/gVTGOg70V1JX2996Cneg9Gkq2koioyYXW87dSIGDpEKW/84U5VvcJnKRdrNlAkBvlAV5oW05T296OhFXdxZ4ZFT6L8ur71Su4hJQQoVOFN5UrMCV40CcWwNbJS62bvVkOV+M7SSoO3qgXwVrsHao";

    /** 公钥 */
    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTUPHultvbsVPXde+US0vfPYDKp4A1QdIkUYqbWd+52Fg5jHIV1f2TQ8ha3eJQKrqr6anyYb07qZRrddQTJ1r2gwiB+ylJfrv2hUT+7hI8NMdPa76YM/e7imNuaHDqeKkWHqSvkQhpWVIcXBVm3Qx5QGg7zZWtrWqAMQACyzXFMwIDAQAB";

    /**
     * 解密字符串
     * @param data 数据，Hex（16进制）或Base64字符串
     * @param keyType 密钥类型
     * @return 解密后的密文
     */
    public static String decryptStr(String data, KeyType keyType) {
        RSA rsa = new RSA(PRIVATE_KEY, PUBLIC_KEY);
        return rsa.decryptStr(data, keyType);
    }

    public static String encryptBase64(String data) {
        RSA rsa = new RSA(PRIVATE_KEY, PUBLIC_KEY);
        return rsa.encryptBase64(data, KeyType.PublicKey);
    }

    public static void main(String[] args) {
        System.out.println(encryptBase64("FB123456"));
        System.out.println(encryptBase64("admin"));

    }

    private RsaUtils(){
        throw new IllegalStateException("工具类不允许创建实例");
    }
}
