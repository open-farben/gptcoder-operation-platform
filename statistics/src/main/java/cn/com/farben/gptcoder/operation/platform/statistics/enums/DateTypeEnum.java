package cn.com.farben.gptcoder.operation.platform.statistics.enums;

import cn.hutool.core.text.CharSequenceUtil;

import java.util.Arrays;

/**
 * 统计日期类型枚举
 * @author wuanhui
 */
public enum DateTypeEnum {

    DAY("day", "日"),
    MONTH("month", "月"),
    YEAR("year", "年");

    private final String code;

    private final String desc;

    DateTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    public static DateTypeEnum exist(String code) {
        if(CharSequenceUtil.isBlank(code)) {
            return null;
        }
        return Arrays.stream(DateTypeEnum.values()).filter(e -> e.code.equals(code)).findAny().orElse(null);
    }
}
