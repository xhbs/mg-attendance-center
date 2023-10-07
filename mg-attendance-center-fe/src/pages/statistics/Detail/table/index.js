/*
 * @Description: 考勤明细table
 * @Author: zhangjie
 * @Date: 2020-07-14 15:39:29
 */
import React, { useState, useEffect } from 'react';
import { Button, Form, DatePicker, message } from 'antd';
import moment from 'moment';
import { Table } from '@hz-components/react-base';
import { DATE_TYPE } from '@constants';
import { CreateDrawer } from '@hz-components/ists-common';
import {
	getDetailList,
	viewDetail,
	resultSelect,
	updateDetail,
	exportData,
} from '@/services/detail';
import { HandleBarOptions } from '@components/CreateHandleBarOptions/index';
import DetailModal from '../../hooks/useModal';
import { tableColumns, tableSearchOptions, modalColumns } from './options';
import { downloadFileStream } from '@utils/utils';
import { useTaskData } from '../../hooks/useEnumData';
import styles from '../../common.less';

const { EnumSelect } = Table;
const showTime = 'HH:mm:ss';

const formItemLayout = {
	labelCol: {
		xs: { span: 2 },
	},
	wrapperCol: {
		xs: { span: 12 },
	},
};

// const codeMap = {};

const Detail = props => {
	const { getFieldDecorator, validateFieldsAndScroll } = props.form;
	const [drawVisible, setDrawVisible] = useState(false);
	const [tableRef, setTableRef] = useState(null);
	const [detailTableRef, setDetailTableRef] = useState(null);
	const [visible, setVisible] = useState(false);
	const [detail, setDetailRecord] = useState({});
	const [currentData, setCurrentData] = useState({});
	const [taskList, setTaskList] = useState([]);
	const [adjustDate, setAdjustDate] = useState([]);
	const [adjustType, setAdjustType] = useState('');
	const [resultData, setResultData] = useState([]);
	const [queryParams, setQueryParams] = useState({});
	const taskData = useTaskData();
	const [exportLoading, setExportLoading] = useState(false);
	const [total, setTotal] = useState(0);

	useEffect(() => {
		setTaskList(taskData || []);
	}, [taskData]);

	// 查看明细弹窗table
	const createDetailPromise = async (params) => {
		const { pageIndex } = params;
		const { ids, task_id } = detail;
		const result = { totalCount: 0, currentPageResult: [], pageIndex };
		return new Promise(resolve => {
			viewDetail({ids, taskId: task_id})
				.then(res => {
					if (!res || !res.data) {
						resolve(result);
					} else {
						result.totalCount = res.data.length;
						result.currentPageResult = res.data || [];
						result.pageIndex = pageIndex;
						resolve(result);
					}
				})
				.catch(() => {
					resolve(result);
				});
		});
	};

	const createPromise = async param => {
		const { pageIndex, pageSize, search = '', date, task_id } = param;
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
		setQueryParams({ ...params, page_size: 0, page_num: 0 });
		const result = { totalCount: 0, currentPageResult: [], pageIndex: 1 };
		return new Promise(resolve => {
			getDetailList(params)
				.then(res => {
					if (!res || !res.data) {
						setTotal(0);
						resolve(result);
					} else {
						result.totalCount = res.data.paging.total;
						result.currentPageResult = res.data.data || [];
						result.pageIndex = res.data.paging.page_num;
						setTotal(res.data.paging.total || 0);
						resolve(result);
					}
				})
				.catch(() => {
					setTotal(0);
					resolve(result);
				});
		});
	};

	// 查看明细
	const handleDetail = data => {
		setDetailRecord(data || []);
		setVisible(true);
		resultSelect().then(res => {
			if (!res || !res.data) return;
			setResultData(res.data || []);
		});
	};

	// 查看明细弹窗
	const handleModal = type => {
		if (!type) {
			if (detailTableRef) {
				detailTableRef.dataLoad();
			}
			setDrawVisible(false);
			return;
		}
		validateFieldsAndScroll((err, values) => {
			if (!err) {
				values.time = moment(values.time).valueOf();
				values.id = currentData.id;
				values.task_id = detail.task_id;
				updateDetail(values)
					.then(res => {
						if (res && res.data) {
							message.success('修改成功');
							if (detailTableRef) {
								detailTableRef.dataLoad();
							}
							if (tableRef) {
								tableRef.dataLoad();
							}
							setDrawVisible(false);
						}
					})
					.catch(() => {
						message.error('修改失败');
					});
			}
		});
	};

	const handleExport = () => {
		setExportLoading(true);
		exportData(queryParams)
			.then(res => {
				downloadFileStream('考勤明细.xlsx', res);
				setExportLoading(false);
			})
			.catch(() => {
				message.error('导出失败');
				setExportLoading(false);
			});
	};

	return (
		<div className="hz-layout-vertical">
			<div style={{ margin: 16 }} className="hz-layout-vertical-body">
				<Table
					columns={tableColumns({
						handleDetail,
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
					serialNoOverlying // 序号是否累加
					createPromise={createPromise}
					searchBarOptions={tableSearchOptions({ taskList })}
					setRef={tableRef => setTableRef(tableRef)}
				/>
			</div>
			<DetailModal
				visible={visible}
				onCancel={() => setVisible(false)}
				onOk={() => setVisible(false)}
				zIndex={10}
				width={800}
				footer={
					<Button type="primary" onClick={() => setVisible(false)}>
						关闭
					</Button>
				}
			>
				{!resultData.length ? null : (
					<Table
						columns={modalColumns({
							onSelect: (e, record) => {
								setDrawVisible(true);
								setAdjustDate(moment(record.pass_time));
								setAdjustType(e);
								setCurrentData(record);
							},
							list: resultData,
						})}
						hasSerialNo // 是否有序号
						serialNoOverlying // 序号是否累加
						createPromise={createDetailPromise}
						setRef={setDetailTableRef}
						pagination={{
							size: "small",
							// simple: true,
						}}
					/>
				)}
			</DetailModal>
			<CreateDrawer
				visible={drawVisible}
				title="新建"
				handleCancel={() => handleModal(false)}
				footer={
					<>
						<Button
							style={{ marginRight: 10 }}
							type="primary"
							onClick={() => handleModal(true)}
						>
							保存
						</Button>
						<Button onClick={() => handleModal(false)}>取消</Button>
					</>
				}
			>
				<div className={styles.formContent}>
					<Form {...formItemLayout}>
						<Form.Item label="调整类型">
							{getFieldDecorator('code', {
								// initialValue: !resultData.length ? '' : resultData[0].code,
								initialValue: adjustType,
							})(<EnumSelect style={{ width: 350 }} list={resultData} />)}
						</Form.Item>
						<Form.Item label="调整时间">
							{getFieldDecorator('time', {
								initialValue: adjustDate, // adjustDate
								rules: [{ required: true, message: '请选择调整时间' }],
							})(<DatePicker showTime={{ format: showTime }} format={DATE_TYPE} />)}
						</Form.Item>
					</Form>
				</div>
			</CreateDrawer>
		</div>
	);
};

export default Form.create({ name: 'detailContent' })(Detail);
