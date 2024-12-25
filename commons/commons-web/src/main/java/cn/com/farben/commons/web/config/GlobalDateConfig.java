package cn.com.farben.commons.web.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class GlobalDateConfig {
    private static final String DATETIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMATTER = "yyyy-MM-dd";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer2() {
        return builder -> {
            DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMATTER);
            DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);

            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(localDateTimeFormatter));
            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(localDateTimeFormatter));

            builder.serializerByType(LocalDate.class, new LocalDateSerializer(localDateFormatter));
            builder.deserializerByType(LocalDate.class, new LocalDateDeserializer(localDateFormatter));
        };
    }
}
