/**
 * 详情列表
 */
import React, { useState } from 'react';
import { Table } from '@hz-components/react-base';
import {
	Modal,
	Button,
	message,
} from 'antd';

import {
	detailsTableColumns,
	detailsCreateHandleOptions,
	detailsCreateSearchOptions,
} from '../../utils';
import {
	getRollCallDetails,
	exportRollCallDetails,
} from '@/services/practiceRollCall';
import {
	filterNullParam,
	REQUEST_SUCCESS,
} from '../../../../dailyAttendance/dailyAttendanceStatistics/utils';
import { DATA_TYPE_NO_SPACE } from '@/constants';
import moment from 'moment';

// 上一次请求参数
let oldParams = {};

function DetailsTable(props) {
	// 表格 ref
	const [listRef, setListRef] = useState(null);

	const {
		visible,
		onCancel,
		record_id,
	} = props;

	/**
	 * 处理请求数据
	 */
	const transformQuery = params => {
		const {
			pageIndex: page_num,
			pageSize: page_size,
			sorter: {
				field,
				order,
			} = {},
			...obj
		} = params;
		return {
			...obj,
			page_num,
			page_size,
			record_id,
			order_field: field,
			order_rule: order ? order === 'ascend' ? 'asc' : 'desc' : undefined,
		}
	}

	/**
	 * 请求数据
	 */
	const createPromise = async params => {
		oldParams = params;
		try {
			const res = await getRollCallDetails(params);
			const {
				data: {
					data,
					paging: {
						page_num,
						total,
					},
				} ={},
				error_code,
				message: msg,
			} = res;
			if (error_code === REQUEST_SUCCESS) {
				return {
					currentPageResult: data,
					totalCount: total,
					pageIndex: page_num,
				};
			}
			message.error(msg || '获取数据失败。');
		} catch (err) {
			console.log(err);
		}
	}

	/**
	 * 导出
	 */
	const exportFn = async () => {
		if (!listRef.getList().length) {
			return message.error('暂无数据');
		}
		const {
			page_size,
			page_num,
			...obj
		} = oldParams;
		try {
			const res = await exportRollCallDetails(filterNullParam(obj));
			if (res) {
				let a = document.createElement('a');
				const url = window.URL.createObjectURL(new Blob([res]));
				a.href = url;
				a.setAttribute('download', `实习点名考勤详情_${moment().format(DATA_TYPE_NO_SPACE)}.xlsx`);
				a.click();
				a = null;
			}
		} catch (err) {
			console.log(err);
		}
	}

	/**
	 * 考勤状态筛选
	 */
	const handleStatus = value => {
		listRef.dataLoad({ attendance_result: value });
	}

	return (
		<Modal
			title='考勤详情'
			visible={visible}
			onCancel={onCancel}
			width={1100}
			footer={
				<Button onClick={onCancel}>取消</Button>
			}
			bodyStyle={{
				height: 700,
				backgroundColor: '#ececec',
			}}
		>
			<Table
				rowKey='id'
				hasSerialNo
				columns={detailsTableColumns}
				handleBarOptions={detailsCreateHandleOptions(exportFn)}
				searchBarOptions={detailsCreateSearchOptions(handleStatus)}
				transformQuery={transformQuery}
				createPromise={createPromise}
				setRef={ref => setListRef(ref)}
			/>
		</Modal>
	);
}

export default DetailsTable;
