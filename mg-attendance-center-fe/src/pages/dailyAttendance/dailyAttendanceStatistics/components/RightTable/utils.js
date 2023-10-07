import {
	getEnumValue,
	getSchoolMonth,
	semesterEnum,
	tableTypeEnum,
} from '../../utils';
import React from 'react';
import { Select } from 'antd';
import { EnumSelect, Table } from '@hz-components/react-base';

const { Ellipsis } = Table;

/**
 * 操作栏
 */
export const createHandleBarOptions = (exportData, tableType, back, backDisabled) => {
	return {
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
						icon: 'hz-import',
						children: '导出',
						onClick: exportData,
					},
				},
			],
		},
		searchOptions: {
			searchKey: tableType === tableTypeEnum.STUDENT ? 'like_no_or_name': 'org_name',
			antdProps: {
				placeholder: tableType === tableTypeEnum.STUDENT ? '学生姓名/学号' : '考勤对象',
			},
		},
	}
}

/**
 * 筛选栏
 */
export const createSearchBarOptions = (options) => {
	const {
		handleChangeSchoolYear,
		yearOption,
		handleChangeSemester,
		handleChangeMonth,
		semester,
		screen,
	} = options;
	return {
		trigger: null,
		conditions: [
			{
				label: '学年',
				render: () => {
					return (
						<Select
							value={screen.school_year}
							onChange={value => {handleChangeSchoolYear(value)}}
						>
							{
								yearOption.map((item) => (
									<Select.Option
										key={item}
										value={item}
									>
										{item}
									</Select.Option>
								))
							}
						</Select>
					)
				},
			},
			{
				label: '学期',
				render: () => {
					return (
						<EnumSelect
							value={screen.school_term}
							createPromise={() => new Promise(resolve => {
								resolve(semesterEnum)
							})}
							onChange={value => {handleChangeSemester(value)}}
						/>
					)
				},
			},
			{
				label: '月份',
				render: () => {
					const optionList = getSchoolMonth(screen.school_year, semester);
					return (
						<Select
							value={screen.year_month}
							onChange={value => {handleChangeMonth(value)}}
						>
							<Select.Option value={''}>
								不限
							</Select.Option>
							{
								optionList.map((item) => (
									<Select.Option
										key={item}
										value={item}
									>
										{item}
									</Select.Option>
								))
							}
						</Select>
					)
				},
			},
		],
	}
}

/**
 * 定义表格字段
 */
export const createColumns = (handleItemDetails) => {
	return [
		{
			title: '考勤对象',
			dataIndex: 'org_name',
			key: 'org_name',
			width: 200,
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '学年',
			dataIndex: 'school_year',
			key: 'school_year',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '学期',
			dataIndex: 'school_term',
			key: 'school_term',
			render: text => <span>{getEnumValue(semesterEnum, text)}</span>,
		},
		{
			title: '月份',
			dataIndex: 'year_month',
			key: 'year_month',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '类型',
			dataIndex: 'type',
			key: 'type',
			render: () => <span>周考勤</span>,
		},
		{
			title: '考勤周期',
			dataIndex: 'check_week',
			key: 'check_week',
			render: (text, row) => <Ellipsis>{row.sub_type ? text : '-'}</Ellipsis>,
		},
		{
			title: '在籍总数',
			dataIndex: 'regist_student_num',
			key: 'regist_student_num',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '最新一期人数',
			dataIndex: 'student_num',
			key: 'student_num',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '学生在校率',
			children: [
				{
					title: '=100%',
					dataIndex: 'range1',
					key: 'range1',
					align: 'center',
					render: text => <Ellipsis>{text}</Ellipsis>,
				},
				{
					title: '90%~99%',
					dataIndex: 'range2',
					key: 'range2',
					align: 'center',
					render: text => <Ellipsis>{text}</Ellipsis>,
				},
				{
					title: '70%~89%',
					dataIndex: 'range3',
					key: 'range3',
					align: 'center',
					render: text => <Ellipsis>{text}</Ellipsis>,
				},
				{
					title: '50%~69%',
					dataIndex: 'range4',
					key: 'range4',
					align: 'center',
					render: text => <Ellipsis>{text}</Ellipsis>,
				},
				{
					title: '30%~49%',
					dataIndex: 'range5',
					key: 'range5',
					align: 'center',
					render: text => <Ellipsis>{text}</Ellipsis>,
				},
				{
					title: '<30%',
					dataIndex: 'range6',
					key: 'range6',
					align: 'center',
					render: text => <Ellipsis>{text}</Ellipsis>,
				},
			],
		},
		{
			title: '操作',
			key: 'operation',
			render: (_, row) => {
				const { sub_type } = row;
				return sub_type
					? <a onClick={() => {handleItemDetails(row)}}>查看明细</a>
					: (
						<a style={{
							color: '#999',
							cursor: 'not-allowed',
						}}>查看明细</a>
					)
			},
		},
	];
}

/**
 * 类型为学生的时候  的表格字段
 */
export const createStudentColumns = (handleStudentItemDetails) => {
	return [
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
			title: '学年',
			dataIndex: 'school_year',
			key: 'school_year',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '学期',
			dataIndex: 'school_term',
			key: 'school_term',
			render: text => <span>{getEnumValue(semesterEnum, text)}</span>,
		},
		{
			title: '月份',
			dataIndex: 'year_month',
			key: 'year_month',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '类型',
			dataIndex: 'type',
			key: 'type',
			render: () => <span>周考勤</span>,
		},
		{
			title: '考勤周期',
			dataIndex: 'check_week',
			key: 'check_week',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '在校',
			dataIndex: 'normal_weeks',
			key: 'normal_weeks',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '缺勤',
			dataIndex: 'absent_weeks',
			key: 'absent_weeks',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '操作',
			key: 'operation',
			render: (_, orw) => {
				return <a onClick={() => {handleStudentItemDetails(orw)}}>考勤明细</a>
			},
		},
	];
}
