/*
 * @Author: xiedan WX042
 * @since: 2020-10-28 17:23:48
 * @lastTime: 2021-01-25 16:28:47
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\task\Address\utils\tableOptions.jsx
 * @Description: 表格配置项
 */

import { Table, EnumSelect } from '@hz-components/react-base';
import styles from '../styles/index.less';

const { Ellipsis } = Table;

/**
 * @description: 表格列  云南中职
 * @param {*}
 * @return {*}
 */
export const tableColumns = (obj) => {
    const { confirmDelete } = obj;
    return [
        {
        title: '设备名称',
        dataIndex: 'name',
        width: '20%',
        render: text => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        {
        title: '设备编码',
        dataIndex: 'device_code',
        width: '20%',
        render: text => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        {
        title: '厂商',
        dataIndex: 'producer',
        width: '20%',
        render: text => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        {
        title: 'IP地址',
        dataIndex: 'ip_addr',
        width: '20%',
        render: text => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        {
        title: '操作',
        dataIndex: 'x',
        width: '20%',
        align: 'center',
        render: (t, record) => {
            return (
            <div>
                <span className={styles.linkText} onClick={()=>{ confirmDelete(record.id) }}>删除</span>
            </div>
            )
        },
        },
    ]
}

/**
 * @description: 搜索栏配置项定义   云南中职屏蔽
 * @param {*}
 * @return {*}
 */
export const createSearchBarOptions = {
    trigger: null,
    conditions: [
        {
            label: "考勤点类型",
            render: (getFieldDecorator) => {
                return (
                    getFieldDecorator("type", {
                        initialValue: "",
                    })(
                        <EnumSelect
                            hasAll
                            createPromise={() => {
                                return new Promise(resolve => {
                                    resolve([
                                        { code: 1, name: '签到' },
                                        { code: 2, name: '签退' },
                                    ]);
                                });
                            }}
                        />
                    )
                )
            },
        },
    ],
}

/**
 * @description: 老版的表格列
 * @param {*}
 * @return {*}
 */
export const tableColumnsHight = (obj) => {
    const { confirmDelete = () => {} } = obj;
    return [
        {
        title: '考勤点类型',
        dataIndex: 'type',
        width: '20%',
        render: text => <Ellipsis>{text === 1 ? '签到' : text === 2 ? '签退' : '--'}</Ellipsis>,
        },
        {
        title: '考勤点名称',
        dataIndex: 'name',
        width: '60%',
        render: text => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        {
        title: '操作',
        dataIndex: 'x',
        width: '20%',
        align: 'center',
        render: (t, record) => {
            return (
            <div>
                <span className={styles.linkText} onClick={()=>{ confirmDelete(record.id) }}>删除</span>
            </div>
            )
        },
        },
    ]

}