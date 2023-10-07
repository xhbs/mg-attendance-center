/*
 * @Author: xiedan WX042
 * @since: 2020-08-27 19:43:46
 * @lastTime: 2021-09-27 11:55:38
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\services\common.js
 * @Description:
 */
import request from '@utils/request';
import Cookies from 'js-cookie';

/**
 * @description: 获取考勤组资源树
 * @param {type} {type}
 * @return {type}
 */
export const getDeviceGroupTree = async () => {
    return request({
        method: 'get',
        url: `/api/biz-scene/v1/attendance/site-group/tree`,
        // data: {
        //     ...params,
        // },
    })
}

/**
 * @description: 获取考勤地点/设备资源树
 * @param {type} {type}
 * @return {type}
 */
export const getDeviceListTree = async (params = {}) => {
    return request({
        method: 'POST',
        url: `/api/biz-scene/v1/attendance/privilege-resources-tree`,
        data: {
            ...params,
        },
    })
}

/**
 * @description: 获取组织成员
 * @param {type} {type}
 * @return {type}
 */
export const getDepartmentMembersTree = (params = {}) => {
    return request({
        method: 'POST',
        url: `/api/mg/v1/object/manage/group/list`,
        data: {
            object_type: "omperson",
            page_num: 1,
            page_size: 100000000,
            search: "",
            send_lib: "",
            ...params,
        },
    })
}

// 获取分组下的人员列表
export const getPersonList = params => {
    return request({
        method: 'post',
        url: `/api/mg/v1/object/manage/group/persons`,
        data: {
            group_ids: params,
            page_num: 1,
            page_size: 1000000,
        },
    })
}

/**
 * @description: 获取表达式组资源树
 * @param {type} {type}
 * @return {type}
 */
export const getExpressGroupTree = async (params = {}) => {
    return request({
        method: 'post',
        url: `/api/biz-scene/v1/attendance/express-group/list`,
        data: {
            ...params,
        },
    })
}

// 任务集合(用于考勤明细下拉选)
export const getTaskListReq = () => {
    return request({
        method: 'get',
        url: `/api/biz-scene/v1/attendance/task/select`,
    })
}

/**
 * @description: 获取用户详情（通过用户code获取)----用于获取用户组织
 * @param {type} {type}
 * @return {type}
 */
export const getUserOrgIndex = () => {
    // const userCode = 'xiaoji';
    const userCode = Cookies.get('usercode') || 'admin';
    return request({
        method: 'get',
        url: `/api/infra-uuv/v0.1/users/${userCode}`,
    })
}

/**
 * @description: 获取组织架构（根据org_index）----用于获取用户/设备组织
 * @param {type} {type}
 * @return {type}
 */
export const getOrganizationsTree = (params = {}) => {
    return request({
        method: 'get',
        url: `/api/infra-uuv/v0.1/organizations/index/${params.orgIndex}/childs-tree`,
        data: {
            type: params.type,
        },
    })
}

/**
 * @description: 获取组织架构（根据org_index）----用于获取用户/设备组织----可懒加载
 * @param {type} {type}
 * @return {type}
 */
export const getLazyOrganizationsTree = (params = {}) => {
    return request({
        method: 'get',
        url: `/api/infra-uuv/v0.1/organizations/index/tree`,
        data: {
          include_child: true,
          lazy_load: true,
          type: 1,
          ...params,
        },
    })
}

/**
 * @description: 校级 实习、请假、申诉 获取人员列表；校级请假申请的编辑弹框
 * @param {*} params
 * @return {*}
 */
export const getPersonByCondition = (params = {}) => {
    return request({
        method: 'post',
        url: `/api/mg/v1/object/manage/for-public/list/condition`,
        data: {
            form_code: 'omperson',
            ...params,
        },
    })
}

/**
 * @description: 查询系统启用模式
 * @param {type} {type}
 * @return {type}
 */
export const getSysConfig = () => {
    return request({
        method: 'get',
        url: `/api/biz-scene/v1/attendance/sys/config/advance`,
    })
}

/**
 * @description: 文件注册--获取id用于上传文件
 * @param {type} {type}
 * @return {type}
 */
export const regisFile = (file) => {
    return request({
        method: 'post',
        url: `/api/mg/v1/fms/files`,
        data: file,
		headers: {
			'Content-Type': 'application/json',
		},
    })
}

/**
 * @description: 文件上传
 * @param {type} {type}
 * @return {type}
 */
export const uploadFile = ({file_id, file}) => {
    return request({
        method: 'post',
        url: `/api/mg/v1/fms/files/upload?id=${file_id}`,
        data: file,
		headers: {
			'Content-Type': 'multipart/form-data',
		},
    })
}

/**
 * @description: 获取用户角色（通过用户usercode获取)
 * @param {type} {type}
 * @return {type}
 */
export const getUserRoleInfo = () => {
    const userCode = process && process.env && process.env.NODE_ENV === 'development' ? 'admin' : Cookies.get('usercode');
    return request({
        method: 'get',
        url: `/api/infra-uuv/v0.1/users/${userCode}/roles`,
    })
}

/**
 * 更加org_id获取组织架构
 * @param data
 * @returns {*}
 */
export const getOrganizationsById = (data) => {
	return request({
		method: 'get',
		url: `/api/infra-uuv/v0.1/organizations/${data.org_id}/childs-tree`,
		data: data.data,
	})
}

/**
 * 通过orgIndex获取组织详情
 * @param data
 * @returns {*}
 */
export const getOrgDetailByOrgIndex = (org_index) => {
	return request({
		method: 'get',
		url: `/api/infra-uuv/v0.1/organizations/detail/${org_index}`,
	})
}



export const getStudentBaseinfoPage = (data) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/dataway/api/student-baseinfo-page`,
        data,
	})
}
export const getStudentRigiterStatis = (data) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/dataway/api/student-reg-statis`,
        data,
	})
}


export const getStudentAttSts = (data) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/dataway/api/student-att-sts`,
        data,
	})
}
export const getStudentAttDetailSts = (data) => {
	return request({
		method: 'post',
		url: `/api/biz-scene/v1/attendance/dataway/api/student-att-detail-page`,
        data,
	})
}