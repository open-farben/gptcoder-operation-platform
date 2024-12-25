package cn.com.farben.gptcoder.operation.commons.mvc.formater;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@JsonComponent
public class LocalDateTimeDerializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        DateConverterConfig dateConverterConfig=new DateConverterConfig();
        Date date = null;
        LocalDateTime localDateTime=null;
        try {
            date=dateConverterConfig.convert(jsonParser.getText());

            Instant instant = date.toInstant();
            ZoneId zone = ZoneId.systemDefault();
            localDateTime = LocalDateTime.ofInstant(instant, zone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localDateTime;
    }
}