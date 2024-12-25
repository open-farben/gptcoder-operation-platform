package cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.mapper;

import cn.com.farben.gptcoder.operation.platform.statistics.command.FunUseStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.PersonUseStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.FunStatDataDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.AdoptionInfoDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.DashboardStatDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.UseStatDetailDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.po.AdoptionInfoPO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 *
 * 插件使用记录表mapper<br>
 * 创建时间：2023/8/15<br>
 * @author ltg
 *
 */
public interface AdoptionInfoMapper extends BaseMapper<AdoptionInfoPO> {
    @Insert({
        """
        <script>
            insert into t_adoption_info (info_id, user_id, fun_id, client_ip, plugins_ver, ide_name, ide_ver, sys_ver, mac_info, gen_num, confirm_num, access_date, cost_time,
                prompt_num, complement_char_num, complement_rate, create_by, deal_flg, model_name, plugin_type)
            values
            <foreach collection="adoptions" separator="," item="adopt">
                (#{adopt.infoId}, #{adopt.userId}, #{adopt.funId}, #{adopt.clientIp}, #{adopt.pluginsVer}, #{adopt.ideName}, #{adopt.ideVer}, #{adopt.sysVer},
                #{adopt.macInfo}, #{adopt.genNum}, #{adopt.confirmNum}, #{adopt.accessDate}, #{adopt.costTime}, #{adopt.promptNum}, #{adopt.complementCharNum},
                #{adopt.complementRate}, #{adopt.createBy}, #{adopt.dealFlg}, #{adopt.modelName}, #{adopt.pluginType})
            </foreach>
        </script>
        """
    })
    Integer batchInsertAdoption(List<AdoptionInfoPO> adoptions);
}
