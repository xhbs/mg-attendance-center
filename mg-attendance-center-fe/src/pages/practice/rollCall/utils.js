import { Table } from '@hz-components/react-base';
import { DatePicker } from 'antd';
import { getEnumValue } from '../../dailyAttendance/dailyAttendanceStatistics/utils';

const {
	EnumSelect,
	Ellipsis,
} = Table;
const { RangePicker } = DatePicker;

// 考勤状态枚举
export const attendanceStateEnum = [
	{ name: '点名中', code: 0, color: '#999' },
	{ name: '点名通过', code: 1, color: '#52c41a' },
	{ name: '点名未通过', code: 2, color: '#f14443' },
];

// 详情 个人考勤状态枚举
export const personStateEnum = [
	{ name: '暂未考勤', code: -1, color: '#52c41a' },
	{ name: '考勤缺勤', code: 99, color: '#f14443' },
	{ name: '考勤正常', code: 0, color: '#999' },
]

// 定义表格字段
export const tableColumns = handle => ([
	{
		title: '考勤状态',
		dataIndex: 'attendance_state',
		key: 'attendance_state',
		sorter: true,
		render: text => (
			<span
				style={{
					color: getEnumValue(attendanceStateEnum, text, 'color'),
				}}
			>
				{getEnumValue(attendanceStateEnum, text)}
			</span>
		),
	},
	{
		title: '学生姓名',
		dataIndex: 'person_names',
		key: 'person_names',
		render: text => <Ellipsis>{text}</Ellipsis>,
	},
	{
		title: '申请时间',
		dataIndex: 'reported_at',
		key: 'reported_at',
		render: text => <Ellipsis>{text}</Ellipsis>,
	},
	{
		title: '考勤人数',
		dataIndex: 'num_of_person',
		key: 'num_of_person',
	},
	{
		title: '考勤正常',
		dataIndex: 'num_of_normal',
		key: 'num_of_normal',
	},
	{
		title: '考勤缺勤',
		dataIndex: 'num_of_absence',
		key: 'num_of_absence',
	},
	{
		title: '暂未考勤',
		dataIndex: 'num_of_none',
		key: 'num_of_none',
	},
	{
		title: '操作',
		key: 'operation',
		render: (_, row) => (
			<a onClick={() => {handle(row)}}>考勤详情</a>
		),
	},
]);

// 定义表格操作栏
export const createHandleOptions = (handle) => ({
	handleOptions: {
		elements: [{
			antdProps: {
				icon: 'hz-export',
				children: '导出',
				onClick: handle,
			},
		}],
	},
	searchOptions: {
		antdProps: {
			placeholder: '学生姓名',
		},
	},
});

// 定义表格搜索栏
export const createSearchOptions = (changeStatus, changeTime) => ({
	trigger: null,
	conditions: [{
		label: '考勤状态',
		render: () => (
			<EnumSelect
				defaultValue={''}
				list={attendanceStateEnum}
				hasAll
				onChange={changeStatus}
			/>
		),
	}, {
		label: '申请时间',
		render: () => (
			<RangePicker
				onChange={changeTime}
			/>
		),
	}],
});

// 定义详情表格字段
export const detailsTableColumns = [
	{
		title: '考勤状态',
		dataIndex: 'attendance_result',
		key: 'attendance_result',
		sorter: true,
		render: text => (
			<span
				style={{
					color: getEnumValue(personStateEnum, text, 'color'),
				}}
			>
				{getEnumValue(personStateEnum, text)}
			</span>
		),
	},
	{
		title: '学生姓名',
		dataIndex: 'person_name',
		key: 'person_name',
		render: text => <Ellipsis>{text}</Ellipsis>,
	},
	{
		title: '学号',
		dataIndex: 'person_no',
		key: 'person_no',
		render: text => <Ellipsis>{text}</Ellipsis>,
	},
	{
		title: '班级',
		dataIndex: 'class_name',
		key: 'class_name',
		render: text => <Ellipsis>{text}</Ellipsis>,
	},
	{
		title: '考勤日期',
		dataIndex: 'attendance_date',
		key: 'attendance_date',
		render: text => <Ellipsis>{text}</Ellipsis>,
	},
	{
		title: '考勤时间',
		dataIndex: 'attendance_time',
		key: 'attendance_time',
		render: text => <Ellipsis>{text}</Ellipsis>,
	},
];

// 定义详情表格操作区
export const detailsCreateHandleOptions = (handle) => ({
	handleOptions: {
		elements: [{
			antdProps: {
				icon: 'hz-export',
				children: '导出',
				onClick: handle,
			},
		}],
	},
	searchOptions: {
		antdProps: {
			placeholder: '学生姓名/学号/班级',
		},
	},
});

// 定义详情表格搜索栏
export const detailsCreateSearchOptions = (changeStatus) => ({
	trigger: null,
	conditions: [{
		label: '考勤状态',
		render: () => (
			<EnumSelect
				defaultValue={''}
				list={personStateEnum}
				hasAll
				onChange={changeStatus}
			/>
		),
	}],
});
