package cn.com.farben.gptcoder.operation.commons.auth.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * license返回前端DTO<br>
 * 创建时间：2023/9/21<br>
 * @author ltg
 *
 */
@Data
public class LicenseDTO implements IDTO {
    /** 授权类型 */
    private String type;

    /** 最近一次授权时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastAuthDate;

    /** 授权截止日期 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate expirationDate;

    /** 总账号数 */
    private Integer accountNumber;

    /** 剩余账号数 */
    private Long surplus;

    /** 客户端限制数 */
    private Integer clientNumber;
}
