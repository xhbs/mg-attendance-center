package com.unisinsight.business.common.utils.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.unisinsight.business.dto.ExcelCheckErrDTO;
import com.unisinsight.business.dto.ExcelCheckResult;
import com.unisinsight.framework.common.exception.BaseErrorCode;
import com.unisinsight.framework.common.exception.BaseException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 模板读取类（支持校验，返回校验成功和失败条数）
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class UploadExcelListener<T> extends AnalysisEventListener<T> {

    private int maxImportCount;

    private int headRow;

    //成功结果集
    private List<T> successList = new ArrayList<>();
    //失败结果集
    private List<ExcelCheckErrDTO<T>> errorList = new ArrayList<>();

    private Class<T> clazz;

    private ExcelCheckResult<T> result;

    //处理逻辑service
    private ExcelCheckManager<T> excelCheckManager;

    public UploadExcelListener(int maxImportCount, int headRow, ExcelCheckManager<T> excelCheckManager) {
        this.maxImportCount = maxImportCount;
        this.headRow = headRow;
        this.excelCheckManager = excelCheckManager;
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param t       解析映射结果
     * @param context 解析结果
     */
    @SneakyThrows
    @Override
    public void invoke(T t, AnalysisContext context) {
        String errMsg;
        //根据excel数据实体中的javax.validation + 正则表达式来校验excel数据
        errMsg = EasyExcelValidHelper.validateEntity(t, headRow);
        String check = "";
        if (null != excelCheckManager) {
            check = excelCheckManager.checkImportExcel(t);
        }
        errMsg += check;
        if (!StringUtils.isEmpty(errMsg)) {
            errorList.add(new ExcelCheckErrDTO<>(t, errMsg, context.readRowHolder().getRowIndex()));
        } else {
            successList.add(t);
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context 解析结果
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据解析完成！");
        Integer errNum = errorList == null ? 0 : errorList.size();
        Integer successNum = successList == null ? 0 : successList.size();
        if (errNum + successNum > maxImportCount) {
            throw BaseException.of(BaseErrorCode.FILE_SIZE_ERROR.getErrorCode(), "excel导入不能超过" + maxImportCount + "条");
        }
        if (null != excelCheckManager) {
            excelCheckManager.checkAfter();
        }
        result = new ExcelCheckResult<>(successList, errorList);
    }

    /**
     * 入excel的头部（第一行数据）数据的index,name,校验excel头部格式，必须完全匹配
     *
     * @param headMap 头部信息
     * @param context 解析结果
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        super.invokeHeadMap(headMap, context);
        if (clazz != null) {
            try {
                Map<Integer, String> indexNameMap = getIndexNameMap(clazz);
                Set<Integer> keySet = indexNameMap.keySet();
                for (Integer key : keySet) {
                    if (StringUtils.isEmpty(headMap.get(key))) {
                        throw new ExcelAnalysisException("解析excel出错，请传入正确格式的excel");
                    }
                    if (!headMap.get(key).equals(indexNameMap.get(key))) {
                        throw new ExcelAnalysisException("解析excel出错，请传入正确格式的excel");
                    }
                }
            } catch (NoSuchFieldException e) {
                log.error("解析excel出错", e);
            }
        }
    }

    /**
     * 获取注解里ExcelProperty的value，用作校验excel
     *
     * @param clazz .class
     * @return 返回体map
     * @throws {@link NoSuchFieldException}
     */
    @SuppressWarnings("unused")
    public Map<Integer, String> getIndexNameMap(Class<T> clazz) throws NoSuchFieldException {
        Map<Integer, String> result = new HashMap<>();
        Field field;
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            field = clazz.getDeclaredField(fields[i].getName());
            field.setAccessible(true);
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            if (excelProperty != null) {
                int index = excelProperty.index();
                String[] values = excelProperty.value();
                StringBuilder value = new StringBuilder();
                for (String v : values) {
                    value.append(v);
                }
                result.put(index, value.toString());
            }
        }
        return result;
    }
}
