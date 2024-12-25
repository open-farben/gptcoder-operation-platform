package cn.com.farben.commons.file.infrastructure.repository.po;

import cn.com.farben.commons.ddd.po.UserTimeColumnPO;
import cn.com.farben.commons.file.infrastructure.enums.FileUseTypeEnum;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 插件版本表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(value = "file_info")
public class FileInfoPO extends UserTimeColumnPO {
    /** 记录ID */
    @Id(keyType = KeyType.None)
    private String id;

    /** 文件类型 */
    private String type;

    /** 文件名 */
    private String fileName;

    /** 大小(字节) */
    private Long fileSize;

    /** 是否文件夹（1是，0否） */
    private Byte folder;

    /** 文件路径 */
    private String path;

    /** 使用类型 */
    private FileUseTypeEnum useType;

    /** 父id，主要针对父为文件夹 */
    private String parentId;

    /** 根id */
    private String rootId;
}
