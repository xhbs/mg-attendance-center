/*
 * @Author: xiedan WX042
 * @since: 2020-09-08 15:57:59
 * @lastTime: 2020-11-10 14:06:42
 * @FilePath: \biz-scene-attendance-web\src\services\deviceService.js
 * @Description: 考勤点
 */
import request from '@utils/request';
// import { stringify } from 'qs';

// const { infraApiOrigin } = window.hzConfig;
// const infraApiOrigin = 'http://192.168.109.160:16505';

/**
 * @description: 根据id查询考勤点组
 */
export const getDevicesGroupById = async (params = {}) => {
    return request({
        method: 'get',
        url: `/api/biz-scene/v1/attendance/rule/${params.id}`,
    })
}

/**
 * @description: 新增 考勤点组
 */
export const addDevicesGroup = async (params = {}) => {
    return request({
        method: 'POST',
        url: `/api/biz-scene/v1/attendance/site-group`,
        data: {
            ...params,
        },
    })
}

/**
 * @description: 删除 考勤点组
 */
export const deleteDevicesGroup = async (id) => {
    return request({
        method: 'DELETE',
        url: `/api/biz-scene/v1/attendance/site-group/${id}`,
    })
}

/**
 * @description: 修改 考勤点组
 */
export const updateDevicesGroup = async (params = {}) => {
    return request({
        method: 'PUT',
        url: `/api/biz-scene/v1/attendance/site-group`,
        data: {
            ...params,
        },
    })
}

/**
 * @description: 分页查询设备组信息
 * @param {type} {type}
 * @return {type} 
 */
export const getDeviceGroupPage = async (params = {}) => {
    return request({
        method: 'get',
        url: `/api/biz-scene/v1/attendance/site/device/page`,
        data: {
            ...params,
        },
    })
}

/**
 * @description: 根据考勤组id查询考勤点设备
 */
export const getDevicesById = async (params = {}) => {
    return request({
        method: 'POST',
        url: `/api/biz-scene/v1/attendance/site-device/page`,
        data: {
            ...params,
        },
    })
}

/**
 * @description: 新增 考勤点
 */
export const addDevices = async (params) => {
    return request({
        method: 'POST',
        url: `/api/biz-scene/v1/attendance/site-device`,
        data: params,
    })
}

/**
 * @description: 删除 考勤点
 */
export const deleteDevices = async (ids) => {
    return request({
        method: 'DELETE',
        url: `/api/biz-scene/v1/attendance/site-device/batch`,
        data: ids,
    })
}

/**
 * @description: 修改 考勤点
 */
export const updateDevices = async (params = {}) => {
    return request({
        method: 'PUT',
        url: `/api/biz-scene/v1/attendance/site-device`,
        data: {
            ...params,
        },
    })
}

/**
 * @description: 根据考勤点id查询详情
 */
export const getDevicesDetailById = async (id) => {
    return request({
        method: 'GET',
        url: `/api/biz-scene/v1/attendance/site-device/${id}`,
    })
}

/**
 * @description: 获取产商字典
 */
export const getProductLists = (id) => {
    return request({
        method: 'GET',
        url: `/api/infra-udm/v0.1/dictionary/lists?item_list=encoder_producer`,
    })
}