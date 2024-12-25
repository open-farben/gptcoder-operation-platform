package cn.com.farben.commons.web.config;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.audit.AuditManager;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisFlexConfiguration {
    private static final Log logger = LogFactory.get();

    public MyBatisFlexConfiguration() {
        //开启审计功能
        AuditManager.setAuditEnable(true);

        //设置 SQL 审计收集器
        AuditManager.setMessageCollector(auditMessage ->
                logger.info("SQL:[{}], 耗时:[{}]ms", auditMessage.getFullSql()
                        , auditMessage.getElapsedTime())
        );
    }
}
