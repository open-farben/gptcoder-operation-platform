package cn.com.farben.commons.file.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import cn.com.farben.commons.file.infrastructure.enums.FileUseTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 * 文件信息DTO<br>
 *
 */
@Data
public class FileInfoDTO implements IDTO {
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

    /** 使用类型 */
    private FileUseTypeEnum useType;

    /** 父id，主要针对父为文件夹 */
    private String parentId;

    /** 根id */
    private String rootId;

    /** 创建者 */
    private String createUser;

    /** 创建时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /** 更新者 */
    private String updateUser;

    /** 更新时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
