/*
 * @Author: xiedan WX042
 * @since: 2020-12-17 19:58:21
 * @lastTime: 2021-10-09 15:43:37
 * @FilePath: \biz-scene-attendance-web\src\services\appeal.js
 * @Description: 申诉
 */
import request from '@utils/request';

/**
 * @description: 分页查询
 */
export const getAppealList = (data = {}) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/appeal-records`,
		data,
	});
};

/**
 * @description: 批量删除
 */
export const delAppeal = (ids = {}) => {
	return request({
		method: 'delete',
		url: `/api/biz-scene/v1/attendance/appeal-records?ids=${ids}`,
	});
};

/**
 * @description: 批量申诉
 */
export const bacthAppeal = (data = []) => {
	return request({
		method: 'put',
        url: `/api/biz-scene/v1/attendance/appeal-records/appeal`,
        data,
	});
};

/**
 * @description: 批量处理
 */
export const dealAppeal = (data = {}) => {
	return request({
		method: 'put',
        url: `/api/biz-scene/v1/attendance/appeal-records/approval`,
        data,
	});
};

/**
 * @description: 新增申诉
 */
export const addAppeal = ({ data = {} }) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/appeal-records/add`,
		data,
	});
};

/**
 * @description: 修改申诉
 */
export const updateAppeal = ({id = '', data = {}}) => {
	return request({
		method: 'put',
        url: `/api/biz-scene/v1/attendance/appeal-records/${id}`,
        data,
	});
};

/**
 * @description: 查询详情
 */
export const getAppealDetail = (id = '') => {
	return request({
		method: 'get',
		url: `/api/biz-scene/v1/attendance/appeal-records/${id}`,
	});
};

/**
 * @description: 导出
 */
export const exportAppeal = (params = {}) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/appeal-records/export`,
		data: {...params},
		responseType: 'blob',
	});
};

/**申诉统计 */

/**
 * @description: 分页查询
 */
export const getStatisticsList = (data = {}) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/appeal-stat/list`,
		data,
	});
};

/**
 * @description: 分页查询---班级
 */
export const getStatisticsListOfClass = (data = {}) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/appeal-stat/list-by-class`,
		data,
	});
};

/**
 * @description: 导出--校级以上
 */
export const exportStatisticsList = (params = {}) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/appeal-stat/export`,
		data: {...params},
		responseType: 'blob',
	});
};

/**
 * @description: 导出---班级
 */
export const exportStatisticsOfClass = (params = {}) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/appeal-stat/export-by-class`,
		data: {...params},
		responseType: 'blob',
	});
};

/**新增申诉 */
/**
 * @description: 导出人员名单的模板
 */
export const exportTemp = () => {
	return request({
		method: 'get',
		url: `/api/biz-scene/v1/attendance/download/excel-tpl/appeal-name-list`,
		responseType: 'blob',
	});
};