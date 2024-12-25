package cn.com.farben.gptcoder.operation.platform.group.infrastructure;

/**
 * sql语句常量类
 */
public class SqlConstants {
    /** 获取可授权的用户信息sql */
    public static final String AUTHORIZED_USER_SQL = """
            <script>
                select o.organ_no as organNo, o.organ_name as organName, o.parent_organ_no as parentOrganNo,
                u.id as userId, u.name as userName
                from t_organ_mng o
                left join plugin_user u on o.organ_no = u.organization
                <where>
                    <when test='organList != null and organList.size > 0'>
                        o.organ_no in
                        <foreach item="item" index="index" collection="organList" open="(" separator="," close=")">
                            #{item}
                        </foreach>
                    </when>
                    <when test='memberList != null and memberList.size > 0'>
                        or u.id in
                        <foreach item="item" index="index" collection="memberList" open="(" separator="," close=")">
                            #{item}
                        </foreach>
                    </when>
                </where>
                order by parentOrganNo
            </script>
            """;

    /** 删除工作组SQL语句 */
    public static final String DELETE_GROUP_SQL = """
            <script>
                delete from working_group where id = #{groupId};
                delete from user_group_relevance where group_id = #{groupId};
                delete from knowledge_right where ltype = 1 and lid = #{groupId};
            </script>
            """;

    /** 获取指定知识库的已授权工作组信息 */
    public static final String KNOWLEDGE_GROUP_SQL = """
            <script>
                select kr.lid
                from knowledge_right kr
                inner join knowledge k on kr.kid = k.id
                <where>
                    kr.ltype = 1 and kr.kid = #{kid}
                </where>
            </script>
            """;

    /** 获取可授权的工作组信息sql */
    public static final String AUTHORIZED_GROUP_SQL = """
            <script>
                select o.organ_no as organNo, o.organ_name as organName, o.parent_organ_no as parentOrganNo,
                wg.id as groupId, wg.group_name as groupName
                from working_group wg
                inner join t_organ_mng o on o.organ_no = wg.organ_no
                <where>
                    <when test='organList != null and organList.size > 0'>
                        o.organ_no in
                        <foreach item="item" index="index" collection="organList" open="(" separator="," close=")">
                            #{item}
                        </foreach>
                    </when>
                    <when test='memberList != null and memberList.size > 0'>
                        or wg.id in
                        <foreach item="item" index="index" collection="memberList" open="(" separator="," close=")">
                            #{item}
                        </foreach>
                    </when>
                </where>
                order by parentOrganNo
            </script>
            """;

    /** 获取指定git库的已授权工作组信息 */
    public static final String GIT_GROUP_SQL = """
            <script>
                select kr.lid
                from knowledge_right kr
                inner join git g on kr.kid = g.id
                <where>
                    kr.ltype = 1 and kr.kid = #{kid}
                </where>
            </script>
            """;

    private SqlConstants() {
        throw new IllegalStateException("常量类不允许实例化");
    }
}
