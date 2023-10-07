/*
 * @Author: heqiang 00621
 * @Date: 2020-07-14 15:15:28
 * @Description: 表格配置
 */

import React from 'react';
import moment from 'moment';
import { Table } from '@hz-components/react-base';
import { PLACEHOLDER, DATE_TYPE, ADJUST_TYPE } from '@constants';

const { Ellipsis } = Table;

export default function columnsObj(props) {
    const { handleDeleteClick } = props || {};

    return [
        {
            title: "申请单号",
            dataIndex: "adjust_no",
            width: "15%",
            render: (text) => <Ellipsis> {text || PLACEHOLDER}</Ellipsis>,
        },
        {
            title: "对象名称",
            dataIndex: "person_name",
            width: "10%",
            render: (text) => <Ellipsis> {text || PLACEHOLDER}</Ellipsis>,
        },
        {
            title: "对象部门",
            dataIndex: "department_name",
            width: "10%",
            render: (text) => <Ellipsis> {text || PLACEHOLDER}</Ellipsis>,
        },
        {
            title: "开始时间",
            dataIndex: "start_adjust_time",
            width: "15%",
            render: text => <Ellipsis>{text ? moment(text).format(DATE_TYPE) : PLACEHOLDER}</Ellipsis>,
        },
        {
            title: "结束时间",
            dataIndex: "end_adjust_time",
            width: "15%",
            render: text => <Ellipsis>{text ? moment(text).format(DATE_TYPE) : PLACEHOLDER}</Ellipsis>,
        },
        {
            title: "调整类型",
            dataIndex: "type",
            width: "12%",
            render: code => {
                const item = ADJUST_TYPE.find(x => x.code === code);
                return <Ellipsis> {item && item.name || PLACEHOLDER}</Ellipsis>;
            },
        },
        {
            title: "调整原因",
            dataIndex: "reason",
            width: "15%",
            render: (text) => <Ellipsis> {text || PLACEHOLDER}</Ellipsis>,
        },
        {
            title: "操作",
            dataIndex: "operation",
            width: "8%",
            render: (text, record) => {
                return (
                    <a
                        className="table-link"
                        onClick={() => handleDeleteClick(record)}
                    >
                        删除
                    </a>
                )
            },
        },
    ]
}
