package cn.com.farben.commons.ddd.assembler;

import cn.com.farben.commons.ddd.domain.IEntity;
import cn.com.farben.commons.ddd.dto.IDTO;
import cn.com.farben.commons.ddd.po.IPO;
import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.core.paginate.Page;

import java.util.Collections;

import java.util.List;
import java.util.Objects;

/**
 * 通用组装器
 * 创建时间: 2023/8/11
 * @author ltg
 *
 */
public class CommonAssemblerUtil {
    /**
     * 将实体数据组装到dto对象
     * @param entity 实体
     * @param dto dto对象
     */
    public static void assemblerEntityToDTO(IEntity entity, IDTO dto) {
        if (Objects.isNull(entity) || Objects.isNull(dto)) {
            return;
        }
        BeanUtil.copyProperties(entity, dto);
    }

    /**
     * 将实体list组装成dto列表
     * @param entityList 实体list
     * @param targetType dto具体类型
     */
    public static <T extends IDTO> List<T> assemblerEntityListToDTOList(List<? extends IEntity> entityList, Class<T> targetType) {
        if (Objects.isNull(entityList)) {
            return Collections.emptyList();
        }
        return BeanUtil.copyToList(entityList, targetType);
    }

    /**
     * 将实体数据组装到po对象
     * @param entity 实体
     * @param po po对象
     */
    public static void assemblerEntityToPO(IEntity entity, IPO po) {
        if (Objects.isNull(entity) || Objects.isNull(po)) {
            return;
        }
        BeanUtil.copyProperties(entity, po);
    }

    /**
     * 将po对象组装到实体
     * @param po po对象
     * @param entity 实体
     */
    public static void assemblerPOToEntity(IPO po, IEntity entity) {
        if (Objects.isNull(entity) || Objects.isNull(po)) {
            return;
        }
        BeanUtil.copyProperties(po, entity);
    }

    /**
     * 将实体list组装成po列表
     * @param entityList 实体list
     * @param targetType po具体类型
     */
    public static <T extends IPO> List<T> assemblerEntityListToPOList(List<? extends IEntity> entityList, Class<T> targetType) {
        if (Objects.isNull(entityList)) {
            return Collections.emptyList();
        }
        return BeanUtil.copyToList(entityList, targetType);
    }

    /**
     * 将po列表组装成实体list
     * @param poList po列表
     * @param targetType 实体具体类型
     */
    public static <T extends IEntity> List<T> assemblerPOListToEntityList(List<? extends IPO> poList, Class<T> targetType) {
        if (Objects.isNull(poList)) {
            return Collections.emptyList();
        }
        return BeanUtil.copyToList(poList, targetType);
    }

    /**
     * 将po分页数据组装成实体分页数据
     * @param poPage po分页数据
     * @param targetClass 目标类型
     */
    public static <T extends IEntity, E extends IPO> Page<T> assemblerPOPageToEntityPage(Page<E> poPage, Class<T> targetClass) {
        if (Objects.isNull(poPage)) {
            return Page.of(0, 0, 0);
        }
        Page<T> resultPage = new Page<>();
        resultPage.setPageNumber(poPage.getPageNumber());
        resultPage.setPageSize(poPage.getPageSize());
        resultPage.setTotalPage(poPage.getTotalPage());
        resultPage.setTotalRow(poPage.getTotalRow());
        resultPage.setRecords(assemblerPOListToEntityList(poPage.getRecords(), targetClass));
        return resultPage;
    }

    /**
     * 将entity分页数据组装成dto分页数据
     * @param entityPage 实体分页数据
     * @param targetClass 目标类型
     */
    public static <T extends IDTO, E extends IEntity> Page<T> assemblerEntityPageToDTOPage(Page<E> entityPage, Class<T> targetClass) {
        if (Objects.isNull(entityPage)) {
            return Page.of(0, 0, 0);
        }
        Page<T> resultPage = new Page<>();
        resultPage.setPageNumber(entityPage.getPageNumber());
        resultPage.setPageSize(entityPage.getPageSize());
        resultPage.setTotalPage(entityPage.getTotalPage());
        resultPage.setTotalRow(entityPage.getTotalRow());
        resultPage.setRecords(assemblerEntityListToDTOList(entityPage.getRecords(), targetClass));
        return resultPage;
    }

    private CommonAssemblerUtil() {
        throw new IllegalStateException("工具类不能实例化");
    }
}
