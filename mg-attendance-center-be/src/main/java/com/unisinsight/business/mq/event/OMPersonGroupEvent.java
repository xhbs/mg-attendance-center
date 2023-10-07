package com.unisinsight.business.mq.event;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

/**
 * 对象管理人员变更 topic接收体
 */
@Data
public class OMPersonGroupEvent {
    private String origin;

    private String action;

    private String resourceType;

    private JSONArray data;
}
