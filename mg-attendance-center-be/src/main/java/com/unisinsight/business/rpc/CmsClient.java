package com.unisinsight.business.rpc;

import com.unisinsight.business.rpc.dto.Get1400CodeResDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * cms
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/8/21
 */
@FeignClient(name = "cms", url = "${service.cms.base-url}", path = "/api/cms/v1")
public interface CmsClient {

    /**
     * 获取GA1400编码
     */
    @GetMapping(path = "/metadata/ga1400code", headers = {"System=biz-scene-ats"})
    Get1400CodeResDTO getGa1400code();
}
