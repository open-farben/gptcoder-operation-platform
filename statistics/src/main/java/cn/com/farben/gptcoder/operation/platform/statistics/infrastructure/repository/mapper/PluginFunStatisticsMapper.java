package cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.mapper;

import cn.com.farben.gptcoder.operation.platform.statistics.command.DashboardStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.FunUseStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.JobUseFunStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.PluginFunctionStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.command.UserDutyCountStatCommand;
import cn.com.farben.gptcoder.operation.platform.statistics.domain.entity.PluginFunStatisticsEntity;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.DashboardStatDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.FunStatDataDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.PluginFunctionStatisticsDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.dto.UseFunctionDTO;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.SqlConstants;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.po.PluginFunctionStatisticsPO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
 * 插件功能统计表mapper
 * @author wuanhui
 */
public interface PluginFunStatisticsMapper extends BaseMapper<PluginFunctionStatisticsPO> {
    /**
     * 概览页功能使用情况统计
     * @param query 查询参数
     * @return 统计结果
     */
    @Select({
        """
        <script>
            select sum(value) AS value, modelAbility
            <choose>
                <when test="param.type == 'day'">
                    ,DATE_FORMAT(day, '%Y-%m-%d') as date
                </when>
                <when test="param.type == 'month'">
                    ,DATE_FORMAT(day, '%Y-%m') as date
                </when>
                <otherwise>
                    ,DATE_FORMAT(day, '%Y') as date
                </otherwise>
            </choose>
            from(
                select count(t.account) as value, t.model_ability as modelAbility, t.day
                from plugin_function_statistics t
                left join plugin_user u on t.account = u.account
                <where>
                    t.day between #{param.startDate} and #{param.endDate}
                    <if test = 'param.fullOrganNo != null and param.fullOrganNo != ""'>
                        and u.full_organization like concat(#{param.fullOrganNo}, '%')
                    </if>
                    <if test = "param.funList != null and param.funList.size() > 0">
                        and t.model_ability in
                        <foreach collection="param.funList" open="(" close=")" separator="," item="funId">
                            #{funId}
                        </foreach>
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
                </where>
                group by t.model_ability, t.day
            ) t1  group by modelAbility, date
            order by date asc
        </script>
        """
    })
    List<FunStatDataDTO> funUseStat(@Param("param") FunUseStatCommand query);

    /**
     * 概览页各职务使用插件次数统计
     * @param query 查询参数
     * @return 统计结果
     */
    @Select({
            """
            <script>
                select sum(t.use_count) as value, u.duty as duty
                from plugin_function_statistics t
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
                group by u.duty
            </script>
            """
    })
    List<UseFunctionDTO> userDutyCountStat(@Param("param") UserDutyCountStatCommand query);


    /**
     * 概览页个职务使用插件功能次数统计
     *
     * @param query 参数
     * @return 统计结果
     */
    @Select({
            """
            <script>
                select sum(t.use_count) as value, t.model_ability as modelAbility
                from plugin_function_statistics t
                left join plugin_user u on t.account = u.account
                <where>
                    t.day between #{param.startDate} and #{param.endDate}
                    <if test = 'param.fullOrganNo != null and param.fullOrganNo != ""'>
                        and u.full_organization like concat(#{param.fullOrganNo}, '%')
                    </if>
                    <if test = 'param.duty != null and param.duty != ""'>
                        and u.duty = #{param.duty}
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
                </where>
                group by t.model_ability
            </script>
            """
    })
    List<FunStatDataDTO> userJobFunctionStat(@Param("param") JobUseFunStatCommand query);

    /**
     *
     * 批量增加插件能力统计信息
     * @param statisticsEntityList 插件能力统计实体列表
     */
    @Insert({
            """
            <script>
                insert into plugin_function_statistics (id, account, `day`, model_ability, use_count, accept_count,
                generate_lines, accept_lines)
                values
                <foreach collection="dataList" separator="," item="item">
                    (#{item.id}, #{item.account}, #{item.day}, #{item.modelAbilityEnum}, #{item.useCount},
                    #{item.acceptCount}, #{item.generateLines}, #{item.acceptLines})
                </foreach>
            </script>
            """
    })
    void addPluginFunStatistics(@Param("dataList") List<PluginFunStatisticsEntity> statisticsEntityList);

    /**
     *
     * 批量更新插件能力统计信息
     * @param statisticsEntityList 插件能力统计实体列表
     */
    @Update({
            """
            <script>
                <foreach collection="dataList" item="item" separator=";">
                    update plugin_function_statistics
                    <set>
                        use_count = #{item.useCount}, accept_count = #{item.acceptCount},
                        generate_lines = #{item.generateLines}, accept_lines = #{item.acceptLines}
                    </set>
                    where id = #{item.id}
                </foreach>
            </script>
            """
    })
    void refreshPluginFunStatistics(@Param("dataList") List<PluginFunStatisticsEntity> statisticsEntityList);

    /**
     * 概览首页统计代码行信息
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param modelAbility 模型能力
     * @return 统计结果
     */
    @Select({
            """
            <script>
                select sum(t.generate_lines) as genNum, sum(t.accept_lines) as confirmNum
                from plugin_function_statistics t
                <where>
                    t.day &gt;= #{startDate} and t.day &lt;= #{endDate} and model_ability = #{modelAbility}
                </where>
            </script>
            """
    })
    DashboardStatDTO dashboardSummary(String startDate, String endDate, String modelAbility);

    /**
     * 概览首页统计代码行信息(过滤权限、机构）
     * @param query 参数
     * @param modelAbility 模型能力
     * @return 统计结果
     */
    @Select({
        """
        <script>
            select sum(t.generate_lines) as genNum, sum(t.accept_lines) as confirmNum
            from plugin_function_statistics t left join plugin_user u on t.account = u.account
            <where>
                t.day &gt;= #{param.startDate} and t.day &lt;= #{param.endDate} and model_ability = #{modelAbility}
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
    DashboardStatDTO dashboardSummaryOrgan(@Param("param") DashboardStatCommand query, @Param("modelAbility") String modelAbility);

    /**
     * 查询用户使用功能情况
     * @param command 参数
     */
    @Select({SqlConstants.PLUGIN_FUNCTION_STATISTICS})
    List<PluginFunctionStatisticsDTO> listPluginFunctionStatistics(@Param("param") PluginFunctionStatCommand command);
}
