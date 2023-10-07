package com.unisinsight.business.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 抽查考勤统计查询 出参
 *
 * @author jiangnan [jian.nan@unisinsight.com]
 * @date 2021/8/31
 */
@Data
public class TaskResultStatResDTO {

    @ApiModelProperty(name = "school_org_index", value = "学校组织编码", required = true, example = "03EF34E0C0BA4D7CE0537D64A8C02609")
    private String schoolOrgIndex;

    @ApiModelProperty(name = "school_org_name", value = "学校名称", required = true, example = "红河州卫生护理学校")
    private String schoolOrgName;

    @ApiModelProperty(name = "index_path_name", value = "所属组织", required = true, example = "蒙自市<红河哈尼族彝族自治州<云南省")
    private String indexPathName;

    @ApiModelProperty(name = "attendance_date", value = "考勤日期", required = true, example = "2021-08-25")
    private String attendanceDate;

    @ApiModelProperty(name = "student_num", value = "抽查学生总数", required = true, example = "1000")
    private Integer studentNum;

    @ApiModelProperty(name = "absence_num", value = "缺勤学生数", required = true, example = "300")
    private Integer absenceNum;

    @ApiModelProperty(name = "absence_rate", value = "缺勤率", required = true, example = "30.00")
    private Float absenceRate;

    @ApiModelProperty(name = "normal_num", value = "在校学生数", required = true, example = "500")
    private Integer normalNum;

    @ApiModelProperty(name = "leave_num", value = "请假学生数", required = true, example = "100")
    private Integer leaveNum;

    @ApiModelProperty(name = "practice_num", value = "实习学生数", required = true, example = "100")
    private Integer practiceNum;
}
