package com.unisinsight.business.mapper;

import com.unisinsight.business.model.CallTheRollStat;
import com.unisinsight.framework.common.base.Mapper;
import jdk.nashorn.internal.codegen.CompilerConstants;

import java.util.List;

/**
 * 点名统计/月(CallTheRollStat)表数据库访问层
 *
 * @author XieHaiBo
 * @since 2023-03-22 17:51:52
 */
public interface CallTheRollStatDao extends Mapper<CallTheRollStat> {


    void insertBatch(List<CallTheRollStat> list);

}

