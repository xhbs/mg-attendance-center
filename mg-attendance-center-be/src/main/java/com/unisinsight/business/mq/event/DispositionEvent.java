package com.unisinsight.business.mq.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 布控告警事件
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/2/3
 * @since 1.0
 */
@Data
public class DispositionEvent {
    // /**
    //  * 该布控告警标识符
    //  */
    // @JsonProperty("notificationId")
    // private String notificationId;

    /**
     * 布控标识
     */
    @JsonProperty("dispositionId")
    private String dispositionId;
    //
    // /**
    //  * 描述布控的主题和目标
    //  */
    // private String title;
    //
    // /**
    //  * 触发时间
    //  */
    // @JsonProperty("triggerTime")
    // private Long triggerTime;
    //
    // /**
    //  * 目标人脸recordId
    //  */
    // @JsonProperty("dispositionTargetId")
    // private Long dispositionTargetId;

    // /**
    //  * 匹配相似度
    //  */
    // private Integer similarity;

    // /**
    //  * 算法类型
    //  */
    // @JsonProperty("modelVersion")
    // private Long modelVersion;
    //
    // /**
    //  * 特征值
    //  */
    // @JsonProperty("longFeature")
    // private String longFeature;
    //
    // /**
    //  * 黑名单库id
    //  */
    // @JsonProperty("tabId")
    // private String tabId;
    //
    // /**
    //  * 是否本级数据（额外字段） 1、本级数据；2、非本级数据
    //  */
    // @JsonProperty("sourceLevel")
    // private Integer sourceLevel;

    /**
     * 抓拍的人脸的faceId
     */
    @JsonProperty("faceId")
    private String faceId;

    // /**
    //  * 布控人脸库中对应的人脸faceId
    //  */
    // @JsonProperty("targetFaceId")
    // private String targetFaceId;

    /**
     * 设备id
     */
    @JsonProperty("deviceId")
    private String deviceId;
    //
    // /**
    //  * 关联卡口编号
    //  */
    // @JsonProperty("tollgateId")
    // private String tollgateId;
    //
    // /**
    //  * 小图在全景图中左上角横坐标
    //  */
    // @JsonProperty("leftTopX")
    // private Double leftTopX;
    //
    // /**
    //  * 小图在全景图中左上角纵坐标
    //  */
    // @JsonProperty("leftTopY")
    // private Double leftTopY;
    //
    // /**
    //  * 小图在全景图中右下角横坐标
    //  */
    // @JsonProperty("rightBtmX")
    // private Double rightBtmX;
    //
    // /**
    //  * 小图在全景图中右下角纵坐标
    //  */
    // @JsonProperty("rightBtmY")
    // private Double rightBtmY;

    /**
     * 小图url
     */
    @JsonProperty("imageUrlPart")
    private String imageUrlPart;

    // /**
    //  * 全景图url
    //  */
    // @JsonProperty("imageUrlFull")
    // private String imageUrlFull;

    /**
     * 抓拍时间
     */
    @JsonProperty("passTime")
    private Long passTime;
    //
    // /**
    //  * 性别编码
    //  */
    // @JsonProperty("genderCode")
    // private Integer genderCode;
    //
    // /**
    //  * 年龄上限
    //  */
    // @JsonProperty("ageUpLimit")
    // private Integer ageUpLimit;
    //
    // /**
    //  * 年龄下限
    //  */
    // @JsonProperty("ageLowerLimit")
    // private Integer ageLowerLimit;
    //
    // /**
    //  * 民族代码
    //  */
    // @JsonProperty("ethicCode")
    // private String ethicCode;
    //
    // /**
    //  * 图片url
    //  */
    // @JsonProperty("targetImageUri")
    // private String targetImageUri;

    // /**
    //  * 证件种类，见附录
    //  */
    // @JsonProperty("idType")
    // private Integer idType;

    /**
     * 证件号（2021/06/02 改为了学号）
     */
    @JsonProperty("idNumber")
    private String idNumber;

    // /**
    //  * 姓名
    //  */
    // private String name;

    // /**
    //  * 出生日期
    //  */
    // @JsonProperty("bornDate")
    // private Date bornDate;
    //
    // /**
    //  * 国籍代码，见附录
    //  */
    // @JsonProperty("nationalityCode")
    // private Integer nationalityCode;
    //
    // /**
    //  * 籍贯省市县代码，见附录
    //  */
    // @JsonProperty("nativeCityCode")
    // private Integer nativeCityCode;
    //
    // /**
    //  * 居住地行政区划，见附录
    //  */
    // @JsonProperty("residenceAdminDiVision")
    // private Integer residenceAdminDiVision;

    // /**
    //  * 告警类型，1黑名单；3白名单
    //  */
    // @JsonProperty("listType")
    // private Integer listType;
    //
    // /**
    //  * 来源标识，来源图片标识
    //  */
    // @JsonProperty("sourceId")
    // private String sourceId;

    // /**
    //  * 信息分类，人工采集还是自动采集
    //  */
    // @JsonProperty("infoKind")
    // private Integer infoKind;
    //
    // /**
    //  * 告警类型（固定5为人脸告警）
    //  */
    // @JsonProperty("notificationType")
    // private String notificationType;

}
