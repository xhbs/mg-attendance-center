/*
 * @Author: heqiang 00621
 * @Date: 2020-09-12 10:16:51
 * @Description: 考勤调整
 */
import request from '@utils/request';

/**
 * @Author: heqiang 00621
 * @description: 考勤调整--分页查询
 * @param {}: 
 * @return {}: 
 */
export const queryAdjustListReq = params => {
    return request({
        method: "post",
        url: `/api/biz-scene/v1/attendance/adjust/page`,
        data: { ...params },
    });
}

/**
 * @Author: heqiang 00621
 * @description: 考勤调整--详情查询
 * @param {type}: 
 * @return {type} 
 */
export const queryAdjustDetailReq = params => {
    return request({
        method: 'get',
        url: `/api/biz-scene/v1/attendance/adjust/${params}`,
    });
}

/**
 * @Author: heqiang 00621
 * @description: 考勤调整--新增调整项
 * @param {type}: 
 * @return {type} 
 */
export const addAdjustItemReq = (params) => {
    return request({
        method: 'post',
        url: `/api/biz-scene/v1/attendance/adjust`,
        data: params,
    });
}

/**
 * @Author: heqiang 00621
 * @description: 考勤调整--批量删除
 * @param {type}: 
 * @return {type} 
 */
export const deleteAdjustBatchReq = params => {
    return request({
        method: 'delete',
        url: `/api/biz-scene/v1/attendance/adjust/batch`,
        data: params,
    });
}

/**
 * @Author: heqiang 00621
 * @description: 考勤调整--导出
 * @param {type}: 
 * @return {type} 
 */
export const exportAdjustBatchReq = params => {
    return request({
        method: 'post',
        url: `/api/biz-scene/v1/attendance/adjust/export`,
        data: params,
        responseType: 'blob',
    });
}

/**
 * @Author: heqiang 00621
 * @description: 参数设置---参数查询/修改
 * @param {type}: 
 * @return {type} 
 */
export const queryParamsReq = (params = ['get', {}]) => {
    return request({
        method: params[0],
        url: `/api/biz-scene/v1/attendance/sys/config`,
        data: params[1],
    });
}


/* @description: 获取组织成员
* @param {type} {type}
* @return {type} 
*/
export const getDepartmentTree = (id) => {
   return request({
       method: 'get',
       url: `/api/biz-scene/v1/attendance/task/tree/${id}`,
   })
}

// 获取分组下的人员列表
export const getMembersList = group => {
   return request({
       method: 'get',
       url: `/api/biz-scene/v1/attendance/task/tree/persons/${group}`,
   })
}