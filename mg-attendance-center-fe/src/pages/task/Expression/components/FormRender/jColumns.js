/*
 * @Desc: 记录型表达式
 * @Author: ranguangyu
 * @Date: 2020-09-03 20:55:15
 */

import { getCodes } from './utils';

const createJColumns = form => {
	const jColumns = [
		{
			title: '编码',
			dataIndex: 'j_code',
			comp: {
				component: 'EnumSelect',
				antdProps: {
					style: { width: '100%' },
					list: getCodes(form),
					allowClear: true,
				},
				options: {
					rules: [{ required: true, message: '必填字段' }],
				},
			},
		},
		{
			title: '时间方向',
			dataIndex: 'start_direction',
			comp: {
				component: 'EnumSelect',
				antdProps: {
					style: { width: '100%' },
					list: [
						{ code: 0, name: '前' },
						// { code: 1, name: '后' },
					],
					allowClear: true,
				},
				options: {
					rules: [{ required: true, message: '必填字段' }],
				},
			},
		},
		{
			title: '偏移次数',
			dataIndex: 'offset_num',
			comp: {
				component: 'EnumSelect',
				antdProps: {
					style: { width: '100%' },
					list: [1, 2, 3, 4, 5],
					allowClear: true,
				},
				options: {
					rules: [{ required: true, message: '必填字段' }],
				},
			},
		},
		{
			title: '打卡动作',
			dataIndex: 'start_type',
			comp: {
				component: 'EnumSelect',
				antdProps: {
					style: { width: '100%' },
					list: [
						{ code: 1, name: '签到' },
						{ code: 2, name: '签离' },
					],
					allowClear: true,
				},
				options: {
					rules: [{ required: true, message: '必填字段' }],
				},
			},
		},
		{
			title: '时间方向',
			dataIndex: 'end_direction',
			comp: {
				component: 'EnumSelect',
				antdProps: {
					style: { width: '100%' },
					list: [
						{ code: 0, name: '前' },
						{ code: 1, name: '后' },
					],
					allowClear: true,
				},
				options: {
					rules: [{ required: true, message: '必填字段' }],
				},
			},
		},
		{
			title: '时间跨度',
			dataIndex: 'interval',
			width: 120,
			comp: {
				component: 'InputNumber',
				antdProps: {
					style: { width: '100%' },
					min: 0,
					maxLength: 8,
				},
				options: {
					rules: [{ required: true, message: '必填字段' }],
				},
			},
		},
		{
			title: '时间跨度单位',
			dataIndex: 'interval_type',
			width: 120,
			comp: {
				component: 'EnumSelect',
				antdProps: {
					style: { width: '100%' },
					list: [
						{ code: 0, name: '小时' },
						{ code: 1, name: '分钟' },
						{ code: 2, name: '秒' },
					],
					allowClear: true,
				},
				options: {
					rules: [{ required: true, message: '必填字段' }],
				},
			},
		},
		{
			title: '有无打卡',
			dataIndex: 'has',
			comp: {
				component: 'EnumSelect',
				antdProps: {
					style: { width: '100%' },
					list: [
						{ code: true, name: '有' },
						{ code: false, name: '没有' },
					],
					allowClear: true,
				},
				options: {
					rules: [{ required: true, message: '必填字段' }],
				},
			},
		},
		{
			title: '打卡动作',
			dataIndex: 'end_type',
			comp: {
				component: 'EnumSelect',
				antdProps: {
					style: { width: '100%' },
					list: [
						{ code: 1, name: '签到' },
						{ code: 2, name: '签离' },
					],
					allowClear: true,
				},
				options: {
					rules: [{ required: true, message: '必填字段' }],
				},
			},
		},
	];

	return jColumns;
};

export default createJColumns;
