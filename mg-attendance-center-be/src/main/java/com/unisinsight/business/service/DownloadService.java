package com.unisinsight.business.service;

import javax.servlet.http.HttpServletResponse;

/**
 * 下载 resources 目录下的文件
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/10/9
 */
public interface DownloadService {

    /**
     * 下载
     */
    void download(String filePath, String fileName, HttpServletResponse resp);
}
