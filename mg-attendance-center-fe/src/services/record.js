/*
 * @Author: zhangjie
 * @Description: 考勤记录
 */
import request from '@utils/request';

// const { infraApiOrigin } = window.hzConfig;
// const infraApiOrigin = 'http://192.168.109.160:16505';
const api = '';

/**
 * @description: 查询考勤记录列表
 */
export const getRecordList = (data = {}) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/original/page`,
		data,
	});
};

/**
 * @description: 导出
 */
export const exportData = (data = {}) => {
	return request({
		method: 'post',
		url: `${api}/api/biz-scene/v1/attendance/original/export`,
		data: { ...data, export_all: 1 },
		responseType: 'blob',
	});
};
