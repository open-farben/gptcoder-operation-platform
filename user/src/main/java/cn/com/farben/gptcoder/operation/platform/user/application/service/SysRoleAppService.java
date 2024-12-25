package cn.com.farben.gptcoder.operation.platform.user.application.service;

import cn.com.farben.commons.errorcode.exception.IllegalParameterException;
import cn.com.farben.gptcoder.operation.commons.user.utils.UserInfoUtils;
import cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.utils.LocalCacheUtils;
import cn.com.farben.gptcoder.operation.platform.user.application.component.AccountOrganComponent;
import cn.com.farben.gptcoder.operation.platform.user.command.MultipleIdCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.PrimaryIdCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.role.AddRoleCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.role.AuthOrganRangeCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.role.EditRoleCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.role.QueryRoleListCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.role.RoleAuthorizeCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.role.RoleRangeAuthCommand;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.SysRoleEntity;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.SysRoleMenuEntity;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.SysRoleRangeEntity;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.AuthRangeListDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysRoleDetailDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysRoleListDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysRoleRangeDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysRoleTreeDTO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums.SysRoleRangeEnum;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums.UseFlagEnum;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.SysRoleMenuRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.SysRoleRangeRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.SysRoleRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.UserAccountRepository;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrPool;
import com.google.common.collect.Lists;
import com.mybatisflex.core.paginate.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 系统菜单关联应用服务类
 * @author wuanhui
 *
 */
@Component
public class SysRoleAppService {

    private final SysRoleRepository sysRoleRepository;

    private final SysRoleMenuRepository sysRoleMenuRepository;

    private final UserAccountRepository accountRepository;

    private final SysRoleRangeRepository roleRangeRepository;

    private final AccountOrganComponent accountOrganComponent;

    private final LocalCacheUtils localCacheUtils;

    public SysRoleAppService(SysRoleRepository sysRoleRepository, SysRoleMenuRepository sysRoleMenuRepository,
                             UserAccountRepository accountRepository, SysRoleRangeRepository roleRangeRepository,
                             AccountOrganComponent accountOrganComponent, LocalCacheUtils localCacheUtils) {
        this.sysRoleRepository = sysRoleRepository;
        this.sysRoleMenuRepository = sysRoleMenuRepository;
        this.accountRepository = accountRepository;
        this.roleRangeRepository = roleRangeRepository;
        this.accountOrganComponent = accountOrganComponent;
        this.localCacheUtils = localCacheUtils;
    }

    /**
     * 新增角色信息
     * @param param 参数
     * @return 操作结果
     */
    public String addRole(AddRoleCommand param) {
        //参数校验
        checkAddRoleParam(param);
        //验证角色编码唯一性
        if(sysRoleRepository.countRoleKey(param.getRoleKey()) > 0) {
            throw new IllegalParameterException("角色代码重复，请重新填写");
        }
        SysRoleEntity entity = new SysRoleEntity();
        String roleId = UUID.randomUUID().toString().replaceAll("-", "");
        BeanUtils.copyProperties(param, entity);
        entity.setId(roleId);
        if(entity.getStatus() == null) {
            entity.setStatus(UseFlagEnum.NORMAL.getCode());
        }
        if(entity.getRoleSort() == null) {
            entity.setRoleSort(1);
        }
        entity.setCreateBy(UserInfoUtils.getUserInfo().getAccount());
        //添加角色表
        sysRoleRepository.addRole(entity);
        return roleId;
    }

    /**
     * 编辑角色信息
     * @param param 参数
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean editRole(EditRoleCommand param) {
        //参数校验
        checkEditRoleParam(param);
        SysRoleEntity entity = sysRoleRepository.findRoleById(param.getId());
        //角色编码有变化，校验其重复性
        if(!entity.getRoleKey().equalsIgnoreCase(param.getRoleKey())) {
            if (sysRoleRepository.countRoleKey(param.getRoleKey()) > 0) {
                throw new IllegalParameterException("角色代码重复，请重新填写");
            }
        }
        //验证角色编码唯一性
        BeanUtils.copyProperties(param, entity);
        entity.setUpdateBy(UserInfoUtils.getUserInfo().getAccount());
        return sysRoleRepository.editRole(entity);
    }

    /**
     * 查询角色列表信息
     * @param param 查询参数
     * @return 角色列表
     */
    public Page<SysRoleListDTO> roleList(QueryRoleListCommand param) {
        if(param.getPageNo() == 0) {
            param.setPageNo(1);
        }
        if(param.getPageSize() == 0) {
            param.setPageSize(20);
        }
        return sysRoleRepository.roleList(param);
    }

    /**
     * 单个/批量删除选定的角色信息
     * @param param 参数
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteRole(MultipleIdCommand param) {
        String[] roleIds = param.getIds().split(StrPool.COMMA);
        List<String> idList = Arrays.asList(roleIds);
        Integer count = accountRepository.countUserByRoleId(idList);
        if(count != null && count > 0) {
            throw new IllegalParameterException("当前角色已有授权用户，请先解除用户授权");
        }
        sysRoleRepository.deleteRole(idList);
        sysRoleMenuRepository.deleteByRoleId(idList);

        //同步删除角色的机构范围
        roleRangeRepository.delByRoleIds(idList);
        for(String item : idList) {
            localCacheUtils.deleteRoleCache(item);
        }
        return true;
    }

    /**
     * 可选角色下拉框
     * @return 角色列表
     */
    public List<SysRoleTreeDTO> selectRoleTree() {
        return sysRoleRepository.selectRoleTree(accountOrganComponent.findMaxRole());
    }


    /**
     * 角色授权接口
     * @param param 参数
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean authorize(RoleAuthorizeCommand param) {
        String roleId = param.getRoleId();
        if(StringUtils.isBlank(roleId)){
            throw new IllegalParameterException("角色ID不能为空");
        }
        if(CollUtil.isEmpty(param.getMenuList())) {
            throw new IllegalParameterException("选择菜单不能为空");
        }
        SysRoleEntity entity = sysRoleRepository.findRoleById(roleId);
        List<SysRoleMenuEntity> dataList = new ArrayList<>(param.getMenuList().size());
        for(String item : param.getMenuList()) {
            dataList.add(new SysRoleMenuEntity(roleId, item));
        }
        //删除角色本地缓存
        localCacheUtils.deleteRoleCache(param.getRoleId());

        //将原来的角色关联删除
        sysRoleMenuRepository.deleteByRoleId(Lists.newArrayList(entity.getId()));
        //添加角色关联表
        return sysRoleMenuRepository.addRoleMenu(dataList);
    }

    /**
     * 查询角色详情信息
     * @param param 参数：角色ID
     * @return 角色信息
     */
    public SysRoleDetailDTO roleDetail(PrimaryIdCommand param) {
        if(StringUtils.isBlank(param.getId())) {
            throw new IllegalParameterException("角色ID不能为空");
        }
        SysRoleEntity entity = sysRoleRepository.findRoleById(param.getId());
        SysRoleDetailDTO detail = new SysRoleDetailDTO();
        BeanUtils.copyProperties(entity, detail);
        detail.setCreateTime(entity.getCreateTime() == null ? "" : entity.getCreateTime().toString());
        detail.setUpdateTime(entity.getUpdateTime() == null ? "" : entity.getUpdateTime().toString());
        detail.setMenuList(sysRoleMenuRepository.findMenuRoleIds(param.getId()));

        //角色范围及保护所属架构
        List<SysRoleRangeDTO> rangeList = roleRangeRepository.findRangeListByRole(param.getId());
        List<Integer> organList = new ArrayList<>();
        List<String> fullOrganList = new ArrayList<>();
        rangeList.forEach(e -> {
            organList.add(e.getOrganNo());
            fullOrganList.add(e.getFullOrganNo());
        });
        detail.setOrganList(organList);
        detail.setFullOrganList(fullOrganList);
        return detail;
    }

    /**
     * 角色权限获取权限范围下拉框
     * @return 范围下拉框
     */
    public List<AuthRangeListDTO> authRangeList() {
        Integer roleRange = accountOrganComponent.findMaxRole();
        List<AuthRangeListDTO> list = new ArrayList<>();
        for(SysRoleRangeEnum item : SysRoleRangeEnum.values()) {
            //排除范围比他小的
            if(item.getCode() < roleRange) {
                continue;
            }
            list.add(new AuthRangeListDTO(item.getCode(), item.getTitle()));
        }
        return list;
    }

    /**
     * 角色权限范围授权操作接口
     * @param param 授权参数
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean rangeAuthorize(RoleRangeAuthCommand param) {
        String userId = UserInfoUtils.getUserInfo().getAccount();
        //参数校验
        checkAuthorizeParam(param);

        SysRoleEntity roleEntity = sysRoleRepository.findRoleById(param.getRoleId());
        //删除旧的授权记录
        roleRangeRepository.delByRoleIds(Lists.newArrayList(param.getRoleId()));

        roleEntity.setRangeScope(param.getRangeType());
        //自定义则额外添加自定义表数据
        if(SysRoleRangeEnum.CUSTOM.getCode().equals(param.getRangeType())) {
            List<SysRoleRangeEntity> list = new ArrayList<>();
            SysRoleRangeEntity entity;
            for(AuthOrganRangeCommand item : param.getOrganList()) {
                entity = new SysRoleRangeEntity();
                entity.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                entity.setRoleId(param.getRoleId());
                entity.setOrganNo(item.getOrganNo());
                entity.setFullOrganNo(item.getFullOrganNo());
                entity.setCreateBy(userId);
                list.add(entity);
            }
            roleRangeRepository.addRoleRange(list);
        }
        //修改角色信息
        sysRoleRepository.editRole(roleEntity);

        //删除角色本地缓存
        localCacheUtils.deleteRoleCache(param.getRoleId());
        return true;
    }

    private void checkAuthorizeParam(RoleRangeAuthCommand param) {
        if(StringUtils.isBlank(param.getRoleId())) {
            throw new IllegalParameterException("角色ID不能为空");
        }
        if(param.getRangeType() == null) {
            throw new IllegalParameterException("角色授权范围类型不能为空");
        }
        if(SysRoleRangeEnum.exist(param.getRangeType()) == null) {
            throw new IllegalParameterException("角色授权范围类型不存在");
        }
        //自定义范围，必须传指定的机构
        if(SysRoleRangeEnum.CUSTOM.getCode().equals(param.getRangeType()) && CollUtil.isEmpty(param.getOrganList())) {
            throw new IllegalParameterException("授权机构不能为空");
        }
    }

    /**
     * 校验新增角色参数
     * @param param 参数
     */
    private void checkAddRoleParam(AddRoleCommand param) {
        if(StringUtils.isBlank(param.getRoleName())) {
            throw new IllegalParameterException("角色名称不能为空");
        }
        if(StringUtils.isBlank(param.getRoleKey())) {
            throw new IllegalParameterException("角色标识不能为空");
        }
        if(param.getRoleName().length() > 25) {
            throw new IllegalParameterException("角色名称长度超出限制");
        }
        if(param.getRoleKey().length() > 50) {
            throw new IllegalParameterException("角色标识长度超出限制");
        }
        if(StringUtils.isNotBlank(param.getRemark()) && param.getRemark().length() > 200) {
            throw new IllegalParameterException("角色描述长度超出限制");
        }
    }

    /**
     * 校验编辑角色参数
     * @param param 参数
     */
    private void checkEditRoleParam(EditRoleCommand param) {
        if(StringUtils.isBlank(param.getId())) {
            throw new IllegalParameterException("角色ID不能为空");
        }
        if(StringUtils.isBlank(param.getRoleName())) {
            throw new IllegalParameterException("角色名称不能为空");
        }
        if(param.getRoleName().length() > 25) {
            throw new IllegalParameterException("角色名称长度超出限制");
        }
        if(StringUtils.isNotBlank(param.getRemark()) && param.getRemark().length() > 200) {
            throw new IllegalParameterException("角色描述长度超出限制");
        }
    }
}
