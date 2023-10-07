package com.unisinsight.business.common.constants;

public final class Constants {

    /**
     * 工作机器ID(0~31)
     */
    public static final long WORKER_ID = 1L;

    /**
     * 数据中心ID(0~31)
     */
    public static final long DATA_CENTER_ID = 5L;

    /**
     * 默认的分页数量
     */
    public static final Integer DEFAULT_PAGE_NUM = 20;

    /**
     * 最大的分页数量
     */
    public static final Integer MAX_PAGE_NUM = 1000;

    /**
     * 最小的页码1
     */
    public static final Integer MIN_PAGE_NUM = 1;

    /**
     * excel最大解析行数
     */
    public static final Integer IMPORT_ROW_MAX = 50000;

    /**
     * 最大导出数据量10W
     */
    public static final int MAXIMUM_EXPORT_SIZE = 100_000;
    public static final int TRUE = 1;
    public static final int FALSE = 0;

    public static final String BASE64_PRE = "data:image/jpg;base64,";

    public Constants() {
    }
}
