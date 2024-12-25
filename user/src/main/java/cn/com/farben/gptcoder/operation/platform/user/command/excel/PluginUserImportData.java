package cn.com.farben.gptcoder.operation.platform.user.command.excel;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

import java.io.Serializable;

/**
 * 插件用户导入信息映射类
 * @author wuanhui
 */
@Data
public class PluginUserImportData implements Serializable {

    @Alias("序号")
    private String sort;

    @Alias("账号")
    private String account;

    @Alias("姓名")
    private String name;

    @Alias("工号")
    private String jobNumber;

    @Alias("机构")
    private String organ;

    @Alias("职务")
    private String duty;

    @Alias("手机号")
    private String mobile;

    @Alias("邮箱")
    private String email;

    @Alias("状态")
    private String status;

    /** 错误提示语 */
    @Alias("失败原因")
    private String errorMsg;

    private String fullOrganization;

}
