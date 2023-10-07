package com.unisinsight.business.service;

import com.unisinsight.business.bo.DailyAttendanceStaticLevelBO;
import com.unisinsight.business.bo.PaginationRes;
import com.unisinsight.business.dto.request.DailyAttendanceHighLvlStaticDTO;
import com.unisinsight.business.dto.request.DailyAttendanceHighLvlStaticQueryReqDTO;
import com.unisinsight.business.model.DailyAttendanceStaticLevelDO;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author tanggang
 * @version 1.0
 *
 * @email tang.gang@inisinsight.com
 * @date 2021/8/17 10:21
 **/
public interface IDailyAttendanceStaticLevelServcie {

   List<DailyAttendanceStaticLevelBO> findDailyAttendanceStaticHighLvlList(DailyAttendanceStaticLevelBO queryBO);

   void insertList(List<DailyAttendanceStaticLevelDO> staticLevelBaseDOList);

   boolean checkWeekExist(DailyAttendanceStaticLevelBO queryBO);

   /**
    *@description 自动分页查询
    *@param
    *@return
    *@date    2021/9/26 14:56
    */
   PaginationRes<DailyAttendanceHighLvlStaticDTO> getPage(DailyAttendanceHighLvlStaticQueryReqDTO reqDTO);

   /**
     *@description 手动分页查询
     *@param
     *@return
     *@date    2021/9/26 14:56
     */
   PaginationRes<DailyAttendanceHighLvlStaticDTO> getPageHandle(DailyAttendanceHighLvlStaticQueryReqDTO reqDTO);

   void batchUpdate(@Param("results") List<DailyAttendanceStaticLevelDO> results);

   void exportLvlExcel(DailyAttendanceHighLvlStaticQueryReqDTO reqDTO, HttpServletResponse httpServletResponse);


   }
