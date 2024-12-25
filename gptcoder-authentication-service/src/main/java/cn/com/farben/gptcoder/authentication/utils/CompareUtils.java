package cn.com.farben.gptcoder.authentication.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 比较是否匹配的工具类
 */
public class CompareUtils {
    /**
     * 比较目标字符串是否包含在指定的配置中
     * @param compareList 配置的列表
     * @param compareStr 配置的字符串
     * @param target 对比目标字符串
     * @return 是否包含
     */
    public static boolean isContain(List<String> compareList, String compareStr, String target) {
        if (CharSequenceUtil.isBlank(compareStr) || CharSequenceUtil.isBlank(target)) {
            return false;
        }
        if (Objects.isNull(compareList) || CollUtil.isEmpty(compareList)) {
            String[] compareArr = compareStr.split(StrPool.COMMA);
            compareList = new ArrayList<>(compareArr.length);
            for (String str : compareArr) {
                compareList.add(str.trim());
            }
        }
        if (target.contains("?")) {
            // uri中有参数，只匹配路径，不管参数
            target = target.substring(0, target.indexOf("?"));
        }

        if (compareList.contains(target)) {
            return true;
        }
        // 不包含，考虑通配符的情况
        for (String str : compareList) {
            if (Pattern.matches(str, target) || target.startsWith(str + StrPool.SLASH)) {
                // 有pathVariable的情况，路径后肯定有/
                return true;
            }
        }
        return false;
    }

    private CompareUtils() {
        throw new IllegalStateException("工具类不允许创建实例");
    }
}
