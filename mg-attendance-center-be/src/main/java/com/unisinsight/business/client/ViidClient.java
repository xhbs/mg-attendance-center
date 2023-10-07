package com.unisinsight.business.client;


import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.unisinsight.business.client.req.FaceTabReq;
import com.unisinsight.business.client.res.TabListRes;
import com.unisinsight.business.client.res.FaceListRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ViidClient {

    public static final String NM = "nm";
    public static final String SM = "sm";

    @Value("${url.viid-face-static}")
    private String viidFaceStatic;

    @Value("${url.viid-face-disposition}")
    private String viidFaceDisposition;

    @Value("${face-macth.similarity-threshold}")
    private Integer similarityThreshold;

    public FaceListRes selectTabByBase64(String base64, List<TabListRes.Data.Repository> repositoryList) {
        FaceTabReq faceTabReq = new FaceTabReq();
        faceTabReq.setThreshold(similarityThreshold);
        faceTabReq.setTabIds(repositoryList.stream().map(TabListRes.Data.Repository::getId).collect(Collectors.toList()));
        faceTabReq.setImage(base64);

        String result = HttpRequest.post(viidFaceStatic + "/api/viid/v3/face-static/feature-retrieval-file")
                .body(JSONObject.toJSONString(faceTabReq))
                .execute()
                .body();
        log.debug("名单库置信结果：" + result);
        return JSONObject.parseObject(result, FaceListRes.class);
    }

    public FaceListRes selectDispositionByBase64(String base64, List<TabListRes.Data.Repository> repositoryList) {
        FaceTabReq faceTabReq = new FaceTabReq();
        faceTabReq.setThreshold(similarityThreshold);
        faceTabReq.setTabIds(repositoryList.stream().map(TabListRes.Data.Repository::getId).collect(Collectors.toList()));
        faceTabReq.setImage(base64);

        String result = HttpRequest.post(viidFaceDisposition + "/api/viid/v3/disposition/feature-retrieval-file")
                .body(JSONObject.toJSONString(faceTabReq))
                .execute()
                .body();
        log.debug("名单库置信结果：" + result);
        return JSONObject.parseObject(result, FaceListRes.class);
    }


}
