/*
 * @Author: xiedan WX042
 * @since: 2020-11-05 17:50:59
 * @lastTime: 2021-06-23 14:36:59
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\services\detailPage.js
 * @Description: 考勤明细
 */

import request from '@utils/request';

/**********************日常考勤明细 相关接口***************************/
/**
 * @description: 查询考勤明细列表
 */
export const getDailyTableList = (data = {}) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/auth/statistics/page`,
		data,
	});
};

/**
 * @description: 查询考勤明细列表表头
 */
export const getDailyTableHeader = () => {
	return request({
		method: 'get',
		url: `/api/biz-scene/v1/attendance/auth/statistics/head?type=att-details-province-head`,
	});
};

/**
 * @description: 日常考勤明细详情
 */
export const getDailyDetail = (params = {}) => {
	return request({
		method: 'get',
		url: `/api/biz-scene/v1/attendance/auth/statistics/detail`,
		data: {...params},
	});
};

/**
 * @description: 日常考勤明细导出
 */
export const exportDailyList = (params = {}) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/auth/statistics/export`,
		responseType: 'blob',
		data: {...params},
	});
};

/**
 * @description: 日常考勤明细导出---省级管理员
 */
export const exportDailyListProvince = (params = {}) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/result/export`,
		responseType: 'blob',
		data: {...params},
	});
};

/**
 * @description: 考勤状态枚举类型
 */
// export const emnuStatusList = () => {
// 	return request({
// 		method: 'get',
// 		url: `/api/biz-scene/v1/attendance/auth/statistics/select`,
// 	});
// };

/**
 * @description: 日常考勤明细状态修改
 */
export const changeDailyStatus = (params) => {
	return request({
		method: 'put',
		url: `/api/biz-scene/v1/attendance/auth/statistics/update`,
		data: params,
	});
};

/**
 * @description: 下载导入模板
 */
export const downTemplate = () => {
	return request({
		method: 'get',
		url: `/api/biz-scene/v1/attendance/auth/statistics/excel/template`,
		responseType: 'blob',
	});
};

/**
 * @description: 上传考勤学生解析导入信息
 */
export const uploadExcelAnalysis = () => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/auth/statistics/excel/analysis`,
		responseType: 'blob',
	});
};


/******************抽查考勤明细 相关接口*************************/

/**
 * @description: 抽查任务枚举
 * @param {*}
 * @return {*}
 */
export const enmuCheckTask = (params) => {
	return request({
		method: 'get',
		url: `/api/biz-scene/v1/attendance/task/select`,
		data: params,
	});
};

/**
 * @description: 抽查---考勤明细表头
 */
export const getCheckTableHeader = (params = {}) => {
	return request({
		method: 'get',
		url: `/api/biz-scene/v1/attendance/selective/statistics/detail/header`,
		data: {...params},
	});
};

/**
 * @description: 抽查---考勤明细列表
 */
export const getCheckDataList = (params = {}) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/selective/statistics/detail`,
		data: {...params},
	});
};

/**
 * @description: 抽查---考勤状态下拉框
 */
export const getEnumStatus = (params = {}) => {
	return request({
		method: 'get',
		url: `/api/biz-scene/v1/attendance/selective/statistics/detail/selector`,
		data: {...params},
	});
};

/**
 * @description: 抽查---考勤明细详情
 */
export const getCheckDetail = (params = {}) => {
	return request({
		method: 'get',
		url: `/api/biz-scene/v1/attendance/selective/statistics/detail`,
		data: {...params},
	});
};

/**
 * @description: 抽查---状态更改
 */
export const checkChangeStatus = (params = {}) => {
	return request({
		method: 'put',
		url: `/api/biz-scene/v1/attendance/selective/statistics/detail`,
		data: {...params},
	});
};

/**
 * @description: 抽查---导出
 */
export const exportCheckList = (params = {}) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/selective/statistics/detail/export`,
		data: {...params},
		responseType: 'blob',
	});
};
