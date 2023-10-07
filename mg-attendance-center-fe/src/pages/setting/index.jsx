/*
 * @Author: heqiang 00621
 * @Date: 2020-08-31 11:28:35
 * @Description: 考勤参数设置
 */
import React, { useState, useEffect } from 'react';
import { Button, Form, Input, message, Select, Spin } from 'antd';
import { RECORD_KEEP_TIME } from '@constants';
import { queryParamsReq } from '@services/adjustService';
import styles from './index.less';

const { Option } = Select;

const formItemLayout = {
	labelCol: { span: 3 },
	wrapperCol: { span: 6 },
};

function Setting(props) {
	const { form: { getFieldDecorator, validateFields, setFieldsValue } } = props || {};
	const [loading, setLoading] = useState(false);
	const [data, setData] = useState({});

	useEffect(() => {
		setLoading(true);
		queryParamsReq().then(res => {
			if (res) {
				const { original_data_hold_months, board_title } = res.data[0] || {};
				setData(res.data[0]);
				setFieldsValue({
					'original_data_hold_months': original_data_hold_months,
					'board_title': board_title,
				});
			} else {
				message.error('无法获取到数据');
			}
		}).catch().finally(() => setLoading(false));
	}, [setFieldsValue]);

	const handleSaveRecord = () => {
		validateFields((error, values) => {
			if (error) {
				return
			}
			const { original_data_hold_months: months, board_title: title } = data || {};
			const { original_data_hold_months: v_months, board_title: v_title } = values || {};
			if (months === v_months && title === v_title) {
				return message.info("您还未修改任何内容");
			}
			setLoading(true);
			queryParamsReq(['put', values])
				.then(() => {
					message.success("修改成功");
					setData(values);
				})
				.catch().finally(() => setLoading(false));;
		});
	}

	return (
		<Spin spinning={loading} size='large'>
			<div className={styles.content}>
				<Form {...formItemLayout} className={styles.formStyles}>
					<Form.Item label="考勤记录保存时长">
						{getFieldDecorator('original_data_hold_months')(
							<Select placeholder='请选择考勤保存时长'>
								{RECORD_KEEP_TIME.map(item => (
									<Option value={item.code} key={item.code}>{item.name}</Option>
								))}
							</Select>
						)
						}
					</Form.Item>
					<Form.Item label="考勤数据看板标题">
						{getFieldDecorator('board_title', {
							rules: [{required: true, message: '请填写考勤数据看板标题'}],
						})(<Input placeholder="考勤数据看板标题" maxLength='14' />)}
					</Form.Item>
				</Form>
				<div className={styles.bottom}>
					<Button onClick={handleSaveRecord} type='primary'>保存</Button>
				</div>
			</div>
		</Spin>
	);
}

export default Form.create()(Setting);
