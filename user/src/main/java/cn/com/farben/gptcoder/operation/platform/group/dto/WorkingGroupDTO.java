package cn.com.farben.gptcoder.operation.platform.group.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 *
 * 工作组DTO<br>
 *
 */
@Data
public class WorkingGroupDTO implements IDTO {
    /** 记录ID */
    private String id;

    /** 工作组名称 */
    private String groupName;

    /** 机构号 */
    private Integer organNo;

    /** 机构名称 */
    private String organName;

    /** 工作组描述 */
    private String introduce;

    /** 人数 */
    private Long userNumber;

    /** 生效时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate effectiveDay;

    /** 失效时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate failureDay;
}
