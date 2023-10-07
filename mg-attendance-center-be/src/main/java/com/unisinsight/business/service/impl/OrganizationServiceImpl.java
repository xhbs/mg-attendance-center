package com.unisinsight.business.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.unisinsight.business.bo.UserClassMappingBO;
import com.unisinsight.business.common.utils.ZipUtil;
import com.unisinsight.business.dto.OrgTreeDTO;
import com.unisinsight.business.dto.UserDetail;
import com.unisinsight.business.job.OrgJob;
import com.unisinsight.business.mapper.OrganizationMapper;
import com.unisinsight.business.model.OrganizationDO;
import com.unisinsight.business.model.UserInfoDO;
import com.unisinsight.business.service.OrganizationService;
import com.unisinsight.business.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 人员组织服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/9/15
 */
@Service
@Slf4j
public class OrganizationServiceImpl implements OrganizationService {

    @Value("${dbServer.ip}")
    private String dbServerIp;

    @Value("${dbServer.port}")
    private String dbServerPort;

    @Value("${spring.datasource.username}")
    private String dbUserName;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Resource
    private OrganizationMapper organizationMapper;
    @Resource
    private JdbcTemplate uuvJdbcTemplate;
    @Resource
    private StatisticsService statisticsService;
    @Resource
    private RedisTemplate redisTemplate;

    public static final String KEY = "orgTree";

    @Override
    public void sync() {
        organizationMapper.sync(dbServerIp, dbServerPort, dbUserName, dbPassword);
    }

    @Override
    public List<UserClassMappingBO> getMappingUser(List<String> orgIndexes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < orgIndexes.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("''").append(orgIndexes.get(i)).append("''");
        }
        return organizationMapper.getMappingUser(dbServerIp, dbServerPort, dbUserName, dbPassword, sb.toString());
    }

    @Override
    public UserInfoDO getUserInfoByMobile(String mobile) {
        String sql = "SELECT *\n" +
                "FROM infra.uuv.user_info u\n" +
                "WHERE u.deleted = 0\n" +
                "  and md5(u.user_code) = " + "'" + mobile + "'";
        return uuvJdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(UserInfoDO.class));
    }

    @Override
    public OrgTreeDTO tree() {
        UserDetail user = statisticsService.getUser();
        log.info("获取组织树,用户信息:{}", user);
        if (user.getUserCode().equals("admin") || user.getRoles().stream().anyMatch(v -> v.getRoleName().contains(StatisticsServiceImpl.PROVINCE_STR))) {
            List<String> values = redisValues(KEY);
            List<OrgTreeDTO> childrens = values.stream().map(v -> {
                String uncompress = ZipUtil.uncompress(v);
                return JSONObject.parseObject(uncompress, OrgTreeDTO.class);
            }).collect(Collectors.toList());
            OrgTreeDTO main = childrens.stream().filter(v -> v.getSubType() == 1).findFirst().get();
            main.setChildrens(childrens.stream().filter(v -> v.getSubType() != 1).sorted(Comparator.comparing(v -> v.getOrgIndex())).collect(Collectors.toList()));
            return main;
        } else if (user.getRoles().stream().anyMatch(v -> v.getRoleName().contains(StatisticsServiceImpl.CITY_STR))) {
            String city = (String) redisTemplate.opsForHash().get(KEY, user.getOrgIndex());
            OrgTreeDTO treeDTO = JSONObject.parseObject(ZipUtil.uncompress(city), OrgTreeDTO.class);
            treeDTO.getChildrens().sort(Comparator.comparing(v -> v.getOrgIndex()));
            return treeDTO;
        } else {
            OrganizationDO organizationDO = organizationMapper.selectByOrgIndex(user.getOrgIndex());
            OrgTreeDTO treeObj = getOrgTreeObj(organizationDO);
            return treeObj;
        }
    }

    private List<String> redisValues(String hKey) {
        Cursor cursor = redisTemplate.opsForHash().scan(KEY, ScanOptions.scanOptions().build());
        List<String> values = new ArrayList<>();
        while (cursor.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry) cursor.next();
            values.add(entry.getValue());
        }
        return values;
    }

    @Override
    public OrgTreeDTO allTree() {
        OrgTreeDTO treeObj;
        List<OrganizationDO> list = organizationMapper.selectBySubType(1);
        treeObj = getOrgTreeObj(list.get(0));
        getChildren(treeObj);
        return treeObj;
    }

    private void getChildren(OrgTreeDTO parent) {
        if (parent.getSubType() >= 4) {
            return;
        }
        List<OrganizationDO> children = organizationMapper.getChildrenByParent(parent.getOrgIndex());

        List<OrgTreeDTO> childrens = children.stream().map(v -> {
            OrgTreeDTO child = new OrgTreeDTO();
            child.setOrgIndex(v.getOrgIndex());
            child.setOrgName(v.getOrgName());
            child.setSubType(Integer.valueOf(v.getSubType()));
            return child;
        }).sorted(Comparator.comparing(OrgTreeDTO::getOrgIndex)).collect(Collectors.toList());
        childrens.parallelStream().forEach(this::getChildren);

        //缓存最父级对象
        if (parent.getSubType() == 1) {
            redisTemplate.opsForHash().put(KEY, "province", ZipUtil.compress(JSONObject.toJSONString(parent)));
        }
        parent.setChildrens(childrens);
        //缓存子级对象
        if (parent.getSubType() == 2) {
            redisTemplate.opsForHash().put(KEY, parent.getOrgIndex(), ZipUtil.compress(JSONObject.toJSONString(parent)));
        }
    }

    private OrgTreeDTO getOrgTreeObj(OrganizationDO organizationDO) {
        OrgTreeDTO orgTreeDTO = new OrgTreeDTO();
        orgTreeDTO.setOrgIndex(organizationDO.getOrgIndex());
        orgTreeDTO.setOrgName(organizationDO.getOrgName());
        orgTreeDTO.setSubType(Integer.valueOf(organizationDO.getSubType()));
        return orgTreeDTO;
    }

}
