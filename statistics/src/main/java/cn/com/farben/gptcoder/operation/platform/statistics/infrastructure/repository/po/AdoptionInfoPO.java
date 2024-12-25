package cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.po;

import cn.com.farben.commons.ddd.po.IPO;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.enums.ModelFeatureEnum;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Table("t_adoption_info")
public class AdoptionInfoPO implements IPO {
    /** 流水ID，主键 */
    @Id(keyType = KeyType.None)
    private String infoId;

    /** 用户ID */
    private String userId;

    /** 方法ID,参考枚举
     * @see ModelFeatureEnum
     */
    private String funId;

    /** 客户端IP */
    private String clientIp;

    /** 插件版本 */
    private String pluginsVer;

    /** ide名称 */
    private String ideName;

    /** ide版本 */
    private String ideVer;

    /** 系统版本 */
    private String sysVer;

    /** 客户端其他信息 */
    private String macInfo;

    /** 生成代码行数 */
    private Integer genNum;

    /** 确认代码行数 */
    private Integer confirmNum;

    /** 请求访问日期 */
    private LocalDate accessDate;

    /** 请求花费时间 */
    private Integer costTime;

    /** 提示字符数 */
    private Integer promptNum;

    /** 补全字符数 */
    private Integer complementCharNum;

    /** 代码补全率 */
    private BigDecimal complementRate;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private LocalDateTime createDate;

    /** 是否已处理过，1：是，0或null：否 */
    private byte dealFlg;

    /** 当前模型 */
    private String modelName;

    /** 插件类型 */
    private String pluginType;
}
