package com.unisinsight.business.common.utils.excel;

public interface ExcelCheckManager<T> {
    /**
     * 解析过程中对于单条解析参数的附加校验
     *
     * @param object 解析参数
     * @return 返回参数
     */
    String checkImportExcel(T object);

    /**
     * 解析完成后续操作
     */
    void checkAfter();

    /**
     * 解析前操作
     */
    void checkBefore();

}
