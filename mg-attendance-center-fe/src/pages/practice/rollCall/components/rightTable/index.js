/**
 * 右侧表格
 */
import React, { useEffect, useState } from 'react';
import { Table } from '@hz-components/react-base';
import { message } from 'antd';

import {
	tableColumns,
	createHandleOptions,
	createSearchOptions,
} from '../../utils';
import {
	getRollCallList,
	exportRollCallList,
} from '@/services/practiceRollCall';
import DetailsTable from '../detailsTable';
import {
	filterNullParam,
	REQUEST_SUCCESS,
} from '../../../../dailyAttendance/dailyAttendanceStatistics/utils';
import { DATA_TYPE_NO_SPACE } from '@/constants';
import moment from 'moment';


// 上一次请求的参数
let oldParams = {};

function RightTable({ activeInfo }) {
	// 是否显示详情列表
	const [visible, setVisible] = useState(false);
	// 表格 ref
	const [listRef, setListRef] = useState(null);
	// 当前点名信息
	const [rowInfo, setRowInfo] = useState({});

	useEffect(() => {
		listRef && listRef.dataLoad();
	// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [activeInfo]);

	/**
	 * 处理请求参数
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
		const {
			// activeOrgIndex,
      // indexPath,
      org_index: activeOrgIndex,
      index_path: indexPath,
		} = activeInfo;
		return {
			...obj,
			page_num,
			page_size,
			org_index_path: `${indexPath}${activeOrgIndex}`,
			order_field: field,
			order_rule: order ? order === 'ascend' ? 'asc' : 'desc' : undefined,
		};
	}

	/**
	 * 获取数据
	 */
	const createPromise = async params => {
		oldParams = params;
		try {
			const res = await getRollCallList(params);
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
	 * 查看详情
	 */
	const handleDetails = row => {
		setRowInfo(row);
		setVisible(true);
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
			const res = await exportRollCallList(filterNullParam(obj));
			if (res) {
				let a = document.createElement('a');
				const url = window.URL.createObjectURL(new Blob([res]));
				a.href = url;
				a.setAttribute('download', `实习点名_${moment().format(DATA_TYPE_NO_SPACE)}.xlsx`);
				a.click();
				a = null;
			}
		} catch (err) {
			console.log(err);
		}
	}

	/**
	 * 状态筛选
	 */
	const handleStatus = state => {
		listRef.dataLoad({ attendance_state: state });
	}

	/**
	 * 时间筛选
	 */
	const handleTime = value => {
		const [start, end] = value;
		listRef.dataLoad({
			report_time_min: start?.format('YYYY-MM-DD'),
			report_time_max: end?.format('YYYY-MM-DD'),
		});
	}

	return (
		<div
			style={{
				flex: 1,
        // marginLeft: 4,
        height: '100%',
			}}
		>
			<Table
				rowKey='id'
				hasSerialNo
				columns={tableColumns(handleDetails)}
				handleBarOptions={createHandleOptions(exportFn)}
				searchBarOptions={createSearchOptions(handleStatus, handleTime)}
				transformQuery={transformQuery}
				createPromise={createPromise}
				forceFirstLoadDisabled={true}
				setRef={ref => setListRef(ref)}
			/>
			{
				visible && (
					<DetailsTable
						visible={visible}
						record_id={rowInfo.record_id}
						onCancel={() => setVisible(false)}
					/>
				)
			}
		</div>
	);
}

export default RightTable;
