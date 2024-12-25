package cn.com.farben.gptcoder.operation.platform.user.dto;

import lombok.Data;

import java.util.List;

/**
 *
 * 系统组织机构树形结构实体
 * @author wuanhui
 *
 */
@Data
public class OrganKnowTreeRetDTO {
    private List<OrganKnowTreeDTO> optionalOrgan;

    /** 已选择人员信息 */
    private List<String> memberList;
}
