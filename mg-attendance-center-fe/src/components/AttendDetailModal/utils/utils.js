/*
 * @Author: xiedan WX042
 * @since: 2020-11-07 11:53:34
 * @lastTime: 2021-01-30 20:08:23
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\components\AttendDetailModal\utils\utils.js
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

export const dataLabel = {
    // 日常明细--考勤信息
    "attendance_info_daily": {
        "task_name": "考勤任务",
        "ats_day": "考勤日期",
        "class_time": "考勤时间",
    },
    // 日常明细--考勤信息--没有考勤时间
    "attendance_info_daily_noTime": {
        "task_name": "考勤任务",
        "ats_day": "考勤日期",
    },
    // 抽查明细--考勤信息
    "attendance_info": {
        "task": "考勤任务",
        "create_user": "创建人",
        "create_role": "创建人角色",
        "create_organization": "创建人组织",
        "time": "考勤日期",
    },
    // 请假信息
    "leave_info": {
        "type": "请假类型",
        "time": "请假时段",
        "reason": "请假原因",
    },
    // 实习信息
    "practice_info": {
        "time": "实习时段",
        "practice_company": "实习单位",
        'company_contacts': '单位联系人',
        'contacts_phone': '联系人电话',
    },
    // 学生信息
    "person_info": {
        "person_name": "姓名",
        "gender_name": "性别",
        "person_no": "学号",
        "admission_time": "入学时间",
        "index_path_name": "所属组织",
    },
}
