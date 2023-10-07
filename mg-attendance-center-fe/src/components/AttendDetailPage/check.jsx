/*
 * @Author: xiedan WX042
 * @since: 2020-12-07 20:39:46
 * @lastTime: 2021-02-08 11:17:58
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\components\AttendDetailPage\check.jsx
 * @Description: 考勤明细列表页   抽查
 */
import React from 'react';
import { message, Modal } from 'antd';
import moment from 'moment';
import { downloadFileStream } from '@utils/utils';
import { Table } from '@hz-components/react-base';
import { HzForm, ExportOptions } from '@hz-components/biz-scene-components';
import { HandleBarOptions } from '@components/CreateHandleBarOptions/index';
import AttendDetailModalCheck from '@components/AttendDetailModal/check';
import SearchDropdown from '@components/SearchDropdown';
import { API_ERROR_CODE, PROVINCE, SCHOOL, DATA_TYPE_NO_SPACE, ATTEND_DETAIL_STATUS } from '@constants';
import {
	getCheckTableHeader,
	getCheckDataList,
	exportCheckList,
	checkChangeStatus,
} from '@/services/detailPage';
import { getUserRoleInfo } from '@/services/common';
import QuickAppeal from './mod/QuickAppeal';
import { tableColumns, tableSearchOptions } from './utils/options';
import { commonForm } from './utils/form';
import mystyle from './style/index.less';

const defaultTime = [
	moment().subtract(6, 'days'),
	moment(),
]

class AttendDetailPageCheck extends React.Component {
	state = {
		userRole: '',

		visible: false,
		batchVisible: false,
		appealVisable: false,

		detail: {},
		searchType: '1',
		dataSource: [],
		currentIndex: 0,
		queryParams: {},
		total: 0,
		statusList: [...ATTEND_DETAIL_STATUS],
		columns: [],
		selectedRowKeys: [],
		selectedRow: [],
		isBatch: 0, // 是否批量修改

		uploadData: [], // 省级日常明细导入学生数据解析的值
		selectedOrg: [], // 所选的组织
	}

	UNSAFE_componentWillMount() {
		getUserRoleInfo().then(res => {
			if(res) {
				this.setState({
					userRole: res[0] && res[0].role_code || '',
				})
			}
		})
	}

	componentDidMount() {
		// 获取表头
		getCheckTableHeader().then(res => {
			if (res && res.error_code === API_ERROR_CODE) {
				this.setState({
					columns: res.data || [],
				},
					this.tableRef.dataLoad
				)
			} else {
				message.error(res && res.message || '未获取表头数据');
			}
		})
	}

	createPromise = param => {
		const params = this.initFlied(param);
		const result = { totalCount: 0, currentPageResult: [], pageIndex: 1 };

		return new Promise(resolve => {
			getCheckDataList(params).then(res => {
				if (res && res.error_code === API_ERROR_CODE) {
					const { data, paging } = res.data || {};
					result.totalCount = paging.total || 0;
					result.currentPageResult = data || [];
					result.pageIndex = paging.page_num;

					this.setState({
						total: paging.total || 0,
						dataSource: data || [],
					})
					resolve(result);
				} else {
					this.setState({
						total: 0,
					})
					resolve(result);
				}
			})
			.catch(() => {
				this.setState({
					total: 0,
				})
				resolve(result);
			});
		});
	};

	// 获取明细列表
	initFlied = (param) => {
		const { searchType, userRole } = this.state;
		const { pageIndex, pageSize, search = '', status, organization = {}, check_task = {}, date = defaultTime } = param;
		const { selectedKeys } = organization;
		const { selectedKeys: selectedKeysCheck } = check_task;

		const params = {
			page_size: pageSize,
			page_num: pageIndex,
		};

		if(status !== '') {
			// 抽查--考勤状态筛选
			params.status = status;
		}

		if(selectedKeys && selectedKeys.length > 0) {
			// 抽查--组织筛选
			params.organization = selectedKeys;
		}
		// 抽查--抽查任务筛选
		if(userRole !== SCHOOL && selectedKeysCheck && selectedKeysCheck.length > 0) {
			params.task = selectedKeysCheck;
		}
		// 校级通过时间筛选
		if (userRole === SCHOOL && date && date.length) {
			params.start_date = moment(date[0]).format('YYYY-MM-DD');
			params.end_date = moment(date[1]).format('YYYY-MM-DD');
		}

		if(search && searchType === '1') {
			params.person_name = search.trim();
		} else if(search && searchType === '2') {
			params.person_no = search.trim();
		}

		this.setState({
			queryParams: {...params},
			selectedOrg: selectedKeys,
		})
		return params;
	}

	// 查看明细
	handleDetail = (data, index) => {
		this.setState({
			currentIndex: index,
			visible: true,
		})
	};

	// 打开修改状态的弹框 batch=1代表批量
	openStatusChange = (data, batch = 0) => {
		this.setState({
			detail: data || {},
			isBatch: batch,
			batchVisible: true,
		})
	}

    // 批量修改状态
    batchChangeStatus = () => {
		const { selectedRow, isBatch, detail } = this.state;
		this.HzFormRef.submit().then(values => {
			if(values) {
				const ids = isBatch ? selectedRow.map(item => item.id) : [detail.id];
				const param = {
					ids,
					status: values.status,
					reason: values.reason,
				}

				checkChangeStatus(param).then(res => {
					if(res && res.error_code === API_ERROR_CODE) {
						this.setState({
							batchVisible: false,
							selectedRowKeys: [],
							selectedRow: [],
						})
						this.tableRef.dataLoad();
					} else {
						message.error(res && res.message || '无法更新状态');
					}
				})
			}
		});
    }

	// value：1=全部 0=当前页
	handleExport = (value) => {
		const { queryParams, total } = this.state;
		if(total > 100000) {
			message.info('最大导出10万条数据', 3);
		}

		const params = {
			...queryParams,
			export_all: !!value,
		}

		exportCheckList(params)
			.then(res => {
				if(res && !res.error_code) {
					const name = `考勤明细_${moment().format(DATA_TYPE_NO_SPACE)}.xlsx`;
					downloadFileStream(name, res);
				}
			})
			.catch(() => {
				message.error('导出失败');
			});
	};
    
    // 按钮栏
    renderCustomizeBtn = () => {
		const { total, selectedRowKeys, userRole } = this.state;
		let beforeBtn = [];

		if(userRole === SCHOOL) {
			beforeBtn = [{
				antdProps: {
				  icon: 'hz-add',
				  children: '快捷申诉',
				  disabled: !selectedRowKeys || selectedRowKeys.length === 0,
				  onClick: ()=>{
					this.setState({
						appealVisable: true,
					})
				  },
				},
			}]
		}
        return [
			...beforeBtn,
            {
                elementType: "custom",
                render: () => {
                    return <ExportOptions itemNum={['data']} exportDisabled={total === 0} exportData={this.handleExport} />;
                },
            },
        ]
	}

	render() {
		const {
			columns,
			selectedRowKeys,
			selectedRow,
			queryParams,
			statusList,
			detail,
			batchVisible,
			visible,
			dataSource,
			currentIndex,
			appealVisable,
			isBatch,
			selectedOrg,
			userRole,
		} = this.state;

		const rowSelection = {
			onChange: (selectedRowKeys, selectedRow) => {
				this.setState({
					selectedRowKeys,
					selectedRow,
				})
			},
			selectedRowKeys,
		}
		const studentList = selectedRow.map(i => {
			return { person_no: i.person_no, person_name: i.person_name };
		})


		return (
			<div className={`hz-layout-vertical ${mystyle.detailTableWapper}`}>
				<div style={{ margin: 16 }} className="hz-layout-vertical-body">
					<Table
						rowKey='id'
						columns={tableColumns({
							userRole,
							type: 'check',
							columns,
							handleDetail: this.handleDetail,
							handleStatusChange: this.openStatusChange,
						})}
						handleBarOptions={HandleBarOptions({
							handleStatusChange: userRole === PROVINCE ? () => this.openStatusChange(null, 1) : null,
							disabledStu: selectedRowKeys.length === 0,
							// searchPlaceholder: '学生姓名/学号',
							customizeBtn: this.renderCustomizeBtn(),
							customizeSearch: {
								render: () => (
									<SearchDropdown
										dropEvent={{
											onSelect: ({key}) => {
												this.setState({
													searchType: key,
												})
											},
										}}
										placeholder={this.state.searchType === '1' ? '学生姓名' : '学号'}
										onSearch={value => this.tableRef.dataLoad({ search: value })}
										onChange={evt => {
											const { value } = evt.target;
											if(value) {
												this.tableRef.updateQuery({ search: value });
											} else {
												this.tableRef.dataLoad({ search: value })
											}
										}}
										allowClear
									/>
								),
							},
						})}
						searchBarOptions={tableSearchOptions({
							// 校级是通过时间筛选的
							type: 'check',
							userRole,
							statusList,
							value: {
								time: defaultTime,
								defaultKeysArr: selectedOrg || [],
								defaultCheckTask: queryParams.task || [],
							},
						})}
						hasSerialNo
						hasFilter // 是否带有"漏斗"（以操作栏和筛选栏存在为前提），控制打开和收起筛选栏
						filterDefaultIsOpened // 筛选"漏斗"初始化状态是否开启
						serialNoOverlying // 序号是否累加
						createPromise={this.createPromise}
						setRef={tableRef => this.tableRef = tableRef}
						scrollX={columns.length * 150}
						antdProps={{
							rowSelection,
						}}
						forceFirstLoadDisabled  // 设置首次不加载，等表头获取完之后再加载
					/>
				</div>

				{/* 修改状态 */}
				<Modal
					title={isBatch ? "批量状态更改" : "状态更改"}
					width={540}
					centered
					destroyOnClose
					maskClosable={false}
					visible={batchVisible}
					onOk={this.batchChangeStatus}
					onCancel={() => {
						this.setState({
							batchVisible: false,
						})
					}}
				>
					<HzForm
						{...{
							labelCol: { span: 6 },
							wrapperCol: { span: 18 },
						}}
						fields={commonForm({
							statusList,
							statusValue: !isBatch && detail && detail.status || '',
						})}
						setRef={ref => this.HzFormRef = ref}
					/>
				</Modal>

				{/* 查询明细 */}
				{
					visible && (
						<AttendDetailModalCheck
							visible={visible}
							dataArr={dataSource}
							currentIndex={currentIndex}
							userRole={userRole}
							onCancel={() => {
								this.setState({
									visible: false,
								})
							}}
						/>
					)
				}
				{/* 快捷申诉 */}
				{
					appealVisable && (
						<QuickAppeal
							visible={appealVisable}
							studentList={studentList}
							onCancel={() => {
								this.setState({
									appealVisable: false,
								})
								this.tableRef.dataLoad();
							}}
						/>
					)
				}
			</div>
		);
	}
};

export default AttendDetailPageCheck;
