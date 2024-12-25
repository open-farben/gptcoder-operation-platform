package cn.com.farben.gptcoder.operation.platform.user.application.component;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;
import cn.com.farben.gptcoder.operation.commons.user.dto.DictCodeDTO;
import cn.com.farben.gptcoder.operation.commons.user.enums.SystemDictCodeTypeEnum;
import cn.com.farben.gptcoder.operation.platform.dictionary.application.service.DictCodeAppService;
import cn.com.farben.gptcoder.operation.platform.user.command.excel.PluginUserImportData;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.PluginUserEntity;
import cn.com.farben.gptcoder.operation.platform.user.dto.OrganMngTreeDTO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.UserSystemConstants;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums.PluginUserStatusEnum;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.OrganMngRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.PluginUserRepository;
import cn.hutool.core.util.IdUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * excel处理业务类
 * @author wuanhui
 */
@Component
public class EasyExcelComponent {

    /** 组织机构查询业务 */
    private final OrganMngRepository organMngRepository;

    /** 系统字典信息服务 */
    private final DictCodeAppService dictCodeAppService;

    private final PluginUserRepository pluginUserRepository;

    public EasyExcelComponent(OrganMngRepository organMngRepository, DictCodeAppService dictCodeAppService,
                              PluginUserRepository pluginUserRepository) {
        this.organMngRepository = organMngRepository;
        this.dictCodeAppService = dictCodeAppService;
        this.pluginUserRepository = pluginUserRepository;
    }

    /**
     * 获取Excel输入流
     * @param templateName 模板全路径+名字
     * @return Excel输入流
     */
    public InputStream getTemplateAsStream(String templateName) {
        if(StringUtils.isBlank(templateName)){
            throw new BusinessException(ErrorCodeEnum.EMPTY_EXCEL_MODEL_PARAM);
        }
        try {
            ClassPathResource resource = new ClassPathResource(templateName);
            return resource.getInputStream();
        } catch (Exception e) {
            throw new BusinessException(ErrorCodeEnum.ERROR_DOWNLOAD_TEMPLATE);
        }
    }

    /**
     * 批量新增导入的插件用户
     * @param dataList 数据列表
     * @param creator 创建者
     * @param password 密码
     * @param count 当前已启用用户数量
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchImportUser(List<PluginUserImportData> dataList, String creator, String password, long count) {
        List<PluginUserEntity> entityList = new ArrayList<>(dataList.size());
        PluginUserEntity entity;
        //启用数量
        int enableNum = 0;
        for(PluginUserImportData item : dataList){
            entity = new PluginUserEntity();
            entity.setId(IdUtil.objectId());
            entity.setAccount(item.getAccount());
            entity.setJobNumber(item.getJobNumber());
            entity.setName(item.getName());
            entity.setPassword(password);
            entity.setOrganization(item.getOrgan());
            entity.setFullOrganization(item.getFullOrganization());
            entity.setDuty(item.getDuty());
            entity.setMobile(item.getMobile());
            entity.setEmail(item.getEmail());
            if(UserSystemConstants.USER_EXCEL_IMPORT_FIELD_STATUS.equals(item.getStatus().trim())) {
                entity.setStatus(PluginUserStatusEnum.ENABLE);
                enableNum++;
            }else {
                entity.setStatus(PluginUserStatusEnum.DISABLE);
            }
            entity.setCreateUser(creator);
            entity.setUpdateUser(creator);
            entityList.add(entity);
        }

        //调用仓储层保存数据
        pluginUserRepository.batchAddUser(entityList);
    }


    /**
     * 获取需要填充excel组织机构列表
     * @return 组装数据列表
     */
    public List<OrganMngTreeDTO> findOrganList() {
        return organMngRepository.findAllNormalOrgan();
    }

    /**
     * 获取需要填充excel职务字典列表
     * @return 组装数据列表
     */
    public List<DictCodeDTO> findDutyList() {
        return dictCodeAppService.findDictByCode(SystemDictCodeTypeEnum.JOB_INFO.getCode());
    }

}
