package cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.platform.statistics.command.DashboardStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.FunUseStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.PersonUseStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.domain.entity.AdoptionInfoEntity;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.FunStatDataDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.AdoptionInfoDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.DashboardStatDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.UseStatDetailDTO;
import com.mybatisflex.core.paginate.Page;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * 插件使用记录仓储接口<br>
 * 创建时间：2023/8/15<br>
 * @author ltg
 *
 */
public interface AdoptionInfoRepository {

    /**
     * 新增插件使用记录
     * @param adoptionInfoEntity 插件使用记录实体
     */
    void addAdoptionInfo(AdoptionInfoEntity adoptionInfoEntity);

    /**
     * 批量新增插件使用记录
     * @param adoptions 插件使用记录实体
     */
    boolean batchAddAdoptionInfo(List<AdoptionInfoEntity> adoptions);

    /**
     * 分页查询插件使用记录
     * @param userId 用户ID
     * @param startTime 开始时间，格式：yyyy-MM-dd
     * @param endTime 结束时间, 格式：yyyy-MM-dd
     * @param pageSize 每页数据量
     * @param pageNo 页码
     * @return 插件使用记录DTO
     */
    Page<AdoptionInfoDTO> pageAdoptionInfo(String userId, String startTime, String endTime, long pageSize, long pageNo);

    /**
     * 使用人数统计记录
     * @param command 参数
     * @return 统计结果
     */
    List<UseStatDetailDTO> personUseStat(PersonUseStatCommand command);


    /**
     * 功能方法使用人数统计
     * @param command 参数
     * @return 统计结果
     */
    List<FunStatDataDTO> funUseStat(FunUseStatCommand command);

    /**
     * 概览汇总使用记录信息
     * @param command 参数
     * @return 统计结果
     */
    DashboardStatDTO dashboardStat(DashboardStatCommand command, List<String> codeList);

    Double findCompletionRate(DashboardStatCommand command, String code);

    /**
     * 获取系统最小一天的需要处理的数据，最多1000条
     * @return 需要处理的数据
     */
    List<AdoptionInfoEntity> listDealData();

    /**
     * 批量设置已处理标识为1
     * @param ids 主键列表
     */
    void setDealFlgBatch(List<String> ids);

    /**
     * 根据账号获取用户某一天处理过的最近记录的时间
     * @param account 账号
     * @param day 日期
     * @return 处理过的最近记录的时间
     */
    LocalDateTime getUserLastDealTime(String account, String day);

    /**
     * 清除已处理过的数据
     */
    void removeProcessedData();
}
