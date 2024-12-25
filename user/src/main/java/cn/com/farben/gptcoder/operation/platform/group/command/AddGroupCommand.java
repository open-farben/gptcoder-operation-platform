package cn.com.farben.gptcoder.operation.platform.group.command;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

/**
 * 新增工作组命令
 */
@Validated
@Data
public class AddGroupCommand {
    /** 工作组名称 */
    @NotBlank(message = "工作组名称不能为空")
    @Size(min = 1, max = 50, message = "工作组名称为1-50个字符")
    private String groupName;

    /** 所属机构 */
    @NotNull(message = "所属机构不能为空")
    @Positive
    private Integer organNo;

    /** 工作组描述 */
    @Size(max = 200, message = "工作组描述最多200个字符")
    private String introduce;

    /** 生效时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent
    private LocalDate effectiveDay;

    /** 失效时间 */
    @Future
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate failureDay;
}
