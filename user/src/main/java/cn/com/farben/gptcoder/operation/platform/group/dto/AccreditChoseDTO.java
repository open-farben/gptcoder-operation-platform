package cn.com.farben.gptcoder.operation.platform.group.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import cn.hutool.core.lang.tree.Tree;
import lombok.Data;

import java.util.List;

/**
 *
 * 授权选择DTO<br>
 *
 */
@Data
public class AccreditChoseDTO implements IDTO {
    /** 可选人员机构信息 */
    private List<Tree<Integer>> optionalOrgan;

    /** 已选择人员信息 */
    private List<String> memberList;
}
