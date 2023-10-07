package com.unisinsight.business;

import com.unisinsight.framework.common.base.Mapper;
import net.hasor.spring.boot.EnableHasor;
import net.hasor.spring.boot.EnableHasorWeb;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * description
 *
 * @author t17153 [tan.gang@h3c.com]R
 * @date 2018/09/13 21:51
 * @since 1.0
 */
@EnableHasor()
@EnableHasorWeb(path = {"/api/biz-scene/v1/attendance/dataway/*"})
@SpringBootApplication(excludeName = {"com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure"})
@MapperScan(basePackages = {"com.unisinsight.**.mapper"}, markerInterface = Mapper.class)
@EnableAsync
@EnableFeignClients
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
