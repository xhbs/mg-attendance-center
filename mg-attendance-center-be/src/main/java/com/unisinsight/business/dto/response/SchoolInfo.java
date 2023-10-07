package com.unisinsight.business.dto.response;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @author zhangmengfei [yf_zhang.mengfei@unisinsight.com]
 * @description 学校信息
 * @date 2021/1/5 15:36
 * @sine 1.0
 */
@Data
public class SchoolInfo {

    /**
     * 学校标识
     */
    private String id;

    /**
     * 学校名称
     */
    private String xxmc;

    /**
     * 所属行政区划编码
     */
    private String sszgljyxzdm;

    private String zt;

    private String xjzt;

    /**
     * 更新时间
     */
    private Timestamp updateTime;
}
