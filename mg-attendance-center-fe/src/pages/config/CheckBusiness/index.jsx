/*
 * @Author: xiedan WX042
 * @since: 2020-12-09 19:10:11
 * @Description: 抽查业务配置
 */
import React, { useState, useEffect } from 'react';
import { Button, Form, message, Spin, InputNumber } from 'antd';
import { getCheckBusinessConfig, putCheckBusinessConfig } from '@/services/businessConfig';
import { API_ERROR_CODE } from '@/constants';
import styles from './index.less';

function CheckBusiness(props) {
	const { form: { getFieldDecorator, validateFields, setFieldsValue } } = props || {};
	const [loading, setLoading] = useState(false);

	useEffect(() => {
		setLoading(true);
		getCheckBusinessConfig().then(res => {
			const { data, error_code, message: msg } = res || {};
			if (API_ERROR_CODE !== error_code) {
				return message.error(msg || '无法获取抽查业务配置');
			}
			setFieldsValue({
				'validity': data.validity || '1',
				'absence': data.absence || '10',
			});
		})
			.catch(() => message.error('无法获取抽查业务配置'))
			.finally(() => setLoading(false))
	}, [setFieldsValue]);

	const handleSaveRecord = () => {
		setLoading(true);
		validateFields((error, values) => {
			if (error) {
				return
			}
			putCheckBusinessConfig(values).then(res => {
				const { error_code, message: msg } = res || {};
				if (API_ERROR_CODE !== error_code) {
					return message.error(msg || '无法修改抽查业务配置');
				}
				message.success('数据修改成功')
			})
				.catch(() => message.error('无法修改抽查业务配置'))
				.finally(() => setLoading(false));
		});
	}

	return (
		<Spin spinning={loading} size='large'>
			<div className={styles.content}>
				<Form layout='inline' className={styles.formStyles} labelAlign='left'>
					<div className="hz-label" style={{ margin: '0  0 20px 0' }}>抽查推荐规则</div>
					<Form.Item label="每月考勤缺勤天数">
						≥&nbsp;&nbsp;
						{getFieldDecorator('absence', {
						initialValue: '10',
						rules: [{ required: true, message: '请填写每月考勤缺勤天数' }],
					})(<InputNumber min={1} max={31} />)}
						&nbsp;&nbsp;<span style={{ color: '#999' }}>则判断学生为推荐抽查学生</span>
					</Form.Item>
					<div style={{ color: '#F7B500' }}>注：缺勤天数设置范围为1~31，需设置天数应不大于每月日常考勤天数，否则该月此规则无效</div>

					<div className="hz-label" style={{ margin: '20px 0' }}>抽查申诉期限</div>
					<Form.Item label="申诉有效期">
						{getFieldDecorator('validity', {
							initialValue: '1',
							rules: [{ required: true, message: '请填写申诉有效期' }],
						})(<InputNumber min={1} max={30} />)}
						&nbsp;&nbsp;天
					</Form.Item>
					<div style={{ color: '#F7B500' }}>注：申诉有效期设置范围为1~30，申诉只针对抽查考勤任务缺勤的情况，超过设置的申请有效期，则无法进行申诉</div>
				</Form>
				<div className={styles.bottom}>
					<Button onClick={handleSaveRecord} type='primary'>保存</Button>
				</div>
			</div>
		</Spin>
	);
}

export default Form.create()(CheckBusiness);
