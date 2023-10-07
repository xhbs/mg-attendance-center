/*
 * @Author: xiedan WX042
 * @since: 2020-09-01 20:21:34
 * @lastTime: 2020-09-16 15:09:50
 * @FilePath: \biz-scene-coree:\hz\biz-scene-attendance-web\src\services\nosence.js
 * @Description: 
 */

import request from '@utils/request';
import { stringify } from 'qs';

/**
 * @description: 获取考勤面板标题
 * @param {type} {type}
 * @return {type} 
 */
export const getBoardTitle = async () => {
    return request({
        method: 'get',
        url: `/api/biz-scene/v1/attendance/sys/config`,
    })
}

/**
 * @description: 
 * @param {type} {type}
 * @return {type} 
 */
export const getPointPage = async (params) => {
    return request({
        method: 'get',
        url: `/api/uss/v1/ats/site/page/${stringify(params)}`,
    })
}