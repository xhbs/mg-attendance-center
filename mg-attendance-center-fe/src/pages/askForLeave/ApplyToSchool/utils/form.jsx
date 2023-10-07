/*
 * @Author: xiedan WX042
 * @since: 2020-11-03 14:45:54
 * @lastTime: 2021-02-01 15:28:25
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\askForLeave\ApplyToSchool\utils\form.jsx
 * @Description: 编辑弹框表单
 */
import React from 'react';
import { Input, DatePicker } from 'antd';
import { EnumSelect } from '@hz-components/react-base';
import TransferTable from "@components/TransferTable";
import { LEAVE_TYPE } from "@constants";

const { TextArea } = Input;
const { RangePicker } = DatePicker;
// const { Option } = Select;

const dateFormat = 'YYYY-MM-DD';

// 请假页面的表单
export const AddAskForLeaveForm = (props) => {
    return [{
        label: '请假学生',
        comp: {
            fieldName: 'person',
            component: (
                <TransferTable />
            ),
            options: {
                initialValue: null,
                rules: [
                    { required: true, message: '请选择请假学生' },
                ],
            },
        },
    },
    {
        label: '请假类型',
        comp: {
            fieldName: 'type',
            component: (
                <EnumSelect
                    style={{width: 340}}
                    list={LEAVE_TYPE}
                />
            ),
            options: {
                initialValue: LEAVE_TYPE[0].code || '',
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
                    style={{width: 340}}
                    dropdownClassName="dateDropdown"
                    format={dateFormat}
                />
            ),
            options: {
                initialValue: '',
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
                initialValue: '',
                rules: [{
                    validator: (rules, val, callBack) => {
                        if (val && val.length > 200 ) {
                            callBack(`请假原因不能超过200个字符`);
                        } else {
                            callBack();
                        }
                    },
                }],
            },
        },
    }];
}
