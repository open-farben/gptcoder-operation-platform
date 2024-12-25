package cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums;

import java.util.Arrays;

/**
 * 系统角色权限范围配置枚举
 * @author wuanhui
 */
public enum SysRoleRangeEnum {
    ALL(0, "全部数据权限", "全部范围"),
    CUSTOM(1, "自定义数据权限", "自定义范围"),
    BELOW_DEPART(2, "本部门及以下数据权限", "本部门及以下范围"),
    DEPARTMENT(3, "本部门数据权限", "本部门范围");/*,
    SELF(4, "仅本人数据权限", "仅本人")*/

    private final Integer code;

    private final String title;

    private final String desc;

    SysRoleRangeEnum(Integer code, String title, String desc) {
        this.code = code;
        this.title = title;
        this.desc = desc;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDesc() {
        return this.desc;
    }

    public static SysRoleRangeEnum exist(Integer type) {
       return Arrays.stream(SysRoleRangeEnum.values()).filter(e -> e.code.equals(type)).findAny().orElse(null);
    }

}
