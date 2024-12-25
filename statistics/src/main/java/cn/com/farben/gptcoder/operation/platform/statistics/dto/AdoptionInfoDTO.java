package cn.com.farben.gptcoder.operation.platform.statistics.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import lombok.Data;

/**
 *
 * 插件使用记录DTO<br>
 * 创建时间：2023/8/15<br>
 * @author ltg
 *
 */
@Data
public class AdoptionInfoDTO implements IDTO {
    /** 用户账号 */
    private String userId;

    /** 用户名称 */
    private String userName;

    /** 使用次数 */
    private Integer useNum;

    /** 生成代码行数 */
    private Integer genNum;

    /** 确认代码行数 */
    private Integer confirmNum;
}
