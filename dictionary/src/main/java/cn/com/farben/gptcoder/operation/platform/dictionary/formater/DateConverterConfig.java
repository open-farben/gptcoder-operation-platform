package cn.com.farben.gptcoder.operation.platform.dictionary.formater;

import cn.hutool.core.text.CharSequenceUtil;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@Component
public class DateConverterConfig implements Converter<String, Date> {

    public static final List<String> formats = new ArrayList<>(4);
    static{
        formats.add("yyyy-MM");
        formats.add("yyyy-MM-dd");
        formats.add("yyyy-MM-dd HH:mm");
        formats.add("yyyy-MM-dd HH:mm:ss");
        formats.add("yyyy-MM-dd HH:mm:ss.SSS");
        formats.add("yyyy/MM");
        formats.add("yyyy/MM/dd");
        formats.add("yyyy/MM/dd HH:mm");
        formats.add("yyyy/MM/dd HH:mm:ss");
        formats.add("yyyy/MM/dd HH:mm:ss.SSS");
    }

    @Override
    public Date convert(String source) {
        String value = source.trim();
        if (CharSequenceUtil.isBlank(value)) {
            return null;
        }
        if(source.matches("^\\d+")){
            return parseDate(source);
        }else if(source.matches("^\\d{4}-\\d{1,2}$")){
            return parseDate(source, formats.get(0));
        }else if(source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")){
            return parseDate(source, formats.get(1));
        }else if(source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")){
            return parseDate(source, formats.get(2));
        }else if(source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")){
            return parseDate(source, formats.get(3));
        }else if(source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{3}$")){
            return parseDate(source, formats.get(4));
        }else if(source.matches("^\\d{4}/\\d{1,2}$")){
            return parseDate(source, formats.get(5));
        }else if(source.matches("^\\d{4}/\\d{1,2}/\\d{1,2}$")){
            return parseDate(source, formats.get(6));
        }else if(source.matches("^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")){
            return parseDate(source, formats.get(7));
        }else if(source.matches("^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")){
            return parseDate(source, formats.get(8));
        }else if(source.matches("^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{3}$")){
            return parseDate(source, formats.get(9));
        }else {
//            logger.warn("Error Date:"+source);
            return parseDate2(source,"yyyy-MM-dd'T'HH:mm:ss.SSSXXX","yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//            return null;
            //throw new IllegalArgumentException("Invalid date value '" + source + "'");
        }
    }

    /**
     * 格式化日期
     * @param dateStr String 字符型日期
     * @param format String 格式
     * @return Date 日期
     */
    public  Date parseDate(String dateStr, String format) {
        Date date=null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            date = dateFormat.parse(dateStr);
        } catch (Exception e) {

        }
        return date;
    }
    public  Date parseDate2(String dateStr, String... parsePatterns) {
        Date date=null;
        try {
            date =  DateUtils.parseDate(dateStr,parsePatterns);
        } catch (Exception e) {
        }
        return date;
    }
    public  Date parseDate(String dateStr) {
        Date date=null;
        try {
            long dateLong=Long.parseLong(dateStr);
            date = new Date(dateLong);
        } catch (Exception e) {

        }
        return date;
    }
    public  static String formatDate(Date date) {
        return DateFormatUtils.format(date, DateConverterConfig.formats.get(3));
    }
    public static void main(String[] args) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

    }
}
