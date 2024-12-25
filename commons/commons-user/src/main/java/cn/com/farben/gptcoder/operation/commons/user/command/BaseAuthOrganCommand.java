package cn.com.farben.gptcoder.operation.commons.user.command;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 顶层登录用户包含的机构信息
 * @author wuanhui
 */
@Data
public class BaseAuthOrganCommand implements Serializable {

    /**
     * 角色权限范围，是否全部权限，不为空表示存在全部权限，不需要判断机构
     * 与isOnly不会同时不为空
     */
    private String isAll;

    /**
     * 角色权限范围，不为空时表示仅个人权限，不需要判断机构
     * 与isAll不会同时不为空
     */
    private String isOnly;

    /** 登录人账号 */
    private String userId;

    /** 包含的机构号列表 */
    private List<Integer> organNoList;

    /** 包含的长架构编码列表 */
    private List<String> fullOrganNoList;
}
