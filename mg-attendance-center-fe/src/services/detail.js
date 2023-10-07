/*
 * @Author: zhangjie
 * @Description: 考勤明细
 */
import request from '@utils/request';

// const { infraApiOrigin } = window.hzConfig;
const api = '';

/**
 * @description: 查询考勤明细列表
 */
export const getDetailList = (data = {}) => {
	return request({
		method: 'post',
		url: `${api}/api/biz-scene/v1/attendance/result/page`,
		data,
	});
};

/**
 * @description: 查询考勤任务列表
 */
export const getTaskList = (data = {}) => {
	return request({
		method: 'get',
		url: `${api}/api/biz-scene/v1/attendance/task/select`,
		data,
	});
};

/**
 * @description: 查看明细
 */
export const viewDetail = (data = {}) => {
	return request({
		method: 'post',
		url: `${api}/api/biz-scene/v1/attendance/result/detail/${data.taskId}`,
		data: data.ids,
	});
};

/**
 * @description: 修改明细
 */
export const updateDetail = (data = {}) => {
	return request({
		method: 'put',
		url: `${api}/api/biz-scene/v1/attendance/result/detail/update`,
		data,
	});
};

/**
 * @description: 查看明细下拉选择
 */
export const resultSelect = (data = {}) => {
	return request({
		method: 'get',
		url: `${api}/api/biz-scene/v1/attendance/result/detail/update/select`,
		data,
	});
};

/**
 * @description: 导出
 */
export const exportData = (data = {}) => {
	return request({
		method: 'post',
		url: `${api}/api/biz-scene/v1/attendance/result/export`,
		data: { ...data, export_all: 1 },
		responseType: 'blob',
	});
};
