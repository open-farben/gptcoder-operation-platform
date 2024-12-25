package cn.com.farben.commons.web.utils;

import cn.com.farben.commons.web.io.FbServletInputStream;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TrimRequestWrapper extends HttpServletRequestWrapper {
    private static final Log logger = LogFactory.get();

    /**
     * 保存处理后的参数
     */
    private final Map<String, String[]> params = new HashMap<>();

    public TrimRequestWrapper(HttpServletRequest request) {
        //将request交给父类，以便于调用对应方法的时候，将其输出
        super(request);
        //对于非json请求的参数进行处理
        if (super.getHeader(HttpHeaders.CONTENT_TYPE) == null ||
                (!super.getHeader(HttpHeaders.CONTENT_TYPE).equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE))) {
            setParams(request);
        }
    }

    private void setParams(HttpServletRequest request) {
        //将请求的的参数转换为map集合
        Map<String, String[]> requestMap = request.getParameterMap();
        if (Objects.nonNull(requestMap) && !requestMap.isEmpty()) {
            for (Map.Entry<String, String[]> entry : requestMap.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();
                String[] newValues = new String[values.length];
                if (values.length > 0) {
                    int index = 0;
                    for (String value : values) {
                        newValues[index] = value.trim();
                        index++;
                    }
                }

                this.params.put(key.trim(), newValues);
            }
        }
    }

    /**
     * 重写getParameter 参数从当前类中的map获取
     */
    @Override
    public String getParameter(String name) {
        String[] values = params.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    /**
     * 重写getParameterValues
     */
    @Override
    public String[] getParameterValues(String name) {
        return params.get(name);
    }


    /**
     * 重写getInputStream方法  post类型的请求参数必须通过流才能获取到值
     * 这种获取的参数的方式针对于内容类型为文本类型，比如Content-Type:text/plain,application/json,text/html等
     * 在springmvc中可以使用@RequestBody 来获取 json数据类型
     * 其他文本类型不做处理，重点处理json数据格式
     * getInputStream() ，只有当方法为post请求，且参数为json格式是，会被默认调用
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        //为空，直接返回
        String str = IoUtil.readUtf8(super.getInputStream());
        if (CharSequenceUtil.isBlank(str)) {
            return super.getInputStream();
        }
        logger.info("str: [{}]", str);
        str = str.trim();
        logger.info("str2: [{}]", str);
        if (JSONUtil.isTypeJSONObject(str)) {
            //json字符串首尾去空格
            JSONObject jo = JSONUtil.parseObj(str);
            JSONObject paramJo = new JSONObject();
            for (Map.Entry<String, Object> entry : jo) {
                String key = entry.getKey();
                Object value = entry.getValue();
                logger.info("value1: [{}]", value);
                if (value instanceof String stringValue) {
                    value = stringValue.trim();
                }
                logger.info("value2: [{}]", value);
                paramJo.set(key.trim(), value);
            }
            str = paramJo.toString();
        }
        logger.info("str3: [{}]", str);
        ByteArrayInputStream bis = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        return new FbServletInputStream(bis);
    }
}
