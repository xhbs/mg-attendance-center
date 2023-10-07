package com.unisinsight.business.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhang jin
 * @date 2020/12/8 17:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Base64FileResDTO {

    /**
     * 文件id
     */
    private String id;

    /**
     * 文件名
     */
    @JsonProperty("file_name")
    private String fileName;

    /**
     * 文件类型
     */
    @JsonProperty("file_type")
    private String fileType;

    /**
     * 文件大小
     */
    @JsonProperty("file_size")
    private Double fileSize;

    /**
     * 文件访问相对路径
     */
    @JsonProperty("file_url")
    private String fileUrl;
}
