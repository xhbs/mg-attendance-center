/*
 * @Author: xiedan WX042
 * @since: 2020-12-23 16:20:19
 * @lastTime: 2021-01-30 20:16:20
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\services\recommendation.js
 * @Description: 抽查推荐
 */

import request from '@utils/request';

/**
 * @description: 列表
 */
export const getRecoList = (data = {}) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/recomm/result/page`,
		data,
	});
};

/**
 * @description: 详情
 */
export const getRecoDetail = (data = {}) => {
	return request({
		method: 'get',
		url: `/api/biz-scene/v1/attendance/recomm/result/detail`,
		data,
	});
};

/**
 * @description: 导出
 */
export const exportReco = (params = {}) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/recomm/result/export`,
		data: {...params},
		responseType: 'blob',
	});
};

/**
 * @description: 将人员加入分组  抽查推荐人员加入考勤组
 * @param {type} {type}
 * @return {type} 
 */
export const addPersonToGroup = (params = {}) => {
    return request({
        method: 'POST',
        url: `/api/mg/v1/object/manage/group/person/add`,
        data: {
            form_id: '',
            req: {
                ...params,
            },
        },
    })
}