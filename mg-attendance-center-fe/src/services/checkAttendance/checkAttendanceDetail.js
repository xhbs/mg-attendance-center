import request from '@utils/request';

/**
 * @description: 个人考勤明细查询
 */
export const getAttendanceDetailList = async (params = {}) => {
  return request({
      method: 'GET',
      url: `/api/biz-scene/v1/attendance/spot-check-attend/details`,
      params: {
          ...params,
      },
  })
}

/**
 * @description: 抽查考勤明细查询
 */
export const getAttendanceDetailData = async (params = {}) => {
  return request({
      method: 'POST',
      url: `/api/biz-scene/v1/attendance/spot-check-attend/list`,
      data: {
          ...params,
      },
  })
}



