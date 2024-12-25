package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po;

import cn.com.farben.commons.ddd.po.IPO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统角色授权范围（自定义机构范围）
 * @author wuanhui
 */
@Data
@Table("sys_role_range")
public class SysRoleRangePO implements IPO {

    @Id(keyType = KeyType.None)
    private String id;

    /** 角色ID */

    private String roleId;

    /** 机构号 */
    private Integer organNo;

    /** 机构长编码 */
    private String fullOrganNo;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
