package cn.com.farben.commons.web.command;

import cn.com.farben.commons.web.validation.FromNotAfterTo;
import lombok.Data;

import java.time.LocalDate;

/**
 * 带from和to日期的基本参数
 */
@Data
@FromNotAfterTo
public class BaseFromToDateCommand {
    /** 起始日期yyyy-MM-dd */
    private LocalDate from;

    /** 截止日期yyyy-MM-dd */
    private LocalDate to;
}
