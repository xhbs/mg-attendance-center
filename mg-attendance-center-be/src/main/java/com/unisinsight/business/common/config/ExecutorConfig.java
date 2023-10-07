package com.unisinsight.business.common.config;

import com.zaxxer.hikari.HikariDataSource;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName : mg-attendance-center-be
 * @Description : 线程池配置类
 * @Author : xiehb
 * @Date: 2022/07/27 09:13
 * @Version 1.0.0
 */
@Configuration
public class ExecutorConfig {

    @Value("${uuv-datasource.driverClassName}")
    private String driverClassName;

    @Value("${uuv-datasource.jdbcUrl}")
    private String jdbcUrl;

    @Value("${uuv-datasource.username}")
    private String username;

    @Value("${uuv-datasource.password}")
    private String password;


    @Value("${ziguang.mysql.driverClassName}")
    private String ziguangDriverClassName;

    @Value("${ziguang.mysql.jdbcUrl}")
    private String ziguangJdbcUrl;

    @Value("${ziguang.mysql.username}")
    private String ziguangUsername;

    @Value("${ziguang.mysql.password}")
    private String ziguangPassword;

    /**
     * 考勤身份置信线程池
     *
     * @return
     */
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(99999);
        executor.setThreadNamePrefix("task-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 考勤身份置信线程池
     *
     * @return
     */
    @Bean
    public Executor identityExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(99999);
        executor.setThreadNamePrefix("identity-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 统计线程池
     *
     * @return
     */
    @Bean
    public Executor statisticsExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(99999);
        executor.setThreadNamePrefix("statistic-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 统计线程池
     *
     * @return
     */
    @Bean
    public Executor zizhuExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(99999);
        executor.setThreadNamePrefix("zizhu-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 统计 定时任务线程池
     *
     * @return
     */
    @Bean
    public Executor workExecotor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(99999);
        executor.setThreadNamePrefix("work-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean
    JdbcTemplate uuvJdbcTemplate() {
        HikariDataSource dataSourceUuv = new HikariDataSource();
        dataSourceUuv.setJdbcUrl(jdbcUrl);
        dataSourceUuv.setDriverClassName(driverClassName);
        dataSourceUuv.setUsername(username);
        dataSourceUuv.setPassword(password);
        return new JdbcTemplate(dataSourceUuv);
    }

    @Bean
    JdbcTemplate ziguangMysqlJdbcTemplate() {
        HikariDataSource dataSourceUuv = new HikariDataSource();
        dataSourceUuv.setJdbcUrl(ziguangJdbcUrl);
        dataSourceUuv.setDriverClassName(ziguangDriverClassName);
        dataSourceUuv.setUsername(ziguangUsername);
        dataSourceUuv.setPassword(ziguangPassword);
        return new JdbcTemplate(dataSourceUuv);
    }

}
