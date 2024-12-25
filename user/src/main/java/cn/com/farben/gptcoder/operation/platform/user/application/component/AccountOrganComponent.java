package cn.com.farben.gptcoder.operation.platform.user.application.component;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;
import cn.com.farben.commons.errorcode.exception.OperationNotAllowedException;
import cn.com.farben.gptcoder.operation.commons.user.command.BaseAuthOrganCommand;
import cn.com.farben.gptcoder.operation.commons.user.dto.CommonOrganMngDTO;
import cn.com.farben.gptcoder.operation.commons.user.dto.SysRoleCacheDTO;
import cn.com.farben.gptcoder.operation.commons.user.utils.UserInfoUtils;
import cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.utils.LocalCacheUtils;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.OrganMngEntity;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.SysAccountRoleEntity;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.SysRoleEntity;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.UserAccountEntity;
import cn.com.farben.gptcoder.operation.platform.user.dto.LoginUserCacheDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.OrganMngTreeDTO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.UserSystemConstants;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums.SysRoleRangeEnum;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums.SystemRoleEnum;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.OrganMngRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.SysAccountRoleRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.SysRoleRangeRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.SysRoleRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.UserAccountRepository;
import cn.hutool.core.text.StrPool;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * 用户、机构、角色相关操作组件类
 * @author wuanhui
 */
@Component
public class AccountOrganComponent {

    private static final Log logger = LogFactory.get();
    private final UserAccountRepository accountRepository;

    private final SysAccountRoleRepository accountRoleRepository;

    private final SysRoleRepository sysRoleRepository;

    private final OrganMngRepository organMngRepository;

    private final SysRoleRangeRepository roleRangeRepository;

    private final LocalCacheUtils localCache;

    public AccountOrganComponent(UserAccountRepository accountRepository, SysAccountRoleRepository accountRoleRepository,
                                 SysRoleRepository sysRoleRepository, OrganMngRepository organMngRepository,
                                 SysRoleRangeRepository roleRangeRepository, LocalCacheUtils localCache) {
        this.accountRepository = accountRepository;
        this.accountRoleRepository = accountRoleRepository;
        this.sysRoleRepository = sysRoleRepository;
        this.organMngRepository = organMngRepository;
        this.roleRangeRepository = roleRangeRepository;
        this.localCache = localCache;
    }

    /**
     * 过滤登录用户的架构权限
     * @param command 参数
     * @param operator 操作员
     */
    public void filterUserAuth(BaseAuthOrganCommand command, String operator) {
        if(StringUtils.isBlank(operator)) {
            throw new BusinessException(ErrorCodeEnum.USER_ERROR_A0800);
        }
        LoginUserCacheDTO userCache = findLoginCache(operator);
        List<SysRoleCacheDTO> roleList = userCache.getRoleList();
        if(roleList.isEmpty()) {
            throw new OperationNotAllowedException("用户未分配角色");
        }
        int otherAuth = 0;
        for(SysRoleCacheDTO item : roleList) {
            switch (item.getRangeScope()) {
                //存在任意一个全部范围的角色
                case 0 -> command.setIsAll(UserSystemConstants.EXIST_ROLE_AUTH_STRING);
                //仅本人权限不返回任何机构
                case 4 -> {
                    command.setIsOnly(UserSystemConstants.EXIST_ROLE_AUTH_STRING);
                    command.setUserId(userCache.getUserId());
                }
                default -> otherAuth++;
            }
        }
        //存在除全部范围和本人范围权限以外，以指定机构为准
        if(otherAuth > 0) {
            command.setIsAll("");
            command.setIsOnly("");
            List<OrganMngTreeDTO> list = fillAuthOrganList(userCache);
            List<String> fullOrganList = new ArrayList<>();
            List<Integer> organList = new ArrayList<>();
            list.forEach(e -> {
                organList.add(e.getOrganNo());
                fullOrganList.add(e.getFullOrganNo());
            });
            command.setOrganNoList(organList);
            command.setFullOrganNoList(fullOrganList);
        }
    }

    /**
     * 查询账号当前最大的角色权限（一个用户存在多角色）
     * @return 角色范围枚举值
     */
    public Integer findMaxRole() {
        LoginUserCacheDTO userCache = findLoginCache(UserInfoUtils.getUserInfo().getAccount());
        //超管直接返回全部权限
        if(SystemRoleEnum.ADMINISTRATOR.getCode().equals(userCache.getRoleId().trim())) {
            return SysRoleRangeEnum.ALL.getCode();
        }
        //查询拥有的角色信息，兼容旧版将角色ID放在用户字段里
        List<SysRoleCacheDTO> roleList = userCache.getRoleList();
        if(roleList.isEmpty()) {
            throw new OperationNotAllowedException("用户未分配角色");
        }
        SysRoleCacheDTO roleCache = roleList.stream().min(Comparator.comparing(SysRoleCacheDTO :: getRangeScope)).get();
        logger.info("当前用户角色最小值：{}", JSONUtil.toJsonStr(roleCache));
        return roleCache.getRangeScope();
    }

    /**
     * 根据用户ID查询其权限内的机构列表
     * @param userId 用户ID
     * @return 机构列表
     */
    public List<Integer> findAuthOrganListByUser(String userId) {
        LoginUserCacheDTO userCache = findLoginCache(userId);
        List<OrganMngTreeDTO> list = fillAuthOrganList(userCache);
        //机构查询无法落地本角色范围是本人的情况，因此本人的角色范围，返回当前登录人机构号
        if(list.isEmpty()) {
            return Lists.newArrayList(Integer.parseInt(userCache.getOrganization()));
        }
        return list.stream().map(OrganMngTreeDTO :: getOrganNo).toList();
    }

    /**
     * 根据用户登录token查询其权限内的机构列表
     * @return 机构列表
     */
    public List<OrganMngTreeDTO> findAuthOrganListByToken() {
        LoginUserCacheDTO userCache = findLoginCache(UserInfoUtils.getUserInfo().getAccount());
        return fillAuthOrganList(userCache);
    }

    /**
     * 获取当前登录人基本信息和拥有角色
     * @param userId 用户账号
     * @return 用户信息
     */
    public LoginUserCacheDTO findLoginCache(String userId) {
        LoginUserCacheDTO userCache = new LoginUserCacheDTO();
        UserAccountEntity entity = accountRepository.findByUserid(userId);
        if(entity != null) {
            BeanUtils.copyProperties(entity, userCache);
            //查询拥有的角色信息，兼容旧版将角色ID放在用户字段里
            userCache.setRoleList(findRoleList(entity.getRoleId(), userId));
        }
        logger.info("当前登录人信息：{}", JSONUtil.toJsonStr(userCache));
        return userCache;
    }

    /**
     * 过滤权限内的机构列表信息
     * @param userCache 参数
     * @return 机构列表
     */
    public List<OrganMngTreeDTO> fillAuthOrganList(LoginUserCacheDTO userCache) {
        if(StringUtils.isBlank(userCache.getUserId())) {
            throw new BusinessException(ErrorCodeEnum.USER_ERROR_A0230);
        }
        //如果存在全部数据权限，直接返回所有机构
        Optional<SysRoleCacheDTO> min = userCache.getRoleList().stream().min(Comparator.comparing(SysRoleCacheDTO::getRangeScope));
        if (min.isEmpty()) {
            return Collections.emptyList();
        }

        SysRoleCacheDTO roleCache = min.get();
        if(SysRoleRangeEnum.ALL.getCode().equals(roleCache.getRangeScope())) {
            return organMngRepository.findAllNormalOrgan();
        }
        List<OrganMngTreeDTO> organList = new ArrayList<>();
        //拼装组织机构
        OrganMngTreeDTO mngTree;
        for(SysRoleCacheDTO role : userCache.getRoleList()) {
            //仅本人权限不返回任何机构
            switch (role.getRangeScope()) {
                case 1 -> organList.addAll(findCustomOrgan(role.getId()));
                case 2 -> organList.addAll(findChildrenOrgan(userCache.getFullOrganization()));
                case 3 -> {
                    CommonOrganMngDTO organMng = localCache.getOrganEntity(userCache.getOrganization(),
                            () -> findOrganCacheByNo(Integer.parseInt(userCache.getOrganization())));
                    if(organMng != null && StringUtils.isNotBlank(organMng.getId())) {
                        mngTree = new OrganMngTreeDTO();
                        BeanUtils.copyProperties(organMng, mngTree);
                        organList.add(mngTree);
                    }
                }
            }
        }
        //机构去重
        return organList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(OrganMngTreeDTO :: getOrganNo))), ArrayList :: new));
    }

    /**
     * 角色自定义数据范围情况
     * @param roleId 角色ID
     * @return 机构列表
     */
    private List<OrganMngTreeDTO> findCustomOrgan(String roleId) {
        List<Integer> rangeList = roleRangeRepository.findRangeOrganNoListByRole(roleId);
        List<OrganMngTreeDTO> mngTreeList = new ArrayList<>(rangeList.size());
        OrganMngTreeDTO mngTree;
        for(Integer item : rangeList) {
            CommonOrganMngDTO organMng = localCache.getOrganEntity(item.toString(), ()-> findOrganCacheByNo(item));
            if(organMng != null && StringUtils.isNotBlank(organMng.getId())) {
                mngTree = new OrganMngTreeDTO();
                BeanUtils.copyProperties(organMng, mngTree);
                mngTreeList.add(mngTree);
            }
        }
        return mngTreeList;
    }

    /**
     * 查指定机构的下级机构
     * @param fullOrganNo 长机构编码
     * @return 下级机构列表
     */
    private List<OrganMngTreeDTO> findChildrenOrgan(String fullOrganNo) {
        List<OrganMngEntity> childrenOrgan = organMngRepository.findChildrenOrganByNo(fullOrganNo);
        if(childrenOrgan.isEmpty()) {
            return new ArrayList<>();
        }
        List<OrganMngTreeDTO> organTree = new ArrayList<>(childrenOrgan.size());
        OrganMngTreeDTO mngTree;
        for(OrganMngEntity item : childrenOrgan) {
            mngTree = new OrganMngTreeDTO();
            BeanUtils.copyProperties(item, mngTree);
            organTree.add(mngTree);
        }
        return organTree;
    }

    /**
     * 查询用户拥有的角色列表信息
     * @param roleId 旧版角色字符串
     * @param account 账号
     * @return 角色信息
     */
    private List<SysRoleCacheDTO> findRoleList(String roleId, String account) {
        List<SysRoleCacheDTO> roleList = new ArrayList<>();
        List<String> userRoles;
        if(StringUtils.isNotBlank(roleId)) {
            userRoles = Arrays.asList(roleId.split(StrPool.COMMA));
        }else {
            List<SysAccountRoleEntity> entityList = accountRoleRepository.findRoleIdsByUser(Lists.newArrayList(account));
            userRoles = entityList.stream().filter(e -> e.getUserId().equals(account)).map(SysAccountRoleEntity :: getRoleId).toList();
        }
        SysRoleCacheDTO roleCache;
        for(String item : userRoles) {
            roleCache = localCache.getRoleCache(item, () -> findRoleCache(item));
            if(Objects.nonNull(roleCache) && StringUtils.isNotBlank(roleCache.getId())) {
                roleList.add(roleCache);
            }
        }
        return roleList;
    }

    private SysRoleCacheDTO findRoleCache(String roleId) {
        SysRoleCacheDTO cache = new SysRoleCacheDTO();
        try {
            SysRoleEntity entity = sysRoleRepository.findRoleById(roleId);
            BeanUtils.copyProperties(entity, cache);
        }catch (Exception ignored) {
        }
        return cache;
    }

    /**
     * 根据机构号查询机构信息，填充本地缓存
     * @param organNo 机构号
     * @return 构信息
     */
    private CommonOrganMngDTO findOrganCacheByNo(Integer organNo) {
        OrganMngEntity entity = organMngRepository.findOrganByNo(organNo);
        if(entity == null) {
            return new CommonOrganMngDTO();
        }
        CommonOrganMngDTO commonOrganMng = new CommonOrganMngDTO();
        BeanUtils.copyProperties(entity, commonOrganMng);
        return commonOrganMng;
    }
}
