package com.unisinsight.business.bo;

import com.github.pagehelper.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 通用分页 出参
 *
 * @author WangYi [wang.yi@unisinsight.com]
 * @date 2020/8/20
 * @since 1.0
 */
@Data
public class PaginationRes<T> {
    @ApiModelProperty("分页参数")
    private Paging paging;

    @ApiModelProperty("列表数据")
    private List<T> data;

    public PaginationRes() {
    }

    public PaginationRes(List<T> data, Paging paging) {
        this.data = data;
        this.paging = paging;
    }

    /**
     * 构建分页返回数据结构
     *
     * @param data 列表数据
     * @param page 分页信息
     * @param <T>  数据类型
     * @return {@link PaginationRes}
     */
    public static <T> PaginationRes<T> of(List<T> data, Page page) {
        Paging paging = new Paging();
        paging.setPageNum(page.getPageNum());
        paging.setPageSize(page.getPageSize());
        paging.setTotal(page.getTotal());

        PaginationRes<T> res = new PaginationRes<>();
        res.setData(data);
        res.setPaging(paging);
        return res;
    }

    public static <T> PaginationRes<T> of(List<T> data, int pageNum, int pageSize, long total) {
        Paging paging = new Paging();
        paging.setPageNum(pageNum);
        paging.setPageSize(pageSize);
        paging.setTotal(total);

        PaginationRes<T> res = new PaginationRes<>();
        res.setData(data);
        res.setPaging(paging);
        return res;
    }

    public static <T> PaginationRes<T> empty(Page page) {
        Paging paging = new Paging();
        paging.setPageNum(page.getPageNum());
        paging.setPageSize(page.getPageSize());
        paging.setTotal(page.getTotal());

        PaginationRes<T> res = new PaginationRes<>();
        res.setData(Collections.emptyList());
        res.setPaging(paging);
        return res;
    }

    public static <T> PaginationRes<T> empty(PaginationReq page) {
        Paging paging = new Paging();
        paging.setPageNum(page.getPageNum());
        paging.setPageSize(page.getPageSize());
        paging.setTotal(0L);

        PaginationRes<T> res = new PaginationRes<>();
        res.setData(Collections.emptyList());
        res.setPaging(paging);
        return res;
    }

    public static <T> PaginationRes<T> empty(int pageNum, int pageSize, long total) {
        Paging paging = new Paging();
        paging.setPageNum(pageNum);
        paging.setPageSize(pageSize);
        paging.setTotal(total);

        PaginationRes<T> res = new PaginationRes<>();
        res.setData(Collections.emptyList());
        res.setPaging(paging);
        return res;
    }
}
