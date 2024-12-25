package cn.com.farben.gptcoder.operation.platform.user.facade;

import cn.com.farben.gptcoder.operation.platform.user.exception.AuthorizationExpiredException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.paginate.Page;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@RequestMapping(produces ="application/json")
public class BaseController {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected StringRedisTemplate stringRedisTemplate;

    @Value("${coder.config.page.PageSize:20}")
    protected int PageSize;
    private int pageNo=1;
    public int getPageNumber() {
        if (request!=null && getRequestInt(Arrays.asList("pageNo"))>0) {
            return getRequestInt(Arrays.asList("pageNo"));
        }else
            return pageNo;
    }

    public void setPageNumber(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        if (request!=null &&  getRequestInt(Arrays.asList("pageSize","limit"))>0) {
//            int pg=Integer.parseInt(request.getParameter("pageSize"));
            return getRequestInt(Arrays.asList("pageSize","limit"));
        }else
            return PageSize;
    }
    public Page getPage(){
        int num=getPageNumber();
        int size=getPageSize()>0?getPageSize():20;
        return new Page(num,size);
    }
    public Page getPage(int num){
        int size=getPageNumber();
        return new Page(num,size);
    }

    public void setPageSize(int pageSize) {
        PageSize = pageSize;
    }

    public String getRequestValue(Collection<String> collection){
        for(String obj:collection){
            if (request.getParameter(obj)!=null) return obj;
        }
        return null;
    }
    public int getRequestInt(Collection<String> collection){
        for(String obj:collection){
            if (request.getParameter(obj)!=null && StringUtils.isNumeric(request.getParameter(obj))) return Integer.parseInt(request.getParameter(obj));
        }
        return 0;
    }

    public String getLocalUrl(){
        String All=request.getRequestURL().toString();
        if (StringUtils.isEmpty(request.getContextPath()))
            return All.replace(request.getRequestURI(),"")+"/";
        else
            return All.replace(request.getRequestURI(),"")+request.getContextPath()+"/";
    }

    public String getUserByUuid(String uuid){
        String prefix="plugin_users:";
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(prefix + uuid))) {
            // uuid不正确或登陆已过期
            throw new AuthorizationExpiredException();
        }
        String result=stringRedisTemplate.opsForValue().get(prefix + uuid);
        JSONObject resultJo = JSONUtil.parseObj(result);
        String user = resultJo.getStr("id");
        return user;
    }
}
