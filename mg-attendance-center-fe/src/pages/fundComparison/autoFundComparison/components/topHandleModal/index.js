/**
 * 上方操作区 弹窗
 */

import React, { useEffect, useState } from 'react';
import { DatePicker, Form, Input, Modal, Select, Radio } from 'antd';
import { getEnumValue } from '../../../../dailyAttendance/dailyAttendanceStatistics/utils';
import { TITLE_ENUM, TITLE_TYPE } from '../topHandle';
import { EnumSelect } from '@hz-components/react-base';
import './index.less';
import {
	getSchoolYear,
	getSchoolTerm,
	semesterEnum,
} from '../../../../dailyAttendance/dailyAttendanceStatistics/utils.js';
import moment from 'moment';
const { Item, create } = Form;
const { RangePicker } = DatePicker;
const { Option } = Select;

const FORM_LAYOUT = {
	labelCol: { span: 4 },
	wrapperCol: { span: 17 },
};

function TopHandleModal(props) {
	const {
		title,
		visible,
		onCancel,
		info = {},
		setInfo,
		projectList,
		form: { getFieldDecorator, setFieldsValue, validateFieldsAndScroll },
	} = props;
	const yearOption = getSchoolYear(5);
	let paramData = {};
	const [screen, setScreen] = useState({
		// 学年
		school_year: yearOption[0],
		// 学期
		school_term: getSchoolTerm(),
	});
	// const [, setSemester] = useState(getSchoolTerm());
	const [rule, setRule] = useState(1);
	const handleRadioChange = (e) => {
		setRule(e.target.value);
	};
	useEffect(() => {
		const { project, time_frame, rules, rule } = info;
		console.log('%c [ info ]-51', 'font-size:13px; background:pink; color:#bf2c9f;', info);
		title === TITLE_TYPE.LIST &&
			setFieldsValue({
				project,
			});
		title === TITLE_TYPE.TIME_FRAME && setFieldsValue({ time_frame });
		title === TITLE_TYPE.RULES && setFieldsValue({ rules, rule });
		setRule(rule);
	}, [info, setFieldsValue, title]);
	/**
	 * 点击确定
	 */
	const handleOk = async () => {
		try {
			let params = await validateFieldsAndScroll();
			params = { ...params, rule };
			if (params?.semester === 0) {
				params.semester = '春季';
			} else if (params.semester === 1) {
				params.semester = '秋季';
			}
			console.log(params);

			setInfo(params);
			onCancel();
		} catch (err) {
			console.log(err);
		}
	};
	const handleChangeSchoolYear = (school_year) => {
		setScreen({
			...screen,
			school_year,
		});
		paramData.year_month = '';
	};
	const handleChangeSemester = (school_term) => {
		// setSemester(school_term);
		setScreen({
			...screen,
			school_term,
		});
		paramData.school_term = school_term;
		paramData.year_month = '';
	};
	const disabledDate = (current) => {
		if (info.year) {
			const [start, end] = info.year.split('-');
			return (
				current && (current < moment(`${start}-01-01`) || current > moment(`${end}-12-31`))
			);
		}
		return false;
	};
	return (
		<Modal
			title={getEnumValue(TITLE_ENUM, title)}
			visible={visible}
			onCancel={onCancel}
			onOk={handleOk}
			wrapClassName="fund-comparison-modal"
		>
			<Form {...FORM_LAYOUT}>
				{title === TITLE_TYPE.LIST && (
					<>
						{/* <Item label="日期">
							{getFieldDecorator('date', {
								rules: [
									{
										required: true,
										message: '请选择日期',
									},
								],
							})(<MonthPicker />)}
						</Item> */}
						<Item label="项目">
							{getFieldDecorator('project', {
								rules: [
									{
										required: true,
										message: '请选择项目',
									},
								],
							})(
								<Select placeholder="请选择项目">
									{projectList.map((item) => (
										<Option key={item.sub_list_index + '+' + item.desc}>
											{item.desc}
										</Option>
									))}
								</Select>,
							)}
						</Item>
					</>
				)}
				{title === TITLE_TYPE.TIME_FRAME && (
					<Item label="考勤时段">
						{getFieldDecorator('time_frame', {
							rules: [
								{
									required: true,
									message: '请选择考勤时段',
								},
							],
						})(<RangePicker disabledDate={disabledDate} />)}
					</Item>
				)}
				{title === TITLE_TYPE.RULES && (
					<>
						<Item label="比对规则">
							<Radio.Group onChange={handleRadioChange} value={rule}>
								{/* <Radio value={0}>
									日常缺勤率≥
									{rule === 0 &&
										getFieldDecorator('rules', {
											rules: [
												{
													required: true,
													message: '请输入1-100的正整数',
													pattern: /^(100|[1-9]\d?)$/,
												},
											],
										})(<Input className="comparison-input" />)}
									%，则比对不通过
								</Radio> */}
								<Radio value={1}>
									点名缺勤率≥
									{rule === 1 &&
										getFieldDecorator('rules', {
											rules: [
												{
													required: true,
													message: '请输入1-100的正整数',
													pattern: /^(100|[1-9]\d?)$/,
												},
											],
										})(<Input className="comparison-input" />)}
									%，则比对不通过
								</Radio>
							</Radio.Group>
						</Item>
					</>
				)}
				{title === TITLE_TYPE.YEAR && (
					<Item label="学年">
						{getFieldDecorator('year', {
							rules: [
								{
									required: true,
									message: '请选择学年',
								},
							],
						})(
							<Select
								onChange={(value) => {
									handleChangeSchoolYear(value);
								}}
							>
								{yearOption.map((item) => (
									<Select.Option key={item} value={item}>
										{item}
									</Select.Option>
								))}
							</Select>,
						)}
					</Item>
				)}
				{title === TITLE_TYPE.SEMESTER && (
					<Item label="学期">
						{getFieldDecorator('semester', {
							rules: [
								{
									required: true,
									message: '请选择学期',
								},
							],
						})(
							<EnumSelect
								createPromise={() =>
									new Promise((resolve) => {
										resolve(semesterEnum);
									})
								}
								onChange={(value) => {
									handleChangeSemester(value);
								}}
							/>,
						)}
					</Item>
				)}
			</Form>
		</Modal>
	);
}

export default create()(TopHandleModal);
