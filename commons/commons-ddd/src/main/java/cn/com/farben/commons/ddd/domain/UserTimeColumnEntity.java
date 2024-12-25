package cn.com.farben.commons.ddd.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 创建者、创建时间、更新者、更新时间四个列
 */
@Getter
@Setter
public class UserTimeColumnEntity implements IEntity {
    /** 创建者 */
    private String createUser;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新者 */
    private String updateUser;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
