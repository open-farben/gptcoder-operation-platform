package cn.com.farben.commons.web.utils;

import cn.com.farben.commons.web.bo.UserInfoBO;

/**
 * 将用户信息放入ThreadLocal，方便其余模块获取
 */
public class UserThreadLocal {
    private UserThreadLocal(){}

    private static final ThreadLocal<UserInfoBO> LOCAL = new ThreadLocal<>();

    public static void put(UserInfoBO userInfo){
        LOCAL.set(userInfo);
    }

    public static UserInfoBO get(){
        return LOCAL.get();
    }

    public static void remove(){
        LOCAL.remove();
    }
}
