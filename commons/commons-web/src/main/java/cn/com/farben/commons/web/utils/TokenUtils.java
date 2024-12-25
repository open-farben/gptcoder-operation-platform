package cn.com.farben.commons.web.utils;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;
import cn.com.farben.commons.web.constants.AuthenticationConstants;
import cn.com.farben.commons.web.bo.UserInfoBO;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;

import java.util.List;
import java.util.Objects;

public class TokenUtils {
    /**
     * 根据访问令牌获取用户信息
     * @param token 访问令牌
     * @return 用户信息
     */
    public static UserInfoBO getUserInfoByToken(String token) {
        try {
            JWT jwt = JWTUtil.parseToken(token);
            Object accountObj = jwt.getPayload(AuthenticationConstants.JWT_ACCOUNT_KEY);
            Object rolesObj = jwt.getPayload(AuthenticationConstants.JWT_ROLE_KEY);
            String account = Objects.isNull(accountObj) ? null : accountObj.toString();
            List<String> roles = Objects.isNull(rolesObj) ? null : (List<String>)rolesObj;
            UserInfoBO dto = new UserInfoBO();
            dto.setAccount(account);
            dto.setRoles(roles);
            dto.setToken(token);
            return dto;
        }catch (Exception e) {
            throw new BusinessException(ErrorCodeEnum.USER_ERROR_A0800);
        }
    }

    private TokenUtils() {
        throw new IllegalStateException("工具类不允许实例化");
    }
}
