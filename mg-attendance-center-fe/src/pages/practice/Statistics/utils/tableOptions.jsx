/*
 * @Author: xiedan WX042
 * @since: 2020-10-28 17:23:48
 * @lastTime: 2021-09-18 17:37:11
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\practice\Statistics\utils\tableOptions.jsx
 * @Description: 表格配置项
 */

import { Table } from '@hz-components/react-base';
import { PRACTICE_TYPE, PLACEHOLDER, ATTEND_DETAIL_STATUS } from '@/constants';
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
                        getFieldDecorator("practice_status", {
                            initialValue: "",
                        })(
                            <EnumSelect
                                hasAll
                                list={PRACTICE_TYPE}
                            />
                        )
                    )
                },
            },
            {
                label: '实习时段',
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
    const { handleEdit, historyDetail } = obj;
    return [
        {
            title: '实习状态',
            dataIndex: 'practice_status',
            width: '10%',
            render: text => {
                const str = PRACTICE_TYPE.find(x => x.code === text) || {};
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
            title: '实习时段',
            dataIndex: '$time',
            width: '15%',
            render: (text, record) => {
                const { start_time, end_time } = record || {};
                return <Ellipsis>{`${start_time && start_time.substr(0,10) || '--'} ~ ${end_time && end_time.substr(0,10) || '--'}`}</Ellipsis>;
            },
        },
        {
            title: '所属组织',
            dataIndex: 'org_path_name',
            width: '20%',
            render: (text) => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        {
            title: '操作',
            dataIndex: '$x',
            width: '10%',
            render: (t, record) => {
                return (
                    <span
                        className={styles.linkText}
                        style={{
                            marginRight: 14,
                            color: record.practice_status !== 1 && '#999',
                            cursor: record.practice_status !== 1 && 'not-allowed',
                        }}
                        onClick={() => { record.practice_status === 1 && historyDetail(record) }}
                    >
                        历史考勤
                    </span>
                )
            },
        },
    ]
}

/**
 * @description: 历史考勤列表
 * @param {*}
 * @return {*}
 */

export const smallColumns = (currentPage) => {
    return [
        {
            title: '序号',
            dataIndex: '$index',
            width: '15%',
            render: (text, record, index) => ((currentPage - 1) * 10) + (index + 1),
        },
        {
            title: '考勤状态',
            dataIndex: 'type',
            width: '35%',
            render: text => {
                const target = ATTEND_DETAIL_STATUS.find(x => x.code === Number(text)) || {};
                return (
                    <Ellipsis>
                        <div style={{ color: target.color && '#ccc' }}>
                            {target.name || '--'}
                        </div>
                    </Ellipsis>
                )
            },
        },
        {
            title: '考勤日期',
            dataIndex: 'date',
            width: '50%',
            render: (text, record) => <Ellipsis>{text}</Ellipsis>,
        },
    ]
}