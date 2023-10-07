/*
 * @Author: xiedan WX042
 * @since: 2020-09-08 15:54:08
 * @lastTime: 2020-11-09 15:27:09
 * @FilePath: \biz-scene-attendance-web\src\services\ruleService.js
 * @Description: 考勤规则管理
 */
import request from '@utils/request';
// import { stringify } from 'qs';

// const { infraApiOrigin } = window.hzConfig;
// const infraApiOrigin = 'http://192.168.109.160:16505';

/**
 * @description: 分页查询考勤规则
 */
export const getRulesList = async (params = {}) => {
    return request({
        method: 'POST',
        url: `/api/biz-scene/v1/attendance/rule/page`,
        data: {
            ...params,
        },
    })
}

/**
 * @description: 根据id查询考勤规则
 */
export const getRulesDetailById = async (id) => {
    return request({
        method: 'get',
        url: `/api/biz-scene/v1/attendance/rule/${id}`,
    })
}

/**
 * @description: 新增
 */
export const addRule = async (params = {}) => {
    return request({
        method: 'POST',
        url: `/api/biz-scene/v1/attendance/rule`,
        data: {
            ...params,
        },
    })
}

/**
 * @description: 删除
 */
export const deleteRule = async (ids) => {
    return request({
        method: 'DELETE',
        url: `/api/biz-scene/v1/attendance/rule/batch`,
        data: ids,
    })
}

/**
 * @description: 修改
 */
export const updateRule = async (params = {}) => {
    return request({
        method: 'PUT',
        url: `/api/biz-scene/v1/attendance/rule`,
        data: {
            ...params,
        },
    })
}

/**
 * @description: 考勤规则名称验重
 */
export const checkRulesName = async (params = {}) => {
    return request({
        method: 'get',
        url: `/api/biz-scene/v1/attendance/rule/check`,
        data: {
            ...params,
        },
    })
}


/******云南中职******/
/**
 * @description: 获取表达式分组id
 * @param {*}
 * @return {*} 只返回一条数据
 */
export const getExpressGroupId = () => {
    return request({
        method: 'post',
        url: '/api/biz-scene/v1/attendance/express-group/list',
        data: {
            page_num: 1,
            page_size: 1,
            type: 2,
        },
    })
}