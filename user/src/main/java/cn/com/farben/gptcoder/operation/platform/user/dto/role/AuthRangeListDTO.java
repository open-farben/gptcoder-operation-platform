package cn.com.farben.gptcoder.operation.platform.user.dto.role;

import cn.com.farben.commons.ddd.dto.IDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统角色授权范围列表返回对象
 * @author wuanhui
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRangeListDTO implements IDTO {

    private Integer type;

    private String value;
}
