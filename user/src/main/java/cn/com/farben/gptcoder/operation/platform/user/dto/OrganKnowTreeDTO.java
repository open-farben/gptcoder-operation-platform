package cn.com.farben.gptcoder.operation.platform.user.dto;

import cn.com.farben.gptcoder.operation.platform.group.domain.entity.AuthorizedUserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 *
 * 系统组织机构树形结构实体
 * @author wuanhui
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class OrganKnowTreeDTO extends OrganMngTreeDTO {

    List<AuthorizedUserEntity> users;


}
