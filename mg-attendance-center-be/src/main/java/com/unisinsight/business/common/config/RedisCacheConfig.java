package com.unisinsight.business.common.config;

import com.unisinsight.business.service.impl.StatisticsServiceImpl;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @ClassName : mg-attendance-center
 * @Description : redis缓存配置
 * @Author : xiehb
 * @Date: 2022/11/09 14:29
 * @Version 1.0.0
 */
//@Component
public class RedisCacheConfig extends CachingConfigurerSupport {


}
