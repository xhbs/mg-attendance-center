package com.unisinsight.business.rpc;

import com.unisinsight.business.common.utils.Results;
import com.unisinsight.business.rpc.dto.ImageSimilarReqDTO;
import com.unisinsight.business.rpc.dto.ImageSimilarResDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 视图库人脸布控服务接口
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/30
 * @since 1.0
 */
@FeignClient(name = "viid-face-disposition", url = "${url.viid-face-snap}", path = "/api/viid/v1/face-snap")
public interface FaceDispositionClient {

    /**
     * 图片1:1比对
     */
    @PostMapping("/file-similar")
    float calcSimilar(ImageSimilarReqDTO req);
}
