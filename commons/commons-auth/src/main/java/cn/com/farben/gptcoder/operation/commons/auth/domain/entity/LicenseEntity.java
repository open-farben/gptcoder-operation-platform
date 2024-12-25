package cn.com.farben.gptcoder.operation.commons.auth.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 * 授权码实体<br>
 * 创建时间：2023/10/08<br>
 * @author ltg
 *
 */
@Data
public class LicenseEntity implements IEntity {
    /** 字段id */
    private String id;

    /** license */
    private String license;

    /** 创建者 */
    private String createUser;

    /** 创建时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
