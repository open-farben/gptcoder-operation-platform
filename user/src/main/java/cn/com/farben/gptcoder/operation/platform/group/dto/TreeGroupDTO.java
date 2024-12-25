package cn.com.farben.gptcoder.operation.platform.group.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import cn.hutool.core.lang.tree.Tree;
import lombok.Data;

import java.util.List;

/**
 *
 * 树形结构返回工作组DTO<br>
 *
 */
@Data
public class TreeGroupDTO implements IDTO {
    /** 可选人员机构信息 */
    private List<Tree<Integer>> optionalOrgan;

    /** 已选择工作组列表 */
    private List<String> memberList;
}
