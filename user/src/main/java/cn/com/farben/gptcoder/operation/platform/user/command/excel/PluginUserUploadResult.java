package cn.com.farben.gptcoder.operation.platform.user.command.excel;

import lombok.Data;

import java.util.List;

/**
 * 插件用户导入数据解析结果
 * @author wuanhui
 */
@Data
public class PluginUserUploadResult {

    /** 总记录数 */
    private int total;

    /** 初次校验成功记录数 */
    private int success;

    /** 初次成功的账号列表 */
    private List<String> accountList;

    /** 校验成功的记录 */
    private List<PluginUserImportData> dataList;

    /** 校验失败的记录 */
    private List<PluginUserImportData> errorList;
}
