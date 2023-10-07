/*
 * @Desc: 表单区
 * @Author: ranguangyu
 * @Date: 2020-09-02 10:07:06
 */
import React, { PureComponent } from 'react';
import { Form, Input, Radio, Icon, Modal, Tooltip } from 'antd';
import { EditTable, EnumSelect } from '@hz-components/react-base';
import classNames from 'classnames';
import PropTypes from 'prop-types';
import request from '@/utils/request';

import { API_ERROR_CODE } from '@constants';
import createTColumns from './tColumns';
import createJColumns from './jColumns';
import { validateRules, fixTime } from './utils';
import styles from './index.less';

class FormRender extends PureComponent {
	static propTypes = {
		data: PropTypes.object,
	};

	static defaultProps = {
		data: {},
	};

	state = {
		record: {},
		currentTab: 't', // t:通用型表达式 j:记录型表达式
	};

	componentDidMount() {
		const { data } = this.props;
		if (data.id) {
			this.dataLoad(data.id);
		}
	}

	dataLoad = id => {
		request({
			url: `/api/biz-scene/v1/attendance/express/${id}`,
			method: 'get',
		}).then(result => {
			if(result && result.error_code === API_ERROR_CODE) {
				const { common_express_list: cList, notes_express_list: nList } = result.data;
				// 当只有记录型表达式有数据时，tab默认切换成记录型表达式
				if((!cList || cList.length < 1) &&nList && nList.length > 0) {
					this.setState({
						currentTab: 'j',
					})
				}
				this.setState({ record: { ...result.data, id } });
			}
		});
	};

	submit = () => {
		const { record } = this.state;
		const { form } = this.props;
		return new Promise(resolve => {
			form.validateFieldsAndScroll((errors, values) => {
				if (errors) {
					return;
				}
				console.log('values', values);
				let { common_express_list, disable } = values;
				common_express_list = common_express_list.map((item, i) => {
					return {
						...item,
						start_time: fixTime(item.start_time),
						end_time: fixTime(item.end_time),
						start_offset:
							item.start_offset ||
							values[`EDITTABLE-common_express_list-${i}-start_offset`],
						end_offset:
							item.end_offset ||
							values[`EDITTABLE-common_express_list-${i}-end_offset`],
					};
				});
				const data = {
					...values,
					id: record.id,
					disable: disable === '1' ? true : false,
					common_express_list,
				};
				for (const field in values) {
					if (field.startsWith('EDITTABLE')) {
						delete data[field];
					}
				}
				resolve(data);
			});
		});
	};

	tabsChange = e => {
		const currentTab = e.target.value;
		this.setState({ currentTab });
	};

	render() {
		const { currentTab, record } = this.state;
		const { form } = this.props;
		const { getFieldDecorator, setFieldsValue, getFieldValue, getFieldsValue } = form;

		const formItemLayout = {
			labelCol: { span: 2 },
			wrapperCol: { span: 4 },
		};

		const formItemLayoutTable = {
			labelCol: { span: 2 },
			wrapperCol: { span: 22 },
		};

		return (
			<div style={{ padding: 16 }} className={styles['express-form-render']}>
				<Form {...formItemLayout}>
					<Form.Item label="表达结果名称">
						{getFieldDecorator('express_name', {
							initialValue: record['express_name'],
							rules: [
								{ required: true, message: '必填字段' },
								{
									max: 100,
									message: '不能超过100个字符',
								},
							],
						})(<Input />)}
					</Form.Item>
					<Form.Item label="触发方式">
						{getFieldDecorator('trigger_type', {
							initialValue:
								typeof record['trigger_type'] !== 'undefined'
									? String(record['trigger_type'])
									: '',
							rules: [{ required: true, message: '必填字段' }],
							onChange: value => {
								if (value === '0') {
									const { common_express_list } = getFieldsValue();
									const ejson = {};
									common_express_list.forEach((item, i) => {
										ejson[`EDITTABLE-common_express_list-${i}-start_offset`] = 0;
										ejson[`EDITTABLE-common_express_list-${i}-end_offset`] = 0;
									});
									// 如果是实时触发：则只支持通用型表达式，清空记录型表达式的数据，切换tab
									this.setState({
										currentTab: 't',
									})
									setFieldsValue({ cron: '', notes_express_list: [], ...ejson });
								}
							},
						})(
							<EnumSelect
								list={[
									{ code: '0', name: '实时触发' },
									{ code: '1', name: '时间触发' },
								]}
							/>,
						)}
					</Form.Item>
					<Form.Item
						label="cron表达式"
						className={classNames({
							hide: getFieldValue('trigger_type') === '0',
						})}
						required
					>
						{getFieldDecorator('cron', {
							initialValue: record['cron'],
							validateTrigger: 'onBlur',
							rules:
								getFieldValue('trigger_type') === '1'
									? [
											{
												validator: (rule, value, callback) => {
													if (!value) {
														callback('必填字段');
														return;
													}
													request({
														url:
															'/api/biz-scene/v1/attendance/cron/check',
														method: 'get',
														data: {
															cron: value,
														},
													}).then(result => {
														if (result.data === true) {
															callback();
														} else {
															callback('格式不正确');
														}
													});
												},
											},
									  ]
									: [],
						})(
							<Input
								suffix={
									<a
										onClick={() => {
											Modal.info({
												title: '举例说明',
												width: 800,
												content: (
													<div>
														<p>
															0 0 10,14,16 * * ?
															每天上午10点，下午2点，4点
														</p>
														<p>
															0 0/30 9-17 * * ?
															朝九晚五工作时间内每半小时
														</p>
														<p>0 0 12 ? * WED 表示每个星期三中午12点</p>
														<p>0 0 12 * * ? 每天中午12点触发</p>
														<p>0 15 10 ? * * 每天上午10:15触发</p>
														<p>0 15 10 * * ? 每天上午10:15触发</p>
														<p>0 15 10 * * ? * 每天上午10:15触发</p>
														<p>
															0 15 10 * * ? 2005
															2005年的每天上午10:15触发
														</p>
														<p>
															0 * 14 * * ?
															在每天下午2点到下午2:59期间的每1分钟触发
														</p>
														<p>
															0 0/5 14 * * ?
															在每天下午2点到下午2:55期间的每5分钟触发
														</p>
														<p>
															0 0/5 14,18 * * ?
															在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
														</p>
														<p>
															0 0-5 14 * * ?
															在每天下午2点到下午2:05期间的每1分钟触发
														</p>
														<p>
															0 10,44 14 ? 3 WED
															每年三月的星期三的下午2:10和2:44触发
														</p>
														<p>
															0 15 10 ? * MON-FRI
															周一至周五的上午10:15触发
														</p>
														<p>0 15 10 15 * ? 每月15日上午10:15触发</p>
														<p>
															0 15 10 L * ?
															每月最后一日的上午10:15触发
														</p>
														<p>
															0 15 10 ? * 6L
															每月的最后一个星期五上午10:15触发
														</p>
														<p>
															0 15 10 ? * 6L 2002-2005"
															2002年至2005年的每月的最后一个星期五上午10:15触发
														</p>
														<p>
															0 15 10 ? * 6#3
															每月的第三个星期五上午10:15触发
														</p>
													</div>
												),
												onOk() {},
											});
										}}
									>
										<Icon
											type="info-circle"
											style={{ color: 'rgba(0,0,0,.45)' }}
										/>
									</a>
								}
							/>,
						)}
					</Form.Item>
					<Form.Item label="重写使能">
						{getFieldDecorator('disable', {
							initialValue:
								typeof record['disable'] === 'boolean'
									? record['disable']
										? '1'
										: '0'
									: '',
							rules: [{ required: true, message: '必填字段' }],
						})(
							<EnumSelect
								list={[
									{ code: '1', name: '是' },
									{ code: '0', name: '否' },
								]}
							/>,
						)}
					</Form.Item>
					<Form.Item label="原子表达式" required {...formItemLayoutTable}>
						<Radio.Group
							defaultValue='t'
							buttonStyle="solid"
							onChange={this.tabsChange}
							value={currentTab}
						>
							<Radio.Button value="t">通用型表达式</Radio.Button>
							{/* 记录型表达式只支持时间触发 */}
							<Radio.Button disabled={getFieldValue('trigger_type')==='0'} value="j">记录型表达式</Radio.Button>
						</Radio.Group>
						<div
							className={classNames({
								hide: currentTab !== 't',
							})}
						>
							{getFieldDecorator('common_express_list', {
								valuePropName: 'dataSource',
								initialValue: record['common_express_list'] || [],
							})(<EditTable columns={createTColumns(form)} bridgeForm={form} />)}
						</div>
						<div
							className={classNames({
								hide: currentTab !== 'j',
							})}
						>
							{getFieldDecorator('notes_express_list', {
								valuePropName: 'dataSource',
								initialValue: record['notes_express_list'] || [],
							})(<EditTable columns={createJColumns(form)} bridgeForm={form} />)}
						</div>
					</Form.Item>
					<Form.Item label="规则逻辑关系" required>
						{getFieldDecorator('unit_express_compose', {
							initialValue: record['unit_express_compose'],
							validateTrigger: 'onBlur',
							rules: [
								{
									validator: (rule, value, callback) => {
										if (!value) {
											callback('必填字段');
											return;
										}
										const {
											common_express_list = [],
											notes_express_list = [],
										} = getFieldsValue();
										const code = common_express_list
											.map(item => item.t_code)
											.concat(notes_express_list.map(item => item.j_code));
										const { error, message } = validateRules(value, code);
										if (error) {
											callback(message);
										} else {
											callback();
										}
									},
								},
							],
						})(
							<Input
								suffix={
									<Tooltip title="示例：((A||B)&&C)||(C&&D)">
										<Icon
											type="info-circle"
											style={{ color: 'rgba(0,0,0,.45)' }}
										/>
									</Tooltip>
								}
							/>,
						)}
					</Form.Item>
				</Form>
			</div>
		);
	}
}

export default Form.create()(FormRender);
