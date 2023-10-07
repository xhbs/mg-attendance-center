package com.unisinsight.business.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考勤详情
 *
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/31
 */
@Data
@ApiModel("考勤详情")
public class FeelAttendDetailDTO {

    @ApiModelProperty(name = "captured_at", value = "打卡时间", example = "2021-9-10 15:32:12")
    private LocalDateTime capturedAt;

    @ApiModelProperty(name = "result", value = "考勤结果",example = "0")
    private Integer result;

    @ApiModelProperty(name = "location", value = "定位(经度:纬度)",example = "11:12")
    private String location;

    @ApiModelProperty(name = "place_name", value = "地名",  example = "云南省xxx学校")
    private String placeName;

    @ApiModelProperty(name = "feed_back_type", value = "反馈类型(0-拍照,1-手动)")
    private Integer feedBackType;

    @ApiModelProperty(name = "match_image", value = "比对图片url")
    private String matchImage;

    @ApiModelProperty(name = "match_result", value = "比对结果")
    private Integer matchResult;

    @ApiModelProperty(name = "comment", value = "备注")
    private String comment;

    @ApiModelProperty(name = "leave_type", value = "请假类型")
    private Short leaveType;

    @ApiModelProperty(name = "leave_start_date", value = "请假开始时间")
    private LocalDate leaveStartDate;

    @ApiModelProperty(name = "leave_end_date", value = "请假结束时间")
    private LocalDate leaveEndDate;

    @ApiModelProperty(name = "leave_reason", value = "请假原因")
    private String leaveReason;

    @ApiModelProperty(name = "absence_reason", value = "缺勤原因")
    private String absenceReason;
}
