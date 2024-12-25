package cn.com.farben.gptcoder.operation.platform.group.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * 工作组实体<br>
 *
 */
@Data
public class WorkingGroupEntity implements IEntity {
    /** 记录ID */
    private String id;

    /** 工作组名称 */
    private String groupName;

    /** 机构号 */
    private Integer organNo;

    /** 工作组描述 */
    private String introduce;

    /** 生效时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate effectiveDay;

    /** 失效时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate failureDay;

    /** 创建者 */
    private String createUser;

    /** 创建时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /** 更新者 */
    private String updateUser;

    /** 更新时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
