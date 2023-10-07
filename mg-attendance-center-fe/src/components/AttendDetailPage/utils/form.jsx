import React from 'react';
import { Radio, Input } from 'antd';
const { TextArea } = Input;

export const commonForm = (props) => {
    const { statusList, statusValue = '' } = props;

    return [
        {
            label: '考勤状态',
            comp: {
                fieldName: 'status',
                component: (
                    <Radio.Group>
                        {
                            statusList.map(item => {
                                return <Radio value={item.code}>{item.name}</Radio>
                            })
                        }
                    </Radio.Group>
                ),
                options: {
                    initialValue: statusValue,
                    rules: [{
                        required: true,
                        message: '请选择考勤状态',
                    }],
                },
            },
        },
        {
            label: '原因备注',
            comp: {
                fieldName: 'reason',
                component: (
                    <TextArea rows={4} />
                ),
                options: {
                    initialValue: '',
                    rules: [{
                        validator: (rules, val, callBack) => {
                            if (val && val.length > 200 ) {
                                callBack(`原因备注不能超过200个字符`);
                            } else {
                                callBack();
                            }
                        },
                    }],
                },
            },
        },
    ]
} 