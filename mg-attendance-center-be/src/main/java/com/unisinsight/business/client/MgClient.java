package com.unisinsight.business.client;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSONObject;
import com.unisinsight.business.client.req.MgMDKReqDTO;
import com.unisinsight.business.dto.PersonDTO;
import com.unisinsight.business.dto.request.CreateTabReq;
import com.unisinsight.business.dto.request.FindMdkReq;
import com.unisinsight.business.dto.request.InsertMdkReq;
import com.unisinsight.business.dto.request.TabListReq;
import com.unisinsight.business.dto.response.CreateTabRes;
import com.unisinsight.business.dto.response.FindMdkRes;
import com.unisinsight.business.dto.response.InsertMdkRes;
import com.unisinsight.business.dto.response.TabListRes;
import com.unisinsight.framework.common.exception.BaseException;
import feign.Body;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executor;

@Slf4j
@Component
public class MgClient {

    @Value("${url.person-base}")
    private String mg;
    @Value("${url.object-manage}")
    private String objectManage;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final String MDK_SAVE_STATUS_KEY = "mdk_save_success";

    //查询名单库
    public TabListRes findTabList() {
        String url = mg + "/api/mg/v3/person-base/search/identity/repository-list";
        String result = HttpRequest.post(url)
                //.header("Cookie","bss-session="+"token")
                .body(JSONObject.toJSONString(new MgMDKReqDTO())).execute().body();
        return JSONObject.parseObject(result, TabListRes.class);
    }

    /**
     * 创建名单库
     *
     * @param tabName 名单库名称
     * @return 名单库id
     */
    public String createTab(String tabName) {
        if (tabName.length() >= 20) {
            tabName = tabName.substring(0, tabName.length() - 5);
        }
        TabListRes res = selectMdk(new TabListReq(tabName));
        if (res.getData().getPaging().getTotalNum() == 0) {
            String createUrl = mg + "/api/mg/v3/person-base/manage/tab";
            CreateTabReq createTabReq = new CreateTabReq();
            createTabReq.setTabName(tabName);
            createTabReq.setIsAffirmed("1");
            createTabReq.setTabType("05");
            createTabReq.setRepoType(1);
            createTabReq.setCreator("admin");
            String createBody = HttpUtil.createPost(createUrl).body(JSONObject.toJSONString(createTabReq)).execute().body();
            CreateTabRes createTabRes = JSONObject.parseObject(createBody, CreateTabRes.class);
            if (!"0000000000".equals(createTabRes.getErrorCode())) {
                log.info("创建名单库{}失败,接口返回：{}", tabName, createBody);
                throw new BaseException("创建名单库失败！");
            }
            return createTabRes.getData().getTabId();
        } else {
            return res.getData().getData().get(0).getTabId();
        }
    }

    public TabListRes selectMdk(TabListReq req) {
        String searchUrl = mg + "/api/mg/v3/person-base/manage/tab/search";
        String body = HttpUtil.createPost(searchUrl).body(JSONObject.toJSONString(req)).execute().body();
        return JSONObject.parseObject(body, TabListRes.class);
    }

    public String insert(PersonDTO personDO, String tabId, String base64) {
//        FindMdkReq findMdkReq = new FindMdkReq();
//        findMdkReq.setIdNumber(personDO.getPersonNo());
//        findMdkReq.setTabId("53000000025035532921052021111210450726246");
//        FindMdkRes.DataDTO dataDTO = find(findMdkReq);
//        if (ObjectUtil.isNotEmpty(dataDTO) && ObjectUtil.isNotEmpty(dataDTO.getPaging()) && dataDTO.getPaging().getTotalNum() != 0) {
//            String image = dataDTO.getData().get(0).getImageUrl();
//            log.info("学生{}在名单库中已经存在,image:{}", personDO.getPersonName(), image);
//            return image;
//        }
        if (stringRedisTemplate.opsForHash().hasKey(MDK_SAVE_STATUS_KEY, personDO.getPersonNo())) {
            return (String) stringRedisTemplate.opsForHash().get(MDK_SAVE_STATUS_KEY, personDO.getPersonNo());
        }
        long time = System.currentTimeMillis();
        String url = mg + "/api/mg/v3/person-base/manage/static-face";
        InsertMdkReq req = new InsertMdkReq();
        InsertMdkReq.FaceListDTO dto = new InsertMdkReq.FaceListDTO();
        dto.setFileName(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        dto.setName(personDO.getPersonName());
        dto.setGender(0);
        dto.setCertificateType("111");
        dto.setCertificateId(personDO.getPersonNo());
        dto.setImage(base64);
        req.setFaceList(Collections.singletonList(dto));
        String image = null;

        boolean b = false;
        String message = null;

        if (StrUtil.isNotBlank(tabId)) {
            req.setTabID(tabId);
            String body = HttpUtil.createPost(url).body(JSONObject.toJSONString(req)).execute().body();
            InsertMdkRes mdkRes = JSONObject.parseObject(body, InsertMdkRes.class);
            if (!"0000000000".equals(mdkRes.getErrorCode()) || CollUtil.isEmpty(mdkRes.getData()) || CollUtil.isNotEmpty(mdkRes.getData().get(0).getFail())) {
                message = "Error:" + JSONObject.toJSONString(mdkRes);
            } else {
                log.info("学生{}插入名单库:{}成功,image:{}", personDO.getPersonName(), personDO.getTabName(), image);
                image = mdkRes.getData().get(0).getSuccess().get(0).getImageUrl();
                b = true;
            }
        } else {
            //上传到默认名单库
            req.setTabID("53000000025035532921052021111210450726246");
            String defaultMdkImage = HttpUtil.createPost(url).body(JSONObject.toJSONString(req)).execute().body();
            InsertMdkRes defaultMdkRes = JSONObject.parseObject(defaultMdkImage, InsertMdkRes.class);
            if ("0000000000".equals(defaultMdkRes.getErrorCode())) {
                image = defaultMdkRes.getData().get(0).getSuccess().get(0).getImageUrl();
                log.info("学生{}插入默认名单库成功,image:{}", personDO.getPersonName(), image);
                b = true;
            } else {
                message = "Error:" + JSONObject.toJSONString(defaultMdkRes);
                log.info("默认名单库上传失败:{},cause:{}", personDO.getPersonName(), defaultMdkImage);
            }
        }
        if (b) {
            stringRedisTemplate.opsForHash().put(MDK_SAVE_STATUS_KEY, personDO.getPersonNo(), image);
        }
        log.info("上传图片到名单库耗时：{}s", (double) (System.currentTimeMillis() - time) / 1000);
        return b ? image : message;
    }

    public FindMdkRes.DataDTO find(FindMdkReq req) {
        String url = mg + "/api/mg/v3/person-base/manage/static-face/search";
        String body = HttpUtil.createPost(url).body(JSONObject.toJSONString(req)).execute().body();
        FindMdkRes res = JSONObject.parseObject(body, FindMdkRes.class);
        return res.getData();
    }

    public void delete(String tabId) {
        String url = mg + "/api/mg/v3/person-base/manage/tab/" + tabId;
        log.info("删除:{}名单库", tabId);
        HttpUtil.createGet(url).setMethod(Method.DELETE).execute().body();
    }

    public void deleteAll() {
        TabListRes mdk = selectMdk(TabListReq.builder().pageSize(300).build());
        for (TabListRes.DataDTO.DataDTO1 dataDTO1 : mdk.getData().getData()) {
            if (dataDTO1.getTabName().contains("默认")) {
                continue;
            }
            delete(dataDTO1.getTabId());
        }
    }

    public JSONObject register(String image, String id) {
        image = image.contains("http") ? image.split("=")[1] : image;
        String url = objectManage + "/api/mg/v1/object/manage/statistics/person/update";
        HashMap<String, String> map = new HashMap<>();
        map.put("user_image", image);
        map.put("id", id);
        log.info("注册入参:{}", JSONObject.toJSONString(map));
        String body = HttpRequest.put(url).body(JSONObject.toJSONString(map)).execute().body();
        log.info("注册返回参数:{}", body);
        return JSONObject.parseObject(body);
    }
}
