/*
 * @Description:
 * @Author: zhangjie
 * @Date: 2020-07-14 15:39:29
 */
import React, { useState } from 'react';
import moment from 'moment';
import { message } from 'antd';
import { Table } from '@hz-components/react-base';
import { getRecordList, exportData } from '@/services/record';
import { HandleBarOptions } from '@components/CreateHandleBarOptions/index';
import { downloadFileStream } from '@utils/utils';
import { tableColumns, tableSearchOptions } from './options';

export default () => {
	const [exportLoading, setExportLoading] = useState(false);
	const [total, setTotal] = useState(0);
	const [queryParams, setQueryParams] = useState({});

	const createPromise = async param => {
		const { pageIndex, pageSize, search = '', date, attendance_way } = param;
		const params = {
			page_size: pageSize,
			page_num: pageIndex,
			// orderFiled: '', // 排序字段
			// orderRule: '', // 排序规则
			attendance_way, // 考勤方式 face_recognition/刷卡/xxx方
			query: search, // 模糊查询
		};
		if (date && date.length) {
			params.ats_start_time = moment(date[0]).valueOf();
			params.ats_end_time = moment(date[1])
				.set('millisecond', 999)
				.valueOf();
		}
		setQueryParams({ ...params, page_size: 0, page_num: 0 });

		const result = { totalCount: 0, currentPageResult: [], pageIndex: 1 };
		return new Promise(resolve => {
			getRecordList(params)
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
		exportData(queryParams)
			.then(res => {
				downloadFileStream('考勤记录.xlsx', res);
				setExportLoading(false);
			})
			.catch(() => {
				message.error('导出失败');
				setExportLoading(false);
			});
	};

	return (
		<Table
			columns={tableColumns({
				handleDetail: () => {},
			})}
			handleBarOptions={HandleBarOptions({
				handleExport,
				disabledExp: total === 0,
				loadingExp: exportLoading,
				searchPlaceholder: '对象名称/对象身份',
			})}
			hasSerialNo // 是否有序号
			hasFilter // 是否带有"漏斗"（以操作栏和筛选栏存在为前提），控制打开和收起筛选栏
			filterDefaultIsOpened // 筛选"漏斗"初始化状态是否开启
			serialNoOverlying // 序号是否累加.
			createPromise={createPromise}
			searchBarOptions={tableSearchOptions()}
		/>
	);
};
