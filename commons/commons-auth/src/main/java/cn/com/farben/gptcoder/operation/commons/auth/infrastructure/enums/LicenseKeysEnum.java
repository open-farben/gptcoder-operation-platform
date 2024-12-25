package cn.com.farben.gptcoder.operation.commons.auth.infrastructure.enums;

import lombok.Getter;

/**
 * license对应json字段key枚举
 */
@Getter
public enum LicenseKeysEnum {
    TYPE("type", "授权类型"), ACCOUNT_NUMBER("accountNumber", "账号数"),
    CLIENT_NUMBER("clientNumber", "客户端限制数"), EXPIRATION_DATE("expirationDate", "截止日期"),
    LICENSE("license", "授权码"), SIGNED("signed", "签名");

    LicenseKeysEnum(String key, String describe) {
        this.key = key;
        this.describe = describe;
    }

    private final String key;
    private final String describe;
}
