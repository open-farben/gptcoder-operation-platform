package cn.com.farben.gptcoder.operation.commons.auth.infrastructure.enums;

import lombok.Getter;

/**
 * 授权类型枚举
 */
@Getter
public enum LicenseTypesEnum {
    STANDARD("Standard", "标准版"), TRIAL("trial", "试用版");

    LicenseTypesEnum(String type, String describe) {
        this.type = type;
        this.describe = describe;
    }

    private final String type;
    private final String describe;
}
