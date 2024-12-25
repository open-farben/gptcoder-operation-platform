package cn.com.farben.gptcoder.operation.commons.mvc.formater;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Configuration
public class LocalDateTimeSerializerConfig {
    @Value("${spring.jackson.date-format:yyyy-MM-dd HH:mm:ss}")
    private String pattern;

    // localDateTime 序列化器
    @Bean
    public LocalDateTimeSerializer localDateTimeSerializer() {
        return new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(pattern));
    }

    // localDateTime 反序列化器
    @Bean
    public LocalDateTimeDeserializer localDateTimeDeserializer() {
        return new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(pattern));
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            builder.featuresToEnable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            builder.serializerByType(LocalDateTime.class, new LocalDateTimeJsonSerializer());
            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDerializer());
            builder.deserializerByType(Date.class,new DateDerializer());
            builder.dateFormat(new SimpleDateFormat(pattern));
            SimpleModule module = new SimpleModule();
            module.addDeserializer(Date.class, new DateDerializer());
            module.addDeserializer(LocalDateTime.class, new LocalDateTimeDerializer());
            module.addSerializer(LocalDateTime.class,new LocalDateTimeJsonSerializer());
            builder.modules(module);
            builder.simpleDateFormat(pattern);
        };
    }
}