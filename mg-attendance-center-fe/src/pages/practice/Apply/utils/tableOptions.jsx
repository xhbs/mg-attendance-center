/*
 * @Author: xiedan WX042
 * @since: 2020-10-28 17:23:48
 * @lastTime: 2021-08-31 10:17:53
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\practice\Apply\utils\tableOptions.jsx
 * @Description: 表格配置项
 */

import { Table } from '@hz-components/react-base';
import moment from 'moment';
import { DATE_TYPE, DATE_TYPE_DAY, LEAVE_STATUS } from '@constants';
import styles from '../styles/index.less';

const { Ellipsis, RangePicker, EnumSelect } = Table;

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
                label: "处理状态",
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
                    return getFieldDecorator('date', {
                        initialValue: '',
                    })(
                        <RangePicker
                            dropdownClassName="dateDropdown"
                            format={DATE_TYPE_DAY}
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
    const { handleEdit, deal } = obj;

    return [
        {
        title: '处理状态',
        dataIndex: 'status',
        width: '8%',
        render: text => {
          const ume = LEAVE_STATUS;
            const target = ume.find(item => text === item.code) || {};
            return <div style={{color: target.color}}>{target.name || '--'}</div>
        },
        sorter: true,
        defaultSortOrder: 'ascend',
        sortDirections: ['descend', 'ascend'],
        },
        {
        title: '学生姓名',
        dataIndex: 'person_names',
        width: '17%',
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
        dataIndex: '$time',
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
        render: text => <Ellipsis>{text && moment(text).format(DATE_TYPE) || '--'}</Ellipsis>,
        },
        {
          title: '操作',
          dataIndex: 'x',
          width: '10%',
          render: (t, record) => {
              return (
                  <span
                      className={styles.linkText}
                      style={{
                          marginRight: 14,
                          color: record.status !== 2 && '#999',
                          cursor: record.status !== 2 && 'not-allowed',
                      }}
                      onClick={()=>{record.status === 2 && deal(record)}}
                  >
                      处置
                  </span>
              )
          },
        },
    ]
}
