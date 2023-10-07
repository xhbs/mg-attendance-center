package com.unisinsight.business.common.utils.excel;

import com.alibaba.excel.annotation.ExcelProperty;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * 使用excel可以使用常规的注解进行校验
 */
public class EasyExcelValidHelper {

    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private EasyExcelValidHelper() {
    }

    public static <T> String validateEntity(T obj, int headRow) throws NoSuchFieldException {
        StringBuilder result = new StringBuilder();
        Set<ConstraintViolation<T>> set = validator.validate(obj, Default.class);
        if (set != null && !set.isEmpty()) {
            for (ConstraintViolation<T> cv : set) {
                Field declaredField = obj.getClass().getDeclaredField(cv.getPropertyPath().toString());
                ExcelProperty annotation = declaredField.getAnnotation(ExcelProperty.class);
                String[] array = annotation.value();
                //拼接错误信息，包含当前出错数据的标题名字+错误信息
                result.append(array[array.length - headRow]).append(":").append(cv.getMessage()).append(";");
            }
        }
        return result.toString();
    }
}
