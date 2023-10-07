package com.unisinsight.business.common.utils.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import java.io.OutputStream;
import java.util.*;

/**
 * 从数据库流式查询然后导出到excel
 *
 * @author WangYi [wangyi01@unisinsight.com]
 * @date 2020/7/29
 */
@Slf4j
public class ExcelExportHelper<T> implements ResultHandler<T> {

    /**
     * 内存缓冲区尺寸
     */
    private static final int BUFFER_SIZE = 1_000;

    /**
     * 每个sheet最大1万行
     */
    private static final int MAX_SHEET_SIZE = 10_000;

    /**
     * 表头信息
     */
    private List<Column<T>> columns;

    /**
     * 已读取行数
     */
    private int index;

    /**
     * 导出总数
     */
    private int total;

    /**
     * 当前sheet编号
     */
    private int currentSheetNo;

    /**
     * 当前sheet已行数
     */
    private int currentSheetSize;

    /**
     * 缓冲区 达到一定数量后写入输出流
     */
    private List<List<Object>> buffer;

    /**
     * 列宽
     */
    private Map<Integer, Integer> columnWidthMap;

    /**
     * 写入的sheet
     */
    private WriteSheet sheet;

    /**
     * 写入磁盘
     */
    private ExcelWriter excelWriter;

    /**
     * 导出进度回调
     */
    private ProgressListener progressListener;

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public ExcelExportHelper(List<Column<T>> columns, int total, OutputStream os) {
        this.columns = columns;
        this.total = total;

        List<List<String>> header = new ArrayList<>(columns.size());
        columnWidthMap = new HashMap<>(columns.size());
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            // 设置列名
            header.add(Collections.singletonList(column.getName()));
            // 设置列宽
            columnWidthMap.put(i, column.getWidth());
        }

        index = 0;
        buffer = new ArrayList<>(128);

        excelWriter = EasyExcel.write(os).head(header).build();
    }

    @Override
    public void handleResult(ResultContext<? extends T> context) {
        index++;
        currentSheetSize++;

        T row = context.getResultObject();
        List<Object> data = new ArrayList<>(columns.size());
        for (Column<T> column : columns) {
            data.add(column.getMapper().convert(index, row));
        }
        buffer.add(data);

        if (buffer.size() >= BUFFER_SIZE) {
            write();
        }
    }

    private WriteSheet newSheet() {
        sheet = new WriteSheet();
        sheet.setColumnWidthMap(columnWidthMap);
        sheet.setSheetName("Sheet" + (++currentSheetNo));
        currentSheetSize = 0;
        return sheet;
    }

    private void write() {
        if (sheet == null || currentSheetSize >= MAX_SHEET_SIZE) {
            sheet = newSheet();
        }

        excelWriter.write(buffer, sheet);
        progressListener.update(((float) index * 100) / total);

        log.info("[Excel导出] - 缓冲区: {}, 写入一次, 已读行数: {}", buffer.size(), index);
        buffer.clear();
    }

    /**
     * 从DB读取完成之后需要手动调用该方法
     */
    public void done() {
        if (buffer.size() > 0) {
            write();
        }

        excelWriter.finish();

        index = 0;
        currentSheetNo = 0;
        currentSheetSize = 0;
        excelWriter = null;
        sheet = null;
        progressListener = null;

        System.gc();

        log.info("[Excel导出] - 导出完成");
    }
}