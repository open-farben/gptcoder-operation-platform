package cn.com.farben.gptcoder.operation.platform.user.infrastructure.utils;

import cn.com.farben.gptcoder.operation.commons.user.utils.CoderClientUtils;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.UserSystemConstants;
import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.digest.DigestUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * web服务操作工具类
 * @author Administrator
 * @version 1.0
 * @title CoderServiceUtils
 * @create 2023/7/27 17:41
 */
public class CoderServiceUtils {

    /**
     * 验证用户登录密码是否正确
     * @param pwd 新密码
     * @param orgPwd 原始密码
     * @return true 密码一致
     */
    public static boolean checkPwd(String pwd, String orgPwd) {
        String decodePassword = Base64.decodeStr(pwd);
        String md5Password = DigestUtil.md5Hex(decodePassword);
        return md5Password.equals(orgPwd);
    }

    /**
     * 校验批导用户账号
     * @param account 账号
     * @param accountSet 已有账号
     * @param sb 错误提示
     */
    public static void checkImportAccount(String account, Set<String> accountSet, StringBuffer sb) {
        //账号必填
        if(StringUtils.isBlank(account)){
            sb.append("账号不能为空；");
        }else{
            if(accountSet.contains(account)) {
                sb.append("文档内账号重复；");
            }else if(account.length() < 3 || account.length() > 18){
                //账号长度
                sb.append("用户账号为3-18个字符；");
            }
        }
    }

    /**
     * 校验批导用户名称
     * @param name 用户名称
     * @param sb 错误提示
     */
    public static void checkImportName(String name, StringBuffer sb) {
        if(StringUtils.isBlank(name)){
            sb.append("用户姓名不能为空；");
        }else if(name.length() < 2 || name.length() > 20){
            sb.append("用户姓名为2-20个字符；");
        }
    }

    /**
     * 校验批导用户手机号
     * @param mobile 手机号
     * @param sb 错误提示
     */
    public static void checkImportMobile(String mobile, StringBuffer sb) {
        if(StringUtils.isNotBlank(mobile) && !CoderClientUtils.checkPhone(mobile)){
            sb.append("手机号不符合格式；");
        }
    }

    /**
     * 校验批导用户邮箱
     * @param email 邮箱
     * @param sb 错误提示
     */
    public static void checkImportEmail(String email, StringBuffer sb) {
        if(StringUtils.isBlank(email)){
            sb.append("邮箱不能为空；");
        }else if(!CoderClientUtils.checkEmail(email)) {
            sb.append("邮箱不正确；");
        } else if (email.length() > 50) {
            sb.append("邮箱最多为50个字符；");
        }
    }

    /**
     * 校验批导用户启用状态
     * @param status 邮箱
     * @param sb 错误提示
     */
    public static void checkImportStatus(String status, StringBuffer sb) {
        if(StringUtils.isBlank(status)){
            sb.append("启用状态不能为空；");
        }else if (!UserSystemConstants.IMPORT_FIELD_DISABLE_LIST.contains(status)) {
            sb.append("启用状态不正确；");
        }
    }
}
