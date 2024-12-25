package cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.utils;

import cn.com.farben.gptcoder.operation.commons.user.dto.CommonOrganMngDTO;
import cn.com.farben.gptcoder.operation.commons.user.dto.DictCodeDTO;
import cn.com.farben.gptcoder.operation.commons.user.dto.SysRoleCacheDTO;
import cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.facade.DictCodeRepository;
import cn.hutool.core.text.CharSequenceUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 本地缓存工具类
 * @author wuanhui
 */
@Component
public class LocalCacheUtils {

    /** 字典码表 */
    private final DictCodeRepository dictCodeRepository;

    /** 字典码表本地缓存 */
    private final static Cache<String, List<DictCodeDTO>> DICT_CODE_CACHE = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    /** 组织机构码表本地缓存 */
    private final static Cache<String, CommonOrganMngDTO> ORGAN_MNG_CACHE = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    /** 系统角色码表本地缓存 */
    private final static Cache<String, SysRoleCacheDTO> SYSTEM_ROLE_CACHE = CacheBuilder.newBuilder()
            .maximumSize(200)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    public LocalCacheUtils(DictCodeRepository dictCodeRepository) {
        this.dictCodeRepository = dictCodeRepository;
    }

    /**
     * 查询系统机构名称
     * @param key 组织机构号
     * @param loader 回调方法
     * @return 字典值
     */
    public String getOrganValue(String key,  Callable<? extends CommonOrganMngDTO> loader){
        try {
            CommonOrganMngDTO commonOrgan = ORGAN_MNG_CACHE.get(key, loader);
            if(CharSequenceUtil.isNotBlank(commonOrgan.getId())) {
                return commonOrgan.getOrganName();
            }
            deleteOrganCache(key);
        }catch (Exception ignored){
        }
        return "-";
    }

    /**
     * 查询系统机构对象
     * @param key 组织机构号
     * @param loader 回调方法
     * @return 字典值
     */
    public CommonOrganMngDTO getOrganEntity(String key,  Callable<? extends CommonOrganMngDTO> loader){
        try {
            CommonOrganMngDTO commonOrgan = ORGAN_MNG_CACHE.get(key, loader);
            if(CharSequenceUtil.isBlank(commonOrgan.getId())) {
                deleteOrganCache(key);
            }
            return commonOrgan;
        }catch (Exception ignored){
        }
        return null;
    }

    /**
     * 查询系统字典码表信息
     * @param code 码表code
     * @param kind 指定码值
     * @return 字典值
     */
    public String getDictValue(String code, String kind){
        List<DictCodeDTO> list = getDictList(code);
        DictCodeDTO dictCode = list.stream().filter(e -> e.getKindCode().equalsIgnoreCase(kind)).findAny().orElse(null);
        return dictCode == null ? "" : dictCode.getKindValue();
    }

    public List<DictCodeDTO> getDictList(String code) {
        try {
            List<DictCodeDTO> list = DICT_CODE_CACHE.get(code, () -> dictCodeRepository.findAllDictByKind(code));
            if(list.isEmpty()){
                deleteCache(code);
            }
            return list;
        }catch (Exception ignored){
        }
        return new ArrayList<>();
    }

    /**
     * 查询系统角色信息
     * @param key 角色ID
     * @param loader 回调方法
     * @return 字典值
     */
    public SysRoleCacheDTO getRoleCache(String key,  Callable<? extends SysRoleCacheDTO> loader){
        try {
            SysRoleCacheDTO roleCache = SYSTEM_ROLE_CACHE.get(key, loader);
            if(CharSequenceUtil.isNotBlank(roleCache.getId())) {
                return roleCache;
            }
            deleteRoleCache(key);
        }catch (Exception ignored){
        }
        return null;
    }

    public synchronized void deleteOrganCache(String key) {
        try {
            ORGAN_MNG_CACHE.invalidate(key);
        }catch (Exception ignored){
        }
    }


    public synchronized void deleteCache(String code) {
        try {
            DICT_CODE_CACHE.invalidate(code);
        }catch (Exception ignored){
        }
    }

    public synchronized void deleteRoleCache(String key) {
        try {
            SYSTEM_ROLE_CACHE.invalidate(key);
        }catch (Exception ignored){
        }
    }
}
