package cn.com.farben.gptcoder.operation.platform.dictionary.application.service;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.IllegalParameterException;
import cn.com.farben.commons.web.constants.MvcConstants;
import cn.com.farben.gptcoder.operation.commons.user.command.BasePageCommand;
import cn.com.farben.gptcoder.operation.commons.user.dto.SystemDictInfoDTO;
import cn.com.farben.gptcoder.operation.commons.user.dto.SystemDictListDTO;
import cn.com.farben.gptcoder.operation.commons.user.utils.UserInfoUtils;
import cn.com.farben.gptcoder.operation.platform.dictionary.command.AddDictCommand;
import cn.com.farben.gptcoder.operation.platform.dictionary.command.DictDetailCommand;
import cn.com.farben.gptcoder.operation.platform.dictionary.command.DictListCommand;
import cn.com.farben.gptcoder.operation.platform.dictionary.command.DisableDictCommand;
import cn.com.farben.gptcoder.operation.platform.dictionary.command.EditDictCommand;
import cn.com.farben.gptcoder.operation.platform.dictionary.domain.SystemDictAggregateRoot;
import cn.com.farben.gptcoder.operation.platform.dictionary.domain.entity.DictEntity;
import cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.facade.DictCodeRepository;
import cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.facade.DictRepository;
import cn.hutool.core.text.CharSequenceUtil;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 *
 * 系统字典操作服务类
 * 创建时间：2023/8/15<br>
 * @author wuanhui
 */
@Component
public class SystemDictAppService {
    private final DictRepository dictRepository;

    private final DictCodeRepository dictCodeRepository;

    public SystemDictAppService(DictRepository dictRepository, DictCodeRepository dictCodeRepository) {
        this.dictRepository = dictRepository;
        this.dictCodeRepository = dictCodeRepository;
    }

    /**
     * 分页查询字典
     * @param command 参数
     * @return 返回值
     */
    public Page<SystemDictListDTO> dictList(DictListCommand command) {
        setPage(command);
        return dictRepository.findDictList(command);
    }

    /**
     * 新增字典数据
     * @param command 参数
     * @param token 当前登录人
     * @return 操作结果
     */
    public boolean addDict(AddDictCommand command, String token) {
        SystemDictAggregateRoot aggregateRoot = new SystemDictAggregateRoot.Builder(dictRepository).build();
        //校验必填参数
        DictEntity dictEntity = new DictEntity();
        BeanUtils.copyProperties(command, dictEntity);
        dictEntity.setCreateBy(UserInfoUtils.getUserInfo().getAccount());
        return aggregateRoot.addDict(dictEntity);
    }

    /**
     * 查询字典详情及关联码表
     * @param command 参数
     * @return 查询结果
     */
    public SystemDictInfoDTO findDictDetail(DictDetailCommand command) {
        setPage(command);
        DictEntity entity = findDictInfo(command.getId());
        SystemDictInfoDTO dictInfo = new SystemDictInfoDTO();
        BeanUtils.copyProperties(entity, dictInfo);
        dictInfo.setDictList(dictCodeRepository.findDictCodeList(entity.getDictCode(), command.getPageSize(), command.getPageNo()));
        return dictInfo;
    }

    /**
     * 编辑字典数据
     * @param command 参数
     * @return 操作结果
     */
    public boolean editDict(EditDictCommand command) {
        //校验必填参数
        if(CharSequenceUtil.isBlank(command.getId())) {
            throw new IllegalParameterException(ErrorCodeEnum.EMPTY_DICT_ID);
        }
        if(CharSequenceUtil.isBlank(command.getDictName())) {
            throw new IllegalParameterException(ErrorCodeEnum.EMPTY_DICT_NAME);
        }
        if(CharSequenceUtil.isNotBlank(command.getMark()) && command.getMark().length() > 200) {
            throw new IllegalParameterException(ErrorCodeEnum.MAX_DICT_MARK_LENGTH);
        }

        findDictInfo(command.getId());
        SystemDictAggregateRoot aggregateRoot = new SystemDictAggregateRoot.Builder(dictRepository).build();
        DictEntity dictEntity = new DictEntity();
        BeanUtils.copyProperties(command, dictEntity);
        dictEntity.setUpdateBy(UserInfoUtils.getUserInfo().getAccount());
        return aggregateRoot.editDict(dictEntity);
    }

    /**
     * 禁用、启用字典数据
     * @param command 参数
     * @return 操作结果
     */
    public boolean disableDict(DisableDictCommand command) {
        SystemDictAggregateRoot aggregateRoot = new SystemDictAggregateRoot.Builder(dictRepository).build();
        //校验必填参数
        return true;
    }

    /**
     * 删除字典数据
     * @param id 参数
     * @return 操作结果
     */
    public boolean deleteDict(String id) {
        //校验必填参数
        return true;
    }

    /**
     * 根据主键ID查询该字典信息
     * @param id 主键
     * @return 字典信息
     */
    private DictEntity findDictInfo(String id) {
        DictEntity entity = dictRepository.findDictById(id);
        if(entity == null) {
            throw new IllegalParameterException(ErrorCodeEnum.IS_NULL_DICT);
        }
        return entity;
    }

    private void setPage(BasePageCommand command) {
        if(command.getPageNo() < 1) {
            command.setPageNo(1);
        }
        if(command.getPageSize() > MvcConstants.MAX_PAGE_SIZE) {
            throw new IllegalParameterException(CharSequenceUtil.format("每页数据量不能大于{}", MvcConstants.MAX_PAGE_SIZE));
        }
        if(command.getPageSize() < 1) {
            command.setPageSize(20);
        }
    }
}
