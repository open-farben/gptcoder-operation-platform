package cn.com.farben.gptcoder.authentication.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import lombok.Data;

/**
 * 令牌对DTO
 */
@Data
public class TokenPairDTO implements IDTO {
    /** 访问令牌 */
    private String accessToken;

    /** 刷新令牌 */
    private String refreshToken;
}
