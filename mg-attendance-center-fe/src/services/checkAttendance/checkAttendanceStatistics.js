import request from '@utils/request';

/**
* @description: 分页查询
*/
export const getCheckAttendStatisList = async (params = {}) => {
  return request({
     method: 'POST',
     url: `/api/biz-scene/v1/attendance/spot-check-attend/count`,
     data: {
         ...params,
     },
  })
}


/**
 * @Author: heqiang 00621
 * @description: 考勤日期查询
 * @param {type}: 
 * @return {type} 
 */
export const getAttendanceDateData = params => {
  return request({
      method: 'GET',
      url: `/api/biz-scene/v1/attendance/spot-check-task/attendance-dates`,
      params: {
        ...params,
      },
  });
}

