/*
 * @Author: xiedan WX042
 * @since: 2020-11-07 11:53:34
 * @lastTime: 2020-11-18 15:24:41
 * @FilePath: \biz-scene-attendance-web\src\components\DetailContent\utils\utils.js
 * @Description: 
 */
import moment from 'moment';

// 考勤记录表格列
export const columns = [{
    title: '规则',
    dataIndex: 'express_name',
    width: '35%',
},
{
    title: '打卡时间',
    dataIndex: 'pass_time',
    width: '40%',
    render: (text) => <div>{text && moment(text).format('YYYY-MM-DD HH:mm:ss')}</div>,
},
{
    title: '结果',
    dataIndex: 'result',
    width: '25%',
}];