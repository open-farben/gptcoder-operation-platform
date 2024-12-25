package cn.com.farben.gptcoder.operation.platform.user.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 系统组织机构树形结构实体
 * @author wuanhui
 *
 */
@Data
public class OrganMngTreeDTO implements IDTO {

    /** 记录ID */
    private String id;

    /** 机构号 */
    private Integer organNo;

    /** 机构号全称 */
    private String fullOrganNo;

    /** 机构名称 */
    private String organName;

    /** 机构简称 */
    private String shortName;

    private Integer organLevel;

    /** 所属父级机构号 */
    private Integer parentOrganNo;

    /** 子集架构 */
    List<OrganMngTreeDTO> children;

    public void addChildren(OrganMngTreeDTO children) {
        if(this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(children);
    }

}
