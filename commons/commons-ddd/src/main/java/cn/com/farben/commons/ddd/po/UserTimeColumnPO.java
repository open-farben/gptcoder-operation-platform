package cn.com.farben.commons.ddd.po;

import com.mybatisflex.annotation.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 创建者、创建时间、更新者、更新时间四个列
 */
@Getter
@Setter
public class UserTimeColumnPO implements IPO {
    /** 创建者 */
    private String createUser;

    /** 创建时间 */
    @Column(onInsertValue = "now()")
    private LocalDateTime createTime;

    /** 更新者 */
    private String updateUser;

    /** 更新时间 */
    @Column(onUpdateValue = "now()", onInsertValue = "now()")
    private LocalDateTime updateTime;
}
