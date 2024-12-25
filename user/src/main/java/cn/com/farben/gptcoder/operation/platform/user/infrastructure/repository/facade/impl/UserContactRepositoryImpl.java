package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.impl;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.gptcoder.operation.platform.user.domain.entity.UserContactEntity;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.UserContactRepository;
import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper.UserContactMapper;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.query.QueryChain;
import org.springframework.stereotype.Repository;

import static cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po.table.UserContactTableDef.USER_CONTACT;

/**
 *
 * 用户扩展信息仓储实现<br>
 * 创建时间：2023/8/15<br>
 * @author ltg
 *
 */
@Repository
public class UserContactRepositoryImpl implements UserContactRepository {
    /** 用户扩展信息DB服务 */
    private final UserContactMapper userContactMapper;
    private static final Log logger = LogFactory.get();

    @Override
    public UserContactEntity findByUserid(String userId) {
        logger.info("根据用户id查找扩展信息：{}", userId);
        UserContactEntity userContactEntity = new UserContactEntity();
        CommonAssemblerUtil.assemblerPOToEntity(
                QueryChain.of(userContactMapper).select()
                        .where(USER_CONTACT.USER_ID.eq(userId)).one(),
                userContactEntity
        );

        return userContactEntity;
    }

    public UserContactRepositoryImpl(UserContactMapper userContactMapper) {
        this.userContactMapper = userContactMapper;
    }
}
