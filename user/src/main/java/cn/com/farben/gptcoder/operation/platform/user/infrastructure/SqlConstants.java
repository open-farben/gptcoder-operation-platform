package cn.com.farben.gptcoder.operation.platform.user.infrastructure;

/**
 * sql语句常量类
 */
public class SqlConstants {
    public static final String PLUGIN_USER_PAGE_SQL = """
            <script>
                select u.id, u.account, u.job_number as jobNumber, u.name, u.organization,u.full_organization, u.duty, u.mobile, u.email, u.plugin,
                u.version, u.last_used_time as lastUsedTime, u.status,u.locked, o.organ_name as organizationName, d.kind_value as dutyName
                from plugin_user u
                left join t_organ_mng o on u.organization = o.organ_no
                left join t_dict_code d on u.duty = d.kind_code and d.dict_code = 'JOB_INFO'
                <where>
                    u.organization in
                    <foreach item="item" index="index" collection="organList" open="(" separator="," close=")">
                        #{item}
                    </foreach>
                    <when test='searchText != null and searchText != ""'>
                        and (u.account like concat('%',#{searchText},'%') or u.job_number like concat('%',#{searchText},'%')
                        or u.name like concat('%',#{searchText},'%'))
                    </when>
                    <when test='organization != null and organization != ""'>
                        and u.full_organization like concat(#{organization},'%')
                    </when>
                    <when test='status != null and status != ""'>
                        and u.status = #{status}
                    </when>
                </where>
                order by u.create_time desc
            </script>
        """;

    private SqlConstants() {
        throw new IllegalStateException("常量类不允许实例化");
    }
}
