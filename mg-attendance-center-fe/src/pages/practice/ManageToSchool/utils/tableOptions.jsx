/*
 * @Author: xiedan WX042
 * @since: 2020-10-28 17:23:48
 * @lastTime: 2021-07-12 13:51:59
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\practice\ManageToSchool\utils\tableOptions.jsx
 * @Description: 表格配置项
 */
import { Table } from '@hz-components/react-base';
import moment from 'moment';
// import CommonTransfer from '@components/CommonTransfer';
import { DATE_TYPE, PLACEHOLDER, LEAVE_STATUS } from '@constants';
import styles from '../styles/index.less';

const { Ellipsis, RangePicker, EnumSelect } = Table;
const format = 'YYYY-MM-DD';

/**
 * @description: 筛选配置
 * @param {*}
 * @return {*}
 */
export const createSearchBarOptions = () => {
    return {
        trigger: null,
        conditions: [
            {
                label: "实习状态",
                render: (getFieldDecorator) => {
                    return (
                        getFieldDecorator("status", {
                            initialValue: "",
                        })(
                            <EnumSelect
                                hasAll
                                list={LEAVE_STATUS}
                            />
                        )
                    )
                },
            },
            {
                label: '实习时段',
                render: getFieldDecorator => {
                    return getFieldDecorator('times')(
                        <RangePicker
                            dropdownClassName="dateDropdown"
                            format={format}
                            showTime={{ format: "HH:mm:ss" }}
                        />,
                    );
                },
            },
        ],
    }
}

/**
 * @description: 表格列
 * @param {*}
 * @return {*}
 */
export const tableColumns = (obj) => {
    const { handleEdit } = obj;
    return [
        {
            title: '实习状态',
            dataIndex: 'status',
            width: '10%',
            render: text => {
                const str = LEAVE_STATUS.find(x => x.code === text) || {};
                return (
                    <Ellipsis>
                        <div style={{ color: str.color || '#333' }}>
                            {str.name || PLACEHOLDER}
                        </div>
                    </Ellipsis>
                )
            },
            sorter: true,
            defaultSortOrder: 'ascend',
            sortDirections: ['descend', 'ascend'],
        },
        {
            title: '学生姓名',
            dataIndex: 'person_names',
            width: '25%',
            render: (text, record, index) => (
                <Ellipsis>
                    <div
                        className={styles.linkText}
                        onClick={() => handleEdit(record, index)}
                    >{text || '--'}</div>
                </Ellipsis>
            ),
        },
        {
            title: '实习时段',
            dataIndex: '$times',
            width: '15%',
            render: (text, record) => {
                const { start_time, end_time } = record || {};
                return <Ellipsis>{`${start_time && start_time.substr(0,10) || '--'} ~ ${end_time && end_time.substr(0,10) || '--'}`}</Ellipsis>;
            },
        },
        {
            title: '上报人',
            dataIndex: 'creator_name',
            width: '10%',
            render: (text) => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        {
            title: '上报人角色',
            dataIndex: 'creator_role_name',
            width: '10%',
            render: (text) => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        {
            title: '上报时间',
            dataIndex: 'reported_at',
            width: '15%',
            render: text => {
                const time = text ? moment(text).format(DATE_TYPE) : PLACEHOLDER;
                return <Ellipsis>{time}</Ellipsis>;
            },
        },
        // {
        //     title: '操作',
        //     dataIndex: 'x',
        //     width: '10%',
        //     render: (t, record) => {
        //         const code = record.status * 1;
        //         const flag = code !== 1;
        //         return (
        //             <div>
        //                 <span
        //                     className={styles.linkText}
        //                     style={{
        //                         marginRight: 14,
        //                         color: flag && '#cccccc',
        //                         cursor: flag && 'not-allowed',
        //                     }}
        //                     onClick={() => { !flag && handleReport(record.id) }}
        //                 >
        //                     上报
        //                 </span>
        //                 <span
        //                     className={styles.linkText}
        //                     style={{
        //                         marginRight: 14,
        //                         color: flag && '#cccccc',
        //                         cursor: flag && 'not-allowed',
        //                     }}
        //                     onClick={() => { !flag && handleDel(record.id) }}
        //                 >
        //                     删除
        //                 </span>
        //             </div>
        //         )
        //     },
        // },
    ]
}
