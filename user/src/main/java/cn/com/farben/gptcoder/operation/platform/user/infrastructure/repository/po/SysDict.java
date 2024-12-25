package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author Gary
 * @since 2024-04-09
 */
@Table("sys_dict")
@Data
public class SysDict implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.None)
    private String code;

    private String value;

}
