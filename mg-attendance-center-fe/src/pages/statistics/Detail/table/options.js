/*
 * @Description:
 * @Author: zhangjie
 * @Date: 2020-07-15 15:17:23
 */
/* eslint-disable jsx-a11y/anchor-is-valid */

import { Table } from '@hz-components/react-base';
import moment from 'moment';
import { DATE_TYPE } from '@constants';

const { Ellipsis, RangePicker, EnumSelect } = Table;
const showTime = 'HH:mm:ss';

export function tableColumns(props) {
	const { handleDetail = () => {} } = props;
	const columns = [
		{
			title: '对象名称',
			dataIndex: 'person_name',
			width: '10%',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '考勤任务',
			dataIndex: 'task_name',
			width: '15%',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '对象身份',
			dataIndex: 'user_type_name',
			width: '15%',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '对象编号',
			dataIndex: 'person_no',
			width: '15%',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '考勤日期',
			dataIndex: 'ats_day',
			width: '15%',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '考勤概况',
			dataIndex: 'result_statistics',
			width: '20%',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		// {
		// 	title: '是否调整',
		// 	dataIndex: 'is_adjusted_name',
		// 	width: '12%',
		// 	render: text => <Ellipsis>{text}</Ellipsis>,
		// },
		{
			title: '操作',
			dataIndex: 'operation',
			width: '10%',
			render: (text, record) => {
				return (
					<a className="table-link" onClick={() => handleDetail(record)}>
						查看明细
					</a>
				);
			},
		},
	];
	return columns;
}

export function tableSearchOptions(props) {
	const { taskList = [] } = props;
	return {
		trigger: null,
		conditions: [
			{
				label: '考勤任务',
				render: getFieldDecorator => {
					return getFieldDecorator('task_id', {
						initialValue: '',
					})(<EnumSelect hasAll list={taskList} />);
				},
			},
			{
				label: '考勤日期',
				render: getFieldDecorator => {
					return getFieldDecorator('date')(
						<RangePicker
							showTime={showTime}
							dropdownClassName="dateDropdown"
							format={'YYYY-MM-DD'}
						/>,
					);
				},
			},
		],
	};
}

// 弹窗table
export function modalColumns(props) {
	const { onSelect = () => {}, list = [] } = props;
	const columns = [
		{
			title: '规则',
			dataIndex: 'express_name',
			width: '35%',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '打卡时间',
			dataIndex: 'pass_time',
			width: '40%',
			render: text => <Ellipsis>{!text ? '--' : moment(text).format(DATE_TYPE)}</Ellipsis>,
		},
		{
			title: '结果',
			dataIndex: 'result',
			width: '25%',
			render: (text, record) => (
				<EnumSelect
					onSelect={e => onSelect(e, record)}
					list={list}
					value={record.result || ''}
				>
					{text}
				</EnumSelect>
			),
		},
	];
	return columns;
}

// export function modalSearchOptions() {
// 	return {
// 		trigger: null,
// 		conditions: [
// 			{
// 				label: '考勤任务',
// 				render: getFieldDecorator => {
// 					return getFieldDecorator('task', {
// 						initialValue: '',
// 					})(<EnumSelect hasAll />);
// 				},
// 			},
// 			{
// 				label: '考勤日期',
// 				render: getFieldDecorator => {
// 					return getFieldDecorator('date', {
// 						initialValue: '',
// 					})(<RangePicker showTime={{ format: showTime }} format={DATE_TYPE} />);
// 				},
// 			},
// 		],
// 	};
// }
