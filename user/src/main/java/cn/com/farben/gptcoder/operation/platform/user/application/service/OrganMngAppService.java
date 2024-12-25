package cn.com.farben.gptcoder.operation.platform.user.application.service;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;
import cn.com.farben.commons.errorcode.exception.IllegalParameterException;
import cn.com.farben.commons.web.constants.MvcConstants;
import cn.com.farben.gptcoder.operation.commons.user.dto.CommonOrganMngDTO;
import cn.com.farben.gptcoder.operation.commons.user.utils.UserInfoUtils;
import cn.com.farben.gptcoder.operation.platform.dictionary.application.component.DistributedLockComponent;
import cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.utils.LocalCacheUtils;
import cn.com.farben.gptcoder.operation.platform.user.application.component.AccountOrganComponent;
import cn.com.farben.gptcoder.operation.platform.user.command.MultipleIdCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.organ.AddOrganCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.organ.EditOrganCommand;
import cn.com.farben.gptcoder.operation.platform.user.command.organ.OrganListCommand;
import cn.com.farben.gptcoder.operation.platform.user.domain.OrganAggregateRoot;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.OrganMngEntity;
import cn.com.farben.gptcoder.operation.platform.user.dto.*;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.OrganMngRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.PluginUserRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.UserAccountRepository;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.paginate.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * 系统组织架构应用服务类
 * @author wuanhui
 *
 */
@Component
public class OrganMngAppService {

    private static final Log logger = LogFactory.get();

    private final OrganMngRepository organMngRepository;

    private final LocalCacheUtils localCache;

    private final DistributedLockComponent lockComponent;

    private final UserAccountRepository userAccountRepository;

    private final PluginUserRepository pluginUserRepository;

    private final AccountOrganComponent accountOrganComponent;


    private static final String ADD_ORGAN_LOCK_SIGN = "system_add_organ_";

    public OrganMngAppService(OrganMngRepository organMngRepository, LocalCacheUtils localCache,
                              DistributedLockComponent lockComponent, UserAccountRepository userAccountRepository,
                              PluginUserRepository pluginUserRepository, AccountOrganComponent accountOrganComponent) {
        this.organMngRepository = organMngRepository;
        this.localCache = localCache;
        this.lockComponent = lockComponent;
        this.userAccountRepository = userAccountRepository;
        this.pluginUserRepository = pluginUserRepository;
        this.accountOrganComponent = accountOrganComponent;
    }

    /**
     * 根据机构号查询对应机构信息
     * @param organization 机构号
     * @return 机构信息
     */
    public CommonOrganMngDTO loadOrganMngByNo(String organization) {
        if(StringUtils.isBlank(organization)) {
            return new CommonOrganMngDTO();
        }
        int organNo = 0;
        try{
            organNo = Integer.parseInt(organization);
        }catch (Exception e){
            logger.error("---------------机构号有误，机构是数字---------------");
            return new CommonOrganMngDTO();
        }
        OrganMngEntity entity = organMngRepository.findOrganByNo(organNo);
        if(entity == null) {
            return new CommonOrganMngDTO();
        }
        CommonOrganMngDTO commonOrganMng = new CommonOrganMngDTO();
        BeanUtils.copyProperties(entity, commonOrganMng);
        return commonOrganMng;
    }

    /**
     * 查询指定用户权限内的机构号列表（机构查询无法落地本角色范围是本人的情况，因此本人的角色范围，返回当前登录人机构号）
     * @param account 用户账号
     * @return 机构号列表
     */
    public List<Integer> findAuthOrganNoList(String account) {
        return accountOrganComponent.findAuthOrganListByUser(account);
    }

    /**
     * 构建组织结构树
     * @return 结构树
     */
    public List<OrganMngTreeDTO> buildOrganTree() {
        List<OrganMngTreeDTO> result = new ArrayList<>();
        List<OrganMngTreeDTO> list = accountOrganComponent.findAuthOrganListByToken();
        if(list.isEmpty()) {
            return result;
        }
        Map<Integer, OrganMngTreeDTO> orderOfAmount = list.stream().collect(Collectors.toMap(OrganMngTreeDTO::getOrganNo, a -> a, (k1, k2) -> k1));
        for (OrganMngTreeDTO productType : list) {
            OrganMngTreeDTO parent = orderOfAmount.get(productType.getParentOrganNo());
            if (parent == null) {
                //如果没有找到父级,放入根目录
                result.add(productType);
            } else {
                // 父级获得子级，再将子级放到对应的父级中
                parent.addChildren(productType);
            }
        }
        return result;
    }
    public String buildOrganSet() {
        List<OrganMngTreeDTO> list = accountOrganComponent.findAuthOrganListByToken();
        String retStr="";
        for(OrganMngTreeDTO organMngTreeDTO:list){
            retStr+=organMngTreeDTO.getFullOrganNo()+",";
        }
        return retStr;
    }

    /**
     * 新增系统机构信息
     * @param command 机构参数
     * @param token 登录用户
     * @return 操作结果
     */
    public boolean addOrgan(AddOrganCommand command, String token) {
        //参数校验
        checkAddParam(command);

        //添加机构加分布式锁
        String lockResult = null;
        try {
            lockResult = lockComponent.lockWith(ADD_ORGAN_LOCK_SIGN, 10);
            String userId = UserInfoUtils.getUserInfo().getAccount();
            OrganMngEntity parent = organMngRepository.findOrganByNo(command.getParentOrganNo());
            if (parent == null) {
                throw new BusinessException(ErrorCodeEnum.NOT_EXIST_PARENT_ORGAN);
            }
            //当前最大的机构号，顶级架构（第一条数据）通过脚本初始化
            List<Integer> maxOrganList = organMngRepository.findMaxOrganNo();
            int currentOrgan;
            if (maxOrganList.size() == 1) {
                currentOrgan = maxOrganList.get(0) * 1000 + 1;
            } else {
                currentOrgan = maxOrganList.get(0) + 1;
            }
            OrganMngEntity entity = new OrganMngEntity();
            BeanUtils.copyProperties(command, entity);
            entity.setFullOrganNo(parent.getFullOrganNo() + "|" + currentOrgan);
            entity.setOrganNo(currentOrgan);
            entity.setOrganLevel(1);
            entity.setCreateBy(userId);
            OrganAggregateRoot organAggregateRoot = new OrganAggregateRoot.Builder(organMngRepository).build();
            return organAggregateRoot.addOrgan(entity);
        } finally {
            if(lockResult != null) {
                lockComponent.release(ADD_ORGAN_LOCK_SIGN);
            }
        }
    }

    /**
     * 修改机构信息
     * @param command 参数
     * @param token 登录用户
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean editOrgan(EditOrganCommand command, String token) {
        //参数校验
        checkEditParam(command);
        String userId = UserInfoUtils.getUserInfo().getAccount();
        OrganMngEntity entity = organMngRepository.findOrganById(command.getId());
        //上级机构不能是自己
        if(entity.getOrganNo().equals(command.getParentOrganNo())) {
            throw new BusinessException(ErrorCodeEnum.PARENT_ORGAN_CANNOT_OWN);
        }
        Integer oldParentNo = entity.getParentOrganNo();
        BeanUtils.copyProperties(command, entity);
        //如果上级有改变的情况
        if(!command.getParentOrganNo().equals(oldParentNo)) {
            //验证上级是否存在
            OrganMngEntity parent = organMngRepository.findOrganByNo(command.getParentOrganNo());
            if(parent == null) {
                throw new BusinessException(ErrorCodeEnum.NOT_EXIST_PARENT_ORGAN);
            }
            //原先需要替换的机构号
            String oldFullOrgan = entity.getFullOrganNo();
            String oldOrgan = oldFullOrgan + "|";
            String fullOrganNo = parent.getFullOrganNo() + "|" + entity.getOrganNo();

            //改变上级则机构对应的全机构号要做相应修改， 机构本身的下级机构也要调整
            List<OrganMngEntity> childrenOrgan = organMngRepository.findChildrenOrganByNo(entity.getFullOrganNo());
            if(!childrenOrgan.isEmpty()) {
                List<String> ids = new ArrayList<>();
                for (OrganMngEntity organMngEntity : childrenOrgan) {
                    ids.add(organMngEntity.getId());
                }
                //更新替换下级全机构号
                organMngRepository.updateChildrenOrgan(oldOrgan, fullOrganNo + "|", ids);
            }
            //修改插件用户和系统用户的管理机构号
            pluginUserRepository.updateUserOrgan(oldFullOrgan, fullOrganNo);
            userAccountRepository.updateSysUserOrgan(oldFullOrgan, fullOrganNo);
            entity.setFullOrganNo(fullOrganNo);
        }
        entity.setUpdateBy(userId);
        OrganAggregateRoot organAggregateRoot = new OrganAggregateRoot.Builder(organMngRepository).build();
        //删除本地缓存
        localCache.deleteOrganCache(entity.getOrganNo() + "");
        return organAggregateRoot.editOrgan(entity);
    }

    /**
     * 删除机构信息
     * @param command 参数
     * @return 操作结果
     */
    public boolean deleteOrgan(MultipleIdCommand command) {
        String ids = command.getIds();
        String[] userArr = ids.split(StrPool.COMMA);
        List<String> idList = Arrays.asList(userArr);
        List<OrganMngEntity> list = organMngRepository.findOrganByIdList(idList);
        if(list.isEmpty()) {
            throw new BusinessException(ErrorCodeEnum.NOT_EXIST_ORGAN);
        }
        List<Integer> organNoList = list.stream().map(OrganMngEntity::getOrganNo).collect(Collectors.toList());
        //如果机构下存在子机构，则不允许删除
        if(organMngRepository.countChildrenOrgan(organNoList) > 0) {
            throw new BusinessException(ErrorCodeEnum.EXIST_CHILDREN_ORGAN);
        }
        //删除本地缓存
        for(Integer item : organNoList) {
            localCache.deleteOrganCache(item + "");
        }
        OrganAggregateRoot organAggregateRoot = new OrganAggregateRoot.Builder(organMngRepository).build();
        return organAggregateRoot.deleteOrgan(idList);
    }

    /**
     * 分页查询机构信息
     * @param command 参数
     * @return 查询结果
     */
    public Page<OrganMngListDTO> organList(OrganListCommand command) {
        if(command.getPageNo() < 1) {
            command.setPageNo(1);
        }
        if(command.getPageSize() > MvcConstants.MAX_PAGE_SIZE) {
            throw new IllegalParameterException(CharSequenceUtil.format("每页数据量不能大于{}", MvcConstants.MAX_PAGE_SIZE));
        }
        if(command.getPageSize() < 1) {
            command.setPageSize(20);
        }
        //如果传了机构号，则查询不包含自己的所有下级
        if(StringUtils.isNotBlank(command.getOrganNo())) {
            command.setOrganNo(command.getOrganNo() + "|");
        }
        return organMngRepository.findOrganList(command);
    }

    /**
     * 查询机构详情（根据主键ID）
     * @param id 主键ID
     * @return 机构详情
     */
    public OrganMngDTO organDetail(String id) {
        if(StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCodeEnum.EMPTY_ORGAN_KEY);
        }
        return organMngRepository.findOrganAndParentById(id);
    }


    private void checkAddParam(AddOrganCommand command) {
        if(StringUtils.isBlank(command.getOrganName())) {
            throw new BusinessException(ErrorCodeEnum.EMPTY_ORGAN_NAME);
        }
        checkAddress(command.getOrganAddress());
        checkPost(command.getOrganPost());
        if(command.getParentOrganNo() == null || command.getParentOrganNo() == 0) {
            throw new BusinessException(ErrorCodeEnum.EMPTY_PARENT_ORGAN);
        }

    }

    private void checkEditParam(EditOrganCommand command) {
        if(StringUtils.isBlank(command.getOrganName())) {
            throw new BusinessException(ErrorCodeEnum.EMPTY_ORGAN_NAME);
        }
        if(command.getParentOrganNo() == null || command.getParentOrganNo() == 0) {
            throw new BusinessException(ErrorCodeEnum.EMPTY_PARENT_ORGAN);
        }
        checkAddress(command.getOrganAddress());
        checkPost(command.getOrganPost());
    }

    /**
     * 校验机构地址长度
     * @param address 机构地址
     */
    private void checkAddress(String address) {
        if(StringUtils.isNotBlank(address) && address.length() > 200) {
            throw new BusinessException(ErrorCodeEnum.EMPTY_ORGAN_NAME);
        }
    }

    /**
     * 校验机构邮编长度
     * @param post 机构邮编
     */
    private void checkPost(String post) {
        if(StringUtils.isNotBlank(post) && post.length() > 10) {
            throw new BusinessException(ErrorCodeEnum.EMPTY_ORGAN_NAME);
        }
    }
}
