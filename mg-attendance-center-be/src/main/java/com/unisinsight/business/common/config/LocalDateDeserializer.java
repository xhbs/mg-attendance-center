package com.unisinsight.business.common.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.unisinsight.framework.common.util.date.DateUtils;

import java.io.IOException;
import java.time.LocalDate;

/**
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/22
 * @since 1.0
 */
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        return LocalDate.parse(jsonParser.getText(), DateUtils.DATE_FORMATTER);
    }
}