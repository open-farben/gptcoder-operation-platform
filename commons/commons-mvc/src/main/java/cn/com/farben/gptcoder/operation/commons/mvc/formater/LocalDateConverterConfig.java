package cn.com.farben.gptcoder.operation.commons.mvc.formater;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class LocalDateConverterConfig implements Formatter<LocalDateTime> {
    @Override
    public LocalDateTime parse(String s, Locale locale) throws ParseException {
        DateConverterConfig dateConverterConfig=new DateConverterConfig();
        Date date = null;
        LocalDateTime localDateTime=null;
        try {
            date=dateConverterConfig.convert(s);
            Instant instant = date.toInstant();
            ZoneId zone = ZoneId.systemDefault();
            localDateTime = LocalDateTime.ofInstant(instant, zone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localDateTime;
    }

    @Override
    public String print(LocalDateTime localDateTime, Locale locale) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        Date date = Date.from(instant);
        String value=DateConverterConfig.formatDate(date);
        return value;
//        return null;
    }
    public static void main(String[] args) {

    }
}