/*
 * @Author: heqiang 00621
 * @Date: 2020-12-15 16:35:24
 * @Description: 实习管理
 */
import request from '@utils/request';
import { stringify } from 'qs';

// 新增实习申请
export const addPracticeApply = ({data = {}}) => {
    return request({
        method: 'POST',
        url: `/api/biz-scene/v1/attendance/practice-records/add`,
        data,
    })
}

// 修改实习申请
export const updatePracticeApply = ({id, data = {}}) => {
    return request({
        method: 'put',
        url: `/api/biz-scene/v1/attendance/practice-records/${id}`,
        data,
    })
}

// 分页查询实习管理
export function queryPracticeManage(params) {
    return request({
        method: 'POST',
        url: `/api/biz-scene/v1/attendance/practice-records`,
        data: params,
    })
}

// 批量删除
export function deleteData(params) {
    return request({
        url: `/api/biz-scene/v1/attendance/practice-records?${stringify(params)}`,
        method: 'DELETE',
    });
}

// 批量上报
export function reportBatch(params) {
    return request({
        url: `/api/biz-scene/v1/attendance/practice-records/report`,
        method: 'PUT',
        data: params,
    });
}

// 查询详情
export function getDetailById(params){
    return request({
        url: `/api/biz-scene/v1/attendance/practice-records/${params}`,
        method: 'GET',
    });
}

// 批量处置
export function dealBatch(params) {
    return request({
        url: `/api/biz-scene/v1/attendance/practice-records/approval`,
        method: 'PUT',
        data: params,
    });
}