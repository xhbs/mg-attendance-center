import React from 'react';
import { EnumSelect, Table } from '@hz-components/react-base';
import { Icon } from 'antd';

import {
	stateEnum,
	stateType,
	weekType,
	attendanceResEnum,
	getEnumValue,
} from '../../utils';

const { Ellipsis } = Table;
const nullList = [null, undefined, ''];

// 筛选栏
export const createSearchBarOptions = (listRef) => {
	return {
		trigger: null,
		conditions: [
			{
				label: '状态',
				render: () => (
					<EnumSelect
						hasAll
						defaultValue=''
						createPromise={() => new Promise(resolve => {
							resolve(stateEnum);
						})}
						onChange={result => {
							listRef.dataLoad({ result });
						}}
					/>
				),
			},
		],
	};
};

// 操作栏
export const createHandleBarOptions = (onCancel) => {
	return {
		handleOptions: {
			elements: [
				{
					elementType: 'custom',
					render: () => (
						<span
							style={{
								cursor: 'pointer',
								userSelect: 'none',
							}}
							onClick={onCancel}
						>
							<Icon
								type='left-circle'
								style={{
									color: '#5584ff',
									marginRight: 10,
								}}
							/>
							日常考勤明细
						</span>
					),
				},
			],
		},
		searchOptions: {
			render: () => null,
		},
	};
};

/**
 * 星期  渲染
 */
export const renderWeek = (text, row, week, handleLookDetails) => {
	return (
		<>
			{
				!nullList.includes(text) ? (
					<a
						style={{
							color: getEnumValue(attendanceResEnum, text, 'color'),
						}}
						onClick={() => {
							handleLookDetails(row, week);
						}}
					>
						{getEnumValue(attendanceResEnum, text)}
					</a>
				) : <span>-</span>
			}
		</>
	);
};

// 星期一到星期五 字段定义
export const weekColumns = (handleLookDetails) => (
	[
		{
			title: '星期一',
			dataIndex: 'result_of_monday',
			key: 'result_of_monday',
			render: (text, row) => renderWeek(text, row, weekType.MONDAY, handleLookDetails),
		},
		{
			title: '星期二',
			dataIndex: 'result_of_tuesday',
			key: 'result_of_tuesday',
			render: (text, row) => renderWeek(text, row, weekType.TUESDAY, handleLookDetails),
		},
		{
			title: '星期三',
			dataIndex: 'result_of_wednesday',
			key: 'result_of_wednesday',
			render: (text, row) => renderWeek(text, row, weekType.WEDNESDAY, handleLookDetails),
		},
		{
			title: '星期四',
			dataIndex: 'result_of_thursday',
			key: 'result_of_thursday',
			render: (text, row) => renderWeek(text, row, weekType.THURSDAY, handleLookDetails),
		},
		{
			title: '星期五',
			dataIndex: 'result_of_friday',
			key: 'result_of_friday',
			render: (text, row) => renderWeek(text, row, weekType.FRIDAY, handleLookDetails),
		},
	]
)

// 定义表格字段
export const columns = (handleLookDetails) => {
	const columns = [
		{
			title: '状态',
			dataIndex: 'result',
			key: 'result',
			sorter: true,
			render: text => (
				<>
					{
						!nullList.includes(text) ? (
							<span
								style={{
									color: text !== stateType.ABSENTEEISM ? '#43cb83' : '#ed5556',
								}}
							>
								{text !== stateType.ABSENTEEISM ? '在校' : '缺勤'}
							</span>
						) : <span>-</span>
					}
				</>
			),
		},
		{
			title: '学生姓名',
			dataIndex: 'person_name',
			key: 'person_name',
		},
		{
			title: '学年',
			dataIndex: 'school_year',
			key: 'school_year',
		},
		{
			title: '学期',
			dataIndex: 'semester',
			key: 'semester',
		},
		{
			title: '月份',
			dataIndex: 'month',
			key: 'month',
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
			dataIndex: 'cycle',
			key: 'cycle',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
	];
	columns.push(...weekColumns(handleLookDetails));
	return columns;
};
