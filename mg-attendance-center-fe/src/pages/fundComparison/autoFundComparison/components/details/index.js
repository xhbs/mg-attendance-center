/**
 * 详情
 */

import React, { useState } from 'react';
import { Icon, message } from 'antd';
import { Table } from '@hz-components/react-base';
import moment from 'moment';
import qs from 'querystring';

import {
	detailsColumns,
	createDetailsHandle,
	subsidType,
} from '../../../utils';
import DetailsLeft from '../detailsLeft';
import AttendanceDetailsPopup from '../../../../dailyAttendance/dailyAttendanceStatistics/components/attendanceDetailsPopup';
import styles from './index.less';
import { subsidList } from '../../../../../services/fundComparison';
import { filterNullParam, REQUEST_SUCCESS } from '../../../../dailyAttendance/dailyAttendanceStatistics/utils';

// 上一次请求的参数
let oldParams = {};

function Details(props) {
	// 是否显示详情
	const [visible, setVisible] = useState(false);
	// 传递给弹窗的信息
	const [param, setParam] = useState({});

	const {
		info,
		subsidRuleId,
		onCancel,
	} = props;

	/**
	 * 请求参数处理
	 * @param params
	 * @returns {*}
	 */
	const transformQuery = params => {
		const { person_no } = info;
		const {
			pageIndex: page_num,
			pageSize: page_size,
			...obj
		} = params;
		return {
			...obj,
			page_num,
			page_size,
			person_no,
			subsid_rule_id: subsidRuleId,
		};
	}

	/**
	 * 请求
	 */
	const createPromise = async params => {
		oldParams = params;
		try {
			const res = await subsidList(params);
			console.log(res);
			const {
				data: {
					data,
					paging: {
						page_num: pageIndex,
						total: totalCount,
					},
				},
				error_code,
				message: msg,
			} = res;
			if (error_code === REQUEST_SUCCESS) {
				return {
					totalCount,
					currentPageResult: data.map(item => {
						const {
							attendance_start_date,
							attendance_end_date,
						} = item;
						return {
							...item,
							subsid_date: `${attendance_start_date}~${attendance_end_date}`,
						}
					}),
					pageIndex,
				}
			}
			message.error(msg || '获取数据失败');
		} catch (err) {
			console.log(err);
		}
	}

	/**
	 * 点击星期 考勤
	 */
	const handleClickWeek = (row, week) => {
		console.log(row);
		const {
			attendance_type,
			task_rel_id,
			attendance_start_date,
		} = row;
		let date = attendance_start_date;
		if (attendance_type === subsidType.DAILY) {
			const time = new Date(attendance_start_date);
			date = moment(time).add(week - 1, 'd').format('YYYY-MM-DD');
		}
		setParam({
			weekDate: date,
			taskId: task_rel_id,
		});
		setVisible(true);
	}

	/**
	 * 导出
	 */
	const exportFn = () => {
		const url = '/api/biz-scene/v1/attendance/subsid/stu/deatail/export';
		const {
			page_num,
			page_size,
			...obj
		} = oldParams;
		const param = qs.stringify(filterNullParam(obj));
		window.open(`${url}?${param}`, '_self');
	}

	return (
		<div className={styles.details}>
			<div className={styles['top-back']}>
				<div
					className={styles.back}
					onClick={onCancel}
				>
					<Icon className={styles.icon} type='left-circle'/>
					详情
				</div>
			</div>
			<div className={styles.main}>
				<DetailsLeft
					info={info}
					onCancel={onCancel}
				/>
				<div className='fund-comparison-details-table'>
					<Table
						columns={detailsColumns(handleClickWeek)}
						handleBarOptions={createDetailsHandle(exportFn)}
						transformQuery={transformQuery}
						createPromise={createPromise}
					/>
				</div>
			</div>
			{
				visible && (
					<AttendanceDetailsPopup
						visible={visible}
						taskId={param.taskId}
						personNo={info.person_no}
						selectWeekDate={param.weekDate}
						onCancel={() => setVisible(false)}
					/>
				)
			}
		</div>
	);
}

export default Details;
