/**
 * 日常考勤  接口
 */
import request from '@utils/request';

/**
 * 获取组织架构(可懒加载)
 * @param data
 * @returns {*}
 */
export function getOrganizationsTree(data) {
	return request({
		method: 'get',
		url: `/api/infra-uuv/v0.1/organizations/index/tree`,
		data: {
			excludeApi: true,
			...data,
		},
	});
}

/**
 * 高级节点考勤统计分页查询
 * @param data
 * @returns {*}
 */
export function getHighAttendance(data) {
	return request({
		method: 'POST',
		url: `/api/biz-scene/v1/attendance/statictis/high/level/page`,
		data,
	});
}

/**
 * 学生考勤统计分页查询
 * @param data
 * @returns {*}
 */
export function getStudentAttendance(data) {
	return request({
		method: 'POST',
		url: `/api/biz-scene/v1/attendance/statictis/stu/page`,
		data,
	});
}

/**
 * 导出 高级节点考勤数据
 * @param data
 * @returns {*}
 */
export function exportHighAttendance (data) {
	return request({
		method: 'POST',
		url: `/api/biz-scene/v1/attendance/statictis/level/export`,
		data,
	});
}

/**
 * 导出 学生考勤数据
 * @param data
 * @returns {*}
 */
export function exportStudentAttendance (data) {
	return request({
		method: 'POST',
		url: `/api/biz-scene/v1/attendance/statictis/stu/export`,
		data,
	});
}

/**
 * 分页查询个人考勤明细
 * @param data
 * @returns {*}
 */
export function getPersonDetails(data) {
	return request({
		method: 'get',
		url: `/api/biz-scene/v1/attendance/daily-results`,
		data,
	});
}

/**
 * 查询个人 星期 考勤明细
 * @param data
 * @returns {*}
 */
export function getPersonWeekDetails(data) {
	return request({
		method: 'get',
		url: `/api/biz-scene/v1/attendance/daily-results/details`,
		data,
	});
}
