package cn.com.farben.gptcoder.operation.commons.user.utils;

import cn.com.farben.commons.errorcode.exception.IllegalParameterException;
import cn.hutool.core.text.CharSequenceUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 公共时间处理转换相关工具类
 * @author wuanhui
 */
public class CommonDateUtils {
    /** 时间格式：日期 */
    public static final String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";

    /** 时间格式：日期，时分 */
    public static final String FORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

    /** 时间格式：日期，时分秒 */
    public static final String FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**开始日期 + 后缀 */
    private static final String START_TIME_STR = " 00:00:00";

    /** 结束日期 + 后缀 */
    private static final String END_TIME_STR = " 23:59:59";

    private static final String ERROR_MESSAGE = "时间格式错误，请检查参数";

    /**
     * 获取查询的开始时间，前端传递格式为：yyyy-MM-dd
     * @param timeStr 前端时间
     * @return 转换后时间
     */
    public static Date convertStrToDate(String timeStr, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(timeStr);
        } catch (Exception e) {
            throw new IllegalParameterException(ERROR_MESSAGE);
        }
    }



    /**
     * 获取查询的开始时间，前端传递格式为：yyyy-MM-dd
     * @param timeStr 前端时间
     * @return 转换后时间
     */
    public static Date convertStartDate(String timeStr) {
        if(CharSequenceUtil.isNotBlank(timeStr) && timeStr.length() == 10) {
            timeStr =  timeStr + START_TIME_STR;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MM_DD_HH_MM_SS);
            return sdf.parse(timeStr);
        } catch (Exception e) {
            throw new IllegalParameterException(ERROR_MESSAGE);
        }
    }

    /**
     * 获取查询的结束时间，前端传递格式为：yyyy-MM-dd
     * @param str 前端时间
     * @return 转换后时间
     */
    public static Date convertEndDate(String str) {
        if(CharSequenceUtil.isBlank(str)) {
            return null;
        }
        if(str.length() == 10) {
            str =  str + END_TIME_STR;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MM_DD_HH_MM_SS);
            return sdf.parse(str);
        } catch (Exception e) {
            throw new IllegalParameterException(ERROR_MESSAGE);
        }
    }

    /**
     * 获取指定日期的最早时间
     * @param date 指定日期
     * @return 时间
     */
    public static Date getCurrentStartDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取指定日期的最晚时间
     * @param date 指定日期
     * @return 时间
     */
    public static Date getCurrentEndDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 获取指定日期的小时
     * @param date 指定日期
     * @return 时间
     */
    public static int getCurrentHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取指定日期的的间隔day天
     * @param today 指定的日期
     * @param day 间隔，往前是负数
     * @return 新时间
     */
    public static Date getBeforeDay(Date today, int day) {
        //获取的是系统当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DATE, day);
        return calendar.getTime();
    }

    /**
     * Date -> String
     *
     * @param date 日期
     * @param formatter 时间格式，默认为yyyy-MM-dd HH:mm:ss
     * @return 返回值
     */
    public static String getDateStr(Date date, String formatter) {
        if (date == null) {
            return "";
        }
        if (CharSequenceUtil.isBlank(formatter)) {
            formatter = FORMAT_YYYY_MM_DD_HH_MM_SS;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(formatter);
        return sdf.format(date);
    }

    /**
     * Date -> String
     *
     * @param date 日期
     * @return 返回值
     */
    public static String getDefaultDateStr(Date date) {
        if (date == null) {
            return "-";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MM_DD_HH_MM_SS);
        return sdf.format(date);
    }

    private CommonDateUtils(){
        throw new IllegalStateException("工具类不允许创建实例");
    }
}
