/*
 * @Description:
 * @Author: zhangjie
 * @Date: 2020-07-15 15:17:23
 */
/* eslint-disable jsx-a11y/anchor-is-valid */

import { Table } from '@hz-components/react-base';
import moment from 'moment';
import { ATTENDANCE_WAYS, DATE_TYPE } from '@constants';
import emptyImg from '@hz-design/base/public/load-empty.svg';
import errorImg from '@hz-design/base/public/load-fail-empty.svg';


const { Ellipsis, RangePicker, EnumSelect } = Table;
const showTime = 'HH:mm:ss';
function getName(code) {
	const data = ATTENDANCE_WAYS.filter(v => v.code === code)[0] || {};
	return data.name || '';
}

export function tableColumns() {
	const columns = [
		{
			title: '对象名称',
			dataIndex: 'person_name',
			width: '10%',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '对象编号',
			dataIndex: 'person_no',
			width: '12%',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '对象身份',
			dataIndex: 'user_type_name',
			width: '15%',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		// {
		// 	title: '考勤日期',
		// 	dataIndex: 'atsDay',
		// 	width: '10%',
		// 	render: text => <Ellipsis>{text}</Ellipsis>,
		// },
		{
			title: '打卡时间',
			dataIndex: 'pass_time',
			width: '15%',
			render: text => <Ellipsis>{moment(text).format(DATE_TYPE)}</Ellipsis>,
		},
		{
			title: '考勤点',
			dataIndex: 'site_name',
			width: '15%',
			render: text => <Ellipsis>{text}</Ellipsis>,
		},
		{
			title: '考勤方式',
			dataIndex: 'attendance_way',
			width: '15%',
			render: text => <Ellipsis>{getName(text)}</Ellipsis>,
		},
		{
			title: '相似度',
			dataIndex: 'similarity',
			width: '10%',
			render: text => <Ellipsis>{isNaN(text) || text < 0 ? '--' : text}</Ellipsis>,
		},
		{
			title: '特征',
			dataIndex: 'attendance_identification',
			width: '8%',
			render: text =>(
				<img
					src={text || emptyImg}
					alt=""
					style={{ width: 64, height: 64 }}
					onError={(e) => {
						e.target.onerror = null;
						e.target.src = errorImg;
					}}
				/>
			),
		},
	];
	return columns;
}

export function tableSearchOptions() {
	return {
		trigger: null,
		conditions: [
			{
				label: '打卡时间',
				render: getFieldDecorator => {
					return getFieldDecorator('date')(
						<RangePicker
							placeholder={['开始时间', '结束时间']}
							showTime={{ format: showTime }}
							format={DATE_TYPE}
						/>,
					);
				},
			},
			{
				label: '考勤方式',
				render: getFieldDecorator => {
					return getFieldDecorator('attendance_way', {
						initialValue: 'face_recognition',
					})(<EnumSelect list={ATTENDANCE_WAYS} />);
				},
			},
		],
	};
}
