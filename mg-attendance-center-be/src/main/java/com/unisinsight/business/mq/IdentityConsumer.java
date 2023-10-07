package com.unisinsight.business.mq;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.unisinsight.business.client.MdaClient;
import com.unisinsight.business.client.MgClient;
import com.unisinsight.business.client.ViidClient;
import com.unisinsight.business.client.res.FaceListRes;
import com.unisinsight.business.common.enums.OriginalRecordSource;
import com.unisinsight.business.common.utils.IdGenerateUtil;
import com.unisinsight.business.model.OriginalRecordDO;
import com.unisinsight.business.mq.event.IdentityEnt;
import com.unisinsight.business.service.AttendanceEventFilter;
import com.unisinsight.business.service.OriginalRecordService;
import com.unisinsight.framework.common.util.date.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @Description TODO
 * @Date 2022/7/7
 * @Author XieHaiBo
 */
@Component
@Slf4j
public class IdentityConsumer {

    @Resource
    private MdaClient mdaClient;
    @Resource
    private ViidClient viidClient;
    @Resource
    private MgClient mgClient;
    @Resource
    private OriginalRecordService originalRecordService;
    @Resource
    private AttendanceEventFilter attendanceEventFilter;
    @Resource
    private Executor identityExecutor;


    //{"recordID":217976019777755,"faceID":"530000000511910000090220220913072413017120601712","deviceID":"53000000051191000009","tollgateID":"53000000051191000009","passTime":1663025053000,"longModelVersion":"0","longFeature":"","shortModelVersion":"0","shortFeature":"","leftTopX":1400.0,"leftTopY":682.0,"rightBtmX":1496.0,"rightBtmY":762.0,"imageUrlPart":"nfs001/10//sfc/namelists/20220913/780929322390917","imageUrlFull":"nfs001/1//sfc/scenefaces/20220913/217976019808738?deviceId=53000000051191000009","hairStyle":"99","isRespirator":1,"respiratorColor":99,"isCap":2,"capStyle":"99","capColor":99,"isGlasses":2,"glassesStyleType":"99","glassColor":99,"isDriver":2,"sourceLevel":1,"RecommendedClassification":0,"SourceID":"53000000051191000009022022091307241301712","ImageIdPart":"53000000051191000009022022091307241311712","ImageIdFull":"53000000051191000009022022091307241314712","IsSuspectedTerrorist":2,"IsCriminalInvolved":2,"IsDetainees":2,"IsVictim":2,"IsSuspiciousPerson":2,"IsForeigner":2,"InfoKind":1,"SubImageInfoList":[{"ImageID":"53000000051191000009022022091307241311712","EventSort":9,"DeviceID":"53000000051191000009","StoragePath":"nfs001/10//sfc/namelists/20220913/780929322390917","Type":"11","FileFormat":"Jpeg","ShotTime":"20220913072413","Width":112,"Height":96},{"ImageID":"53000000051191000009022022091307241314712","EventSort":9,"DeviceID":"53000000051191000009","StoragePath":"nfs001/1//sfc/scenefaces/20220913/217976019808738?deviceId=53000000051191000009","Type":"14","FileFormat":"Jpeg","ShotTime":"20220913072413","Width":2560,"Height":1440}]}
    @KafkaListener(topics = "viid_snapface_topic", containerFactory = "identityBatchFactory")
    public void consumer(List<ConsumerRecord<String, String>> recordList) throws InterruptedException {
        List<IdentityEnt> identityEnts = recordList.stream()
                .map(e -> JSONObject.parseObject(e.value(), IdentityEnt.class))
                .collect(Collectors.toList());

        List<CompletableFuture<OriginalRecordDO>> futures = identityEnts.stream()
                .map(e -> CompletableFuture.supplyAsync(() -> identity(e), identityExecutor))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<OriginalRecordDO> originalRecords = futures.stream().map(e -> {
            try {
                return e.get();
            } catch (Exception ex) {
                log.error("图片置信报错", ex);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());

        if (CollUtil.isNotEmpty(originalRecords)) {
            originalRecordService.batchSave(originalRecords);
        }
    }

    private OriginalRecordDO identity(IdentityEnt identityEnt) {

        String base64 = mdaClient.getRelativeBase64(identityEnt.getImageUrlPart());
        if (StrUtil.isBlank(base64)) {
            return null;
        }
        FaceListRes mdkRes = viidClient.selectDispositionByBase64(base64, new ArrayList<>());
        if (mdkRes.getData().getCount() == 0) {
            return null;
        }
        List<FaceListRes.DataDTO.DataDTO1> data = mdkRes.getData().getData();
        //根据名单库标签分组
        Map<String, List<FaceListRes.DataDTO.DataDTO1>> listMap = data.stream().collect(Collectors.groupingBy(FaceListRes.DataDTO.DataDTO1::getTabName));
        List<FaceListRes.DataDTO.DataDTO1> saveList = new ArrayList<>();
        //根据相似度排序
        for (Map.Entry<String, List<FaceListRes.DataDTO.DataDTO1>> entry : listMap.entrySet()) {
            List<FaceListRes.DataDTO.DataDTO1> value = entry.getValue();
            FaceListRes.DataDTO.DataDTO1 dto1 = value.stream().max(Comparator.comparingDouble(v -> v.getSimilarity() == null ? 0 : v.getSimilarity())).get();
            saveList.add(dto1);
        }

        if (saveList.stream().map(FaceListRes.DataDTO.DataDTO1::getIdNumber).distinct().count() > 1) {
            //身份证不相同,取名单库中相似度最高的
            saveList.sort(Comparator.comparingDouble(v -> v.getSimilarity() == null ? 0 : v.getSimilarity()));
            saveList = CollUtil.reverse(saveList);
        }
        saveList = saveList.subList(0, 1);
        FaceListRes.DataDTO.DataDTO1 data1 = saveList.get(0);
        if (attendanceEventFilter.checkAttendanceEvent(data1.getIdNumber(), identityEnt.getDeviceID(), identityEnt.getPassTime())) {
            return genOriginData(identityEnt, data1);
        }
        return null;
    }

    /**
     * 根据布控告警生成原始数据
     */
    private OriginalRecordDO genOriginData(IdentityEnt ent, FaceListRes.DataDTO.DataDTO1 identity) {
        return genOriginData(ent, identity, OriginalRecordSource.IDENTITY.getValue());
    }

    /**
     * 根据布控告警生成原始数据
     */
    private OriginalRecordDO genOriginData(IdentityEnt ent, FaceListRes.DataDTO.DataDTO1 identity, Short type) {

        OriginalRecordDO data = new OriginalRecordDO();
        data.setId(IdGenerateUtil.getId());
        data.setPersonNo(identity.getIdNumber());// 身份证
        data.setCaptureImage(ent.getImageUrlPart());
        data.setDeviceCode(ent.getDeviceID());
        data.setPassTime(DateUtils.fromMilliseconds(ent.getPassTime()));
        data.setSource(type);
        data.setCreateTime(LocalDateTime.now());
        return data;
    }
}
