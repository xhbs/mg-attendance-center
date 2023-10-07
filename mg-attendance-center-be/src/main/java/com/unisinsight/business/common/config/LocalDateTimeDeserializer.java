package com.unisinsight.business.common.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.unisinsight.framework.common.util.date.DateUtils;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * LocalDateTime 反序列化
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/3/12
 * @since 1.0
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        return LocalDateTime.parse(jsonParser.getText(), DateUtils.DATETIME_FORMATTER);
    }
}
