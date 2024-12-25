package cn.com.farben.gptcoder.operation.platform.user.dto;

import lombok.Data;

/**
 * 插件用户导入结果类
 * @author wuanhui
 */
@Data
public class PluginUserImportDTO {

    /** 总记录数 */
    private int total;

    /** 成功记录数 */
    private int success;

    /** 默认密码 */
    private String defaultPwd;

    /** 失败内容存入redis缓存，将对应Key返回给前端 */
    private String cacheKey;
}
