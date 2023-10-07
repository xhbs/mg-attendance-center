package com.unisinsight.business.job;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.unisinsight.business.common.utils.ZipUtil;
import com.unisinsight.business.dto.OrgTreeDTO;
import com.unisinsight.business.dto.UserDetail;
import com.unisinsight.business.service.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName : mg-attendance-center
 * @Description :
 * @Author : xiehb
 * @Date: 2022/11/10 15:49
 * @Version 1.0.0
 */
@Component
@Slf4j
public class OrgJob {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private OrganizationService organizationService;


    @PostConstruct
    @Scheduled(cron = "0 0 0 1/7 * ?")
    public void job() {
        log.info("初始化加载组织树");
        organizationService.allTree();
    }
}
