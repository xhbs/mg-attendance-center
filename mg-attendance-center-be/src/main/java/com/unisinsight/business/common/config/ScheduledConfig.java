package com.unisinsight.business.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @desc 
 * @author  cn [cheng.nian@unisinsight.com]
 * @time    2020/9/16 11:22
 */
@Configuration
public class ScheduledConfig  implements SchedulingConfigurer {

    @Resource
    @Qualifier(value = "myThreadPoolTaskScheduler")
    private TaskScheduler taskScheduler;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 自定义的线程池
        taskRegistrar.setTaskScheduler(taskScheduler);
    }

    @Bean(name = "myThreadPoolTaskScheduler")
    public TaskScheduler getMyThreadPoolTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5);
        taskScheduler.setThreadNamePrefix("unisinsight-scheduled-");
        taskScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //调度器shutdown被调用时等待当前被调度的任务完成
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        //等待时长,先设置这么多，调试后再定
        taskScheduler.setAwaitTerminationSeconds(60);
        return taskScheduler;
    }

}
