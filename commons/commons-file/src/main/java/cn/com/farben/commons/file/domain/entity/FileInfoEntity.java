package cn.com.farben.commons.file.domain.entity;

import cn.com.farben.commons.ddd.domain.UserTimeColumnEntity;
import cn.com.farben.commons.file.infrastructure.enums.FileUseTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件信息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileInfoEntity extends UserTimeColumnEntity {
    /** 记录ID */
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
