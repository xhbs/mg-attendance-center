package com.unisinsight.business.mapper;

import com.unisinsight.business.dto.request.AppealRecordQueryReqDTO;
import com.unisinsight.business.dto.response.AppealRecordListDTO;
import com.unisinsight.business.model.AppealRecordDO;
import com.unisinsight.framework.common.base.Mapper;
import org.apache.ibatis.session.ResultHandler;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

import java.util.List;

/**
 * 申诉记录
 *
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2020/12/11
 * @since 1.0
 */
public interface AppealRecordMapper extends Mapper<AppealRecordDO>, InsertUseGeneratedKeysMapper<AppealRecordDO> {

    /**
     * 条件查询
     *
     * @param req 参数
     * @return 记录列表
     */
    List<AppealRecordListDTO> findByConditions(AppealRecordQueryReqDTO req);

    /**
     * 根据条件统计导出数量
     *
     * @param req 查询参数
     * @return 总数
     */
    int countByConditions(AppealRecordQueryReqDTO req);

    /**
     * 导出请假记录
     *
     * @param req     查询参数
     * @param handler 流式导出回调
     */
    void exportByConditions(AppealRecordQueryReqDTO req,
                            ResultHandler<AppealRecordListDTO> handler);
}