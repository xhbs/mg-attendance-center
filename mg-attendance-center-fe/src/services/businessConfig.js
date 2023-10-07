/*
 * @Author: heqiang 00621
 * @Date: 2020-12-15 09:46:41
 * @Description: 业务配置
 */

import request from '@utils/request';

// 获取抽查业务配置
export function getCheckBusinessConfig() {
    return request({
        method: 'get',
        url: `/api/biz-scene/v1/attendance/business-config/extract`,
    });
}

// 修改抽查业务配置
export function putCheckBusinessConfig(params) {
    return request({
        method: 'PUT',
        url: `/api/biz-scene/v1/attendance/business-config/extract`,
        data: params,
    });
}

/**
 * @name: 申诉，时间选择范围天数
 */
export async function getDateConfig() {
  return request({
      method: 'get',
      url: `/api/biz-scene/v1/attendance/system-configs`,
  });
}

// 获取在离校配置
export function getSchoolStatusConfig() {
    return request({
        method: 'get',
        url: `/api/biz-scene/v1/attendance/business-config/on-off-school`,
    });
}

// 修改在离校配置

export function putSchoolStatusConfig(data) {
    return request({
        method: 'put',
        url: `/api/biz-scene/v1/attendance/business-config/on-off-school`,
        data,
    });
}
