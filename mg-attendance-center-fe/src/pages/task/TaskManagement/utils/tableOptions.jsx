/*
 * @Author: xiedan WX042
 * @since: 2020-10-28 17:23:48
 * @lastTime: 2021-01-25 16:26:58
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\task\TaskManagement\utils\tableOptions.jsx
 * @Description: 表格配置项
 */

import { Table, EnumSelect } from '@hz-components/react-base';
import moment from 'moment';
import { Select } from 'antd';
import { DATE_TYPE } from '@constants';
import styles from '../styles/index.less';

const { Ellipsis } = Table;
const { Option } = Select;

/**
 * @description: 筛选配置
 * @param {*}
 * @return {*}
 */
export const createSearchBarOptions = (obj) => {
    // isHigh:是否高级  false--云南中职  true--原来的功能
    // isTaskType: 是否有任务类型筛选  只有省级才有
    const { rulesEnumList, isHigh, isTaskType, onChangeType = () => {}, onChangeRule = () => {}, value: {ruleValue} } = obj;
    const rule = !isHigh ? [{
        label: "考勤规则",
        render: () => {
            return (
                <Select
                    defaultValue=''
                    value={ruleValue}
                    style={{ width: 120 }}
                    onChange={onChangeRule}
                >
                    <Option key='-1' value=''>不限</Option>
                    {
                        rulesEnumList.map((item, index) => {
                            return (
                                <Option key={index} value={item.rule_id}>{item.rule_name}</Option>
                            )
                        })
                    }
                </Select>
            )
        },
    }] : [];

    const taskType = (!isHigh && isTaskType) ? [{
        label: "任务类型",
        render: () => {
            return (
                <EnumSelect
                    hasAll
                    list={[
                        {code: '0', name: '日常考勤'},
                        {code: '1', name: '抽查考勤'},
                    ]}
                    defaultValue=''
                    onChange={onChangeType}
                />
            )
        },
    }] : [];

    return {
        trigger: null,
        conditions: [
            {
                label: "任务状态",
                render: (getFieldDecorator) => {
                    return (
                        getFieldDecorator("taskStatus", {
                            initialValue: "",
                        })(
                            <EnumSelect
                                hasAll
                                createPromise={() => {
                                    return new Promise(resolve => {
                                        resolve([
                                            { code: 'running', name: '启用' },
                                            { code: 'stop', name: '禁用' },
                                        ]);
                                    });
                                }}
                            />
                        )
                    )
                },
            },
            ...taskType,
            ...rule,
        ],
    }
}

/**
 * @description: 表格列
 * @param {*}
 * @return {*}
 */
export const tableColumns = (obj) => {
    const { handleEdit, useing, confirmDelete, isHigh, isTaskType } = obj;
    const taskType = !isHigh && isTaskType ? [{
        title: '任务类型',
        dataIndex: 'task_type_name',
        width: '10%',
        render: text => <Ellipsis>{text || '--'}</Ellipsis>,
    }] : [];

    const hight = !isHigh ? [{
        title: '考勤地点',
        dataIndex: 'site_names',
        width: '10%',
        render: text => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        {
        title: '考勤对象',
        dataIndex: 'group_names',
        width: '10%',
        render: text => <Ellipsis>{text || '--'}</Ellipsis>,
        }] : [];

    return [
        {
        title: '任务状态',
        dataIndex: 'status_name',
        width: '10%',
        render: text => <span style={{ color: text === '启用' ? '#52c41a' : '#aaa' }}>{text || '--'}</span>,
        },
        {
        title: '任务名称',
        dataIndex: 'name',
        width: '15%',
        render: (text, record) => (
            <Ellipsis>
                <div className={styles.linkText} onClick={() => handleEdit(record)}>{text || '--'}</div>
            </Ellipsis>
        ),
        },
        {
        title: '考勤规则',
        dataIndex: 'rule_name',
        width: '15%',
        render: text => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        /****新增****/
        ...taskType,
        ...hight,
        /*******/
        {
        title: '创建时间',
        dataIndex: 'create_time',
        width: '15%',
        render: text => <Ellipsis>{text && moment(text).format(DATE_TYPE) || '--'}</Ellipsis>,
        },
        {
        title: '创建人',
        dataIndex: 'user_name',
        width: '10%',
        render: text => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        {
        title: '操作',
        dataIndex: 'x',
        width: '10%',
        align: 'center',
        render: (t, record) => {
            return (
            <div>
                <span
                    className={styles.linkText}
                    style={{marginRight: 14}}
                    onClick={()=>{ useing(record)
                }}>
                    {record.status_name === '禁用' ? '启用' : '禁用'}
                </span>
                <span className={styles.linkText} onClick={()=>{ confirmDelete(record.id) }}>删除</span>
            </div>
            )
        },
        },
    ]
}
