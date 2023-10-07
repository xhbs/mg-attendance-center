/*
 * @Author: xiedan WX042
 * @since: 2020-08-27 11:01:08
 * @lastTime: 2021-10-20 09:56:32
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\task\TaskManagement\components\FormPage.jsx
 * @Description: 考勤任务---任务管理---表单页面
 */
import React from 'react';
import { Form, Input, message, DatePicker, InputNumber, Spin } from 'antd';
import moment from 'moment';
import { EnumSelect, TreeSelect } from '@hz-components/react-base';
import {
	getCheckAttendById,
	addCheckAttend,
	updateCheckAttend,
} from '@services/checkAttendance/checkAttendanceTask';
import { getLazyOrganizationsTree } from '@services/common';
import { API_ERROR_CODE, REGEX_NAME, DATE_TYPE_DAY } from '@constants';
import { disabledDate, getSchoolYear, getSchoolMonth } from '@utils/utils';

const { Item } = Form;
const { RangePicker } = DatePicker;

const formItemLayout = {
	labelCol: {
		xs: { span: 2 },
		sm: { span: 2 },
	},
	wrapperCol: {
		xs: { span: 16 },
		sm: { span: 16 },
	},
};

class FormPage extends React.Component {
	state = {
		formData: {
			taskName: '',
		},
		spinning: true,
		// checkName: true,  // 名称验重  true--没有重复

		yearEnum: [], // 学年列表
		monthEnum: [], // 学期列表
		yearValue: '', // 学年
		termValue: '', // 学期
		monthValue: '', // 月份

		treeData: [],
		maxCheckNum: 1, // 最大的抽查天数
	};

	componentDidMount() {
		const year = getSchoolYear(5);
		this.setState({
			yearEnum: year || [],
		});
		// 编辑时，获取详情
		const { detailId, operationType } = this.props;
		// 如果未查看详情模式，则无需获取组织树
		const fun = operationType === 'view' ? [] : [this.getOrgTreeList()];

		if (operationType !== 'add' && detailId) {
			fun.push(this.getTaskDetailByIdReq());
		} else {
			this.setState({
				yearValue: year[0] || '',
				monthEnum: getSchoolMonth(year[0]) || [],
			});
		}
		Promise.all(fun).finally(() => {
			this.setState({
				spinning: false,
			});
		});
	}

	// 获取详情
	getTaskDetailByIdReq = () => {
		const { detailId, operationType } = this.props;
		const semesterMap = {
			春季: '0',
			秋季: '1',
		};
		return getCheckAttendById(detailId).then((res) => {
			if (res && res.error_code === API_ERROR_CODE) {
				const { data } = res;
				const {
					name,
					start_date,
					end_date,
					day_count,
					target_org_indexes,
					target_org_names,
					school_year,
					semester,
					month,
					minimum_absence_rate,
				} = data || {};

				let maxCheckNum = 1;
				if (start_date && end_date) {
					maxCheckNum =
						Math.abs(
							moment(`${start_date} 00:00:00`).diff(
								moment(`${end_date} 23:59:59`),
								'day',
							),
						) + 1;
				}
				this.setState({
					formData: {
						taskName: name || '',
						date: [moment(start_date), moment(end_date)],
						attendance:
							operationType === 'view' ? target_org_names : target_org_indexes || [],
						checkNum: day_count || '',
					},
					yearValue: school_year || '',
					termValue: semesterMap[semester] || '',
					monthValue: month || '',
					percent: minimum_absence_rate || 0,
					maxCheckNum,

					monthEnum: getSchoolMonth(school_year, semesterMap[semester]) || [],
				});
			}
		});
	};

	// 提交表单
	handleSubmit = (callback) => {
		const { form, onFormPageClose, operationType, detailId } = this.props;
		const semesterMap = {
			0: '春季',
			1: '秋季',
		};
		form.validateFieldsAndScroll((err, values) => {
			if (!err) {
				const params = {
					id: operationType === 'add' ? '' : detailId,
					name: values.task_name || '',
					day_count: values.check_num,
					minimum_absence_rate: values.percent || 0,
					month: values.month || '',
					school_year: values.year,
					semester: semesterMap[values.term] || '',
					start_date:
						(values.date &&
							values.date[0] &&
							moment(values.date[0]).format(DATE_TYPE_DAY)) ||
						'',
					end_date:
						(values.date &&
							values.date[1] &&
							moment(values.date[1]).format(DATE_TYPE_DAY)) ||
						'',
					target_org_indexes: values.attendance || [],
				};
				const fun =
					operationType === 'add' ? addCheckAttend(params) : updateCheckAttend(params);
				const text = operationType === 'add' ? `新建` : `修改`;
				fun.then((res) => {
					if (res && res.error_code === API_ERROR_CODE) {
						message.success(`${text}任务成功`);
						onFormPageClose();
					}
				}).finally(() => {
					callback(true);
				});
			} else {
				callback(true);
			}
		});
	};

	onChangeType = (v, field) => {
		const { form } = this.props;
		const { setFieldsValue } = form;
		console.log(v, field);
		if (field === 'year') {
			this.setState({
				yearValue: v,
				termValue: '',
				monthEnum: getSchoolMonth(v) || [],
				monthValue: '',
			});
			setFieldsValue({
				year: v,
				term: '',
				month: '',
			});
		} else if (field === 'term') {
			const { yearValue } = this.state;
			this.setState({
				termValue: v,
				monthEnum: getSchoolMonth(yearValue, v) || [],
				monthValue: '',
			});
			setFieldsValue({
				term: v,
				month: '',
			});
		} else {
			this.setState({
				monthValue: v,
			});
			setFieldsValue({
				month: v,
			});
		}
	};

	// 获取组织
	getOrgTreeList = () => {
		const {
			orgIndex: { org_name, org_index },
		} = this.props;
		const respose = [
			{
				org_name,
				org_index,
				child: null,
			},
		];

		return getLazyOrganizationsTree({
			org_index,
			type: 1,
			include_child: true,
			lazy_load: true,
			excludeApi: true,
		})
			.then((resp) => {
				if (resp) {
					respose[0].child = resp;
				} else {
					respose[0].child = [];
				}
			})
			.catch(() => {
				respose[0].child = [];
			})
			.finally(() => {
				this.setState({
					treeData: respose,
				});
			});
	};

	render() {
		const { form, orgIndex, operationType } = this.props;
		const { getFieldDecorator, getFieldValue } = form;
		const {
			formData: { taskName, date, attendance, checkNum },
			yearEnum,
			monthEnum,
			yearValue,
			termValue,
			monthValue,
			percent,
			treeData,
			spinning,
			maxCheckNum,
		} = this.state;

		return (
			<div style={{ padding: '16px 32px' }}>
				<Spin spinning={spinning}>
					<Form {...formItemLayout} onSubmit={this.handleSubmit}>
						<Item label="任务名称">
							{getFieldDecorator('task_name', {
								initialValue: taskName,
								// validateTrigger: 'onBlur', // 失去焦点时校验
								rules: [
									{
										required: true,
										message: '请输入任务名称',
									},
									{
										validator: (rules, val, callBack) => {
											if (val && val.length > 100) {
												callBack('任务名称不能超过100个字符');
												return;
											}
											if (!REGEX_NAME.test(val)) {
												callBack('请输入正确的任务名称');
												return;
											}
											callBack();
										},
									},
								],
							})(
								<Input
									style={{ width: 340 }}
									onBlur={(v) => {
										// this.checkTaskNameReq(v.target.value);
									}}
									disabled={operationType === 'view'}
								/>,
							)}
						</Item>

						<div className="hz-label" style={{ margin: 10 }}>
							考勤规则
						</div>
						<Item label="考勤时段">
							{getFieldDecorator('date', {
								initialValue: date || [],
								rules: [
									{
										required: true,
										message: '请选择考勤时段',
									},
									{
										validator: (rules, val, callBack) => {
											if (val && val.length === 2 && (!val[0] || !val[1])) {
												callBack('请选择完整的时间段');
											} else {
												callBack();
											}
										},
									},
								],
							})(
								<RangePicker
									style={{ width: 340 }}
									disabledDate={disabledDate}
									format="YYYY-MM-DD"
									getCalendarContainer={(triggerNode) => triggerNode.parentNode}
									onChange={(date) => {
										if (date && date.length === 2) {
											const maxCheckNum =
												moment(date[1].startOf('day')).diff(
													moment(date[0].startOf('day')),
													'day',
												) + 1;
											this.setState({ maxCheckNum });
										}
									}}
									disabled={operationType === 'view'}
								/>,
							)}
						</Item>
						<Item label="抽查天数">
							考勤
							{getFieldDecorator('check_num', {
								initialValue: checkNum || '',
								rules: [
									{
										required: true,
										message: ' ',
									},
									{
										validator: (rules, val, callBack) => {
											if (!val || (val && val > maxCheckNum)) {
												callBack(`抽查天数必须在1到${maxCheckNum}之间`);
											} else {
												callBack();
											}
										},
									},
								],
							})(
								<InputNumber
									min={1}
									max={maxCheckNum}
									style={{ margin: '0 5px' }}
									disabled={operationType === 'view'}
								/>,
							)}
							天
						</Item>

						<div className="hz-label" style={{ margin: 10 }}>
							考勤对象
						</div>
						{operationType === 'view' ? (
							<Item label="考勤目标">
								{getFieldDecorator('attendance', {
									initialValue: attendance || '',
									rules: [
										{
											required: true,
											message: '请选择考勤目标',
										},
									],
								})(<Input style={{ width: 340 }} disabled />)}
							</Item>
						) : (
							<Item label="考勤目标">
								{treeData.length ? (
									getFieldDecorator('attendance', {
										initialValue: attendance || [],
										rules: [
											{
												required: true,
												message: '请选择考勤目标',
											},
										],
									})(
										<TreeSelect
											style={{ width: 340 }}
											placeholder="请选择"
											treeProps={{
												dataSetting: {
													dataKey: 'org_index',
													dataViewKey: 'org_name',
													childArrayKey: 'child',
												},
												searchSetting: {
													placeholder: '搜索组织',
													maxLength: '30',
												},
												data: treeData,
												expandedKeys: orgIndex.org_index,
												hasSearch: true,
												checkStrictly: true, // 严格勾选
											}}
											onExtendsChange={(a) => {
												console.log('组织=======：：', a);
											}}
											multiple
											disabled={operationType === 'view'}
										/>,
									)
								) : (
									<TreeSelect
										style={{ width: 340 }}
										placeholder="请选择"
										treeProps={{
											dataSetting: {
												dataKey: 'org_index',
												dataViewKey: 'org_name',
												childArrayKey: 'child',
											},
											data: [],
										}}
									/>
								)}
							</Item>
						)}

						{/* 考勤目标筛选 */}
						{operationType === 'view' ||
						(getFieldValue('attendance') && getFieldValue('attendance').length) ? (
							<Item
								label={
									<>
										<span
											style={{
												display: 'inline-block',
												marginRight: '4px',
												color: '#fc2344',
												fontSize: '14px',
												fontFamily: 'SimSun, sans-serif',
												verticalAlign: 'middle',
											}}
										>
											*
										</span>
										目标筛选
									</>
								}
							>
								{getFieldDecorator('target_filter', {
									initialValue: true,
								})(
									<Form layout="inline">
										<Item label="学年">
											{getFieldDecorator('year', {
												initialValue: yearValue || '',
											})(
												<EnumSelect
													list={yearEnum}
													onChange={(e) => this.onChangeType(e, 'year')}
													disabled={operationType === 'view'}
												/>,
											)}
										</Item>
										<Item label="学期">
											{getFieldDecorator('term', {
												initialValue: termValue || '',
											})(
												<EnumSelect
													hasAll
													list={[
														{ code: '1', name: '秋季' },
														{ code: '0', name: '春季' },
													]}
													onChange={(e) => this.onChangeType(e, 'term')}
													disabled={operationType === 'view'}
												/>,
											)}
										</Item>
										<Item label="月份">
											{getFieldDecorator('month', {
												initialValue: monthValue || '',
											})(
												<EnumSelect
													hasAll
													list={monthEnum}
													onChange={(e) => this.onChangeType(e, 'month')}
													disabled={operationType === 'view'}
												/>,
											)}
										</Item>
										<Item label="缺勤率">
											≥
											{getFieldDecorator('percent', {
												initialValue:
													percent || percent === 0 ? percent : 50,
											})(
												<InputNumber
													min={0}
													max={100}
													style={{ margin: '0 5px' }}
													formatter={(v) => Number(v)}
													disabled={operationType === 'view'}
												/>,
											)}
											%
										</Item>
									</Form>,
								)}
							</Item>
						) : null}
					</Form>
				</Spin>
			</div>
		);
	}
}

export default Form.create()(FormPage);