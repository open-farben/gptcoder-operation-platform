package cn.com.farben.gptcoder.operation.platform.user.application.service;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;
import cn.com.farben.commons.errorcode.exception.IllegalParameterException;
import cn.com.farben.gptcoder.operation.commons.user.dto.SysRoleCacheDTO;
import cn.com.farben.gptcoder.operation.commons.user.enums.FlagTypeEnum;
import cn.com.farben.gptcoder.operation.commons.user.utils.UserInfoUtils;
import cn.com.farben.gptcoder.operation.platform.user.application.component.AccountOrganComponent;
import cn.com.farben.gptcoder.operation.platform.user.command.PrimaryIdCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.role.AddMenuCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.role.DisableMenuCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.role.EditMenuCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.role.QueryMenuCommand;
import cn.com.farben.gptcoder.operation.platform.user.domain.SysMenuAggregateRoot;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.SysMenuEntity;
import cn.com.farben.gptcoder.operation.platform.user.dto.LoginUserCacheDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysMenuDetailDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysMenuListDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysMenuTreeDTO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums.SysMenuTypeEnum;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums.SystemRoleEnum;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.SysMenuRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.SysRoleMenuRepository;
import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * 系统菜单关联应用服务类
 * @author wuanhui
 *
 */
@Component
public class SysMenuAppService {
    private final SysMenuRepository sysMenuRepository;

    private final SysRoleMenuRepository sysRoleMenuRepository;

    private final AccountOrganComponent accountOrganComponent;

    public SysMenuAppService(SysMenuRepository sysMenuRepository,
                             SysRoleMenuRepository sysRoleMenuRepository,
                             AccountOrganComponent accountOrganComponent) {
        this.sysMenuRepository = sysMenuRepository;
        this.sysRoleMenuRepository = sysRoleMenuRepository;
        this.accountOrganComponent = accountOrganComponent;
    }

    /**
     * 列表页查询菜单列表
     * @param param 参数
     * @return 查询结果
     */
    public List<SysMenuListDTO> list(QueryMenuCommand param) {
        return sysMenuRepository.menuList(param);
    }

    /**
     * 根据ID查询菜单详情
     * @param param 参数
     * @return 菜单信息
     */
    public SysMenuDetailDTO getInfo(PrimaryIdCommand param) {
        if(StringUtils.isBlank(param.getId())) {
            throw new BusinessException(ErrorCodeEnum.EMPTY_ORGAN_KEY);
        }
        SysMenuEntity entity = sysMenuRepository.findMenuById(param.getId());
        SysMenuDetailDTO detail = new SysMenuDetailDTO();
        BeanUtils.copyProperties(entity, detail);
        return detail;
    }

    /**
     * 构建选择的目录或者菜单树，默认是带有菜单的树结果
     * @param type 参数
     * @return 树列表
     */
    public List<SysMenuTreeDTO> selectMenuTree(String type, String auth) {
        type = type != null ? type.trim() : "";
        if(StringUtils.isNotBlank(type) && !SysMenuTypeEnum.DIRECTORY.getCode().equals(type)) {
            throw new IllegalParameterException("菜单树类型有误，请确认后再试");
        }
        List<SysMenuTreeDTO> menuList = sysMenuRepository.selectMenuTree(type);
        //如果需要判断授权，则将没有权限的菜单选项disabled
        if(StringUtils.isNotBlank(auth)) {
            filterRoleMenu(menuList);
        }
        //按目录类型分组
        List<SysMenuTreeDTO> directoryList;
        if (!SysMenuTypeEnum.DIRECTORY.getCode().equals(type)) {
            directoryList = menuList.stream().filter(e -> SysMenuTypeEnum.DIRECTORY.getCode().equalsIgnoreCase(e.getMenuType().trim())).toList();
            for(SysMenuTreeDTO item : directoryList) {
                List<SysMenuTreeDTO> list = menuList.stream().filter(e -> e.getParentId().equals(item.getId())
                        && SysMenuTypeEnum.MENU.getCode().equals(e.getMenuType())).toList();
                item.setChildren(new Vector<>(list));
            }
        }else {
            directoryList = menuList;
        }

        //将菜单组装成父子级关系
        Map<String, SysMenuTreeDTO> treeMap = directoryList.stream().collect(Collectors.toMap(SysMenuTreeDTO::getId, a -> a, (k1, k2) -> k1));
        List<SysMenuTreeDTO> result = new ArrayList<>();
        for (SysMenuTreeDTO item : directoryList) {
            SysMenuTreeDTO parent = treeMap.get(item.getParentId());
            if (parent == null) {
                //如果没有找到父级,放入根目录
                result.add(item);
            } else {
                // 父级获得子级，再将子级放到对应的父级中
                parent.addChildren(item);
            }
        }
        return result;
    }

    /**
     * 添加系统菜单
     * @param param 参数
     * @return 操作结果
     */
    public Boolean addMenu(AddMenuCommand param) {
        //参数校验
        checkAddParam(param);
        SysMenuEntity entity = new SysMenuEntity();
        BeanUtils.copyProperties(param, entity);
        entity.setCreateBy(UserInfoUtils.getUserInfo().getAccount());
        SysMenuAggregateRoot aggregateRoot = new SysMenuAggregateRoot.Builder(sysMenuRepository).build();
        return aggregateRoot.addMenu(entity);
    }

    /**
     * 修改系统菜单
     * @param param 参数
     * @return 操作结果
     */
    public Boolean editMenu(EditMenuCommand param) {
        //参数校验
        checkPrimaryKey(param.getId());
        checkAddParam(param);
        SysMenuEntity entity = sysMenuRepository.findMenuById(param.getId());
        BeanUtils.copyProperties(param, entity);
        entity.setUpdateBy(UserInfoUtils.getUserInfo().getAccount());
        SysMenuAggregateRoot aggregateRoot = new SysMenuAggregateRoot.Builder(sysMenuRepository).build();
        return aggregateRoot.editMenu(entity);
    }

    /**
     * 查询登录用户拥有的菜单数（后台返回列表数据，前端去构建菜单树）
     * @return 操作结果
     */
    public List<SysMenuListDTO> findLoginMenu() {
        LoginUserCacheDTO entity = findLoginUser();
        //判断角色，如果是超管角色，返回全部菜单
        if(SystemRoleEnum.ADMINISTRATOR.getCode().equals(entity.getRoleId().trim())){
            return sysMenuRepository.findAllMenu();
        }
        return findLoginMenuList(entity);
    }

    /**
     * 删除菜单信息
     * @param param 菜单ID
     * @return 操作结果
     */
    public Boolean deleteMenu(PrimaryIdCommand param) {
        checkPrimaryKey(param.getId());
        sysMenuRepository.findMenuById(param.getId());
        //判断菜单是否被授权
        if(sysRoleMenuRepository.findRoleByMenuIds(Lists.newArrayList(param.getId())) > 0) {
            throw new IllegalParameterException("当前菜单已有授权角色，请先解除角色授权");
        }
        SysMenuAggregateRoot aggregateRoot = new SysMenuAggregateRoot.Builder(sysMenuRepository).build();
        return aggregateRoot.deleteMenu(param.getId());
    }

    /**
     * 菜单启用、禁用
     * @param param 菜单ID
     * @return 操作结果
     */
    public Boolean disableMenu(DisableMenuCommand param) {
        //参数校验
        checkDisableParam(param);
        SysMenuEntity menuEntity = sysMenuRepository.findMenuById(param.getId());
        menuEntity.setStatus(param.getStatus());
        SysMenuAggregateRoot aggregateRoot = new SysMenuAggregateRoot.Builder(sysMenuRepository).build();
        return aggregateRoot.editMenu(menuEntity);
    }


    /**
     * 校验菜单启用、禁用参数
     * @param param 参数
     */
    private void checkDisableParam(DisableMenuCommand param) {
        checkPrimaryKey(param.getId());
        if(param.getStatus() == null){
            throw new IllegalParameterException("启用状态不能为空");
        }
        if(FlagTypeEnum.exist(param.getStatus()) == null) {
            throw new BusinessException(ErrorCodeEnum.NOT_EXIST_FLAG);
        }
    }

    private void checkPrimaryKey(String id) {
        if(StringUtils.isBlank(id)){
            throw new IllegalParameterException("菜单ID不能为空");
        }
    }


    private void checkAddParam(AddMenuCommand param) {
        if(StringUtils.isBlank(param.getMenuType())){
            throw new IllegalParameterException("菜单类型不能为空");
        }
        if(SysMenuTypeEnum.exist(param.getMenuType()) == null){
            throw new IllegalParameterException("菜单类型不存在");
        }
        checkMenuName(param.getMenuName());
        //排序号
        if(param.getOrderNum() == null) {
            throw new IllegalParameterException("菜单排序号不能为空");
        }
        if(StringUtils.isNotBlank(param.getIcon()) && param.getIcon().length() > 90) {
            throw new IllegalParameterException("菜单图标长度过长");
        }
        if(StringUtils.isNotBlank(param.getComponent()) && param.getComponent().length() > 200){
            throw new IllegalParameterException("组件路径长度过长");
        }
        //校验菜单
        if(SysMenuTypeEnum.MENU.getCode().equalsIgnoreCase(param.getMenuType())) {
            checkMenuParam(param);
        }else {
            //目录类型将路径等字段置空
            if(param.getPath().length() > 190){
                throw new IllegalParameterException("路由地址长度过长");
            }
            param.setPermission("");
            param.setQuery("");
        }
        if(StringUtils.isNotBlank(param.getRemark()) && param.getRemark().length() > 400){
            throw new IllegalParameterException("菜单描述长度过长");
        }

    }

    /**
     * 菜单类型时，校验菜单相关参数
     * @param param 参数
     */
    private void checkMenuParam(AddMenuCommand param) {
        //挂在那个目录下
        if(StringUtils.isBlank(param.getParentId())) {
            throw new IllegalParameterException("上级目录不能为空");
        }
        //路由地址
        if(StringUtils.isBlank(param.getPath())) {
            throw new IllegalParameterException("路由地址不能为空");
        }
        if(param.getPath().length() > 190){
            throw new IllegalParameterException("路由地址长度过长");
        }
    }

    private void checkMenuName(String name) {
        //名称
        if(StringUtils.isBlank(name)) {
            throw new IllegalParameterException("菜单名称不能为空");
        }
        //长度不超过50
        if(name.length() > 50){
            throw new IllegalParameterException("菜单名称长度过长");
        }
    }

    /**
     * 查询登录人拥有的菜单信息
     * @param entity 登录人信息
     * @return 拥有菜单信息
     */
    private List<SysMenuListDTO> findLoginMenuList(LoginUserCacheDTO entity) {
        List<SysRoleCacheDTO> roleList = entity.getRoleList();
        if(CollUtil.isEmpty(roleList)) {
            throw new IllegalParameterException("该用户暂时没有分配任何菜单权限，请联系管理员。");
        }
        //多角色需处理交集
        List<String> roleIds = roleList.stream().map(SysRoleCacheDTO :: getId).toList();
        return sysRoleMenuRepository.findMenuByRole(roleIds);
    }

    /**
     * 查询登录人信息，优先查询缓存
     * @return 登录人信息
     */
    private LoginUserCacheDTO findLoginUser() {
        LoginUserCacheDTO entity = accountOrganComponent.findLoginCache(UserInfoUtils.getUserInfo().getAccount());
        if(entity == null || StringUtils.isBlank(entity.getUserId())) {
            throw new BusinessException(ErrorCodeEnum.USER_ERROR_A0201);
        }
        return entity;
    }

    /**
     * 过滤当前登录拥有的菜单，没权限的菜单标记disabled为true
     * @param menuList 全部菜单列表
     */
    private void filterRoleMenu(List<SysMenuTreeDTO> menuList) {
        LoginUserCacheDTO entity = findLoginUser();
        //超管直接跳过
        if(SystemRoleEnum.ADMINISTRATOR.getCode().equals(entity.getRoleId().trim())){
            return;
        }
        List<SysMenuListDTO> authMenu = findLoginMenuList(entity);
        if(authMenu.isEmpty()) {
            return;
        }
        for(SysMenuTreeDTO item : menuList) {
            //没存在的菜单设置字段为true
            if(authMenu.stream().filter(e -> e.getId().equals(item.getId())).findAny().orElse(null) == null) {
                item.setDisabled(true);
            }
        }
    }
}
