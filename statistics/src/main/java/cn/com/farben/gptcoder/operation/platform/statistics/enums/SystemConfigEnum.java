package cn.com.farben.gptcoder.operation.platform.statistics.enums;

public enum SystemConfigEnum {

    SYSTEM_BASE_SPEED("system_base_speed", "平均响应速率设置");

    private final String code;

    private final String desc;

    SystemConfigEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}
