/*
 * @Author: xiedan WX042
 * @since: 2020-11-03 14:45:54
 * @lastTime: 2021-03-29 11:39:11
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\components\LeaveAndPracticeModal\utils\form.jsx
 * @Description: 编辑弹框表单
 */
import React from 'react';
import { Input, DatePicker } from 'antd';
import moment from 'moment';
import { EnumSelect } from '@hz-components/react-base';
import { LEAVE_TYPE } from "@constants";
import noPersonImg from '@hz-design/base/public/person-empty.svg';

const { TextArea } = Input;
const { RangePicker } = DatePicker;

const dateFormat = 'YYYY-MM-DD';

// 弹框里面的表单
export const DetailForm = (props) => {
    const { detailData = {} } = props;
    return [{
        label: '请假学生照',
        comp: {
            fieldName: 'person_url',
            component: (
                <div style={{width: 120}}>
                    <div className="hz-rate-image-vertical">
                        <div className="rate-container">
                            <img
                                alt=''
                                src={detailData.person_url || noPersonImg}
                                onError={(e) => {
                                    e.target.onerror = null;
                                    e.target.src = noPersonImg;
                                }}
                                style={{width: '100%', maxHeight: '100%'}}
                            />
                        </div>
                    </div>
                </div>
            ),
        },
    },
    {
        label: '学生姓名',
        comp: {
            fieldName: 'person',
            component: (
                <div>{detailData.person_name || '--'}</div>
            ),
            options: {
                initialValue: [{person_no: detailData.person_no}] || '',
            },
        },
    },
    {
        label: '学号',
        comp: {
            fieldName: 'person_no',
            component: (
                <div>{detailData.person_no || '--'}</div>
            ),
            options: {
                initialValue: detailData.person_no || '',
            },
        },
    },
    {
        label: '班级',
        comp: {
            fieldName: 'department',
            component: (
                <div>{detailData.department_name || '--'}</div>
            ),
            options: {
                initialValue: detailData.department_name || '',
            },
        },
    },
    {
        label: '请假类型',
        comp: {
            fieldName: 'type',
            component: (
                <EnumSelect list={LEAVE_TYPE} />
            ),
            options: {
                initialValue: detailData.type || '',
                rules: [{
                    required: true,
                    message: '请选择请假类型',
                }],
            },
        },
    },
    {
        label: '请假时段',
        comp: {
            fieldName: 'date',
            component: (
                <RangePicker
                    dropdownClassName="dateDropdown"
                    format={dateFormat}
                />
            ),
            options: {
                initialValue: [detailData.start_time && moment(detailData.start_time) || null, detailData.end_time && moment(detailData.end_time) || null] || [],
                rules: [{
                    required: true,
                    message: '请选择请假时段',
                }],
            },
        },
    },
    {
        label: '请假原因',
        comp: {
            fieldName: 'reason',
            component: (
                <TextArea rows={5} />
            ),
            options: {
                initialValue: detailData.reason || '',
            },
        },
    }];
}
