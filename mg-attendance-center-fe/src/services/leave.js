/*
 * @Author: xiedan WX042
 * @since: 2020-12-15 16:27:48
 * @lastTime: 2021-09-23 14:15:36
 * @FilePath: \biz-scene-attendance-web\src\services\leave.js
 * @Description: 请假相关接口
 */

import request from '@utils/request';

/**
 * @description: 查询列表
 */
export const getLeaveList = (data = {}) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/leave-records`,
		data,
	});
};

/**
 * @description: 查询详情
 */
export const getLeaveDetail = (id = '') => {
	return request({
		method: 'get',
		url: `/api/biz-scene/v1/attendance/leave-records/${id}`,
	});
};

/**
 * @description: 处理---省级
 */
export const dealLeave = (data = {}) => {
	return request({
		method: 'put',
		url: `/api/biz-scene/v1/attendance/leave-records/approval`,
		data,
	});
};

/**
 * @description: 新增---校级
 */
export const addLeave = ({data = {}}) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/leave-records/add`,
		data,
	});
};

/**
 * @description: 修改---校级
 */
export const updateLeave = ({id, data = {}}) => {
	return request({
		method: 'put',
		url: `/api/biz-scene/v1/attendance/leave-records/${id}`,
		data,
	});
};

/**
 * @description: 上报---校级
 */
export const reportLeave = (data = []) => {
	return request({
		method: 'put',
		url: `/api/biz-scene/v1/attendance/leave-records/report`,
		data,
	});
};

/**
 * @description: 删除
 */
export const delLeave = (data = '') => {
	return request({
		method: 'delete',
		url: `/api/biz-scene/v1/attendance/leave-records?ids=${data}`,
	});
};

/**
 * 请假统计接口
 */

/**
 * @description: 查询统计列表
 */
export const queryLeaveStatistics = (data = {}) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/leave-stat/list`,
		data,
	});
};

/**
 * @description: 导出列表
 */
export const exportLeaveStatistics = params => {
  return request({
      method: 'POST',
      url: `/api/biz-scene/v1/attendance/leave-stat/export`,
      data: params,
      responseType: 'blob',
  })
}