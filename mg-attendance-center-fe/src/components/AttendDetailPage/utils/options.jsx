/*
 * @Description:
 * @Author: zhangjie
 * @Date: 2020-07-15 15:17:23
 */
import React from 'react';
import { Button } from 'antd';
import moment from 'moment';
import { Table } from '@hz-components/react-base';
import CommonTransfer from '@components/CommonTransfer';
import { enmuCheckTask } from "@services/detailPage";
import { ATTEND_DETAIL_STATUS, SCHOOL, PROVINCE } from '@/constants';
import { disabledDateBefore } from '@utils/utils';

const { Ellipsis, RangePicker, EnumSelect } = Table;
const format = 'YYYY-MM-DD';

export function tableColumns(obj) {
	const { userRole, type, columns, handleDetail, handleStatusChange } = obj;
	let cols = [...columns];
	// 校级抽查不展示抽查任务列
	if(userRole === SCHOOL && type === 'check') {
		cols = cols.filter(i => i.code !== 'task');
	}

	const len = cols && cols.length || 0;
	const data = cols.map((v, i) => {
		return {
			title: () => <div style={{maxWidth: 128}}><Ellipsis>{v.name}</Ellipsis></div>,
			key: i,
			dataIndex: v.code,
			width: len < 10 ? '15%' : 150,
			render: text => {
				// 抽查明细状态列颜色显示
				if(type === 'check' && v.code === 'status') {
					const target = ATTEND_DETAIL_STATUS.find(item => Number(text) === item.code) || {};
					return <div style={{color: target.color}}>{target.name || '--'}</div>
				}
				if(v.code === 'attendance_date') {
					const str = text && moment(text).format(format) || '--';
					return <Ellipsis>{str}</Ellipsis>
				}
				// 日常明细状态列颜色显示
				if(type === 'daily' && v.code === 'result') {
					// 通过判断最后两个字符
					const target = ATTEND_DETAIL_STATUS.find(item => text === item.name) || {};
					return <div style={{color: target.color || "#ccc"}}>{text || '--'}</div>
				}
				return <Ellipsis>{text || '--'}</Ellipsis>
			},
			className: 'ellipsis',
			sorter: v.sorter,
			defaultSortOrder: 'descend',
		};
	});
	data.push({
		title: '操作',
		dataIndex: 'operation',
		width: 150,
		fixed: len < 10 ? '' : 'right',
		render: (text, record, index) => {
			return (
				<div>
					<a className="table-link" onClick={() => handleDetail(record, index)} style={{marginRight: 16}}>
						查看明细
					</a>
					{
						userRole === PROVINCE && (
							<a className="table-link" onClick={() => handleStatusChange(record)}>
								状态更改
							</a>
						)
					}
				</div>
			);
		},
	})
	return data;
}

export function tableSearchOptions(props) {
	// type区分日常考勤和抽查考勤  daily  check
	const { type = 'daily', userRole, statusList, value: {time, defaultKeysArr, defaultCheckTask} } = props;

	const option = userRole === SCHOOL || type === 'daily' ? {
		label: '考勤日期',
		render: getFieldDecorator => {
			return getFieldDecorator('date', {
				initialValue: time,
			})(
				<RangePicker
					disabledDate={disabledDateBefore}
					dropdownClassName="dateDropdown"
					format={format}
					showTime={{ format: "HH:mm:ss" }}
				/>,
			);
		},
	} : {
		label: '抽查任务',
		render: getFieldDecorator => {
			return getFieldDecorator('check_task')(
				<CommonTransfer
					getTree={() => {
						return new Promise((resolve) => {
							enmuCheckTask({
								task_name: '',
								task_type: 1,
							}).then(res => {
								resolve({
									isSuccess: true,
									msg: '',
									data: res && res.data || [],
								});
							})
						});
					}}
					defaultKeys={defaultCheckTask}
					treeTarget={{}}
					treeSetting={{
						dataKey: 'task_id',
						dataViewKey: 'task_name',
						parentKey: '',
						childArrayKey: '',
					}}
					overlayType='popover'
					getPopupContainer={null}
					customButtonValue={(value) => {
						return (
							<Button style={{width: 220}}>
								{
									defaultCheckTask.length > 0 ? `已选${value || defaultCheckTask.length}个任务` : '全选'
								}
							</Button>
						)
					}}
					searchSetting={{
						placeholder: '搜索任务',
					}}
					mode='tree'
				/>,
			);
		},
	}

	return {
		trigger: null,
		conditions: [
			{
				label: '考勤状态',
				render: getFieldDecorator => {
					return getFieldDecorator('status', {
						initialValue: '',
					})(<EnumSelect hasAll list={statusList} />);
				},
			},
			{...option},
			{
				label: '所属组织',
				render: getFieldDecorator => {
					return getFieldDecorator('organization')(
                        <CommonTransfer
                            type='organization'
                            defaultKeys={defaultKeysArr}
							treeTarget={{}}
							overlayType='popover'
							getPopupContainer={null}
							customButtonValue={(value) => {
                                return (
                                    <Button style={{width: 220}}>
                                        {
                                            defaultKeysArr.length > 0 ? `已选${value || defaultKeysArr.length}个组织` : '全选'
                                        }
                                    </Button>
                                )
							}}
							checkStrictly
                        />,
					);
				},
			},
		],
	};
}

export const listColumns = (obj) => {
	const { handleStatusChange = () => {} } = obj;

	return [{
		title: '规则',
		dataIndex: 'express_name',
		width: '25%',
	},
	{
		title: '打卡时间',
		dataIndex: 'pass_time',
		width: '30%',
		render: (text) => <div>{text && moment(text).format('YYYY-MM-DD HH:mm:ss') || '--'}</div>,
	},
	{
		title: '结果',
		dataIndex: 'result',
		width: '25%',
	},{
		title: '操作',
		dataIndex: 'operation',
		render: (text, record) => {
			return (
				<div>
					<a className="table-link" onClick={() => handleStatusChange(record)}>
						状态更改
					</a>
				</div>
			);
		},
	}];
}
