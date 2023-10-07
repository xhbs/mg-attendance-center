package com.unisinsight.business.mq.event;

import com.unisinsight.business.rpc.dto.OMPersonDTO;
import lombok.Data;

import java.util.List;

/**
 * 对象管理人员分组变更事件
 */
@Data
public class OMPersonEvent {

    private String origin;

    private String action;

    private String resourceType;

    private List<OMPersonDTO> data;
}
