/*
 * @Author: xiedan WX042
 * @since: 2020-12-09 19:10:12
 * @Description: 在离校业务配置
 */
import React, { useState, useEffect } from 'react';
import { Button, Form, message, Spin, InputNumber } from 'antd';
import ClassNames from 'classnames';
import { API_ERROR_CODE } from '@/constants';
import { getSchoolStatusConfig, putSchoolStatusConfig } from '@/services/businessConfig';
import styles from './index.less';

const arr = Array.from({length: 31}, (v, k) => ({title: k+1, isSelected: true}));

function SchoolStatus(props) {
	const { form: { getFieldDecorator, validateFields, setFieldsValue } } = props || {};
	const [loading, setLoading] = useState(false);
	const [dates, setDates] = useState(arr); // 每月在校日期
	const [downDate, setDownDate] = useState(''); // 鼠标按下拖动的开始日期
	const [isMouseDown, setIsMouseDown] = useState(false); // 鼠标是否按下即将开始拖拽

	useEffect(() => {
		setLoading(true);
		getSchoolStatusConfig().then(res => {
			const { data = {}, error_code, message: msg } = res || {};
			if (API_ERROR_CODE !== error_code) {
				return message.error(msg || '无法获取在离校业务配置');
			}
			const { stat_period_config = {}, normal_attendance } = data;
			initDate(stat_period_config.start_day_of_month, stat_period_config.end_day_of_month);
			setFieldsValue({ 'normal_attendance': normal_attendance || '10' });
		})
			.catch(() => message.error('无法获取在离校业务配置'))
			.finally(() => setLoading(false))
	// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [setFieldsValue]);

	// 初始化日期
	const initDate = (s = 1, e = 31) => {
		let a = [...dates];
		a = a.map(i => {
			if(i.title >= s && i.title <= e) {
				return {
					...i,
					isSelected: true,
				}
			}
			return {
				...i,
				isSelected: false,
			};
		})
		setDates(a);
	}

	const handleSaveRecord = () => {
		setLoading(true);
		validateFields((error, values) => {
			if (error) {
				setLoading(false)
				return
			}
			const period = dates.filter(i => i.isSelected).map(k => k.title);
			const len = period.length;
			if(!period || len < 2) {
				message.error('每月在校天数不能少于2天');
				setLoading(false)
				return
			}
			if(Number(values.normal_attendance) > len) {
				message.error('每月考勤天数不能大于每月在校天数');
				setLoading(false)
				return
			}
			const params = {
				normal_attendance: values.normal_attendance,
				stat_period_config: {
					start_day_of_month: period[0],
					end_day_of_month: period[len - 1],
				},
			}
			putSchoolStatusConfig(params).then(res => {
				const { error_code, message: msg } = res || {};
				if (API_ERROR_CODE !== error_code) {
					return message.error(msg || '无法修改在离校业务配置');
				}
				message.success('数据修改成功')
			})
				.catch(() => message.error('无法修改在离校业务配置'))
				.finally(() => setLoading(false));
		});
	}

	// 鼠标拖拽选择
	const onDragSelected = (value) => {
		let a = [...dates];
		a = a.map(i => {
			if(i.title >= downDate && i.title <= value.title) {
				return {
					...i,
					isSelected: true,
				}
			}
			return i;
		})
		setDates(a);
	}
	
	// 鼠标按下
	const onMouseDown = (value) => {
		// 全局鼠标弹起事件
		document.body.onmouseup = onMouseUp;
		setIsMouseDown(true);
		setDownDate(value.title);
		
	}

	// 鼠标弹起
	const onMouseUp = () => {
		setIsMouseDown(false);
        document.body.onmouseup = null;
	}

	// 重置
	const handleReset = () => {
		setDates(Array.from({length: 31}, (v, k) => ({title: k+1, isSelected: false})))
		setFieldsValue({ 'normal_attendance': '' });
	}

	return (
		<Spin spinning={loading} size='large'>
			<div className={styles.content}>
				<Form layout='inline' className={styles.formStyles} labelAlign='left'>
					<div className="hz-label" style={{ margin: '0  0 20px 0' }}>每月在校日期</div>
					<div className={styles.btnWapper}>
						{
							dates && dates.map((item, index) => {
								return (
									<span
										className={ClassNames({
											[styles.btnItem]: true,
											[styles.isSelected]: item.isSelected,
										})}
										style={{
											borderTop: index > 11 && 'none',
											borderRight: ![11, 23, 30].includes(index) && !item.isSelected && 'none',
										}}
										key={item.title}
										value={item.title}
										onMouseDown={e => onMouseDown(item)}
										onMouseEnter={() => {isMouseDown && onDragSelected(item)}}	
									>
										{item.title}
									</span>
								)
							})
						}
					</div>
					<div className="hz-label" style={{ margin: '0  0 20px 0' }}>学生在校判断规则</div>
					<Form.Item label="每月考勤正常天数">
						≥&nbsp;&nbsp;
						{getFieldDecorator('normal_attendance', {
						initialValue: '10',
						rules: [{ required: true, message: '请填写每月考勤正常天数' }],
					})(<InputNumber min={1} max={dates && dates.filter(i => i.isSelected).length || 31} />)}
						&nbsp;&nbsp;<span style={{ color: '#999' }}>则判断学生在校</span>
					</Form.Item>
					<div style={{ color: '#F7B500' }}>注：正常天数只包括正常考勤，请假、实习不算正常天数</div>
				</Form>
				<div className={styles.bottom}>
					<Button onClick={handleSaveRecord} type='primary'>保存</Button>
					<Button onClick={handleReset} style={{marginLeft: '20px'}}>重置</Button>
				</div>
			</div>
		</Spin>
	);
}

export default Form.create()(SchoolStatus);
