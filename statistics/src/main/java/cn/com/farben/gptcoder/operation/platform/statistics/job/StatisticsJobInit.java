package cn.com.farben.gptcoder.operation.platform.statistics.job;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class StatisticsJobInit implements ApplicationRunner {
    private final Scheduler scheduler;

    private final Map<JobDetail, Set<? extends Trigger>> jobMap = HashMap.newHashMap(3);

    public StatisticsJobInit(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initStatisticsJob();
        initConsumerHealthCheckJob();
        initSubscribeHealthCheckJob();

        // boolean replace 表示启动时对数据库中的quartz的任务进行覆盖。
        scheduler.scheduleJobs(jobMap, true);
    }

    private void initStatisticsJob() {
        JobDetail jobDetail = JobBuilder.newJob(PluginStatisticsJob.class)
                .withIdentity(PluginStatisticsJob.class.getName())
                .withDescription("统计插件使用情况")
                .storeDurably()
                .build();
        // 创建任务触发器
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(PluginStatisticsJob.class.getName() + "触发器")
                .withSchedule(CronScheduleBuilder.cronSchedule("6 */10 * * * ?"))
                .build();
        Set<Trigger> set = new HashSet<>();
        set.add(trigger);

        jobMap.put(jobDetail, set);
    }

    private void initConsumerHealthCheckJob() {
        JobDetail jobDetail = JobBuilder.newJob(ConsumerHealthCheckJob.class)
                .withIdentity(ConsumerHealthCheckJob.class.getName())
                .withDescription("消费者健康检查")
                .storeDurably()
                .build();
        // 创建任务触发器
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(ConsumerHealthCheckJob.class.getName() + "触发器")
                .withSchedule(CronScheduleBuilder.cronSchedule("25 0 * * * ?"))
                .build();
        Set<Trigger> set = new HashSet<>();
        set.add(trigger);

        jobMap.put(jobDetail, set);
    }

    private void initSubscribeHealthCheckJob() {
        JobDetail jobDetail = JobBuilder.newJob(SubscribeHealthCheckJob.class)
                .withIdentity(SubscribeHealthCheckJob.class.getName())
                .withDescription("订阅者自我健康检查")
                .storeDurably()
                .build();
        // 创建任务触发器
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(SubscribeHealthCheckJob.class.getName() + "触发器")
                .withSchedule(CronScheduleBuilder.cronSchedule("3 */30 * * * ?"))
                .build();
        Set<Trigger> set = new HashSet<>();
        set.add(trigger);

        jobMap.put(jobDetail, set);
    }
}
