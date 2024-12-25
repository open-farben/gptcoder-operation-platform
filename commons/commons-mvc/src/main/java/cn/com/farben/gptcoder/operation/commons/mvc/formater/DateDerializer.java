package cn.com.farben.gptcoder.operation.commons.mvc.formater;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.Date;

@JsonComponent
public class DateDerializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        DateConverterConfig dateConverterConfig=new DateConverterConfig();
        Date date = null;
        try {
            date=dateConverterConfig.convert(jsonParser.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;

    }
}