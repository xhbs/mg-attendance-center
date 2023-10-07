package com.unisinsight.business.dto.request;

import com.unisinsight.business.dto.response.AttachFileDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * 点名 入参
 *
 * @author XieHaiBo [jian.nan@unisinsight.com]
 * @date 2023/3/9
 */
@Data
public class CallTheRollDto extends UpdateAttendDto {

    @ApiModelProperty(value = "抽查任务id")
    private Integer taskId;

    /**
     * 实习单位
     */
    @ApiModelProperty(value = "实习单位", required = true)
    private String practiceCompany;

    /**
     * 单位联系人
     */
    @ApiModelProperty(value = "单位联系人", required = true)
    private String companyContacts;

    /**
     * 单位联系电话
     */
    @ApiModelProperty(value = "单位联系电话", required = true)
    private String contactsPhone;

    /**
     * 附件文件列表
     */
    @ApiModelProperty(value = "附件文件列表")
    private List<AttachFileDTO> files;
}
