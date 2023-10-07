/*
 * @Author: heqiang 00621
 * @Date: 2020-08-31 11:28:35
 * @Description: 考勤调整管理
 */
import React, { useState } from 'react';
import { Button, message, Modal } from 'antd';
import moment from 'moment';
import { Table } from '@hz-components/react-base';
import { CreateDrawer } from '@hz-components/ists-common';
import { HandleBarOptions } from '@components/CreateHandleBarOptions';
import {
	queryAdjustListReq,
	deleteAdjustBatchReq,
	exportAdjustBatchReq,
	addAdjustItemReq,
} from '@services/adjustService';
import columns from './components/columnsObj';
import searchBarOptions from './components/searchBarOptions';
import DetailModalContent from './components/detailModalContent';
import { downloadFileStream } from '@utils/utils';
import { DATA_TYPE_NO_SPACE, API_ERROR_CODE } from '../../constants';
// import { initTreeStructure } from './components/initTreeStructure';
import styles from './index.less';

function Adjustment() {
	const [listRef, setListRef] = useState(null);
	const [listRefChild, setListRefChild] = useState(null);
	const [selectedRowKeys, setSelectedRowKeys] = useState([]);
	const [visible, setVisible] = useState(false);
	// 查看详情id
	const [detailId, setDetailId] = useState(null);
	// 查询参数
	const [dataInfo, setDataInfo] = useState({});
	// 列表数据
	const [tableData, setTableData] = useState([]);

	const columnsObj = () => {
		return { handleDeleteClick }
	};

	const handleClickAddBtn = () => {
		setDetailId(null);
		setVisible(true);
	}

	const handleDeleteClick = id => {
		const ids = id ? [id.id] : selectedRowKeys;
		if (id) {
			setSelectedRowKeys([]);
		}
		Modal.confirm({
			title: '确定删除所选数据吗？',
			content: '此操作不可逆，请慎重考虑。',
			okText: '删除',
			okType: 'danger',
			cancelText: '取消',
			onOk: () => batchDelete(ids),
		});
	}

	// 批量删除
	const batchDelete = id => {
		deleteAdjustBatchReq(id).then(() => {
			message.success('删除成功');
			listRef.dataLoad();
		});
	}

	const handleClickExport = () => {
		exportAdjustBatchReq(dataInfo).then(res => {
			const date = moment().format(DATA_TYPE_NO_SPACE);
			downloadFileStream(`考勤调整管理---${date}.xlsx`, res);
		});
	}

	const createPromise = params => {
		return queryAdjustListReq(params).then(res => {
			const obj = {
				totalCount: 0,
				currentPageResult: [],
			};
			if (res) {
				const { data, paging } = res.data || {};
				obj.totalCount = paging.total;
				obj.currentPageResult = data;
			} else {
				message.error('无法获取到数据');
			}
			setTableData(obj.currentPageResult);
			return obj;
		})
	}

	const transformQuery = params => {
		const {
			adjust_end_time,
			adjust_start_time,
			adjust_type,
			pageIndex,
			pageSize,
			search,
		} = params || {};
		const obj = {};
		if (adjust_start_time && adjust_start_time.length) {
			obj.start_time_start = moment(adjust_start_time[0]).valueOf();
			obj.start_time_end = moment(adjust_start_time[1]).valueOf();
		}
		if (adjust_end_time && adjust_end_time.length) {
			obj.end_time_start = moment(adjust_end_time[0]).valueOf();
			obj.end_time_end = moment(adjust_end_time[1]).valueOf();
		}
		if (adjust_type || adjust_type === 0) {
			obj.type = adjust_type;
		}
		const dataObj = {
			...obj,
			page_num: pageIndex,
			page_size: pageSize,
			query: search,
		}
		setDataInfo(dataObj);
		return dataObj;
	}

	const handleSaveBtn = () => {
		listRefChild.validateFieldsAndScroll((error, values) => {
			if (error) { return }
			const { task_id, type, adjust_time, reason, adjust_task } = values || {};
			const obj = {
				task_id,
				type,
				target_list: adjust_task,
				start_adjust_time: moment(adjust_time[0]).valueOf(),
				end_adjust_time: moment(adjust_time[1]).valueOf(),
				reason,
			};
			addAdjustItemReq(obj).then(res => {
				if (res.error_code === API_ERROR_CODE) {
					listRef.dataLoad();
					message.success('新增成功');
					setVisible(false);
				}
			});
		})
	}

	const renderFooter = () => {
		return (
			<React.Fragment>
				<Button
					onClick={handleSaveBtn}
					type="primary"
					style={{ marginRight: 16 }}
				>
					保存
				</Button>
				<Button onClick={() => setVisible(false)}>取消</Button>
			</React.Fragment>
		);
	}

	const BarOptionsObj = {
		handleAdd: handleClickAddBtn,
		handleDel: handleDeleteClick,
		handleExport: handleClickExport,
		disabledDel: !selectedRowKeys.length,
		disabledExp: !tableData.length,
		searchPlaceholder: '对象名称/申请单号',
	};

	return (
		<div className={styles.adjustContent}>
			<Table
				rowKey="id"
				columns={columns(columnsObj())}
				hasSerialNo // 是否有序号
				hasFilter // 是否带有"漏斗"（以操作栏和筛选栏存在为前提），控制打开和收起筛选栏
				filterDefaultIsOpened // 筛选"漏斗"初始化状态是否开启
				serialNoOverlying // 序号是否累加
				createPromise={createPromise}
				handleBarOptions={HandleBarOptions(BarOptionsObj)}
				searchBarOptions={searchBarOptions()}
				antdProps={{
					rowSelection: {
						selectedRowKeys,
						onChange: arr => setSelectedRowKeys(arr),
					},
				}}
				transformQuery={transformQuery}
				setRef={listRef => setListRef(listRef)}
			/>
			<CreateDrawer
				visible={visible}
				title='新建'
				handleCancel={() => setVisible(false)}
				footer={renderFooter()}
			>
				<DetailModalContent
					ref={ref => setListRefChild(ref)}
					id={detailId}
					// treeList={treeList}
				/>
			</CreateDrawer>
		</div>
	);
}

export default Adjustment;
