package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.impl;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.gptcoder.operation.platform.user.command.role.QueryRoleListCommand;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.SysRoleEntity;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysRoleListDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysRoleTreeDTO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.SysRoleRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper.SysRoleMapper;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.SysRolePO;
import cn.hutool.core.text.CharSequenceUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.table.SysRoleTableDef.SYS_ROLE;

/**
 *
 * 系统角色信息仓储实现
 * @author wuanhui
 *
 */
@Repository
public class SysRoleRepositoryImpl implements SysRoleRepository {

    private final SysRoleMapper sysRoleMapper;

    public SysRoleRepositoryImpl(SysRoleMapper sysRoleMapper) {
        this.sysRoleMapper = sysRoleMapper;
    }

    /**
     * 新增角色
     *
     * @param entity 角色信息
     * @return 操作结果
     */
    @Override
    public Boolean addRole(SysRoleEntity entity) {
        SysRolePO role = new SysRolePO();
        BeanUtils.copyProperties(entity, role);
        return sysRoleMapper.insertSelectiveWithPk(role) > 0;
    }

    /**
     * 根据角色ID查询
     * @param roleId 角色ID
     * @return 操作结果
     */
    @Override
    public SysRoleEntity findRoleById(String roleId) {
        SysRoleEntity sysRoleEntity = new SysRoleEntity();
        CommonAssemblerUtil.assemblerPOToEntity(
                QueryChain.of(sysRoleMapper).select().where(SYS_ROLE.ID.eq(roleId)).one(),
                sysRoleEntity
        );
        return sysRoleEntity;
    }

    /**
     * 修改角色
     *
     * @param entity 角色信息
     * @return 操作结果
     */
    @Override
    public Boolean editRole(SysRoleEntity entity) {
        SysRolePO role = new SysRolePO();
        BeanUtils.copyProperties(entity, role);
        return sysRoleMapper.update(role) > 0;
    }

    /**
     * 查询角色列表信息
     *
     * @param param 查询参数
     * @return 角色列表
     */
    @Override
    public Page<SysRoleListDTO> roleList(QueryRoleListCommand param) {
        QueryChain<SysRolePO> queryChain = QueryChain.of(sysRoleMapper);
        queryChain.from(SYS_ROLE);
        queryChain.select(SYS_ROLE.ALL_COLUMNS);
        queryChain.where(SYS_ROLE.ROLE_TYPE.eq(1));
        if (CharSequenceUtil.isNotBlank(param.getRoleName())) {
            queryChain.where(SYS_ROLE.ROLE_NAME.like(param.getRoleName()).or(SYS_ROLE.ROLE_KEY.like(param.getRoleName())));
        }
        if (Objects.nonNull(param.getStatus())) {
            queryChain.where(SYS_ROLE.STATUS.eq(param.getStatus()));
        }
        queryChain.orderBy(SYS_ROLE.CREATE_TIME.desc());
        return sysRoleMapper.paginateAs(param.getPageNo(), param.getPageSize(), queryChain.toQueryWrapper(), SysRoleListDTO.class);
    }

    /**
     * 删除角色信息
     *
     * @param roleList 角色ID列表
     * @return 操作结果
     */
    @Override
    public Boolean deleteRole(List<String> roleList) {
        return sysRoleMapper.deleteRole(roleList) > 0;
    }


    /**
     * 可选角色下拉框
     * @param rangeScope 当前用户所有角色都最高层级
     * @return 角色列表
     */
    @Override
    public List<SysRoleTreeDTO> selectRoleTree(Integer rangeScope) {
        return sysRoleMapper.selectRoleTree(rangeScope);
    }

    /**
     * 查询指定角色编码是否存在
     * @param roleKey 角色编码
     * @return 数量
     */
    @Override
    public Integer countRoleKey(String roleKey) {
        return sysRoleMapper.countRoleKey(roleKey);
    }

}
