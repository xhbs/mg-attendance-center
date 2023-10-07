package com.unisinsight.business.common.utils.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.unisinsight.business.dto.ExcelCheckResult;
import com.unisinsight.business.dto.ExportConvert;
import com.unisinsight.framework.common.exception.BaseErrorCode;
import com.unisinsight.framework.common.exception.BaseException;
import com.unisinsight.framework.common.util.date.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class ExcelUtil {

    /**
     * 导出excel
     */
    public static void export(HttpServletResponse resp, Class<? extends ExportConvert> tClass,
                              List<? extends ExportConvert> data, String excelName) {
        for (int i = 0; i < data.size(); i++) {
            data.get(i).convertToExcel(i);
        }

        resp.setContentType("application/vnd.ms-excel");
        resp.setCharacterEncoding("UTF-8");
        String time = DateUtils.format(LocalDateTime.now(), DateUtils.PATTERN_DATETIME_MINI);
        try {
            String fileName = URLEncoder.encode(excelName + "_" + time, "UTF-8");// 防止中文乱码
            resp.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            EasyExcel.write(resp.getOutputStream(), tClass)
                    .sheet("sheet0")
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())// 设置字段宽度为自动调整，不太精确
                    .doWrite(data);
        } catch (IOException e) {
            log.error("导出失败", e);
            throw BaseException.of(BaseErrorCode.DOWNLOAD_ERROR.getErrorCode(), "excel导出失败");
        }
    }

    /**
     * 导出excel
     */
    public static void exportExcel(HttpServletResponse response, Class<?> tClass, List<?> list, String excelNm) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("UTF-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode(excelNm, "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), tClass)
                    .sheet("sheet0")
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())// 设置字段宽度为自动调整，不太精确
                    .doWrite(list);
        } catch (IOException e) {
            log.error("导出失败", e);
            throw BaseException.of(BaseErrorCode.DOWNLOAD_ERROR.getErrorCode(), "excel导出失败");
        }
    }

    /**
     * 解析excel
     *
     * @param file              文件流
     * @param objectClass       解析映射对象.class
     * @param sheetNo           工作簿（sheet）序号，从0开始
     * @param headRow           头部占用行数
     * @param excelCheckManager 解析数据前后自定义接口
     * @param maxImportCount    解析最大行数
     * @return {@link ExcelCheckResult<T>}
     */
    public static <T> ExcelCheckResult<T> importExcel(MultipartFile file, Class<? extends Object> objectClass,
                                                      Integer sheetNo, Integer headRow, ExcelCheckManager<T> excelCheckManager, int maxImportCount) {
        try {
            //导入前准备操作
            if (null != excelCheckManager) {
                excelCheckManager.checkBefore();
            }
            UploadExcelListener<T> listener = new UploadExcelListener<>(maxImportCount, headRow, excelCheckManager);
            ExcelReader excelReader = EasyExcel.read(file.getInputStream(), objectClass, listener).build();
            ReadSheet readSheet = EasyExcel.readSheet(sheetNo).build();
            excelReader.read(readSheet);
            excelReader.finish();
            ExcelCheckResult<T> result = listener.getResult();
            if (null == result) {
                throw BaseException.of(BaseErrorCode.DATA_EMPTY_ERROR.getErrorCode(), "excel解析数据为空");
            }
            return result;
        } catch (IOException e) {
            log.error("excel解析失败", e);
            throw BaseException.of(BaseErrorCode.SYS_INTERNAL_ERROR.getErrorCode(), "excel解析失败");
        } catch (BaseException be) {
            log.error("excel解析", be);
            throw be;
        } catch (IllegalArgumentException e) {
            log.error("daaaaaaaaaaaaaaaaaaaaaaaaaaaaa", e);
            throw e;
        } catch (Exception e) {
            log.error("excel数据超过限制", e);
            throw BaseException.of(BaseErrorCode.FILE_SIZE_ERROR.getErrorCode(), "excel导入失败");
        }
    }
}
