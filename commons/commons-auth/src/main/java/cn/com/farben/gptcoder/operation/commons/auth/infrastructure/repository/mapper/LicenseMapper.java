package cn.com.farben.gptcoder.operation.commons.auth.infrastructure.repository.mapper;

import cn.com.farben.gptcoder.operation.commons.auth.infrastructure.repository.po.LicensePO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 *
 * license表mapper<br>
 * 创建时间：2023/9/21<br>
 * @author ltg
 *
 */
public interface LicenseMapper extends BaseMapper<LicensePO> {
    /**
     * 查询启用的插件用户数量
     */
    @Select("""
            <script>
                select count(*) from plugin_user where status = 'enable'
            </script>
            """)
    long countEnabledUser();
}
