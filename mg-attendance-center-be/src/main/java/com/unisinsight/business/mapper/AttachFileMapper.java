package com.unisinsight.business.mapper;

import com.unisinsight.business.model.AttachFileDO;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 附件文件
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/11
 * @since 1.0
 */
public interface AttachFileMapper extends Mapper<AttachFileDO> {

    /**
     * 批量保存
     */
    void batchSave(@Param("files") List<AttachFileDO> list);
}