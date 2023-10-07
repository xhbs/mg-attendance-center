/*
 * @Author: heqiang 00621
 * @Date: 2020-07-14 15:13:13
 * @Description: 搜索区域配置
 */

import React from 'react';
import { Table } from '@hz-components/react-base';
import { DATE_TYPE, ADJUST_TYPE } from '@constants';

const { EnumSelect, RangePicker } = Table;
const showTime = 'HH:mm:ss';
const time_pla = ['开始时间', '结束时间'];

function searchBarOptions() {

    return {
        /* trigger 自定义查询按钮 */
        // trigger: "分析",
        // trigger: null,
        trigger: null,
        conditions: [
            {
                label: '调整开始时间',
                render: (getFieldDecorator) => {
                    return getFieldDecorator('adjust_start_time')(
                        <RangePicker
                            showTime={{ format: showTime }}
                            format={DATE_TYPE}
                            placeholder={time_pla}
                        />,
                    );
                },
            },
            {
                label: '调整结束时间',
                render: (getFieldDecorator) => {
                    return getFieldDecorator('adjust_end_time')(
                        <RangePicker
                            showTime={{ format: showTime }}
                            format={DATE_TYPE}
                            placeholder={time_pla}
                        />,
                    );
                },
            },
            {
                label: '调整类型',
                render: (getFieldDecorator) => {
                    return getFieldDecorator('adjust_type', { initialValue: '' })(
                        <EnumSelect
                            hasAll
                            list={ADJUST_TYPE}
                        />);
                },
            },
        ],
    };
}

export default searchBarOptions;
