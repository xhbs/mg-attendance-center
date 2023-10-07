package com.unisinsight.business.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unisinsight.business.rpc.dto.DispositionListObject;
import com.unisinsight.business.rpc.dto.DispositionObject;
import com.unisinsight.business.rpc.dto.ResponseStatus;
import com.unisinsight.business.dto.request.DispositionReqDTO;
import com.unisinsight.business.dto.response.ResponseStatusListObject;
import com.unisinsight.business.mapper.DispositionRecordMapper;
import com.unisinsight.business.model.DispositionRecord;
import com.unisinsight.business.rpc.CmsClient;
import com.unisinsight.business.rpc.dto.Get1400CodeResDTO;
import com.unisinsight.business.service.DispositionService;
import com.unisinsight.framework.common.exception.BaseException;
import com.unisinsight.framework.common.util.date.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 新增考勤分组后自动布控服务
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/11/30
 * @since 1.0
 */
@Slf4j
@Service
public class DispositionServiceImpl implements DispositionService {

    @Value("${service.mda.base-url}")
    private String mdaService;

    /**
     * 布控告警相似度阈值
     */
    @Value("${disposition.similarityDegree:0.7}")
    private Float dispositionSimilarityDegree;

    @Resource
    private DispositionRecordMapper dispositionRecordMapper;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private CmsClient cmsClient;

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 缓存平台1400编码
     */
    private String ga1400Code;

    @Override
    public void createDispositions(List<String> tabIds) {
        log.info("[考勤布控] - 创建布控，名单库列表: {}", tabIds);

        List<DispositionObject> dispositions = tabIds.stream()
                .map(tabId -> {
                    DispositionObject disposition = new DispositionObject();
                    disposition.setTabId(tabId);
                    disposition.setDispositionModel(2);// 2：无感考勤
                    disposition.setTitle("无感考勤 - " + tabId);
                    disposition.setDispositionCategory("2");// 1：人脸布控
                    disposition.setPriorityLevel(1);// 优先级 1：最高
                    disposition.setApplicantName("考勤系统");
                    disposition.setApplicantOrg("admin");
                    disposition.setSimilarityDegree(dispositionSimilarityDegree);// 相似度阈值
                    disposition.setOperateType(0);// 0： 布控
                    disposition.setDispositionRange("2");// 3：区域布控
                    disposition.setDispositionArea("000000");// 所有区域
                    disposition.setBeginTime(LocalDateTime.now().format(DateUtils.DATETIME_MINI_FORMATTER));// 开始：当前时间
                    disposition.setEndTime("21000101000000");// 结束：设置一个长远的时间
                    return disposition;
                }).collect(Collectors.toList());

        DispositionListObject listObject = new DispositionListObject();
        listObject.setDispositionObject(dispositions);
        DispositionReqDTO req = new DispositionReqDTO();
        req.setDispositionListObject(listObject);

        List<ResponseStatus> results = null;
        try {
            String json = objectMapper.writeValueAsString(req);
            log.info("[考勤布控] - 创建 req: {}", json);

            HttpEntity<String> entity = new HttpEntity<>(json, buildHttpHeaders());
            ResponseStatusListObject resp = restTemplate.postForObject(mdaService + "/VIID/Dispositions",
                    entity, ResponseStatusListObject.class);
            log.info("[考勤布控] - 创建 res: {}", resp);

            if (resp != null && resp.getResponseStatusObjects() != null) {
                results = resp.getResponseStatusObjects().getResponseStatusObject();
            }
        } catch (Exception e) {
            log.error("[考勤布控] - 创建失败:", e);
        }

        // 保存创建布控结果
        List<DispositionRecord> records = new ArrayList<>(tabIds.size());
        for (int i = 0; i < tabIds.size(); i++) {
            DispositionRecord record = new DispositionRecord();
            record.setTabId(tabIds.get(i));
            Date time = new Date();
            record.setCreateTime(time);
            record.setUpdateTime(time);

            // 如果创建成功则保存对应的布控ID
            if (results != null && !results.isEmpty()) {
                String id = results.get(i).getId();
                if (!StringUtils.isEmpty(id)) {
                    record.setDispositionId(id);
                }
            }
            records.add(record);
        }
        dispositionRecordMapper.insertOrUpdate(records);
    }

    @Override
    public void deleteDispositions(List<String> tabIds) {
        Example example = Example.builder(DispositionRecord.class)
                .where(Sqls.custom()
                        .andIn("tabId", tabIds))
                .build();

        // 查询需要删除的布控列表
        List<DispositionRecord> records = dispositionRecordMapper.selectByCondition(example);
        if (records.isEmpty()) {
            log.info("[考勤布控] - 未找到需要删除的布控");
            return;
        }

        // 还未创建的名单库列表
        List<String> noDispositionTabs = records.stream()
                .filter(record -> StringUtils.isEmpty(record.getDispositionId()))
                .map(DispositionRecord::getTabId)
                .collect(Collectors.toList());
        if (!noDispositionTabs.isEmpty()) {
            example = Example.builder(DispositionRecord.class)
                    .where(Sqls.custom()
                            .andIn("tabId", noDispositionTabs))
                    .build();
            dispositionRecordMapper.deleteByCondition(example);
            log.info("[考勤布控] - 删除布控，名单库 {} 还未创建布控，直接删除", noDispositionTabs);
        }

        // 需要删除的布控ID参数
        String ids = records.stream()
                .filter(record -> !StringUtils.isEmpty(record.getDispositionId()))
                .map(DispositionRecord::getDispositionId)
                .collect(Collectors.joining(";"));
        log.info("[考勤布控] - 需要删除的布控: {}", ids);

        if (StringUtils.isEmpty(ids)) {
            // 没有要删除的布控
            return;
        }

        List<ResponseStatus> results = null;
        try {
            HttpEntity<String> entity = new HttpEntity<>(null, buildHttpHeaders());
            ResponseStatusListObject resp = restTemplate.exchange(mdaService + "/VIID/Dispositions?IDList="
                    + ids, HttpMethod.DELETE, entity, ResponseStatusListObject.class).getBody();
            log.info("[考勤布控] - 删除 res: {}", resp);

            if (resp != null && resp.getResponseStatusObjects() != null) {
                results = resp.getResponseStatusObjects().getResponseStatusObject();
            }
        } catch (Exception e) {
            log.error("[考勤布控] - 删除失败:", e);
            return;
        }

        if (results == null) {
            // 删除布控失败，不更新数据库
            return;
        }

        // 删除成功的布控列表
        List<String> dispositionIds = results.stream()
                .filter(status -> "0".equals(status.getStatusCode()))
                .map(ResponseStatus::getId)
                .collect(Collectors.toList());
        if (dispositionIds.isEmpty()) {
            log.warn("[考勤布控] - 删除成功列表为空，无法删除本地记录");
            return;
        }

        // 删除本地数据库布控记录
        example = Example.builder(DispositionRecord.class)
                .where(Sqls.custom()
                        .andIn("dispositionId", dispositionIds))
                .build();
        dispositionRecordMapper.deleteByCondition(example);
    }

    /**
     * 构建MDA请求头
     */
    private HttpHeaders buildHttpHeaders() {
        if (ga1400Code == null) {
            try {
                Get1400CodeResDTO res = cmsClient.getGa1400code();
                ga1400Code = res.getGa1400code();
            } catch (Exception e) {
                log.error("获取ga1400Code失败", e);
            }
            if (ga1400Code == null) {
                throw new BaseException("未获取到ga1400Code，无法布控");
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=UTF-8");
        headers.set("User-Identify", ga1400Code);
        return headers;
    }
}
