package cn.com.farben.commons.web.utils;

import cn.hutool.core.text.CharSequenceUtil;

public class XssUtil {
    public static String cleanXSS(String value) {
        if (CharSequenceUtil.isBlank(value)) {
            return value;
        }
        //在这里自定义需要过滤的字符
        value = value.replace("<", "& lt;").replace(">", "& gt;");
        value = value.replace("\\(", "& #40;").replace("\\)", "& #41;");
        value = value.replace("'", "& #39;");
        value = value.replace("eval((.*))", "");
        value = value.replace("[\"'][s]*javascript:(.*)[\"']", "\"\"");
        value = value.replace("<script>", "");
        return value;
    }

    private XssUtil(){
        throw new IllegalStateException("工具类不允许创建实例");
    }
}
