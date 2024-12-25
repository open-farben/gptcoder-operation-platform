package cn.com.farben.gptcoder.operation.commons.user.utils;

import cn.com.farben.commons.errorcode.exception.OperationNotAllowedException;
import cn.com.farben.commons.web.bo.UserInfoBO;
import cn.com.farben.commons.web.utils.UserThreadLocal;
import cn.hutool.core.text.CharSequenceUtil;

import java.util.Objects;

public class UserInfoUtils {
    public static UserInfoBO getUserInfo() {
        UserInfoBO userInfo = UserThreadLocal.get();
        if (Objects.isNull(userInfo) || CharSequenceUtil.isBlank(userInfo.getAccount())) {
            throw new OperationNotAllowedException("未获取到登陆用户信息");
        }
        return userInfo;
    }

    private UserInfoUtils() {
        throw new IllegalStateException("工具类不允许实例化");
    }
}
