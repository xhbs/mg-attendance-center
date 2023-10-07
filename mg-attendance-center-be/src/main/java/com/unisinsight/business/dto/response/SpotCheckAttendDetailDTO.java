package com.unisinsight.business.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 抽查考勤
 *
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/31
 */
@Data
public class SpotCheckAttendDetailDTO {

    @ApiModelProperty(name = "task_id", value = "抽查考勤任务id", required = true)
    private Integer taskId ;

    @ApiModelProperty(name = "person_name ", value = "学生姓名", required = true, example = "张三")
    private String personName ;

    @ApiModelProperty(name = "person_no ", value = "学生编号", required = true, example = "张三")
    private String personNo ;

    @ApiModelProperty(name = "person_url ", value = "照片", required = true)
    private String personUrl ;

    @ApiModelProperty(name = "class_name ", value = "班级名", required = true, example = "大一1班")
    private String className ;

    @ApiModelProperty(name = "day", value = "有感考勤日期", required = true, example = "2021-9-10")
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate day;

    @ApiModelProperty(name = "status", value = "考勤状态(0-不限,1-已考勤,2-未考勤)", required = true, example = "true")
    private Integer status;

    @ApiModelProperty(name = "result", value = "考勤结果", required = true, example = "0")
    private Integer result;

    @ApiModelProperty(name = "can_feel_attend", value = "是否可以有感考勤", required = true, example = "true")
    private Boolean canFeelAttend;
}
