import request from '@utils/request';

/**
* @description: 新增
*/
export const updateDailyAttend = async (params = {}) => {
   return request({
      method: 'POST',
      url: `/api/biz-scene/v1/attendance/daily-attendance-setting`,
      data: {
          ...params,
      },
   })
}

//查询日常考勤
export const getDailyAttend = async () => {
  return request({
    method: 'GET',
    url: `/api/biz-scene/v1/attendance/daily-attendance-setting`,
  })
}