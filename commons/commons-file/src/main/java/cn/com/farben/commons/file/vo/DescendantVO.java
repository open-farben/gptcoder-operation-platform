package cn.com.farben.commons.file.vo;

import cn.com.farben.commons.file.domain.entity.FileInfoEntity;
import lombok.Data;

import java.util.List;

/**
 * 强制删除目录反馈vo
 */
@Data
public class DescendantVO {
    private FileInfoEntity entity;
    private List<FileInfoEntity> descendantList;
}
