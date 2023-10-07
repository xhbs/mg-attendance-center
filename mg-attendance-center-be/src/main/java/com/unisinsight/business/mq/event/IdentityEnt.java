package com.unisinsight.business.mq.event;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description TODO
 * @Date 2022/7/7
 * @Author XieHaiBo
 */
@NoArgsConstructor
@Data
public class IdentityEnt {

    @JSONField(name = "recordID")
    private Long recordID;
    @JSONField(name = "faceID")
    private String faceID;
    @JSONField(name = "deviceID")
    private String deviceID;
    @JSONField(name = "tollgateID")
    private String tollgateID;
    @JSONField(name = "passTime")
    private Long passTime;
    @JSONField(name = "longModelVersion")
    private String longModelVersion;
    @JSONField(name = "longFeature")
    private String longFeature;
    @JSONField(name = "shortModelVersion")
    private String shortModelVersion;
    @JSONField(name = "default_alg")
    private Boolean defaultAlg;
    @JSONField(name = "shortFeature")
    private String shortFeature;
    @JSONField(name = "leftTopX")
    private Double leftTopX;
    @JSONField(name = "leftTopY")
    private Double leftTopY;
    @JSONField(name = "rightBtmX")
    private Double rightBtmX;
    @JSONField(name = "rightBtmY")
    private Double rightBtmY;
    @JSONField(name = "imageUrlPart")
    private String imageUrlPart;
    @JSONField(name = "imageUrlFull")
    private String imageUrlFull;
    @JSONField(name = "isDriver")
    private Integer isDriver;
    @JSONField(name = "imageReliability")
    private Integer imageReliability;
    @JSONField(name = "sourceLevel")
    private Integer sourceLevel;
    @JSONField(name = "RecommendedClassification")
    private Integer recommendedClassification;
    @JSONField(name = "SourceID")
    private String sourceID;
    @JSONField(name = "ImageIdPart")
    private String imageIdPart;
    @JSONField(name = "ImageIdFull")
    private String imageIdFull;
    @JSONField(name = "LinkFaceVehicleID")
    private String linkFaceVehicleID;
    @JSONField(name = "IsSuspectedTerrorist")
    private Integer isSuspectedTerrorist;
    @JSONField(name = "IsCriminalInvolved")
    private Integer isCriminalInvolved;
    @JSONField(name = "IsVictim")
    private Integer isVictim;
    @JSONField(name = "IsSuspiciousPerson")
    private Integer isSuspiciousPerson;
    @JSONField(name = "IsForeigner")
    private Integer isForeigner;
    @JSONField(name = "InfoKind")
    private Integer infoKind;
    @JSONField(name = "SubImageInfoList")
    private List<SubImageInfoListDTO> subImageInfoList;
    @JSONField(name = "origin")
    private Integer origin;
    @JSONField(name = "standardData")
    private Boolean standardData;
    @JSONField(name = "IpcLinkFaceVehicleID")
    private String ipcLinkFaceVehicleID;
    @JSONField(name = "domainId")
    private String domainId;

    @NoArgsConstructor
    @Data
    public static class SubImageInfoListDTO {
        @JSONField(name = "ImageID")
        private String imageID;
        @JSONField(name = "DeviceID")
        private String deviceID;
        @JSONField(name = "StoragePath")
        private String storagePath;
        @JSONField(name = "Type")
        private String type;
        @JSONField(name = "FileFormat")
        private String fileFormat;
        @JSONField(name = "ShotTime")
        private String shotTime;
    }
}
