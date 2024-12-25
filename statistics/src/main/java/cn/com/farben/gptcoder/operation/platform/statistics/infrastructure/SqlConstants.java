package cn.com.farben.gptcoder.operation.platform.statistics.infrastructure;

/**
 * sql语句常量类
 */
public class SqlConstants {
    /** 用户使用插件情况sql */
    public static final String PLUGIN_USAGE_STATISTICS = """
        <script>
            select p.account, p.job_number as jobNumber, p.name, p.organization, o.organ_name as organizationName,
            p.duty, dc.kind_value as dutyName,
            CAST((SUM(case when fs.model_ability='code_hinting' then us.active_time else 0 end) / 60) AS DECIMAL(10,2)) AS activeTime,
            IFNULL(CAST((SUM(case when fs.model_ability='code_hinting' then us.total_completion else 0 end) / SUM(case when fs.model_ability='code_hinting' then fs.accept_count else 0 end)) AS DECIMAL(10,4)), 0) AS avgCompletionRate,
            sum(fs.generate_lines) as generationLines, sum(fs.accept_lines) as acceptLines,
            CAST((SUM(case when fs.model_ability='code_hinting' then us.total_response else 0 end) / SUM(case when fs.model_ability='code_hinting' then us.total_call_number ELSE 0 end)) AS DECIMAL(10,2)) AS avgResponseRate,
            p.current_model as currentModel, p.plugin as currentPlugin, p.version as currentPluginVersion,
            p.last_used_time as lastUsedTime
            from plugin_usage_statistics us
            join plugin_user p on p.account = us.account
            <when test='param.fullOrganNo != null and param.fullOrganNo != ""'>
                and p.full_organization like concat(#{param.fullOrganNo},'%')
            </when>
            left join t_organ_mng o on p.organization = o.organ_no
            left join t_dict_code dc on dc.kind_code = p.duty and dc.dict_code = 'JOB_INFO'
            left join plugin_function_statistics fs on fs.account = us.account and fs.day = us.day
            
            <where>
                us.day between #{param.startDay} and #{param.endDay}
                <when test='param.searchText != null and param.searchText != ""'>
                    and (p.account like concat('%',#{param.searchText},'%') or p.name like concat('%',#{param.searchText},'%') or
                    p.job_number like concat('%',#{param.searchText},'%'))
                </when>
                <if test='param.isOnly != null and param.isOnly != ""'>
                    and p.account = #{param.userId}
                </if>
                <if test='param.organNoList != null and param.organNoList.size() > 0'>
                    and p.organization in
                    <foreach collection="param.organNoList" open="(" close=")" separator="," item="organNo">
                        #{organNo}
                    </foreach>
                </if>
            </where>
            group by p.account
            <when test='param.orderStr != null and param.orderStr != ""'>
                order by ${param.orderStr} ${param.orderBy}
            </when>
            
            <when test='param.orderStr == null or param.orderStr == ""'>
                order by lastUsedTime desc
            </when>
        </script>
    """;

    /** 用户使用功能情况sql */
    public static final String PLUGIN_FUNCTION_STATISTICS = """
                <script>
                    select p.account, p.job_number as jobNumber, p.name, p.organization, o.organ_name as organizationName,
                    p.duty, dc.kind_value as dutyName, sum(fs.use_count) as totalUseCount,
                    sum(case when fs.model_ability='code_hinting' then fs.use_count else 0 end) as hintingCount,
                    sum(case when fs.model_ability='code_explain' then fs.use_count else 0 end) as explainCount,
                    sum(case when fs.model_ability='code_correction' then fs.use_count else 0 end) as correctionCount,
                    sum(case when fs.model_ability='code_comment' then fs.use_count else 0 end) as commentCount,
                    sum(case when fs.model_ability='code_conversion' then fs.use_count else 0 end) as conversionCount,
                    sum(case when fs.model_ability='ai_answer' then fs.use_count else 0 end) as questionCount,
                    sum(case when fs.model_ability='unit_test' then fs.use_count else 0 end) as testCount,
                    sum(case when fs.model_ability='knowledge_qa' then fs.use_count else 0 end) as knowledgeQaCount,
                    sum(case when fs.model_ability='code_search' then fs.use_count else 0 end) as codeSearchCount
                    from plugin_function_statistics fs
                    join plugin_user p on fs.account = p.account
                    <when test='param.fullOrganNo != null and param.fullOrganNo != ""'>
                        and p.full_organization like concat(#{param.fullOrganNo},'%')
                    </when>
                    left join t_organ_mng o on p.organization = o.organ_no
                    left join t_dict_code dc on dc.kind_code = p.duty and dc.dict_code = 'JOB_INFO'
                    
                    <where>
                        fs.day between #{param.startDay} and #{param.endDay}
                        <when test='param.searchText != null and param.searchText != ""'>
                            and (p.account like concat('%',#{param.searchText},'%') or p.name like concat('%',#{param.searchText},'%') or
                            p.job_number like concat('%',#{param.searchText},'%'))
                        </when>
                        <if test='param.isOnly != null and param.isOnly != ""'>
                            and p.account = #{param.userId}
                        </if>
                        <if test='param.organNoList != null and param.organNoList.size() > 0'>
                            and p.organization in
                            <foreach collection="param.organNoList" open="(" close=")" separator="," item="organNo">
                                #{organNo}
                            </foreach>
                        </if>
                    </where>
                    group by p.account
                    <when test='param.orderStr != null and param.orderStr != ""'>
                        order by ${param.orderStr} ${param.orderBy}
                    </when>
                    <when test='param.orderStr == null or param.orderStr == ""'>
                        order by p.last_used_time desc
                    </when>
                </script>
            """;

    private SqlConstants() {
        throw new IllegalStateException("常量类不允许实例化");
    }
}
