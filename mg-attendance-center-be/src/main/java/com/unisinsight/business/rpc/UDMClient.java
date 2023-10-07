package com.unisinsight.business.rpc;

import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.rpc.dto.UDMChannelDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 设备管理
 */
@FeignClient(name = "bss-udm", url = "${service.bss-udm.base-url}", path = "/api/infra-udm/v0.1")
public interface UDMClient {

    /**
     * 查询通道列表
     *
     * @param apeIdList 通道ID，多个以 , 分割
     */
    @GetMapping(path = "/channel/list", headers = {"User=usercode:admin&username:admin"})
    PaginationRes<UDMChannelDTO> getChannelList(@RequestParam("ape_id_list") String apeIdList);
}
