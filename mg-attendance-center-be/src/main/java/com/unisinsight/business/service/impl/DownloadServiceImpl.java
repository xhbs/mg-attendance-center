package com.unisinsight.business.service.impl;

import com.unisinsight.business.service.DownloadService;
import com.unisinsight.framework.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/10/9
 */
@Service
@Slf4j
public class DownloadServiceImpl implements DownloadService {

    @Override
    public void download(String filePath, String fileName, HttpServletResponse resp) {
        InputStream is = null;
        OutputStream os = null;

        try {
            resp.setHeader("Content-disposition", "attachment;filename=" +
                    URLEncoder.encode(fileName, "UTF-8"));

            is = new ClassPathResource(filePath).getInputStream();
            os = resp.getOutputStream();

            int b = 0;
            byte[] buffer = new byte[8096];
            while (b != -1) {
                b = is.read(buffer);
                os.write(buffer);
            }
            os.flush();
        } catch (Exception e) {
            log.error("", e);
            throw new BaseException("下载失败");
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }
}
