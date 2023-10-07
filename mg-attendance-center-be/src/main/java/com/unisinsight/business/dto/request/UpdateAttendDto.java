package com.unisinsight.business.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * 有感考勤 入参
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/17
 */
@Data
public class UpdateAttendDto {
    /**
     * 学生编号
     */
    @ApiModelProperty(value = "学生编号",required = true)
    @NotNull
    @NotEmpty
    private String personNo;
    /**
     * 考勤日期
     */
    @ApiModelProperty(value = "考勤日期",required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate attendDate;
    /**
     * 类型(0-在校,3-请假,99-缺勤)
     * -1, "休息
     * 0, "在校"
     * 2, "实习"
     * 3, "请假"
     * 99, "缺勤"
     */
    @ApiModelProperty(value = "类型(0-在校,3-请假,99-缺勤)",required = true)
    private Integer type;
    /**
     * 定位(经度:纬度)
     */
    @ApiModelProperty(value = "定位(经度:纬度)")
    private String  location;
    /**
     * 地名
     */
    @ApiModelProperty(value = "地名")
    private String  placeName;

    /**
     * 反馈类型
     */
    @ApiModelProperty(value = "反馈类型 0-拍照 1-手动")
    private Integer backType;
    /**
     * 比对图片
     */
    @ApiModelProperty(value = "比对图片")
    private String picture;
    /**
     * 比对结果
     */
    @ApiModelProperty(value = "比对结果")
    private Integer matchResult;
    /**
     * 手动备注
     */
    @ApiModelProperty(value = "手动备注")
    private String comment;
    /**
     * 缺勤原因
     */
    @ApiModelProperty(value = "缺勤原因")
    private String absenceReason;
    /**
     * 请假类型
     */
    @ApiModelProperty(value = "请假类型; 1：病假，2：事假，3：实习登记，99：其他")
    private Short leaveType;
    /**
     * 请假开始时间
     */
    @ApiModelProperty(value = "请假开始时间，13位时间戳")
    private Long startTime;

    /**
     * 请假结束时间
     */
    @ApiModelProperty(value = "请假结束时间，13位时间戳")
    private Long endTime;
    /**
     * 请假证明url
     */
    @ApiModelProperty(value = "请假证明url")
    private String leaveImg;
    /**
     * 请假原因
     */
    @ApiModelProperty(value = "请假原因")
    private String reason;
}
