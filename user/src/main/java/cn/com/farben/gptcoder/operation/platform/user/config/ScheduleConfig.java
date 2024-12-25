package cn.com.farben.gptcoder.operation.platform.user.config;

import cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper.SysDictMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;

@Configuration
public class ScheduleConfig implements SchedulingConfigurer {

    @Autowired
    SysDictMapper sysDictMapper;
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    BeanFactory beanFactory;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        int cpuCoreCount = Runtime.getRuntime().availableProcessors();
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(cpuCoreCount));
    }

}