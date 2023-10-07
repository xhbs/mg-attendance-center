package com.unisinsight.business.common.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.unisinsight.business.mapper.OrganizationMapper;
import com.unisinsight.business.rpc.InfraClient;
import com.unisinsight.framework.common.util.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
public class UserCache {

    private static final ThreadLocal<UserStruct> STRUCT_THREAD_LOCAL = new ThreadLocal<>();

    public static UserStruct getUserStruct() {
        if (ObjectUtil.isEmpty(STRUCT_THREAD_LOCAL.get())) {
            OrganizationMapper organizationMapper = SpringUtil.getBean(OrganizationMapper.class);
            InfraClient infraClient = SpringUtil.getBean(InfraClient.class);
            User user = UserUtils.getUser();
            log.info("打印当前登录用户{}", JSONObject.toJSONString(user));
            UserStruct struct = new UserStruct(UserUtils.getUser(), organizationMapper, infraClient);
            STRUCT_THREAD_LOCAL.set(struct);
        }
        return STRUCT_THREAD_LOCAL.get();
    }

    public static UserStruct getUserStruct(User user) {
        if (ObjectUtil.isEmpty(STRUCT_THREAD_LOCAL.get())) {
            OrganizationMapper organizationMapper = SpringUtil.getBean(OrganizationMapper.class);
            InfraClient infraClient = SpringUtil.getBean(InfraClient.class);
            UserStruct struct = new UserStruct(user, organizationMapper, infraClient);
            STRUCT_THREAD_LOCAL.set(struct);
        }
        return STRUCT_THREAD_LOCAL.get();
    }

    public static void clear() {
        STRUCT_THREAD_LOCAL.remove();
    }

}
