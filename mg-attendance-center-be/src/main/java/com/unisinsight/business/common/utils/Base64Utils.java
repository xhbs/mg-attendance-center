
package com.unisinsight.business.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 修订记录：
 * jiangbo 2020/4/26 15:50 创建
 *
 * @author jiangbo
 */
@Slf4j
public class Base64Utils {

    /**
     * 根据图片URL得到Base64编码
     */
    public static String imageUrlToBase64(String imageUrl) {
        InputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);

            is = conn.getInputStream();
            os = new ByteArrayOutputStream();

            byte[] buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            is.close();
            byte[] data = os.toByteArray();
            return Base64.encodeBase64String(data);
        } catch (IOException e) {
            log.error("转换图片为base64失败", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
