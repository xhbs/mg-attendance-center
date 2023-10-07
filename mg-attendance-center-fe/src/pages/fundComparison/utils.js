import React from 'react';
import { Select } from 'antd';
import { Table } from '@hz-components/react-base';

import notImg from '@/assets/not_img.png';
import { getEnumValue, attendanceResEnum } from '../dailyAttendance/dailyAttendanceStatistics/utils';
import { weekColumns } from '../dailyAttendance/dailyAttendanceStatistics/components/attendanceDetails/utils';

const { Ellipsis } = Table;

// 项目类型
export const projectEnum = [
	{ name: '免学费', code: 0 },
	{ name: '国家助学金', code: 1 },
];

// 数据类型
export const dataEnum = [
	{ name: '学校审核名单', code: 0 },
	{ name: '主管审核名单', code: 1 },
];

// 比对状态
const statusEnum = [
	{ name: '不通过', code: '0', color: '#ff0000' },
	{ name: '通过', code: '1', color: '#008000' },
];

// 对比状态类型
export const statusType = {
	PASS: '1',
	NOT_PASS: '0',
}

// 比对类型
export const comparisonType = {
	// 自动比对
	AUTO: 0,
	// 手动比对
	MANUAL: 1,
}

// 考勤类型
export const subsidEnum = [
	{ name: '抽查考勤', code: 0 },
	{ name: '日常考勤', code: 1 },
]

// 考勤类型
export const subsidType = {
	// 抽查
	SPOT_CHECK: 0,
	// 日常
	DAILY: 1,
}

/**
 * 高级节点（省市区）表格字段
 */
export const highColumns = (handle) => (
	[
		{
			title: '资助单位',
			dataIndex: 'org_name',
			key: 'org_name',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '考勤时段',
			dataIndex: 'time_frame',
			key: 'time_frame',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '在籍人数',
			dataIndex: 'student_num',
			key: 'student_num',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '资助审核人数',
			dataIndex: 'sub_num',
			key: 'sub_num',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '比对通过人数',
			dataIndex: 'match_pass_num',
			key: 'match_pass_num',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '比对不通过人数',
			dataIndex: 'match_no_pass_num',
			key: 'match_no_pass_num',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '操作',
			key: 'operation',
			render: (_, row) => {
				const { sub_type } = row;
				return sub_type
					? <a onClick={() => {handle(row)}}>详情</a>
					: (
						<a style={{
							color: '#999',
							cursor: 'not-allowed',
						}}>详情</a>
					)
			},
		},
	]
);

/**
 * 学生表格字段
 */
export const studentColumns = (handle) => (
	[
		{
			title: '比对状态',
			dataIndex: 'status',
			key: 'status',
			sorter: true,
			render: text => {
				return (
					<Ellipsis style={{
						color: getEnumValue(statusEnum, text, 'color'),
					}}>
						{getEnumValue(statusEnum, text)}
					</Ellipsis>
				)
			},
		},
		{
			title: '学生照片',
			dataIndex: 'person_pic',
			key: 'person_pic',
			render: text => (
				<img
					src={text || notImg}
					alt=''
					width='60px'
					height='60px'
					style={{
						border: '1px solid #ccc',
					}}
				/>
			),
		},
		{
			title: '学生姓名',
			dataIndex: 'person_name',
			key: 'person_name',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '班级',
			dataIndex: 'org_name',
			key: 'org_name',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '学号',
			dataIndex: 'person_no',
			key: 'person_no',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '考勤时段',
			dataIndex: 'time_frame',
			key: 'time_frame',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '缺勤率',
			dataIndex: 'absent_rate',
			key: 'absent_rate',
			sorter: true,
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '考勤周期',
			dataIndex: 'check_week',
			key: 'check_week',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '正常次数',
			dataIndex: 'normal_num',
			key: 'normal_num',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '缺勤次数',
			dataIndex: 'absent_num',
			key: 'absent_num',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '操作',
			key: 'operation',
			render: (_, row) => <a onClick={() => {handle(row)}}>详情</a>,
		},
	]
);

// 表格 handle 区域
export const createHandleOptions = (exportFn, isStudent, back, backDisabled) => (
	{
		handleOptions: {
			elements: [
				{
					antdProps: {
						icon: 'left-circle',
						children: '返回上一级',
						onClick: back,
						disabled: backDisabled,
					},
				},
				{
					antdProps: {
						icon: 'hz-export',
						children: '导出',
						onClick: exportFn,
					},
				},
			],
		},
		searchOptions: {
			searchKey: isStudent ? 'search_key' : 'org_name',
			antdProps: {
				placeholder: isStudent ? '学生姓名/班级/学号' : '资助单位',
			},
		},
	}
)

// 表格 筛选 区域
export const createSearchOptions = (isStudent, handleChange) => {
	if (isStudent) {
		return  (
			{
				trigger: null,
				conditions: [
					{
						label: '比对状态',
						render: () => (
							<Select
								defaultValue=''
								onChange={handleChange}
							>
								<Select.Option value=''>不限</Select.Option>
								{
									statusEnum.map(item => (
										<Select.Option
											key={item.code}
											value={item.code}
										>
											{item.name}
										</Select.Option>
									))
								}
							</Select>
						),
					},
				],
			}
		);
	}
}

/**
 * 定义详情表格字段
 * @param {Function} handle
 */
export const detailsColumns = handle => {
	const columns = [
		{
			title: '状态',
			dataIndex: 'result',
			key: 'result',
			render: text => (
				<span
					style={{
						color: getEnumValue(attendanceResEnum, text, 'color'),
					}}
				>
				{getEnumValue(attendanceResEnum, text)}
			</span>
			),
		},
		{
			title: '考勤任务',
			dataIndex: 'task_name',
			key: 'task_name',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '考勤类型',
			dataIndex: 'attendance_type',
			key: 'attendance_type',
			render: text => <Ellipsis>{getEnumValue(subsidEnum, text)}</Ellipsis>,
		},
		{
			title: '考勤日期',
			dataIndex: 'subsid_date',
			key: 'subsid_date',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
	];
	columns.push(...weekColumns(handle))
	return columns;
}

/**
 * 创建详情表格 handle区
 */
export const createDetailsHandle = (exportFn) => (
	{
		handleOptions: {
			elements: [
				{
					elementType: 'custom', // elementType 为 custom 时用户可通过 render 方法渲染任何内容
					render: () => {
						return <span>考勤分布</span>
					},
				},
				{
					antdProps: {
						icon: 'hz-export',
						children: '导出',
						onClick: exportFn,
					},
				},
			],
		},
		searchOptions: {
			show: false,
		},
	}
);
