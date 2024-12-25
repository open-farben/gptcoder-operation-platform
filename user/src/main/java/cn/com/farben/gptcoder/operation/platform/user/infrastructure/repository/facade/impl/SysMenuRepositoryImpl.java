package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.impl;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.gptcoder.operation.platform.user.command.role.QueryMenuCommand;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.SysMenuEntity;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysMenuListDTO;
import cn.com.farben.gptcoder.operation.platform.user.dto.role.SysMenuTreeDTO;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.SysMenuRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper.SysMenuMapper;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.SysMenuPO;
import com.mybatisflex.core.query.QueryChain;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.table.SysMenuTableDef.SYS_MENU;

/**
 *
 * 系统菜单配置仓储实现
 * @author wuanhui
 *
 */
@Repository
public class SysMenuRepositoryImpl implements SysMenuRepository {

    private final SysMenuMapper sysMenuMapper;

    public SysMenuRepositoryImpl(SysMenuMapper sysMenuMapper) {
        this.sysMenuMapper = sysMenuMapper;
    }

    @Override
    public SysMenuEntity findMenuById(String id) {
        SysMenuEntity sysMenuEntity = new SysMenuEntity();
        CommonAssemblerUtil.assemblerPOToEntity(
                QueryChain.of(sysMenuMapper).select()
                        .where(SYS_MENU.ID.eq(id)).one(),
                sysMenuEntity
        );
        return sysMenuEntity;
    }

    /**
     * 超管查询所有系统菜单
     * @return 菜单列表
     */
    @Override
    public List<SysMenuListDTO> findAllMenu() {
        return sysMenuMapper.findAllMenu();
    }

    /**
     * 新增菜单操作
     * @param entity 菜单对象
     * @return 影响行数
     */
    @Override
    public Boolean addMenu(SysMenuEntity entity) {
        SysMenuPO menu = new SysMenuPO();
        BeanUtils.copyProperties(entity, menu);
        return sysMenuMapper.insertSelectiveWithPk(menu) > 0;
    }

    /**
     * 修改菜单操作
     * @param entity 菜单对象
     * @return 影响行数
     */
    @Override
    public Boolean editMenu(SysMenuEntity entity) {
        SysMenuPO menu = new SysMenuPO();
        BeanUtils.copyProperties(entity, menu);
        return sysMenuMapper.update(menu) > 0;
    }

    /**
     * 删除指定ID的菜单信息
     * @param id 菜单ID
     * @return 影响行数
     */
    @Override
    public Boolean deleteMenu(String id) {
        return sysMenuMapper.deleteById(id) > 0;
    }

    /**
     * 列表页查询菜单列表
     * @param param 参数
     * @return 查询结果
     */
    @Override
    public List<SysMenuListDTO> menuList(QueryMenuCommand param) {
        return sysMenuMapper.menuList(param);
    }

    /**
     * 编辑角色时，弹出的可选菜单目录下拉框
     * @param menuType 菜单类型
     * @return 列表数据
     */
    @Override
    public List<SysMenuTreeDTO> selectMenuTree(String menuType) {
        return sysMenuMapper.selectMenuTree(menuType);
    }
}
