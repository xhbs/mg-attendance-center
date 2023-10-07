/**
 * 考勤明细 弹窗
 */

import React, { useState } from 'react';
import { message } from 'antd';
import { Table } from '@hz-components/react-base';

import {
	REQUEST_SUCCESS,
	semesterEnum,
	getEnumValue,
	filterNullParam,
} from '../../utils';
import AttendanceDetailsPopup from '../attendanceDetailsPopup';
import { getPersonDetails } from '../../../../../services/dailyAttendance';
import moment from 'moment';
import {
	columns,
	createHandleBarOptions,
	createSearchBarOptions,
} from './utils';
import styles from './index.less';

function AttendanceDetails(props) {
	const {
		visible,
		personNo,
		onCancel,
		screen,
	} = props;
	// 表格ref
	const [listRef, setListRef] = useState(null);
	// 是否显示 星期的 考勤明细
	const [showWeekDetails, setShowWeekDetails] = useState(false);
	// 选择的星期的日期
	const [selectWeekDate, setSelectWeekDate] = useState('');

	/**
	 * 请求的参数处理
	 */
	const transformQuery = params => {
		const {
			pageIndex: page_num,
			pageSize:page_size,
			result,
			sorter,
		} = params;
		const {
			school_year,
			school_term,
			year_month: month,
		} = screen;
		// 排序  ture-倒序  false-升序
		const orderObj = {
			descend: true,
			ascend: false,
		};
		return {
			person_no: personNo,
			page_num,
			page_size,
			result,
			school_year,
			semester: school_term ?  getEnumValue(semesterEnum, school_term): '',
			month,
			order_by_result_desc: sorter ? orderObj[sorter.order]: '',
		}
	}

	/**
	 * 获取数据
	 */
	const getData = async (params) => {
		try {
			const result = await getPersonDetails(filterNullParam(params));
			const {
				data: {
					data = [],
					paging: {
						total: totalCount,
						page_num: pageIndex,
					},
				} = {},
				error_code,
				message: msg,
			} = result || {};
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
							cycle: `${attendance_start_date}~${attendance_end_date}`,
						}
					}),
					pageIndex,
				}
			}
			message.error(msg);
		} catch (err) {
			console.log(err);
		}
	}

	/**
	 * 查看星期 考勤明细
	 */
	const handleLookDetails = (row, week) => {
		const time = new Date(row.attendance_start_date);
		const date = moment(time).add(week - 1, 'd').format('YYYY-MM-DD');
		setSelectWeekDate(date);
		setShowWeekDetails(true);
	}

	return (
		<div
			className={styles['attendance-details']}
			hidden={!visible}
		>
			<Table
				rowKey='id'
				columns={columns(handleLookDetails)}
				transformQuery={transformQuery}
				createPromise={getData}
				hasSerialNo
				searchBarOptions={createSearchBarOptions(listRef)}
				handleBarOptions={createHandleBarOptions(onCancel)}
				setRef={listRef => setListRef(listRef)}
			/>
			{
				showWeekDetails && (
					<AttendanceDetailsPopup
						visible={showWeekDetails}
						selectWeekDate={selectWeekDate}
						personNo={personNo}
						onCancel={() => {setShowWeekDetails(false)}}
					/>
				)
			}
		</div>
	);
}

export default AttendanceDetails;
