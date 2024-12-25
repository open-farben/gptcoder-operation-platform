package cn.com.farben.gptcoder.operation.platform.group.command;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.IllegalParameterException;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 授权工作组命令
 */
@Validated
@Data
public class AccreditGroupCommand {
    /** 工作组id */
    @NotBlank(message = "工作组id不能为空")
    private String id;

    /** 用户id */
    @NotBlank(message = "用户id不能为空")
    private String userIds;

    public void verify() {
        if (!JSONUtil.isTypeJSONArray(userIds)) {
            // 框架都是json格式
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0400, "用户id必须是json数组");
        }
        JSONArray userJa = JSONUtil.parseArray(userIds);
        if (userJa.isEmpty()) {
            throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0400, "用户id不能为空");
        }
        for (Object o : userJa) {
            if (!(o instanceof String)) {
                throw new IllegalParameterException(ErrorCodeEnum.USER_ERROR_A0400, "用户id必须是字符串");
            }
        }
    }
}
