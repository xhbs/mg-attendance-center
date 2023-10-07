/*
 * @Desc: 通用型表达式
 * @Author: ranguangyu
 * @Date: 2020-09-03 20:53:09
 */
import { TimePicker } from 'antd';
import moment from 'moment';
import { EnumSelect } from '@hz-components/react-base';

import { getCodes } from './utils';

const createTColumns = form => {
	const { trigger_type } = form.getFieldsValue();

	const tColumns = [
		{
			title: '编码',
			dataIndex: 't_code',
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
			title: '考勤日',
			dataIndex: 'start_offset',
			// comp: {
			// 	component: 'EnumSelect',
			// 	antdProps: {
			// 		style: { width: '100%' },
			// 		list: [
			// 			{ code: 0, name: '当日' },
			// 			{ code: 1, name: '次日' },
			// 			{ code: 2, name: '前一日' },
			// 		],
			// 		allowClear: true,
			// 		disabled: trigger_type === '0', // 触发方式为“实时触发”时，考勤日置灰
			// 	},
			// 	options: {
			// 		initialValue: trigger_type === '0' ? 0 : undefined, // 触发方式为“实时触发”时，考勤日默认为“当日”
			// 		rules: [{ required: true, message: '必填字段' }],
			// 	},
			// },
			render: (text, record, index, getProps) => {
				return (
					<EnumSelect
						style={{ width: '100%' }}
						list={[
							{ code: 0, name: '当日' },
							{ code: 1, name: '次日' },
							{ code: 2, name: '前一日' },
						]}
						allowClear
						disabled={trigger_type === '0'}
						{...getProps({
							initialValue: trigger_type === '0' ? 0 : text,
							rules: [{ required: true, message: '必填字段' }],
						})}
					/>
				)
			},
		},
		{
			title: '时间点',
			dataIndex: 'start_time',
			render: (text, record, index, getProps, { getTargetValue }) => {
				return (
					<TimePicker
						style={{ width: '100%' }}
						defaultOpenValue={moment()}
						{...getProps({
							initialValue: text ? moment(text, 'HH:mm:ss') : undefined,
							rules: [
								{ required: true, message: '必填字段' },
								{
									validator: (rule, value, callback) => {
										const endTime = getTargetValue('end_time');
										if (!value || !endTime) {
											callback();
											return;
										}
										// 获取组合时间
										function getTime(v, offset) {
											const map = {
												'0': moment(v), // 今天（当日）
												'1': moment(v).add(1, 'day'), // 明天（次日）
												'2': moment(v).add(-1, 'day'), // 昨天（前一日）
											};
											return map[offset];
										}
										const composeStartTime = getTime(
											value,
											getTargetValue('start_offset') || '0',
										);
										const composeEndTime = getTime(
											endTime,
											getTargetValue('end_offset') || '0',
										);
										if (composeStartTime > composeEndTime) {
											callback('开始时间不能大于结束时间');
										}
										callback();
									},
								},
							],
						})}
					/>
				);
			},
		},
		{
			title: '考勤日',
			dataIndex: 'end_offset',
			// comp: {
			// 	component: 'EnumSelect',
			// 	antdProps: {
			// 		style: { width: '100%' },
			// 		list: [
			// 			{ code: 0, name: '当日' },
			// 			{ code: 1, name: '次日' },
			// 			{ code: 2, name: '前一日' },
			// 		],
			// 		allowClear: true,
			// 		disabled: trigger_type === '0', // 触发方式为“实时触发”时，考勤日置灰
			// 	},
			// 	options: {
			// 		initialValue: trigger_type === '0' ? 0 : undefined, // 触发方式为“实时触发”时，考勤日默认为“当日”
			// 		rules: [{ required: true, message: '必填字段' }],
			// 	},
			// },
			render: (text, record, index, getProps) => {
				return (
					<EnumSelect
						style={{ width: '100%' }}
						list={[
							{ code: 0, name: '当日' },
							{ code: 1, name: '次日' },
							{ code: 2, name: '前一日' },
						]}
						allowClear
						disabled={trigger_type === '0'}
						{...getProps({
							initialValue: trigger_type === '0' ? 0 : text,
							rules: [{ required: true, message: '必填字段' }],
						})}
					/>
				)
			},
		},
		{
			title: '时间点',
			dataIndex: 'end_time',
			render: (text, record, index, getProps, { getTargetValue }) => {
				return (
					<TimePicker
						style={{ width: '100%' }}
						defaultOpenValue={moment()}
						{...getProps({
							initialValue: text ? moment(text, 'HH:mm:ss') : undefined,
							rules: [
								{ required: true, message: '必填字段' },
								{
									validator: (rule, value, callback) => {
										const startTime = getTargetValue('start_time');
										if (!value || !startTime) {
											callback();
											return;
										}
										// 获取组合时间
										function getTime(v, offset) {
											const map = {
												'0': moment(v), // 今天（当日）
												'1': moment(v).add(1, 'day'), // 明天（次日）
												'2': moment(v).add(-1, 'day'), // 昨天（前一日）
											};
											return map[offset];
										}
										const composeStartTime = getTime(
											startTime,
											getTargetValue('start_offset') || '0',
										);
										const composeEndTime = getTime(
											value,
											getTargetValue('end_offset') || '0',
										);
										if (composeStartTime > composeEndTime) {
											callback('结束时间不能小于开始时间');
										}
										callback();
									},
								},
							],
						})}
					/>
				);
			},
		},
		{
			title: '打卡类型',
			dataIndex: 'type',
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
	];

	return tColumns;
};

export default createTColumns;
