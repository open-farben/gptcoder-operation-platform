package cn.com.farben.commons.web.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class XssStringJsonDeserializer extends JsonDeserializer<String> {
    @Override
    public Class<String> handledType() {
        return String.class;
    }

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return XssUtil.cleanXSS(jsonParser.getValueAsString());
    }
}
