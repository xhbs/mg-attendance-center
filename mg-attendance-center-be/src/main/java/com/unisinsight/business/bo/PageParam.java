package com.unisinsight.business.bo;

import com.github.pagehelper.util.StringUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import static com.unisinsight.business.common.constants.Constants.*;

/**
 * @author : winnie [wangliu023@qq.com]
 * @date : 2020/5/13
 */
@Data
@ApiModel(value = "PageParam", description = "分页参数")
public class PageParam {
    /**
     * 页大小
     */
    @ApiModelProperty(value = "页大小", required = true, example = "20")
    private int pageSize;

    /**
     * 页码
     */
    @ApiModelProperty(value = "页码", required = true, example = "1")
    private int pageNum;

    /**
     * 排序字段
     */
    @ApiModelProperty(value = "排序字段", example = "ID")
    private String orderField;

    /**
     * 排序规则
     */
    @ApiModelProperty(value = "排序规则(降序-DESC,升序-ASC)", example = "DESC")
    private String orderRule;

    public String getOrderBy(){
        if(StringUtil.isEmpty(orderField)){
            return null;
        }
        return  orderField+" " +orderRule;
    }
    /**
     * 默认页面大小20
     */
    public int getPageSize() {
        if (pageSize <= 0) {
            return DEFAULT_PAGE_NUM;
        }
        return pageSize;
    }

    /**
     * 默认页码1
     */
    public int getPageNum() {
        if (pageNum <= 0) {
            pageNum = MIN_PAGE_NUM;
        }
        return pageNum;
    }

    /**
     * 预处理
     */
    public void pretreatment(String orderField, String orderRule) {
        //页码
        setPageNum(getPageNum());
        //页容量
        setPageSize(getPageSize());
        //页容量不得超过-MAX_PAGE_NUM
        if (getPageSize() > MAX_PAGE_NUM) {
            setPageSize(MAX_PAGE_NUM);
        }
        if (StringUtil.isNotEmpty(orderField)) {
            setOrderField(orderField);
        }
        if (StringUtil.isNotEmpty(orderRule)) {
            setOrderRule(orderRule);
        }
    }

}
