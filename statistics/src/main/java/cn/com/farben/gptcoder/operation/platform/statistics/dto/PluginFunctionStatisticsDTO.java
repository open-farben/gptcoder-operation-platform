package cn.com.farben.gptcoder.operation.platform.statistics.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import cn.hutool.core.annotation.Alias;
import lombok.Data;

/**
 *
 * 用户使用功能情况统计结果DTO<br>
 * 创建时间：2023/11/1<br>
 * @author ltg
 *
 */
@Data
public class PluginFunctionStatisticsDTO implements IDTO {
    /** 序号 */
    @Alias("序号")
    private Integer sort;

    /** 用户账号 */
    @Alias("账号")
    private String account;

    /** 工号 */
    @Alias("工号")
    private String jobNumber;

    /** 姓名 */
    @Alias("姓名")
    private String name;

    /** 组织架构名称 */
    @Alias("组织机构")
    private String organizationName;

    /** 职位名称 */
    @Alias("职位")
    private String dutyName;

    /** 累计使用次数 */
    @Alias("累计使用次数")
    private Integer totalUseCount;

    /** 提示代码次数 */
    @Alias("提示代码次数")
    private Integer hintingCount;

    /** 解释代码次数 */
    @Alias("解释代码次数")
    private Integer explainCount;

    /** 纠正代码次数 */
    @Alias("纠正代码次数")
    private Integer correctionCount;

    /** 注释代码次数 */
    @Alias("注释代码次数")
    private Integer commentCount;

    /** 转换代码次数 */
    @Alias("转换代码次数")
    private Integer conversionCount;

    /** 技术问答次数 */
    @Alias("技术问答次数")
    private Integer questionCount;

    /** 生成单元测试次数 */
    @Alias("生成单元测试次数")
    private Integer testCount;

    /** 知识库问答次数 */
    @Alias("知识库问答次数")
    private Integer knowledgeQaCount;

    /** 代码搜索次数 */
    @Alias("代码搜索次数")
    private Integer codeSearchCount;
}
