package com.unisinsight.business.client.jinshan;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.File;

@Slf4j
public class UploadUtlis {

    public static String url = "https://sso.ynjy.cn/1538820424547176449/";

    public static void main(String[] args) {
        String s = "https://highlight-video.cdn.bcebos.com/video/6s/990b4cd8-447a-11ee-a2ea-6c92bf3a2c89.mp4";
        Boolean upload = upload(s);
    }

    public static TokenRes getTopken() {
        String tokenUrl = url + "/gettoken1";
        String res = HttpUtil.get(tokenUrl);
        return JSONObject.parseObject(res, TokenRes.class);
    }

//    "https://sso.ynjy.cn/1538820424547176449/upload1?at=0897d47c4a1fae16d8960958fc06a76c&gid=y2OqMDE&parent_id=5xNV8MDE&ct=baa24491c46fa125805fcd2edcd29fcb"

    public static Boolean upload(String fileUrl) {
        TokenRes token = getTopken();
        String uploadUrl = url + "/upload1";

        String fileName = FileUtil.getName(fileUrl);
        String filePath = "/temp/" + fileName;
        HttpUtil.downloadFile(fileUrl, filePath);

        // 文件上传
        File file = FileUtil.file(filePath);
        HttpResponse response = HttpRequest.post(uploadUrl)
                .form("at", token.getAccessToken())
                .form("ct", token.getCompanyToken())
                .form("gid", "y2OqMDE")
                .form("parent_id", "5xNV8MDE")
                .form("file", file)
                .execute();
        FileUtil.del(file);
        if (response.getStatus() == HttpStatus.OK.value()) {
            return true;
        } else {
            log.info("金山上传报错：{}",response.body());
            return false;
        }
    }

}
