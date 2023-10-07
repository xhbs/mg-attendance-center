package com.unisinsight.business.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unisinsight.business.bo.PaginationReq;
import com.unisinsight.business.common.utils.EspecialStrUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 需要审批的记录列表查询 通用参数
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/24
 * @since 1.0
 */
@Getter
@Setter
public class ApprovalRecordQueryReqDTO extends PaginationReq {

    /**
     * 流程状态
     */
    @ApiModelProperty(value = "流程状态： 1未上报，2审批中，3同意，4拒绝")
    private Integer status;

    /**
     * 组织编号列表
     */
    @ApiModelProperty(value = "组织编号列表")
    private List<String> orgIndexPaths;

    /**
     * 模糊搜索
     */
    @ApiModelProperty(value = "模糊搜索，人员编号或者人员姓名")
    private String search;

    /**
     * 是否是H5端调用
     */
    @ApiModelProperty(value = "是否是H5端调用")
    private boolean fromMobile;

    /**
     * 当前管理员编号
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String userCode;

    /**
     * 当前管理员所属组织
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String adminIndexPath;

    /**
     * 最大导出数量
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private Integer limit;

    public String getSearch() {
        if (search != null) {
            return EspecialStrUtils.change(search);
        }
        return null;
    }
}
