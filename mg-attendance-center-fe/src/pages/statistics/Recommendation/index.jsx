/*
 * @Author: xiedan WX042
 * @since: 2020-12-03 16:47:26
 * @lastTime: 2021-02-04 16:02:07
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\statistics\Recommendation\index.jsx
 * @Description: 抽查推荐---省级、县市级
 */
import React from 'react';
import { message, Modal, TreeSelect } from 'antd';
import moment from 'moment';
import { Table } from '@hz-components/react-base';
import { HzForm, ExportOptions } from '@hz-components/biz-scene-components';
import { HandleBarOptions } from '@components/CreateHandleBarOptions/index';
import AbsenceDetails from '@components/AbsenceDetails';
import SearchDropdown from '@components/SearchDropdown';
import {
	getRecoList,
	exportReco,
	addPersonToGroup,
} from '@/services/recommendation';
import { getDepartmentMembersTree, getPersonByCondition } from '@/services/common';
import { downloadFileStream } from '@utils/utils';
import { DATA_TYPE_NO_SPACE, API_ERROR_CODE } from '@constants';
import { tableColumns, tableSearchOptions } from './utils/options';
import mystyle from './style/index.less';

class Recommendation extends React.Component {
	state = {
		visible: false,
		addVisable: false,
		searchType: '1',
		dataSource: [],
		currentIndex: 0,
		detail: null,
		queryParams: {},
		total: 0,
		selectedRowKeys: [],
		groupTreeNode: [],
		selectedOrg: [],
		selectedDate: [],
		defaultTime: moment(),

		personIds: [], // 加入考勤组的人员的id集合
		addGroupLoading: false, // 加入考勤组按钮loading
	}
	
	componentDidMount() {
		this.getAttendanceGroupTree();
	}

	// 获取考勤组列表
	getAttendanceGroupTree = () => {
		getDepartmentMembersTree().then(res => {
			if(res && res.data) {
				const arr = this.transTreeData(res.data || []);
				this.setState({
					groupTreeNode: arr,
				})
				
			}
		})
	}

	// 转换考勤组树节点字段
	transTreeData = (data = []) => {
		data.map(item => {
			item.title = item.group_name;
			item.value = item.id;
			if(item.children && Array.isArray(item.children) && item.children.length > 0) {
				this.transTreeData(item.children);
			}
			return item;
		})
		return data;
	}

	createPromise = async param => {
		const params = this.initFlied(param);
		const result = { totalCount: 0, currentPageResult: [], pageIndex: 1 };

		return new Promise(resolve => {
			getRecoList(params)
				.then(res => {
					const { error_code, message: msg } = res || {};
					if (error_code !== API_ERROR_CODE) {
						message.error(msg || '无法获取数据');
						resolve(result);
					}
					const { paging, data } = res.data;
					result.totalCount = paging.total;
					result.currentPageResult = data || [];
					result.pageIndex = paging.page_num;
					this.setState({
						dataSource: data || [],
						total: paging.total || 0,
					})
					resolve(result);
				})
				.catch(() => {
					this.setState({
						total: 0,
					})
					resolve(result);
				});
		});
	};

	/**
     * @description: 构造表格检索参数
     * @param info
     */
    initFlied = (info) => {
		const { defaultTime, searchType } = this.state;
		const { pageIndex, pageSize, search = '', date = defaultTime, group = {} } = info;
		const { selectedKeys = [], selectedNodes = [] } = group;
		const params = {
			page_size: pageSize,
			page_num: pageIndex,
		};

		if(search && searchType === '1') {
			params.person_name = search.trim();
		} else if(search && searchType === '2') {
			params.person_no = search.trim();
		}

		if(selectedNodes.length > 0) {
			params.org_list = selectedNodes.map(i => {
				return {
					index_path: i.index_path,
					org_index: i.org_index,
					sub_type: i.sub_type,
				}
			});
		}

		if (date) {
			params.start_time = moment(`${moment(date).startOf('month').format('YYYY-MM-DD')} 00:00:00`).valueOf();
			params.end_time = moment(`${moment(date).endOf('month').format('YYYY-MM-DD')} 23:59:59`).set('millisecond', 999).valueOf();
		}

		this.setState({
			queryParams: { ...params },
			selectedOrg: selectedKeys,
			selectedDate: date,
		})

        return params;
    };

	// 查看明细
	handleDetail = (data, index) => {
		this.setState({
			currentIndex: index,
			detail: data,
			visible: true,
		})
	};

	// value：1=全部 0=当前页
	handleExport = (value) => {
		const { queryParams, total } = this.state;
		if(total > 100000) {
			message.info('最大导出10万条数据', 3);
		}
		const params = {
			...queryParams,
			export_all: value,
		}
		exportReco(params)
			.then(res => {
				downloadFileStream(`抽查推荐_${moment().format(DATA_TYPE_NO_SPACE)}.xlsx`, res);
			})
			.catch(() => {
				message.error('导出失败');
			});
	};

	// 加入考勤组
	joinAttendGroup = () => {
		// const { detail, selectedRowKeys } = this.state;
		const { personIds } = this.state;
		this.hzFormRef.submit().then(values => {
			if(values) {
				const obj = {
					group_id: values.group.value || '',
					object_ids: personIds,
					// object_ids: detail ? [detail.person_no] : selectedRowKeys,
				}
				addPersonToGroup(obj).then(res => {
					message.success('操作成功');
				}).finally(() => {
					this.setState({
						addVisable: false,
					})
				})
			}
		})
	}

	// 批量加入考勤组
	handleAddGroup = (obj, batch = 0) => {
		const { selectedRowKeys } = this.state;
		if(batch && selectedRowKeys && selectedRowKeys.length > 1000) {
			message.info('最多选择1000人');
			return;
		}
		if(batch) {
			this.setState({ addGroupLoading: true });
		}

		const params = {
			page_num: 1,
			page_size: batch ? selectedRowKeys.length : 1,
			conditions: [{
				"operate":"in",
				"value": batch ? selectedRowKeys : [obj.person_no],
				"key": 'user_code',
			}],
		}
		getPersonByCondition(params).then(res => {
			if(res && res.error_code === API_ERROR_CODE) {
				const { data } = res.data || {};
				if(!data || data.length === 0) {
					message.error('人员已被删除，不能加入考勤组');
					return;
				}
				const personIds = data.map(i => i.id);
				this.setState({
					personIds,
					addVisable: true,
				})
			} else {
				message.error(res && res.message || '无法获取人员id');
			}
		}).catch(() => {
			message.error('无法获取人员id');
		}).finally(() => {
			this.setState({ addGroupLoading: false });
		});
	}

	onSearch = (value) => {
		if(this.tableRef) {
			this.tableRef.dataLoad({ search: value })
		}
	}

	render () {
		const { detail, selectedRowKeys, total, defaultTime, selectedOrg, visible, dataSource, currentIndex, selectedDate, addVisable, groupTreeNode, addGroupLoading } = this.state;
		
		const rowSelection = {
			onChange: selectedRowKeys => {
				this.setState({
					selectedRowKeys,
				})
			},
			selectedRowKeys,
		}

		return (
			<div className={`hz-layout-vertical ${mystyle.detailTableWapper}`}>
				<div style={{ margin: 16 }} className="hz-layout-vertical-body">
					<Table
						rowKey='person_no'
						columns={tableColumns({
							handleDetail: this.handleDetail,
							handleAddGroup: (v) => this.handleAddGroup(v),
						})}
						handleBarOptions={HandleBarOptions({
							customizeBtn: [{
								antdProps: {
									icon: 'hz-add',
									children: '加入考勤组',
									disabled: !selectedRowKeys.length,
									loading: addGroupLoading,
									onClick: () => this.handleAddGroup(null, 1),
								},
							},{
								elementType: "custom",
								render: () => {
									return <ExportOptions itemNum={['data']} exportDisabled={total === 0} exportData={this.handleExport} />;
								},
							}],
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
										onSearch={this.onSearch}
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
							value: {
								time: defaultTime,
								defaultKeysArr: selectedOrg || [],
							},
						})}
						hasSerialNo
						hasFilter // 是否带有"漏斗"（以操作栏和筛选栏存在为前提），控制打开和收起筛选栏
						filterDefaultIsOpened // 筛选"漏斗"初始化状态是否开启
						serialNoOverlying // 序号是否累加
						createPromise={this.createPromise}
						setRef={tableRef => this.tableRef = tableRef}
						antdProps={{
							rowSelection,
						}}
					/>
				</div>

				{/* 查询明细 */}
				{
					visible && (
						<AbsenceDetails
							visible={visible}
							type='school'
							onCancel={() => {
								this.setState({
									visible: false,
									detail: null,
								})
							}}
							dataArr={dataSource}
							currentIndex={currentIndex}
							date={selectedDate}
							captionType='text'
							statisticsInfo={{
								describe: detail.describe || '',
								interval: detail.interval || '',
								frequency: detail.frequency || '',
							}}
						/>
					)
				}
				{/* 加入考勤组 */}
				{
					addVisable && (
						<Modal
						title="加入考勤组"
						width={450}
						centered
						destroyOnClose
						maskClosable={false}
						visible={addVisable}
						onOk={this.joinAttendGroup}
						onCancel={() => {
							this.setState({
								addVisable: false,
								detail: null,
							})
						}}
					>
						<HzForm
							{...{
								labelCol: { span: 8 },
								wrapperCol: { span: 15 },
							}}
							fields={[
								{
									label: '要加入的考勤组',
									comp: {
										fieldName: 'group',
										component: (
											<TreeSelect
												style={{ width: '100%' }}
												dropdownStyle={{ maxHeight: 260, overflow: 'auto' }}
												labelInValue
												treeData={groupTreeNode}
												placeholder="请选择"
												treeDefaultExpandAll
											/>
										),
										options: {
											initialValue: '',
											rules: [{
												required: true,
												message: '请选择要加入的考勤组',
											}],
										},
									},
								},
							]}
							setRef={ref => this.hzFormRef = ref}
						/>
					</Modal>
					)
				}
			</div>
		);
	}
};

export default Recommendation;
