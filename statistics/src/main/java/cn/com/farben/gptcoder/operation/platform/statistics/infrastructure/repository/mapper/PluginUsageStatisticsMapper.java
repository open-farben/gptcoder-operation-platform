package cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.mapper;

import cn.com.farben.gptcoder.operation.platform.statistics.command.DashboardStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.PersonUseStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.PluginUsageStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.domain.entity.PluginUsageStatisticsEntity;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.DashboardStatDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.PluginUsageStatisticsDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.UseStatDetailDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.SqlConstants;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.po.PluginUsageStatisticsPO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 插件使用统计表mapper
 * @author wuanhui
 */
public interface PluginUsageStatisticsMapper extends BaseMapper<PluginUsageStatisticsPO> {

    /**
     * 概览页用户插件使用人数统计
     * @param command 参数
     * @return 统计结果
     */
    @Select({
        """
        <script>
            select count(value) AS value, duty, day as date
            from (
                select DISTINCT(t.account) AS value, u.duty,
            <choose>
                <when test="param.type == 'day'">
                    DATE_FORMAT(t.day, '%Y-%m-%d') as day
                </when>
                <when test="param.type == 'month'">
                    DATE_FORMAT(t.day, '%Y-%m') as day
                </when>
                <otherwise>
                    DATE_FORMAT(t.day, '%Y') as day
                </otherwise>
            </choose>
                from plugin_usage_statistics t
                left join plugin_user u on t.account = u.account
                <where>
                    t.day &gt;= #{param.startDateTime} and t.day &lt;= #{param.endDateTime}
                    <if test = 'param.fullOrganNo != null and param.fullOrganNo != ""'>
                        and u.full_organization like concat(#{param.fullOrganNo}, '%')
                    </if>
                    <if test='param.isOnly != null and param.isOnly != ""'>
                        and u.account = #{param.userId}
                    </if>
                    <if test='param.organNoList != null and param.organNoList.size() > 0'>
                        and u.organization in
                        <foreach collection="param.organNoList" open="(" close=")" separator="," item="organNo">
                            #{organNo}
                        </foreach>
                    </if>
                    and u.duty is not null
                </where>
                group by VALUE, u.duty, day
            ) t1 group by duty, date
            order by date asc
        </script>
        """
    })
    List<UseStatDetailDTO> personUseStat(@Param("param") PersonUseStatCommand command);


    /**
     * 新版记录概览页统计数量汇总
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 统计结果
     */
    @Select({
        """
        <script>
            select count(distinct account) as userNum, sum(total_call_number) as totalNum, sum(total_response) as totalResponse, 
            avg(avg_completion) as completionRate
            from plugin_usage_statistics
            <where>
                day &gt;= #{startDate} and day &lt;= #{endDate}
            </where>
        </script>
        """
    })
    DashboardStatDTO dashboardSummary(String startDate, String endDate);

    /**
     * 新版记录概览页统计数量汇总（过滤权限、机构）
     * @param command 参数
     * @return 统计结果
     */
    @Select({
        """
        <script>
            select count(distinct t.account) as userNum, sum(t.total_call_number) as totalNum, sum(t.total_response) as totalResponse, 
            avg(t.avg_completion) as completionRate
            from plugin_usage_statistics t left join plugin_user u on t.account = u.account
            <where>
                day &gt;= #{param.startDate} and day &lt;= #{param.endDate}
                <if test='param.isOnly != null and param.isOnly != ""'>
                    and u.account = #{param.userId}
                </if>
                <if test='param.organNoList != null and param.organNoList.size() > 0'>
                    and u.organization in
                    <foreach collection="param.organNoList" open="(" close=")" separator="," item="organNo">
                        #{organNo}
                    </foreach>
                </if>
            </where>
        </script>
        """
    })
    DashboardStatDTO dashboardSummaryByOrgan(@Param("param") DashboardStatCommand command);

    /**
     *
     * 批量增加插件使用统计信息
     * @param statisticsEntityList 插件使用统计实体列表
     */
    @Insert({
            """
            <script>
                insert into plugin_usage_statistics (id, account, `day`, active_time, total_completion, total_response,
                    total_call_number, avg_completion)
                values
                <foreach collection="dataList" separator="," item="item">
                    (#{item.id}, #{item.account}, #{item.day}, #{item.activeTime}, #{item.totalCompletion},
                    #{item.totalResponse}, #{item.totalCallNumber}, #{item.avgCompletion})
                </foreach>
            </script>
            """
    })
    void addPluginUsageStatistics(@Param("dataList") List<PluginUsageStatisticsEntity> statisticsEntityList);

    /**
     * 批量更新插件使用统计信息
     * @param statisticsEntityList 插件使用统计实体列表
     */
    @Update({
            """
            <script>
                <foreach collection="dataList" item="item" separator=";">
                    update plugin_usage_statistics
                    <set>
                        active_time = #{item.activeTime}, total_completion = #{item.totalCompletion},
                        total_response = #{item.totalResponse}, total_call_number = #{item.totalCallNumber},
                        avg_completion = #{item.avgCompletion}
                    </set>
                    where id = #{item.id}
                </foreach>
            </script>
            """
    })
    void refreshPluginUsageStatistics(@Param("dataList") List<PluginUsageStatisticsEntity> statisticsEntityList);

    /**
     * 查询插件使用记录
     * @param command 参数
     * @return 插件用户使用情况统计结果DTO,按照最近使用时间倒序排序
     */
    @Select({SqlConstants.PLUGIN_USAGE_STATISTICS})
    List<PluginUsageStatisticsDTO> listPluginUsageStatistics(@Param("param") PluginUsageStatCommand command);
}
