/*
 * @Author: heqiang 00621
 * @Date: 2020-12-16 14:30:45
 * @Description: 实习统计--省级
 */

import request from '@utils/request';
import { stringify } from 'qs';

// 分页获取实习统计
export const queryPracticeStatistics = params => {
    return request({
        method: 'POST',
        url: `/api/biz-scene/v1/attendance/practice-persons`,
        data: params,
    })
}

// 导出
export const exportPracticeStatistics = params => {
    return request({
        method: 'POST',
        url: `/api/biz-scene/v1/attendance/practice-persons/export`,
        data: params,
		responseType: 'blob',
    })
}

// 查询详情
export function getStaticDetail(params){
    return request({
        url: `/api/biz-scene/v1/attendance/practice-persons/${params}`,
        method: 'GET',
    });
}

// 查询历史考勤
export function getHistoryDetail(params = {}){
    return request({
        url: `/api/biz-scene/v1/attendance/result/history?${stringify(params)}`,
        method: 'GET',
    });
}