/**
 * 实习点名接口
 */
import request from '@utils/request';

const BASE_URL = '/api/biz-scene/v1/attendance/practice-attendance'

/**
 * 获取点名列表
 * @param data
 * @returns {*}
 */
export function getRollCallList(data) {
	return request({
		method: 'post',
		url: `${BASE_URL}/list`,
		data,
	});
}

/**
 * 获取点名详情列表
 * @param data
 * @returns {*}
 */
export function getRollCallDetails(data) {
	return request({
		method: 'post',
		url: `${BASE_URL}/details`,
		data,
	});
}

/**
 * 导出 点名列表
 * @param data
 * @returns {*}
 */
export function exportRollCallList(data) {
	return request({
		method: 'post',
		url: `${BASE_URL}/list-export`,
		responseType: 'blob',
		data,
	});
}

/**
 * 导出 点名详情列表
 * @param data
 * @returns {*}
 */
export function exportRollCallDetails(data) {
	return request({
		method: 'post',
		url: `${BASE_URL}/details-export`,
		responseType: 'blob',
		data,
	});
}
