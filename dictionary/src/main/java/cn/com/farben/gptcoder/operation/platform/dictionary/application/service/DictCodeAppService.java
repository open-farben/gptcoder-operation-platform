package cn.com.farben.gptcoder.operation.platform.dictionary.application.service;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.IllegalParameterException;
import cn.com.farben.gptcoder.operation.commons.user.dto.DictCodeDTO;
import cn.com.farben.gptcoder.operation.commons.user.dto.DictCodeInfoDTO;
import cn.com.farben.gptcoder.operation.commons.user.enums.FlagTypeEnum;
import cn.com.farben.gptcoder.operation.commons.user.enums.SystemDictCodeTypeEnum;
import cn.com.farben.gptcoder.operation.commons.user.utils.UserInfoUtils;
import cn.com.farben.gptcoder.operation.platform.dictionary.command.AddDictCodeCommand;
import cn.com.farben.gptcoder.operation.platform.dictionary.command.DictIdCommand;
import cn.com.farben.gptcoder.operation.platform.dictionary.command.DisableDictCommand;
import cn.com.farben.gptcoder.operation.platform.dictionary.command.EditDictCodeCommand;
import cn.com.farben.gptcoder.operation.platform.dictionary.command.PrimaryIdCommand;
import cn.com.farben.gptcoder.operation.platform.dictionary.domain.DictCodeAggregateRoot;
import cn.com.farben.gptcoder.operation.platform.dictionary.domain.entity.DictCodeEntity;
import cn.com.farben.gptcoder.operation.platform.dictionary.domain.entity.DictEntity;
import cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.facade.DictCodeRepository;
import cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.facade.DictRepository;
import cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.utils.LocalCacheUtils;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * 系统字典映射操作服务类
 * 创建时间：2023/8/15<br>
 * @author wuanhui
 */
@Component
public class DictCodeAppService {
    private final DictCodeRepository dictCodeRepository;

    private final DictRepository dictRepository;

    private final LocalCacheUtils localCache;

    public DictCodeAppService(DictCodeRepository dictCodeRepository, DictRepository dictRepository, LocalCacheUtils localCache) {
        this.dictCodeRepository = dictCodeRepository;
        this.dictRepository = dictRepository;
        this.localCache = localCache;
    }

    /**
     * 查询字典明细
     * @param command 参数
     * @return 返回值
     */
    public DictCodeInfoDTO dictCodeDetail(PrimaryIdCommand command) {
        DictCodeEntity entity = dictCodeRepository.findDictCodeById(command.getId());
        DictCodeInfoDTO codeInfo = new DictCodeInfoDTO();
        BeanUtils.copyProperties(entity, codeInfo);
        return codeInfo;
    }

    /**
     * 查询字典码表信息
     * @param dictCode 码表
     * @param kind 码表编码
     * @return 返回值
     */
    public DictCodeDTO findDictByKind(String dictCode, String kind) {
        if(CharSequenceUtil.isBlank(dictCode) || CharSequenceUtil.isBlank(kind)) {
            throw new IllegalParameterException("字典编码不能为空");
        }
        List<DictCodeDTO> list = dictCodeRepository.findDictByKind(dictCode, kind);
        if(list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 查询字典码表信息（正常未禁用的）
     * @param dictCode 所属码表
     * @return 返回值
     */
    public List<DictCodeDTO> findDictByCode(String dictCode) {
        if(CharSequenceUtil.isBlank(dictCode)) {
            throw new IllegalParameterException("字典编码不能为空");
        }
        return dictCodeRepository.findDictByKind(dictCode, "");
    }

    /**
     * 从缓存李获取系统字典配置
     * @param dictCode 字典code
     * @return 所属码表
     */
    public List<DictCodeDTO> findDictOptionCacheItem(String dictCode) {
        if(SystemDictCodeTypeEnum.exist(dictCode) == null) {
            return new ArrayList<>();
        }
        return localCache.getDictList(dictCode);
    }

    /**
     * 查询字典码表信息（所有字典信息）
     * @param dictCode 所属码表
     * @return 返回值
     */
    public List<DictCodeDTO> findAllDictByCode(String dictCode) {
        if(CharSequenceUtil.isBlank(dictCode)) {
            throw new IllegalParameterException("字典编码不能为空");
        }
        return dictCodeRepository.findAllDictByKind(dictCode);
    }

    /**
     * 新增字典数据
     * @param command 参数
     * @return 操作结果
     */
    public boolean addDictCode(AddDictCodeCommand command) {
        //校验必填参数
        checkAddParam(command);

        //验证所属码表是否存在
        DictEntity dict = dictRepository.findByCode(command.getDictCode());
        if(dict == null) {
            throw new IllegalParameterException("查询的码表字典不存在");
        }

        //校验重复性
        List<DictCodeDTO> list = dictCodeRepository.findDictByKind(command.getDictCode(), command.getKindCode());
        if(!list.isEmpty()) {
            throw new IllegalParameterException(ErrorCodeEnum.EXIST_DICT_KIND_CODE);
        }

        DictCodeEntity dictEntity = new DictCodeEntity();
        BeanUtils.copyProperties(command, dictEntity);
        if(dictEntity.getDisable() == null) {
            dictEntity.setDisable(FlagTypeEnum.ZERO.getCode());
        }
        dictEntity.setCreateBy(UserInfoUtils.getUserInfo().getAccount());
        DictCodeAggregateRoot aggregateRoot = new DictCodeAggregateRoot.Builder(dictCodeRepository).build();
        return aggregateRoot.addDictCode(dictEntity);
    }

    /**
     * 新修改字典数据
     * @param command 参数
     * @return 操作结果
     */
    public boolean editDictCode(EditDictCodeCommand command) {
        //参数校验
        checkEditParamSize(command);
        if(command.getDisable() != null && FlagTypeEnum.exist(command.getDisable()) == null) {
            throw new IllegalParameterException(ErrorCodeEnum.NOT_EXIST_FLAG);
        }
        DictCodeEntity entity = findById(command.getId());
        //校验码值是否被占用
        if(CharSequenceUtil.isNotBlank(command.getKindCode()) && !command.getKindCode().equals(entity.getKindCode())) {
            List<DictCodeDTO> list = dictCodeRepository.findDictByKind(entity.getDictCode(), command.getKindCode());
            if(!list.isEmpty()) {
                throw new IllegalParameterException(ErrorCodeEnum.EXIST_DICT_KIND_CODE);
            }
        }
        //删除本地缓存
        localCache.deleteCache(entity.getDictCode());

        DictCodeAggregateRoot aggregateRoot = new DictCodeAggregateRoot.Builder(dictCodeRepository).build();
        DictCodeEntity dictEntity = new DictCodeEntity();
        BeanUtils.copyProperties(command, dictEntity);
        dictEntity.setId(entity.getId());
        dictEntity.setUpdateBy(UserInfoUtils.getUserInfo().getAccount());
        return aggregateRoot.editDictCode(dictEntity);
    }

    /**
     * 启用、禁用字典编码数据
     * @param command 参数
     * @return 操作结果
     */
    public boolean disableDictCode(DisableDictCommand command) {
        //必填参数由框架校验
        if(FlagTypeEnum.exist(command.getDisable()) == null) {
            throw new IllegalParameterException(ErrorCodeEnum.NOT_EXIST_FLAG);
        }
        DictCodeEntity entity = findById(command.getId());
        entity.setDisable(command.getDisable());
        entity.setUpdateBy(UserInfoUtils.getUserInfo().getAccount());
        DictCodeAggregateRoot aggregateRoot = new DictCodeAggregateRoot.Builder(dictCodeRepository).build();
        return aggregateRoot.editDictCode(entity);
    }

    /**
     * 删除字典编码数据
     * @param command 参数
     * @return 操作结果
     */
    public boolean deleteDictCode(DictIdCommand command) {
        //拆分ID参数，多个以分割
        String[] ids = command.getIds().split(StrPool.COMMA);
        List<String> idList = Arrays.asList(ids);
        List<DictCodeEntity> entityList = dictCodeRepository.findDictCodeByIdList(idList);
        if(entityList.isEmpty()) {
            throw new IllegalParameterException(ErrorCodeEnum.IS_NULL_DICT);
        }
        List<String> codeList = entityList.stream().map(DictCodeEntity::getDictCode).toList();
        //删除本地缓存
        for(String item : codeList) {
            localCache.deleteCache(item);
        }
        DictCodeAggregateRoot aggregateRoot = new DictCodeAggregateRoot.Builder(dictCodeRepository).build();
        return aggregateRoot.deleteDictCode(idList);
    }

    /**
     * 根据主键ID查询
     * @param id 主键ID
     * @return 查询结果
     */
    private DictCodeEntity findById(String id) {
        DictCodeEntity entity = dictCodeRepository.findDictCodeById(id);
        if(entity == null) {
            throw new IllegalParameterException(ErrorCodeEnum.IS_NULL_DICT_CODE);
        }
        return entity;
    }

    /**
     * 校验新增字典配置时必填项
     * @param command 参数
     */
    private void checkAddParam(AddDictCodeCommand command) {
        if(CharSequenceUtil.isBlank(command.getDictCode())){
            throw new IllegalParameterException(ErrorCodeEnum.EMPTY_DICT_CODE_TWO);
        }
        if(CharSequenceUtil.isBlank(command.getKindCode())){
            throw new IllegalParameterException(ErrorCodeEnum.EMPTY_DICT_KIND_CODE);
        }
        if(CharSequenceUtil.isBlank(command.getKindValue())){
            throw new IllegalParameterException(ErrorCodeEnum.EMPTY_DICT_KIND_VALUE);
        }
        if(command.getOrderNo() == null){
            throw new IllegalParameterException(ErrorCodeEnum.EMPTY_DICT_CODE_ORDER);
        }
        if(command.getDisable() != null && FlagTypeEnum.exist(command.getDisable()) == null) {
            throw new IllegalParameterException(ErrorCodeEnum.NOT_EXIST_FLAG);
        }
        checkAddParamSize(command);
    }

    /**
     * 校验新增字典配置的长度范围
     * @param command 参数
     */
    private void checkAddParamSize(AddDictCodeCommand command) {
        if(command.getKindCode().length() > 50){
            throw new IllegalParameterException(ErrorCodeEnum.MAX_DICT_KIND_CODE);
        }
        if(command.getKindValue().length() > 50){
            throw new IllegalParameterException(ErrorCodeEnum.MAX_DICT_KIND_VALUE);
        }
        if(command.getOrderNo() > 635525 || command.getOrderNo() < 0){
            throw new IllegalParameterException(ErrorCodeEnum.MAX_DICT_KIND_ORDER);
        }
    }

    /**
     * 校验新增字典配置的长度范围
     * @param command 参数
     */
    private void checkEditParamSize(EditDictCodeCommand command) {
        if(CharSequenceUtil.isNotBlank(command.getKindCode()) && command.getKindCode().length() > 50){
            throw new IllegalParameterException(ErrorCodeEnum.MAX_DICT_KIND_CODE);
        }
        if(CharSequenceUtil.isNotBlank(command.getKindValue()) && command.getKindValue().length() > 50){
            throw new IllegalParameterException(ErrorCodeEnum.MAX_DICT_KIND_VALUE);
        }
        if(command.getOrderNo() > 635525 || command.getOrderNo() < 0){
            throw new IllegalParameterException(ErrorCodeEnum.MAX_DICT_KIND_ORDER);
        }
    }
}
