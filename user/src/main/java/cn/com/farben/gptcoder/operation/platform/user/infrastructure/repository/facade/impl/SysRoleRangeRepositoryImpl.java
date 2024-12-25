package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.impl;

import cn.com.farben.gptcoder.operation.platform.user.domain.entity.SysRoleRangeEntity;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysRoleRangeDTO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.SysRoleRangeRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper.SysRoleRangeMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.table.SysRoleRangeTableDef.SYS_ROLE_RANGE;

/**
 *
 * 系统角色授权范围仓储实现
 * @author wuanhui
 *
 */
@Repository
public class SysRoleRangeRepositoryImpl implements SysRoleRangeRepository {

    private final SysRoleRangeMapper sysRoleRangeMapper;

    public SysRoleRangeRepositoryImpl(SysRoleRangeMapper sysRoleRangeMapper) {
        this.sysRoleRangeMapper = sysRoleRangeMapper;
    }

    /**
     * 添加自定义角色范围表
     *
     * @param list 列表数据
     * @return 操作结果
     */
    @Override
    public Boolean addRoleRange(List<SysRoleRangeEntity> list) {
        return sysRoleRangeMapper.addRoleRange(list) > 0;
    }

    /**
     * 根据角色ID查询该角色关联的机构范围
     * @param roleId 角色ID
     * @return 机构范围
     */
    @Override
    public List<SysRoleRangeDTO> findRangeListByRole(String roleId) {
        return sysRoleRangeMapper.findRangeListByRole(roleId);
    }

    /**
     * 根据角色ID查询该角色关联的机构号
     * @param roleId 角色ID
     * @return 机构范围
     */
    @Override
    public List<Integer> findRangeOrganNoListByRole(String roleId) {
        return sysRoleRangeMapper.findRangeOrganNoListByRole(roleId);
    }

    /**
     * 删除角色需同步删除对应的机构范围
     *
     * @param roleIds 多个角色ID
     * @return 机构范围
     */
    @Override
    public Boolean delByRoleIds(List<String> roleIds) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.where(SYS_ROLE_RANGE.ROLE_ID.in(roleIds));
        return sysRoleRangeMapper.deleteByQuery(queryWrapper) > 0;
    }
}
