package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po;

import cn.com.farben.commons.ddd.po.IPO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统角色表
 * @author wuanhui
 */
@Data
@Table("sys_role")
public class SysRolePO implements IPO {

    /** 角色ID */
    @Id(keyType = KeyType.None)
    private String id;

    /** 角色名称 */
    private String roleName;

    /** 角色权限字符串 */
    private String roleKey;

    /** 显示顺序 */
    private Integer roleSort;

    /** 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限） */
    private String dataScope;

    /** 菜单树选择项是否关联显示: 0是  1否 */
    private byte menuCheck;

    /** 部门树选择项是否关联显示: 0是  1否 */
    private byte deptCheck;

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
