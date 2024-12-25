package cn.com.farben.gptcoder.operation.platform.user.dto.role;

import cn.com.farben.commons.ddd.dto.IDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统角色列表查询结果
 * @author wuanhui
 */
@Data
public class SysRoleListDTO implements IDTO {

    private String id;

    /** 角色名称 */
    private String roleName;

    /** 角色权限字符串，角色代码 */
    private String roleKey;

    /** 显示顺序 */
    private Integer roleSort;

    /** 状态（0正常 1停用） */
    private Byte status;

    /** 描述 */
    private String remark;

    /** 角色类型 */
    private Integer roleType;

    /** 角色范围 */
    private Integer rangeScope;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /** 修改者 */
    private String updateBy;

    /** 修改时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
