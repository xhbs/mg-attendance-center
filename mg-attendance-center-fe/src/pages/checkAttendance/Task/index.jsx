/*
 * @Author: xiedan WX042
 * @since: 2021-08-31 11:05:09
 * @lastTime: 2021-10-19 14:36:34
 * @Description: 抽查考勤任务---抽查考勤任务
 */
import React from 'react';
import { Modal, message, Button } from 'antd';
import classNames from 'classnames';
import ReactScrollbar from 'react-custom-scrollbars';
import { CreateDrawer } from '@hz-components/ists-common';
import { Table } from '@hz-components/react-base';
import { HandleBarOptions } from '@components/CreateHandleBarOptions/index';
import LeftTree from './mod/LeftTree';
import FormPage from './mod/FormPage';
import FormPageCollRole from './mod/FormPageCollRole';
import {
	getCheckAttendPage,
	delCheckAttend,
	hasTestButton,
} from '@services/checkAttendance/checkAttendanceTask';
import { getUserRoleInfo, getUserOrgIndex, getOrgDetailByOrgIndex } from '@services/common';
import { API_ERROR_CODE, SCHOOL } from '@constants';
import { getSchoolYear, getSchoolMonth } from '@utils/utils';
import { createSearchBarOptions, tableColumns } from './utils/tableOptions';
import Statistics from '../Statistics';
import styles from './styles/index.less';

const titleMap = {
	add: '新建',
	view: '详情',
	update: '编辑',
	collRole: '点名',
};

class CheckAttendanceTask extends React.Component {
	state = {
		userRole: '',
		userCode: '',

		visible: false,
		operationType: 'add', // 新建--add, 编辑--update, 查看--view
		selectedRowKeys: [],
		editRowData: {}, // 待编辑/查看统计的表格行
		isLoading: false, // 表单页面确认按钮是否加载中

		yearEnum: [], // 学年列表
		monthEnum: [], // 学期列表
		yearValue: '', // 学年
		termValue: '', // 学期
		monthValue: '', // 月份

		orgIndexObj: '', // 当前用户的组织
		showStatisticsPage: false, // 展示统计页面

		activeTreeNode: null, // 左侧选择的组织
		rootPath: '',
	};

	componentDidMount() {
		const year = getSchoolYear(5);
		this.setState({
			yearEnum: year || [],
			yearValue: year[0] || '',
			monthEnum: getSchoolMonth(year[0]) || [],
		});

		getUserRoleInfo()
			.then((res) => {
				console.log(res, 'res---')
				if (res) {
					this.setState({
						userRole: (res[0] && res[0].role_code) || '',
					});
					// 如果为校级，则不请求组织树
					if(res[0] && res[0].role_code !== SCHOOL) {
					// 获取组织
						this.getOrgTreeList();
					}
				}
			})
			.finally(() => {
				this.listRef.dataLoad();
			});
		hasTestButton().then((res) => {
			if (res.data) {
				this.customizeBtn.push({
					antdProps: {
						icon: 'hz-add',
						children: '测试点名',
						disabled: false,
						onClick: () => {
							localStorage.setItem('csdm', true);
							this.setState({
								visible: true,
								operationType: 'collRole',
							});
						},
					},
				});
			}
		});
	}

	// 获取组织
	getOrgTreeList = () => {
		getUserOrgIndex().then((res) => {
			if (res) {
				this.setState({
					userCode: res.user_code,
					orgIndexObj: {
						org_index: res.org_index,
						org_name: res.org_name,
					},
				});
        		if (!res?.org_index) return
				// 获取当前用户到根节点的路径 0/1/530000/532500/532503/03EF34E0C0BA4D7CE0537D64A8C02609
				getOrgDetailByOrgIndex(res.org_index).then((resp) => {
					if (resp) {
						this.setState({
							rootPath: resp.index_path,
						});
					}
				});
			}
		});
	};

	/**
	 * 分页查询列表
	 * @param query
	 */
	createPromise = (params) => {
		const init = this.initFlied(params);
		return new Promise((resolve) => {
			getCheckAttendPage(init).then((res) => {
				console.log(res, 'getCheckAttendPage')
				if (res && res.error_code === API_ERROR_CODE) {
					const { data, paging } = res.data;
					resolve({
						pageIndex: (paging && paging.page_num) || 1,
						totalCount: (paging && paging.total) || 0,
						currentPageResult: data || [],
					});
				} else {
					resolve({
						pageIndex: 1,
						totalCount: 0,
						currentPageResult: [],
					})
				}
			}, (error) => {
				console.log(error)
				resolve({
					pageIndex: 1,
					totalCount: 0,
					currentPageResult: [],
				})
			})
		});
	};

	/**
	 * @description: 构造表格检索参数
	 * @param info
	 */
	initFlied = (info) => {
		const { yearValue, termValue, monthValue, activeTreeNode } = this.state;
		console.log('activeTreeNode====::', activeTreeNode);
		console.log(info, yearValue, termValue, monthValue);
		const { pageIndex, pageSize, search, status, sorter } = info || {};
		const { order } = sorter || {};
		const obj = {
			page_size: pageSize,
			page_num: pageIndex,
			school_year: yearValue,
			order_rule: order && order === 'descend' ? 'DESC' : 'ASC', // 默认升序
			order_field: 'state',
			state: status,
			search,
		};
		if (['0', '1'].includes(termValue)) {
			obj.semester = termValue === '0' ? '春季' : '秋季';
		}
		if (monthValue) {
			obj.month = monthValue;
		}
		if (activeTreeNode) {
			const { index_path, org_index } = activeTreeNode;
			obj.creator_org_index_path = `${index_path}${org_index}`;
		}
		return obj;
	};

	// 改变考勤周期
	onChangeType = (v, field) => {
		if (field === 'year') {
			this.setState({
				yearValue: v,
				termValue: '',
				monthEnum: getSchoolMonth(v) || [],
				monthValue: '',
			});
		} else if (field === 'term') {
			const { yearValue } = this.state;
			this.setState({
				termValue: v,
				monthEnum: getSchoolMonth(yearValue, v) || [],
				monthValue: '',
			});
		} else {
			this.setState({
				monthValue: v,
			});
		}
		this.listRef.dataLoad();
	};

	// 点击新建
	handleAdd = () => {
		this.setState({
			visible: true,
			operationType: 'add',
		});
	};

	//点击点名
	handleRole = () => {
		this.setState({
			visible: true,
			operationType: 'collRole',
		});
	};
	// 编辑
	handleEdit = (data) => {
		const { userRole, userCode } = this.state;
		const { state, creator_code } = data;
		// 是否为编辑模式：1、非校级；2、未开始的 支持点击编辑；3、该任务为当前用户创建
		const isEdit = userRole !== SCHOOL && state === 1 && userCode === creator_code;

		this.setState({
			editRowData: data,
			visible: true,
			operationType: isEdit ? 'update' : 'view',
		});
	};

	// 删除
	handleDel = (ids) => {
		const idsArr = Array.isArray(ids) ? ids : [ids];
		delCheckAttend(idsArr).then((res) => {
			if (res && res.error_code === API_ERROR_CODE) {
				message.success('删除成功');
				this.listRef.dataLoad();
			}
		});
	};
	customizeBtn = [
		{
			antdProps: {
				icon: 'hz-add',
				children: '点名',
				disabled: false,
				onClick: this.handleRole,
			},
		},
	];
	// 批量删除
	batchDelete = () => {
		const { selectedRowKeys } = this.state;
		const ids = selectedRowKeys || '';
		this.confirmDelete(ids);
	};

	// 点击单个数据的删除确认
	confirmDelete = (ids) => {
		const options = {
			title: '确定删除数据吗？',
			content: '此操作不可逆，请慎重考虑。',
			okText: '删除',
			okType: 'danger',
			cancelText: '取消',
			onOk: () => {
				this.handleDel(ids);
			},
		};
		Modal.confirm(options);
	};

	// 关闭新建页面
	onCancel = () => {
		this.setState({
			visible: false,
		});
		this.listRef.dataLoad();
	};

	// 点击确认调用表单提交
	onConfirm = () => {
		// 确认按钮先变成loading状态
		this.setState(
			{
				isLoading: true,
			},
			() => {
				const { handleSubmit } = this.FormRefs;
				handleSubmit((v) => {
					if (v) {
						this.setState({
							isLoading: false,
						});
					}
				});
			},
		);
	};

	// 打开考勤统计页面
	openStatisticsPage = (data) => {
		console.log(data);
		this.setState({
			editRowData: data,
			showStatisticsPage: true,
		});
	};

	// 关闭统计页面
	closeStatisticsPage = () => {
		this.setState(
			{
				editRowData: {},
				showStatisticsPage: false,
			},
			() => {
				console.log(this.listRef);
				if (this.listRef) {
					this.listRef.dataLoad();
				}
			},
		);
	};

	renderFooter = () => {
		const { isLoading, operationType } = this.state;
		return (
			<React.Fragment>
				{operationType !== 'view' ? (
					<Button
						onClick={this.onConfirm}
						type="primary"
						style={{ marginRight: 16 }}
						loading={isLoading}
					>
						确认
					</Button>
				) : null}
				<Button onClick={this.onCancel}>取消</Button>
			</React.Fragment>
		);
	};

	changeTreeNode = (node) => {
		console.log(node);
		this.setState(
			{
				activeTreeNode: node,
			},
			this.listRef.dataLoad,
		);
	};

	render() {
		const {
			visible,
			selectedRowKeys,
			operationType,
			editRowData,
			rulesEnumList,
			userRole,
			yearEnum,
			monthEnum,
			yearValue,
			termValue,
			monthValue,
			// treeData,
			orgIndexObj,
			showStatisticsPage,
			userCode,
			activeTreeNode,
			rootPath,
		} = this.state;
		const rowSelection = {
			onChange: (selectedRowKeys) => {
				this.setState({ selectedRowKeys });
			},
			selectedRowKeys,
			getCheckboxProps: (record) => ({
				// 1、非本人创建；2、不是未开始状态；不允许批量删除
				disabled: record.state !== 1 || userCode !== record.creator_code,
			}),
		};

		const wapperDisplay = visible || showStatisticsPage ? 'none' : 'block';
		return (
			<>
				<div className="hz-layout-vertical">
					<div
						className={classNames('hz-layout-horizontal', styles.mainWapper)}
						style={{ display: wapperDisplay }}
					>
						<div
							className="hz-layout-fl"
							style={{
								width: 280,
								height: '100%',
								backgroundColor: '#fff',
							}}
						>
							{userRole ? (
								<LeftTree
									setActiveInfo={this.changeTreeNode}
									showSubType={3}
									rootPath={rootPath}
								/>
							) : null}
						</div>
						<div className="hz-layout-main" style={{ height: '100%' }}>
							<div
								className={classNames(
									'hz-layout-vertical',
									styles.taskManagmentWapper,
								)}
							>
								<div className="hz-layout-vertical-body">
									<Table
										rowKey="id"
										hasSerialNo
										serialNoOverlying
										handleBarOptions={  userRole !== SCHOOL ? HandleBarOptions({
											handleAdd: this.handleAdd,
											handleDel: this.batchDelete,
											customizeBtn: this.customizeBtn,
											disabledDel: selectedRowKeys.length === 0,
											searchPlaceholder: '任务名称/考勤目标/创建人',
										}) : null}
										searchBarOptions={createSearchBarOptions({
											onChangeType: this.onChangeType,
											yearEnum,
											monthEnum,
											value: {
												yearValue,
												termValue,
												monthValue,
											},
										})}
										columns={tableColumns({
											handleEdit: this.handleEdit,
											confirmDelete: this.confirmDelete,
											openStatisticsPage: this.openStatisticsPage,
											userCode,
											userRole,
										})}
										createPromise={this.createPromise}
										antdProps={
											(userRole !== SCHOOL && {
												rowSelection,
											}) ||
											{}
										}
										setRef={(ref) => {
											this.listRef = ref;
										}}
										forceFirstLoadDisabled
									/>
								</div>
							</div>
						</div>
					</div>

					<CreateDrawer
						visible={visible}
						title={titleMap[operationType] || '新建'}
						handleCancel={this.onCancel}
						footer={this.renderFooter()}
					>
						<ReactScrollbar renderTrackHorizontal={() => <div />}>
							{operationType !== 'collRole' ? (
								<FormPage
									wrappedComponentRef={(ref) => {
										this.FormRefs = ref;
									}}
									onFormPageClose={this.onCancel}
									operationType={operationType}
									detailId={editRowData.id}
									rulesEnumList={rulesEnumList}
									orgIndex={orgIndexObj}
								/>
							) : (
								<FormPageCollRole
									wrappedComponentRef={(ref) => {
										this.FormRefs = ref;
									}}
									onFormPageClose={this.onCancel}
									operationType={operationType}
									detailId={editRowData.id}
									rulesEnumList={rulesEnumList}
									orgIndex={orgIndexObj}
								/>
							)}
						</ReactScrollbar>
					</CreateDrawer>

					{showStatisticsPage ? (
						<Statistics
							visible={showStatisticsPage}
							data={editRowData}
							userRole={userRole}
							closePage={this.closeStatisticsPage}
							preNodeObj={activeTreeNode}
						/>
					) : null}
				</div>
			</>
		);
	}
}

export default CheckAttendanceTask;
