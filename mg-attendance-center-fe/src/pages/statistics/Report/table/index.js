/*
 * @Description:
 * @Author: zhangjie
 * @Date: 2020-07-14 15:39:29
 */
import React, { useState, useEffect } from 'react';
import { message, Spin } from 'antd';
import moment from 'moment';
import { Table } from '@hz-components/react-base';
import { getReportList, getColumns, exportData } from '@/services/report';
import { useTaskData } from '../../hooks/useEnumData';
import { HandleBarOptions } from '@components/CreateHandleBarOptions/index';
import { downloadFileStream } from '@utils/utils';
import { tableColumns, tableSearchOptions } from './options';

export default () => {
	const [tableRef, setTableRef] = useState(null);
	const [exportLoading, setExportLoading] = useState(false);
	const [taskList, setTaskList] = useState([]);
	const [queryParams, setQueryParams] = useState(JSON.stringify({}));
	const [loading, setLoading] = useState(true);
	const [columns, setColumns] = useState([]);
	const [total, setTotal] = useState(0);
	const taskData = useTaskData();

	useEffect(() => {
		setTaskList(taskData || []);
	}, [taskData]);
	useEffect(() => {
		getColumns(JSON.parse(queryParams))
			.then(res => {
				setLoading(false);
				if (res && res.data) {
					setColumns(res.data || []);
					if (tableRef) {
						tableRef.dataLoad();
					}
				}
			})
			.catch(err => {
				setLoading(false);
				setColumns([]);
				throw err;
			});
	}, [queryParams, tableRef]);

	const createPromise = async param => {
		const { pageIndex, pageSize, search = '', task_id, date } = param;
		const params = {
			page_size: pageSize,
			page_num: pageIndex,
			// orderFiled: '', // 排序字段
			// orderRule: '', // 排序规则
			task_id,
			query: search, // 模糊查询
		};
		if (date && date.length) {
			// params.ats_start_time = moment(date[0]).valueOf();
			// params.ats_end_time = moment(date[1])
			// 	.set('millisecond', 999)
			// 	.valueOf();
			params.ats_start_time = moment(
				moment(date[0]).format('YYYY-MM-DD') + ' 00:00:00',
			).valueOf();
			params.ats_end_time = moment(moment(date[1]).format('YYYY-MM-DD') + ' 23:59:59')
				.set('millisecond', 999)
				.valueOf();
		}
		setQueryParams(JSON.stringify({ ...params, page_size: pageSize, page_num: pageIndex }));
		const result = { totalCount: 0, currentPageResult: [], pageIndex: 1 };
		return new Promise(resolve => {
			getReportList(params)
				.then(res => {
					if (!res || !res.data) {
						setTotal(0);
						resolve(result);
					} else {
						result.totalCount = res.data.paging.total;
						result.currentPageResult = res.data.data || [];
						result.pageIndex = res.data.paging.page_num;
						setTotal(res.data.paging.total);
						resolve(result);
					}
				})
				.catch(() => {
					setTotal(0);
					resolve(result);
				});
		});
	};

	const handleExport = () => {
		setExportLoading(true);
		exportData(JSON.parse(queryParams))
			.then(res => {
				downloadFileStream('考勤报表.xlsx', res);
				setExportLoading(false);
			})
			.catch(() => {
				message.error('导出失败');
				setExportLoading(false);
			});
	};

	return (
		// <Spin spinning={!columns.length ? true : false} size="large">
		<Spin spinning={loading} size="large">
			<div style={{ height: 'calc(100vh - 96px)' }}>
				<Table
					columns={tableColumns(columns)}
					handleBarOptions={HandleBarOptions({
						handleExport,
						disabledExp: total === 0,
						loadingExp: exportLoading,
						searchPlaceholder: '对象名称',
					})}
					hasSerialNo
					hasFilter // 是否带有"漏斗"（以操作栏和筛选栏存在为前提），控制打开和收起筛选栏
					filterDefaultIsOpened // 筛选"漏斗"初始化状态是否开启
					serialNoOverlying // 序号是否累加
					createPromise={createPromise}
					searchBarOptions={tableSearchOptions({ taskList })}
					setRef={tableRef => setTableRef(tableRef)}
					scrollX={columns.length * 150}
				/>
			</div>
		</Spin>
	);
};
