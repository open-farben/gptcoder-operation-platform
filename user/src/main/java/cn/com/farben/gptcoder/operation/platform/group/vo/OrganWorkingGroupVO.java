package cn.com.farben.gptcoder.operation.platform.group.vo;

import cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.po.WorkingGroupPO;
import lombok.Data;

import java.util.List;

@Data
public class OrganWorkingGroupVO {
    private String organNo;
    private String organName;
    private String parentOrganNo;

    private List<WorkingGroupPO> groupList;
}
