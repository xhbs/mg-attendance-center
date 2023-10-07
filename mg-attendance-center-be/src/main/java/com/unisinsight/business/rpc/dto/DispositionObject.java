package com.unisinsight.business.rpc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * MDA-1400 布控
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/11/30
 * @since 1.0
 */
@Data
public class DispositionObject {
    /**
     * 布控ID
     */
    @JsonProperty("DispositionID")
    private String dispositionID;

    /**
     * 布控名称
     */
    @JsonProperty("Title")
    private String title;

    /**
     * 布控类别 1 人员 2 人脸 3 机动车 4 非机动车 5 关键字  9 特权布控
     */
    @JsonProperty("DispositionCategory")
    private String dispositionCategory;

    /**
     * 布控特征
     */
    @JsonProperty("TargetFeature")
    private String targetFeature;

    /**
     * 布控图像url
     */
    @JsonProperty("TargetImageURI")
    private String targetImageUri;

    /**
     * 优先级  1-3 1 表示最高
     */
    @JsonProperty("PriorityLevel")
    private Integer priorityLevel;

    /**
     * 申请人
     */
    @JsonProperty("ApplicantName")
    private String applicantName;

    /**
     * 申请人联系方式
     */
    @JsonProperty("ApplicantOrg")
    private String applicantOrg;

    /**
     * 申请单位
     */
    @JsonProperty("ApplicantInfo")
    private String applicantInfo;

    /**
     * 布控开始时间 传1400 date格式 格式yyyyMMddHHmmss
     */
    @JsonProperty("BeginTime")
    private String beginTime;

    /**
     * 布控结束时间 传1400 date格式 格式yyyyMMddHHmmss
     */
    @JsonProperty("EndTime")
    private String endTime;

    /**
     * 布控创建时间 传1400 date格式 格式yyyyMMddHHmmss
     */
    @JsonProperty("CreatTime")
    private String creatTime;

    /**
     * 布控操作类型 0：布控  1：撤控
     */
    @JsonProperty("OperateType")
    private Integer operateType;

    /**
     * 布控状态 0 布控中 1 已撤控 2 布控到期 9 未布控
     */
    @JsonProperty("DispositionStatus")
    private Integer dispositionStatus;

    /**
     * 布控范围 1 卡口 2 区域布控 3 设备布控 101 经纬度布控
     */
    @JsonProperty("DispositionRange")
    private String dispositionRange;

    /**
     * 布控卡口列表 卡口布控时使用
     */
    @JsonProperty("TollgateList")
    private String tollgateList;

    /**
     * 布控行政区域代码
     */
    @JsonProperty("DispositionArea")
    private String dispositionArea;

    /**
     * 布控设备列表 设备布控时使用
     */
    @JsonProperty("DeviceList")
    private String deviceList;

    /**
     * 告警信息接收地址
     */
    @JsonProperty("ReceiveAddr")
    private String receiveAddr;

    /**
     * 告警信息接收手机号码 多个以,分割
     */
    @JsonProperty("ReceiveMobile")
    private String receiveMobile;

    /**
     * 布控理由
     */
    @JsonProperty("Reason")
    private String reason;

    /**
     * 撤控单位名称
     */
    @JsonProperty("DispositionRemoveOrg")
    private String dispositionRemoveOrg;

    /**
     * 撤控人
     */
    @JsonProperty("DispositionRemovePerson")
    private String dispositionRemovePerson;

    /**
     * 传1400 date格式 格式yyyyMMddHHmmss
     */
    @JsonProperty("DispositionRemoveTime")
    private String dispositionRemoveTime;

    /**
     * 撤控原因
     */
    @JsonProperty("DispositionRemoveReason")
    private String dispositionRemoveReason;

    /**
     * 返回结果图片约定 -1 不要图片
     */
    @JsonProperty("ResultImageDeclare")
    private String resultImageDeclare;

    /**
     * 返回结果特征值约定 -1 不要特征值 1 需要返回特征值
     */
    @JsonProperty("ResultFeatureDeclare")
    private Integer resultFeatureDeclare;

    /**
     * 名单库ID
     */
    @JsonProperty("TabID")
    private String tabId;

    /**
     * 预警灵敏度
     */
    @JsonProperty("AlarmSensitivity")
    private Integer alarmSensitivity;

    /**
     * 布控模式 0 默认值 2 考勤布控
     */
    @JsonProperty("DispositionModel")
    private Integer dispositionModel;

    /**
     * 相似度阈值
     */
    @JsonProperty("Similaritydegree")
    private Float similarityDegree;
}
