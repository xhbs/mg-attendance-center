package com.unisinsight.business.rpc;

import com.unisinsight.business.common.utils.Results;
import com.unisinsight.business.rpc.dto.OMStaticDataReqDTO;
import com.unisinsight.business.rpc.dto.OMStaticDataResDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 对象管理
 */
@FeignClient(name = "mg-om", url = "${service.mg-om.base-url}")
public interface ObjectManageClient {

    /**
     * 静态枚举类---新增
     *
     * @param reqDTO
     * @return
     */
    @PostMapping("/api/mg/v1/object/manage/for-public/dic/static-data/add")
    Results<OMStaticDataResDTO> addStaticDataList(@RequestBody OMStaticDataReqDTO reqDTO);

    /**
     * 静态枚举类---查询
     *
     * @param formCode 默认 omperson
     * @param type     默认 groupType
     * @return
     */
    @GetMapping("/api/mg/v1/object/manage/for-public/dic/static-data")
    Results<List<OMStaticDataResDTO>> getStaticDataList(@RequestParam(value = "form_code", defaultValue = "omperson") String formCode,
                                                        @RequestParam(value = "type", defaultValue = "groupType") String type);
}
