package com.unisinsight.business.common.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.unisinsight.framework.common.util.date.DateUtils;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/22
 * @since 1.0
 */
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(localDateTime.format(DateUtils.DATETIME_FORMATTER));
    }
}