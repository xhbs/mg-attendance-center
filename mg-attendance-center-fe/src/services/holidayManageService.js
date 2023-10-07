import request from '@utils/request';

/**
 * @description: 分页查询考勤规则
 */
export const getHolidayList = async (params = {}) => {
  return request({
      method: 'GET',
      url: `/api/biz-scene/v1/attendance/daily-exclude-date`,
      params: {
          ...params,
      },
  })
}

//新增节假日类型
export const addHolidayType = async (params = {}) => {
  return request({
    method: 'POST',
    url: `/api/biz-scene/v1/attendance/daily-exclude-date`,
    data: {
        ...params,
    },
  })
}

//更新节假日类型
export const updateHolidayType = async (params = {}) => {
  return request({
    method: 'PUT',
    url: `/api/biz-scene/v1/attendance/daily-exclude-date`,
    data: {
        ...params,
    },
  })
}

/**
 * @description: 删除
 */
 export const deleteHoliday = async (ids) => {
  return request({
      method: 'DELETE',
      url: `/api/biz-scene/v1/attendance/daily-exclude-date`,
      data: ids,
  })
}

