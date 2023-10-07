/*
 * @Author: xiedan WX042
 * @since: 2020-10-28 17:23:48
 * @lastTime: 2021-10-18 09:40:37
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\practice\Statistics\utils\tableOptions.jsx
 * @Description: 表格配置项
 */

import { Table } from '@hz-components/react-base';
// import moment from 'moment';
import { ATTEND_STATUS, LEAVE_TYPE, PLACEHOLDER } from '@/constants';
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
                label: "请假状态",
                render: (getFieldDecorator) => {
                    return (
                        getFieldDecorator("leave_status", {
                            initialValue: "",
                        })(
                            <EnumSelect
                                hasAll
                                list={ATTEND_STATUS}
                            />
                        )
                    )
                },
            },
            {
                label: "请假类型",
                render: (getFieldDecorator) => {
                    return (
                        getFieldDecorator("leave_type", {
                            initialValue: "",
                        })(
                            <EnumSelect
                                hasAll
                                list={LEAVE_TYPE}
                            />
                        )
                    )
                },
            },
            {
                label: '请假时段',
                render: getFieldDecorator => {
                    return getFieldDecorator('times', {
                        initialValue: '',
                    })(
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
            title: '请假状态',
            dataIndex: 'leave_state',
            width: '10%',
            render: text => {
                const str = ATTEND_STATUS.find(x => x.code === text) || {};
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
            dataIndex: 'person_name',
            width: '15%',
            render: (text, record, index) => (
                <Ellipsis>
                    <div className={styles.linkText} onClick={() => handleEdit(record, index)}>{text || '--'}</div>
                </Ellipsis>
            ),
        },
        {
            title: '学号',
            dataIndex: 'person_no',
            width: '15%',
            render: text => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        {
            title: '请假类型',
            dataIndex: 'type',
            width: '10%',
            render: text => {
                const str = LEAVE_TYPE.find(x => x.code === text) || {};
                return (
                    <Ellipsis>{str.name || PLACEHOLDER}</Ellipsis>
                )
            },
        },
        {
          title: '学校',
          dataIndex: 'school_name',
          width: '15%',
          render: text => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        {
          title: '班级',
          dataIndex: 'org_name',
          width: '15%',
          render: text => <Ellipsis>{text || '--'}</Ellipsis>,
        }, 
        {
          title: '上报人',
          dataIndex: 'creator_name',
          width: '10%',
          render: (text) => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        {
          title: '审核人',
          dataIndex: 'approved_by',
          width: '10%',
          render: (text) => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        // {
        //     title: '请假时段',
        //     dataIndex: '$time',
        //     width: '20%',
        //     render: (text, record) => {
        //         const { start_date, end_date } = record || {};
        //         return <Ellipsis>{`${start_date && start_date.substr(0,10) || '--'} ~ ${end_date && end_date.substr(0,10) || '--'}`}</Ellipsis>;
        //     },
        // },
        // {
        //     title: '所属组织',
        //     dataIndex: 'org_path_name',
        //     width: '20%',
        //     render: (text) => <Ellipsis>{text || '--'}</Ellipsis>,
        // },
    ]
}
