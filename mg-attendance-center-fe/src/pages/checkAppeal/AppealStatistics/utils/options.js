import React from 'react';
import { EnumSelect, Table } from '@hz-components/react-base';
import { LEAVE_DEAL_STATUS } from '@constants';

const { Ellipsis } = Table;

/**
 * 操作栏
 */
export const createHandleBarOptions = (exportData, isStu, handleBack, backDisabled) => {
	return {
		handleOptions: {
			elements: [
				{
					antdProps: {
						icon: 'left-circle',
						children: '返回上一级',
						onClick: handleBack,
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
			searchKey: isStu ?  'search_s' : `search`,
			antdProps: {
        placeholder: isStu ? '学生姓名/学号' : '组织',
			},
		},
	}
}

/**
 * 筛选栏
 */
export const createSearchBarOptions = (obj) => {
  // const { userRole } = obj;
  // 只有省级的才有 '5: 无需处理' 状态
  // let list = [...LEAVE_DEAL_STATUS];
  // if(userRole !== PROVINCE) {
  //     list = list.filter(i => i.code !== 5);
  // }
	return {
		trigger: null,
		conditions: [
			{
				label: '申诉状态',
				render: (getFieldDecorator) => {
					return getFieldDecorator('status', {
						initialValue: '',
					})(<EnumSelect hasAll list={LEAVE_DEAL_STATUS} />);
				},
			},
		],
	}
}

/**
 * 学校层级以上
 */
export const createColumns = (handleItemDetails) => {
	return [
		{
			title: '组织',
			dataIndex: 'org_name',
			key: 'org_name',
			width: 260,
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '申诉总人数',
			dataIndex: 'total',
			key: 'total',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '未处理人数',
			dataIndex: 'not_approval_num',
			key: 'not_approval_num',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '同意人数',
			dataIndex: 'passed_num',
			key: 'passed_num',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '拒绝人数',
			dataIndex: 'rejected_num',
			key: 'rejected_num',
			render: (text, row) => <Ellipsis>{row.sub_type ? text : '-'}</Ellipsis>,
		},
		{
			title: '操作',
			key: 'operation',
			render: (_, row) => {
				const { sub_type } = row;
				return sub_type
					? <a onClick={() => {handleItemDetails(row)}}>详情</a>
					: (
						<a style={{
							color: '#999',
							cursor: 'not-allowed',
						}}>详情</a>
					)
			},
		},
	];
}

/**
 * 学校层级的详情
 */
export const createStudentColumns = (handleStudentItemDetails) => {
	return [
		{
			title: '申诉状态',
			dataIndex: 'status',
			key: 'status',
			render: text => {
        const target = LEAVE_DEAL_STATUS.find(x => x.code === text) || {};
        return <span style={{color: target.color || '#666'}}>{target.name}</span>
      },
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
			title: '考勤时段',
			dataIndex: 'attendance_date_range',
			key: 'attendance_date_range',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '申诉时间',
			dataIndex: 'reported_at',
			key: 'reported_at',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '操作',
			key: 'operation',
			render: (_, orw) => {
				return <a onClick={() => {handleStudentItemDetails(orw)}}>申诉详情</a>
			},
		},
	];
}
