package com.unisinsight.business.common.utils;

import cn.hutool.core.util.ArrayUtil;
import com.seeta.sdk.*;
import com.seeta.sdk.util.LoadNativeCore;
import com.seeta.sdk.util.SeetafaceUtil;
import com.unisinsight.framework.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SeetaUtil {

    @Value("${seeta.prePath}")
    private String seetaModelPrePath;
    @Value("${seeta.detector_cstas}")
    private String detector_cstas;
    @Value("${seeta.landmarker_cstas}")
    private String landmarker_cstas;
    @Value("${seeta.fas}")
    private String fas;

    static {
        LoadNativeCore.LOAD_NATIVE(SeetaDevice.SEETA_DEVICE_AUTO);
    }

    public boolean liveDetection(MultipartFile file) throws Exception {
        return liveDetection(file.getInputStream());
    }

    /**
     * 活体检测
     *
     * @throws Exception
     */
    public boolean liveDetection(InputStream inputStream) throws Exception {
        log.info("开始活体检测");
        SeetaImageData image = SeetafaceUtil.toSeetaImageData(ImageIO.read(inputStream));
        FaceDetector detector = new FaceDetector(
                new SeetaModelSetting(0, str2Arr(detector_cstas), SeetaDevice.SEETA_DEVICE_AUTO));
        //关键点定位器face_landmarker_pts5 就是五个关键点，face_landmarker_pts68就是68个关键点，根据模型文件来的
        FaceLandmarker faceLandmarker = new FaceLandmarker(
                new SeetaModelSetting(0, str2Arr(landmarker_cstas), SeetaDevice.SEETA_DEVICE_AUTO));
        //攻击人脸检测器
        FaceAntiSpoofing faceAntiSpoofing = new FaceAntiSpoofing(
                new SeetaModelSetting(0, str2Arr(fas), SeetaDevice.SEETA_DEVICE_AUTO));
        SeetaRect[] detects = detector.Detect(image);
        if (detects.length > 1) {
            throw new BaseException("检测到多张人脸");
        }
        if (ArrayUtil.isEmpty(detects)) {
            throw new BaseException("未检测到人脸");
        }
        SeetaRect seetaRect = detects[0];
        SeetaPointF[] pointFS = new SeetaPointF[5];
        int[] ints = new int[5];
        faceLandmarker.mark(image, seetaRect, pointFS, ints);
        FaceAntiSpoofing.Status predict = faceAntiSpoofing.Predict(image, seetaRect, pointFS);
        log.info("活体检测结果:{}", predict);
        //输出
        return predict.equals(FaceAntiSpoofing.Status.REAL);

    }

    private String[] str2Arr(String pathStr) {
        List<String> list = Arrays.asList(pathStr.split(","));
        return list.stream().map(v -> seetaModelPrePath + v).collect(Collectors.toList()).toArray(new String[]{});
    }

}
