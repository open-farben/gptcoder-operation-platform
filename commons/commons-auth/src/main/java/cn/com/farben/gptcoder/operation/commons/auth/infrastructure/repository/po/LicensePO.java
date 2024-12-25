package cn.com.farben.gptcoder.operation.commons.auth.infrastructure.repository.po;

import cn.com.farben.commons.ddd.po.IPO;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.time.LocalDateTime;

/**
 * license表
 */
@Data
@Table("license")
public class LicensePO implements IPO {
    /** 字段id */
    @Id(keyType = KeyType.None)
    private String id;

    /** license */
    private String license;

    /** 创建者 */
    private String createUser;

    /** 创建时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
