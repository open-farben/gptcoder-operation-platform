package cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.mapper;

import cn.com.farben.gptcoder.operation.platform.statistics.domain.entity.SpeedConfigEntity;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.po.SpeedConfigPO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * 响应速率配置操作mapper
 * @author wuanhui
 */
public interface SpeedConfigMapper extends BaseMapper<SpeedConfigPO> {

    @Select({
            """
            <script>
                select `id`, config_code, config_name, speed, create_by, create_date
                from t_speed_config
                where config_code = #{code} limit 1
            </script>
            """
    })
    SpeedConfigEntity findSpeedConfig(String code);
}
