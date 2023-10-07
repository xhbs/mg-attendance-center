/*
 * @Author: xiedan WX042
 * @since: 2020-10-28 17:23:48
 * @lastTime: 2021-01-25 16:27:48
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\task\Rules\utils\tableOptions.jsx
 * @Description: 表格配置项
 */

import { Table } from '@hz-components/react-base';
import moment from 'moment';
import { Input, TimePicker } from 'antd';
import { DATE_TYPE } from '@constants';
import styles from '../styles/index.less';

const { Ellipsis, EnumSelect } = Table;

/**
 * @description: 表格列  云南中职
 * @param {*}
 * @return {*}
 */
export const tableColumns = (obj) => {
    const { handleEdit, confirmDelete, isRulesType = false } = obj;

    const rulesType = isRulesType ? [{
        title: '规则类型',
        dataIndex: 'life_cycle_name',
        width: '10%',
        render: text => <Ellipsis>{text || '--'}</Ellipsis>,
    }] : [];

    return [
        {
            title: '规则名称',
            dataIndex: 'rule_name',
            width: isRulesType ? '20%' : '30%',
            render: (text, record) => (
                <Ellipsis>
                    <div className={styles.linkText} onClick={() => handleEdit(record)}>{text || '--'}</div>
                </Ellipsis>
            ),
        },
        ...rulesType,
        {
            title: '创建时间',
            dataIndex: 'create_time',
            width: '30%',
            render: text => <Ellipsis>{text && moment(text).format(DATE_TYPE) || '--'}</Ellipsis>,
        },
        {
            title: '创建人',
            dataIndex: 'user_name',
            width: '25%',
            render: text => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        {
            title: '操作',
            dataIndex: 'x',
            width: '15%',
            align: 'center',
            render: (t, record) => {
                return (
                    <div className={styles.linkText} onClick={() => { confirmDelete(record.id) }}>删除</div>
                )
            },
        },
    ]
}

/**
 * @description: 表单页编辑表格的配置
 * @param {*}
 * @return {*}
 */
export const editTableColums = (obj = {}) => {
    // 是否可编辑
    const { isEdit = true } = obj;

    return [
        {
            title: '时段名称',
            dataIndex: 'express_name',
            width: '28%',
            render: (text, record, index, getProps) => {
                return (
                    <Input
                        {...getProps({
                            initialValue: text || '',
                            rules: [
                                {
                                    required: true,
                                    message: '必填字段',
                                },
                            ],
                        })}
                        value={text || ''}
                        style={{ width: '100%' }}
                    />
                );
            },
        }, {
            title: '考勤开始时间',
            dataIndex: 'start',
            width: '25%',
            render: (text, record, index, getProps, {getTargetValue}) => {
                return (
                    <TimePicker
                        {...getProps({
                            initialValue: text ? moment(text, 'HH:mm:ss') : undefined,
                            rules: [
                                {
                                    required: true,
                                    message: '必填字段',
                                },
                            ],
                        })}
                        value={text ? moment(text, 'HH:mm:ss') : undefined}
                        defaultOpenValue={moment(moment(), 'HH:mm:ss')}
                        style={{ width: '100%' }}
                        disabled={!isEdit}
                    />
                );
            },
        }, {
            title: '考勤结束时间',
            dataIndex: 'end',
            width: '25%',
            render: (text, record, index, getProps, {getTargetValue}) => {
                return (
                    <TimePicker
                        {...getProps({
                            initialValue: text ? moment(text, 'HH:mm:ss') : undefined,
                            rules: [
                                {
                                    required: true,
                                    message: '必填字段',
                                },
								// {
									// validator: (rule, value, callback) => {
									// 	const startTime = getTargetValue('start');
									// 	if (!value || !startTime) {
									// 		callback();
									// 		return;
									// 	}
									// 	const composeStartTime = moment(startTime);
									// 	const composeEndTime = moment(value);;
									// 	if (composeStartTime > composeEndTime) {
									// 		callback('结束时间不能小于开始时间');
									// 	}
									// 	callback();
									// },
								// },
                            ],
                        })}
                        value={text ? moment(text, 'HH:mm:ss') : undefined}
                        defaultOpenValue={moment(moment(), 'HH:mm:ss')}
                        style={{ width: '100%' }}
                        disabled={!isEdit}
                    />
                );
            },
        },
    ];
}

// 老版的表格内容

export const tableColumnsHight = (obj) => {
    const { handleEdit, confirmDelete } = obj;
    return [
		{
		  title: '规则名称',
		  dataIndex: 'rule_name',
		  width: '20%',
		  render: (text, record) => (
			<Ellipsis>
				<div className={styles.linkText} onClick={() => handleEdit(record)}>{text}</div>
			</Ellipsis>
		  ),
		},
		{
		  title: '创建时间',
		  dataIndex: 'create_time',
		  width: '20%',
		  render: text => <Ellipsis>{text && moment(text).format(DATE_TYPE)}</Ellipsis>,
		},
		{
		  title: '创建人',
		  dataIndex: 'user_name',
		  width: '15%',
		  render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
		  title: '时间类型',
		  dataIndex: 'life_cycle_name',
		  width: '15%',
		  render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
		  title: '循环方式',
		  dataIndex: 'cycle_type_name',
		  width: '15%',
		  render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
		  title: '操作',
		  dataIndex: 'x',
		  width: '15%',
		  align: 'center',
		  render: (t, record) => {
			return (
			  <div className={styles.linkText} onClick={()=>{ confirmDelete(record.id) }}>删除</div>
			)
		  },
		},
	]
}

/**
 * @description: 筛选配置
 * @param {*}
 * @return {*}
 */
export const createSearchBarOptions = {
    trigger: null,
    conditions: [{
        label: "规则类型",
        render: (getFieldDecorator) => {
            return (
                getFieldDecorator("life_cycle", {
                    initialValue: "",
                })(
                    <EnumSelect
                        hasAll
                        list={[
                            {code: '0', name: '日常考勤'},
                            {code: '1', name: '抽查考勤'},
                        ]}
                    />
                )
            )
        },
    }],
}