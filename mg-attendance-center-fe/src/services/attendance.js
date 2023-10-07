import request from '@utils/request';

/**
 * @description: 分页查询
 */
export const getStatistics = (data = {}, type='province') => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/statistics/${type}`,
		data,
	});
};


export const getAllSchoolTop = (data) => {
  return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/statistics/all-school-top-5`,
		data,
	});
}

export const getAllSchool = (params) => {
  return request({
		method: 'get',
		url: `/api/biz-scene/v1/attendance/statistics/school-count`,
    params,
	});
}

export const getProvincialSchoolAttendance = (data) => {
  return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/statistics/province-term`,
		data,
	});
}

export const getProvincialSchool = (data) => {
  return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/statistics/province-2-every-city`,
		data,
	});
}

export const getCityStatistics = (data) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/statistics/city`,
		data,
	});
}

export const getCitySchoolTop = (data) => {
  return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/statistics/city-top5`,
		data,
	});
}

export const getCityTerm = (data) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/statistics/city-term`,
		data,
	});
}

export const getCitySchool = (data) => {
  return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/statistics/city-2-every-school`,
		data,
	});
}

export const getSchoolStatistics = (data = {}) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/statistics/school`,
		data,
	});
};


export const getSchoolTop = (data) => {
  return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/statistics/school-class-top5`,
		data,
	});
}

export const getSchoolTerm = (data) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/statistics/school-term`,
		data,
	});
}


export const getOrganizationsTree = (params) => {
	return request({
		method: 'get',
		url: `/api/biz-scene/v1/attendance/org/tree`,
		params,
	});
}