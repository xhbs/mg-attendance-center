package com.unisinsight.business.mq.event;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * uuv kafka 消息事件
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/24
 * @since 1.0
 */
@Setter
@Getter
public class UUVEvent {
    /**
     * 操作类型add/update/delete/
     */
    private String action;
    /**
     * 0 设备 1 人员
     */
    private String type;

    private List<Data> data;

    @Setter
    @Getter
    public static class Data {
        private int deleted;
        private String indexPath;
        private int orgId;
        private String orgIndex;
        private String orgName;
        private String orgParentIndex;
        private int parentId;
        private short subType;
        private short isVirtual;
    }
}
