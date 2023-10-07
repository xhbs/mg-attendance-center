import request from '@utils/request';

/**
* @description: 分页查询
*/
export const getCheckAttendPage = async (params = {}) => {
   return request({
      method: 'POST',
      url: `/api/biz-scene/v1/attendance/spot-check-task/list`,
      data: {
          ...params,
      },
   })
}

// 查询详情
export const getCheckAttendById = async (id) => {
  return request({
    method: 'GET',
    url: `/api/biz-scene/v1/attendance/spot-check-task/${id}`,
  })
}

// 新建
export const addCheckAttend = async (params = {}) => {
  return request({
    method: 'POST',
    url: `/api/biz-scene/v1/attendance/spot-check-task`,
    data: {
        ...params,
    },
  })
}

// 编辑
export const updateCheckAttend = async (params = {}) => {
  return request({
    method: 'PUT',
    url: `/api/biz-scene/v1/attendance/spot-check-task`,
    data: {
        ...params,
    },
  })
}

// 删除
export const delCheckAttend = async (ids = []) => {
  return request({
    method: 'DELETE',
    url: `/api/biz-scene/v1/attendance/spot-check-task`,
    data: ids,
  })
}

// 新建点名
export const addCheckAttendCallRole = async (params = {}) => {
  return request({
		method: 'POST',
		url: `/api/biz-scene/v1/attendance/spot-check-task/add_call_the_roll`,
		data: {
			...params,
		},
  });
}
// 测试
export const hasTestButton = async () => {
  return request({
		method: 'GET',
		url: `/api/biz-scene/v1/attendance/param/open_call_the_roll_test`,
  });
}
