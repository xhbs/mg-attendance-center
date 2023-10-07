/*
 * @Author: zhangjie
 * @Description: 考勤报表
 */
import request from '@utils/request';

// const { infraApiOrigin } = window.hzConfig;
// const infraApiOrigin = 'http://192.168.109.160:16505';
const api = '';

/**
 * @description: 查询考勤报表列表
 */
export const getReportList = (data = {}) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/result/statistics/page`,
		data,
	});
};

/**
 * @description: 导出
 */
export const exportData = (data = {}) => {
	return request({
		method: 'post',
		url: `${api}/api/biz-scene/v1/attendance/result/statistics/export`,
		data: { ...data, export_all: 1 },
		responseType: 'blob',
	});
};

/**
 * @description: 获取表头字段
 */
export const getColumns = (data = {}) => {
	return request({
		method: 'post',
		url: `${api}/api/biz-scene/v1/attendance/result/statistics/page/head`,
		data,
	});
};
