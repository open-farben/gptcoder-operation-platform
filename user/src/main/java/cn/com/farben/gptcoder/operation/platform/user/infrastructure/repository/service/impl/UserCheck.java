package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.service.impl;

import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper.PluginUserMapper;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper.UserAccountMapper;
import com.mybatisflex.core.update.UpdateChain;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.table.PluginUserTableDef.PLUGIN_USER;
import static cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.table.UserAccountTableDef.USER_ACCOUNT;

@Service
public class UserCheck {
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    private UserAccountMapper userAccountMapper;
    @Autowired
    private PluginUserMapper pluginUserMapper;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final int MAX_LOGIN_ATTEMPTS = 5; // 最大登录尝试次数
    private static final long LOGIN_LOCKOUT_DURATION = 30*60000; // 锁定时间,单位毫秒
    private static String REDISKEY = "loginAttemptMap";

    public class LoginAttemptInfo {
        int attemptCount = 0;
        long lastAttemptTime = 0;
        boolean isLocked = false;
    }
    public RMap<String, LoginAttemptInfo> getMap(){
        return redissonClient.getMap(REDISKEY);
    }
    public void clearMap(){
        redissonClient.getMap(REDISKEY).fastRemoveAsync();
    }
    public void removeMap(String user){
        redissonClient.getMap(REDISKEY).fastRemoveAsync(user);
    }
    public LoginAttemptInfo getInfo(String username){
        RMap<String, LoginAttemptInfo> rMap=redissonClient.getMap(REDISKEY);
        if (rMap==null) return null;
        return rMap.get(username);
    }
    public boolean preLogin(String username) {
        return preLogin(username,0);
    }
    public boolean preLogin(String username,int type) {
        // 检查用户是否已被锁定
        RMap<String, LoginAttemptInfo> loginAttemptMap=getMap();
        if (loginAttemptMap==null) return true;
        LoginAttemptInfo loginAttemptInfo = loginAttemptMap.get(username);
        if (loginAttemptInfo != null
                && loginAttemptInfo.isLocked
                && System.currentTimeMillis() - loginAttemptInfo.lastAttemptTime < LOGIN_LOCKOUT_DURATION) {
            return false;
        }else
            return true;
    }
    public void lockUser(String userId,int type){
        if (type==0){
            UpdateChain.of(userAccountMapper).set(USER_ACCOUNT.LOCKED, 1)
                    .where(USER_ACCOUNT.USER_ID.eq(userId)).update();
        }else{
            UpdateChain.of(pluginUserMapper).set(PLUGIN_USER.LOCKED, 1)
                    .where(PLUGIN_USER.ACCOUNT.eq(userId)).update();
        }
    }
    public void unlockUser(String userId,int type){
        if (type==0){
            UpdateChain.of(userAccountMapper).set(USER_ACCOUNT.LOCKED, 0)
                    .where(USER_ACCOUNT.USER_ID.eq(userId)).update();
        }else{
            UpdateChain.of(pluginUserMapper).set(PLUGIN_USER.LOCKED, 0)
                    .where(PLUGIN_USER.ACCOUNT.eq(userId)).update();
        }
        removeMap(userId);
    }

    public String checkUser(String username) {
        RMap<String, LoginAttemptInfo> loginAttemptMap=getMap();
        if (loginAttemptMap==null) return null;
        LoginAttemptInfo loginAttemptInfo = loginAttemptMap.get(username);
        if (loginAttemptInfo != null){
            if (loginAttemptInfo.isLocked || loginAttemptInfo.attemptCount==4) {
                return "系统已被锁定，请30分钟后再试！";
            }else if(loginAttemptInfo.attemptCount>=2){
                String msg="若再次尝试登录失败，该账号将被锁定30分钟，当前剩余"+(4-loginAttemptInfo.attemptCount)+"次尝试机会";
                return msg;
            }else
                return null;
        }else
            return null;
    }

    @Async
    public void postLogin(String username, LoginAttemptInfo loginAttemptInfo, boolean loginSuccess,int type) {
        RMap<String, LoginAttemptInfo> loginAttemptMap=getMap();
        if (!loginSuccess) {
            if (loginAttemptInfo == null) {
                loginAttemptInfo = new LoginAttemptInfo();
            }
            loginAttemptInfo.attemptCount+=1;
            loginAttemptInfo.lastAttemptTime = System.currentTimeMillis();
            logger.warn("user "+username+" login failed, attempt count: "+loginAttemptInfo.attemptCount);
            if (loginAttemptInfo.attemptCount >= MAX_LOGIN_ATTEMPTS) {
                logger.warn("User "+username+" is locked.");
                loginAttemptInfo.isLocked = true;
                lockUser(username,type);
            }
            loginAttemptMap.put(username, loginAttemptInfo);
        } else {
            // 登录成功,清除登录尝试信息
            loginAttemptMap.fastRemoveAsync(username);
        }
    }
}
