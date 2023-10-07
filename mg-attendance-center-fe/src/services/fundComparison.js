/**
 * 资助比对 接口
 */

import request from '@utils/request';

const BASE_URL = '/api/biz-scene/v1/attendance/subsid';

/**
 * 比对生成数据
 * @param data
 * @returns {*}
 */
export const comparison = data => {
	return request({
		method: 'post',
		url: `${BASE_URL}/level/generateSubsidRecords`,
		data,
		timeout: 10000,
	});
}

/**
 * 高级节点资助比对
 * @param data
 * @returns {*}
 */
export function highList(data) {
	return request({
		method: 'post',
		url: `${BASE_URL}/level/page`,
		data,
	});
}

/**
 * 学生资助对比列表
 * @param data
 * @returns {*}
 */
export function studentList(data) {
	return request({
		method: 'post',
		url: `${BASE_URL}/stu/static/page`,
		data,
	});
}

/**
 * 学生考勤信息
 * @param data
 * @returns {*}
 */
export function subsidList(data) {
	return request({
		method: 'post',
		url: `${BASE_URL}/stu/deatail/page`,
		data,
	});
}

/**
 * 上传excel
 * @param type
 * @param data
 * @returns {*}
 */
export function uploadExcel(type, data) {
	return request({
		method: 'post',
		url: `${BASE_URL}/stu-list/uploadSubsidStuExcelHandleSync?prj_type=${type}`,
		data,
	});
}

/**
 * 非学生层级导出预检
 * @param data
 * @returns {*}
 */
export function exportPreCheck(data) {
	return request({
		method: 'get',
		url: `${BASE_URL}/level/preCheckExportExcel`,
		data,
	});
}

/**
 * 获取资助名单
 * @param data
 * @returns {*}
 */
export function findSubListindex(data) {
	return request({
		method: 'post',
		url: `${BASE_URL}/stu-list/findSubListIndex`,
		data,
	});
}