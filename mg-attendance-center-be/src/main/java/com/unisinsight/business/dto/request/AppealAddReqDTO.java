package com.unisinsight.business.dto.request;

import com.unisinsight.business.dto.response.AttachFileDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 申诉请求 入参
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/11 11:23:53
 * @since 1.0
 */
@Data
public class AppealAddReqDTO  {
    /**
     * 申诉标题
     */
    @ApiModelProperty(value = "申诉标题", required = true)
    @NotNull
    @Length(max = 255)
    private String title;

    /**
     * 申诉内容
     */
    @ApiModelProperty(value = "申诉内容", required = true)
    @NotNull
    @Length(max = 500)
    private String content;

    /**
     * 人员列表
     */
    @ApiModelProperty(value = "人员列表")
    private List<String> personNos;

    /**
     * 申诉学生名单excel路径
     */
    @ApiModelProperty(value = "申诉学生名单excel路径")
    private String nameListExcelPath;

    /**
     * 考勤开始时间
     */
    @ApiModelProperty(value = "考勤开始时间", required = true)
    @NotNull
    private Long startTime;

    /**
     * 考勤结束时间
     */
    @ApiModelProperty(value = "考勤结束时间", required = true)
    @NotNull
    private Long endTime;

    /**
     * 附件文件列表
     */
    @ApiModelProperty(value = "附件文件列表")
    private List<AttachFileDTO> files;

    /**
     * 是否开始上报
     */
    @ApiModelProperty(value = "是否开始上报")
    private boolean report;
}
