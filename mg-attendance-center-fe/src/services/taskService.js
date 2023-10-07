/*
 * @Author: xiedan WX042
 * @since: 2020-09-08 15:00:05
 * @lastTime: 2020-11-13 16:10:57
 * @FilePath: \biz-scene-attendance-web\src\services\taskService.js
 * @Description: 考勤任务管理
 */
import request from '@utils/request';
// import { stringify } from 'qs';

// const { infraApiOrigin } = window.hzConfig;
// const infraApiOrigin = 'http://192.168.109.160:16505';

/**
 * @description: 分页查询考勤任务
 */
export const getTaskList = async (params = {}) => {
    return request({
        method: 'POST',
        url: `/api/biz-scene/v1/attendance/task/page`,
        data: {
            ...params,
        },
    })
}

/**
 * @description: 根据id查询考勤任务
 */
export const getTaskDetailById = async (id) => {
    return request({
        method: 'get',
        url: `/api/biz-scene/v1/attendance/task/${id}`,
    })
}

/**
 * @description: 新增 考勤任务
 */
export const addTask = async (params = {}) => {
    return request({
        method: 'POST',
        url: `/api/biz-scene/v1/attendance/task`,
        data: {
            ...params,
        },
    })
}

/**
 * @description: 删除 考勤任务
 */
export const deleteTask = async (ids) => {
    return request({
        method: 'DELETE',
        url: `/api/biz-scene/v1/attendance/task/batch`,
        data: ids,
    })
}

/**
 * @description: 修改 考勤任务
 */
export const updateTask = async (params = {}) => {
    return request({
        method: 'PUT',
        url: `/api/biz-scene/v1/attendance/task`,
        data: {
            ...params,
        },
    })
}

/**
 * @description: 修改 任务状态
 */
export const updateStatus = async (params = {}) => {
    return request({
        method: 'PUT',
        url: `/api/biz-scene/v1/attendance/task/status/${params.status}`,
        data: params.ids,
    })
}

/**
 * @description: 考勤规则下拉列表
 */
export const getRulesEnum = async (params = {}) => {
    return request({
        method: 'get',
        url: `/api/biz-scene/v1/attendance/rule/select`,
        data: {
            ...params,
        },
    })
}

/**
 * @description: 任务名称验重
 */
export const checkTaskName = async (params = {}) => {
    return request({
        method: 'get',
        url: `/api/biz-scene/v1/attendance/task/check`,
        data: {
            ...params,
        },
    })
}

/**
 * @description: 查询对象管理静态枚举类型
 */
export const getTaskOmTypes = async (params = {}) => {
    return request({
        method: 'get',
        url: `/api/biz-scene/v1/attendance/task/om/types`,
    })
}

