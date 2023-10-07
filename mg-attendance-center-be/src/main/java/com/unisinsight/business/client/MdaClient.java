package com.unisinsight.business.client;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class MdaClient {

    @Value("${url.image-mda}")
    private String imageProvinceMda;

    /**
     * 相对路径转绝对路径
     *
     * @param urls    相对路径数组
     * @param netType 网段标识
     * @return key：相对路径，value：绝对路径
     */
    public HashMap<String, String> relativeToAbsolute(List<String> urls, Integer netType) {
        String urlString = imageProvinceMda + "/api/viid/v3/mda/addr-trans";
        String body = HttpUtil.createPost(urlString)
                .header("is_outer", netType + "")
                .body(JSONObject.toJSONString(urls))
                .execute().body();
        JSONObject res = JSONObject.parseObject(body);
        JSONArray data = res.getJSONArray("data");
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < urls.size(); i++) {
            map.put(urls.get(i), data.getString(i));
        }
        return map;
    }

    public String absoluteToBase64(String url) {
        byte[] bytes = HttpUtil.downloadBytes(url);
        return Base64.encode(bytes);
    }

    public String getRelativeBase64(String url) {
        String absoluteUrl = imageProvinceMda + "/api/viid/v3/mda/image-data?image_url=" + url;
        if (absoluteUrl.contains("http")) {
            try {
                return absoluteToBase64(absoluteUrl);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
