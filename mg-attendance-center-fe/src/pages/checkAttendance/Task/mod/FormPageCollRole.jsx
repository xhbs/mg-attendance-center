/*
 * @Author: xiedan WX042
 * @since: 2020-08-27 11:01:08
 * @lastTime: 2021-10-20 09:56:32
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\task\TaskManagement\components\FormPage.jsx
 * @Description: 考勤任务---任务管理---表单页面
 */
import React from 'react';
import { Form, message, DatePicker, Spin, Transfer, Tree } from 'antd';
import moment from 'moment';
// import { TreeSelect } from '@hz-components/react-base';
import {
	getCheckAttendById,
	addCheckAttendCallRole,
} from '@services/checkAttendance/checkAttendanceTask';
import { getLazyOrganizationsTree } from '@services/common';
import { API_ERROR_CODE, DATE_TYPE_DAY } from '@constants';
import { getSchoolYear, getSchoolMonth } from '@utils/utils';
import style from '../styles/index.less';
const { Item } = Form;
const { RangePicker } = DatePicker;
const { TreeNode } = Tree;
const formItemLayout = {
	labelCol: {
		xs: { span: 2 },
		sm: { span: 2 },
	},
	wrapperCol: {
		xs: { span: 16 },
		sm: { span: 16 },
	},
};
function findNode(tree, title) {
	for (let i = 0; i < tree.length; i++) {
		if (tree[i].title.includes(title)) {
			return tree[i];
		}
		if (tree[i].children) {
			let result = findNode(tree[i].children, title);
			if (result) {
				return { ...tree[i], children: [result] };
			} else {
				return tree[i];
			}
		}
	}
}

class FormPageCollRole extends React.Component {
	state = {
		formData: {
			taskName: '',
		},
		spinning: true,
		// checkName: true,  // 名称验重  true--没有重复

		yearEnum: [], // 学年列表
		monthEnum: [], // 学期列表
		yearValue: '', // 学年
		termValue: '', // 学期
		monthValue: '', // 月份
		targetKeys: [],
		treeData: [],
		expandedKeys: [],
		searchValue: '',
		dataSourceData: [],
		autoExpandParent: true,
		maxCheckNum: 1, // 最大的抽查天数
	};

	componentDidMount() {
		const year = getSchoolYear(5);
		this.setState({
			yearEnum: year || [],
		});
		// 编辑时，获取详情
		const { detailId } = this.props;
		// 如果未查看详情模式，则无需获取组织树
		const fun = [this.getOrgTreeList()];

		if (detailId) {
			fun.push(this.getTaskDetailByIdReq());
		} else {
			this.setState({
				yearValue: year[0] || '',
				monthEnum: getSchoolMonth(year[0]) || [],
			});
		}
		Promise.all(fun).finally(() => {
			this.setState({
				spinning: false,
			});
		});
	}
	componentDidUpdate(prevProps, prevState) {
		const { form } = this.props;
		if (prevState.targetKeys !== this.state.targetKeys) {
			if (this.state.targetKeys.length) {
				form.setFieldsValue({
					attendance: true,
				});
			}
			// Now fetch the new data here.
		}
	}
	componentWillUnmount() {
		localStorage.removeItem('csdm');
	}
	// 获取详情
	getTaskDetailByIdReq = () => {
		const { detailId, operationType } = this.props;
		const semesterMap = {
			春季: '0',
			秋季: '1',
		};
		return getCheckAttendById(detailId).then((res) => {
			if (res && res.error_code === API_ERROR_CODE) {
				const { data } = res;
				const {
					name,
					start_date,
					end_date,
					day_count,
					target_org_indexes,
					target_org_names,
					school_year,
					semester,
					month,
					minimum_absence_rate,
				} = data || {};

				let maxCheckNum = 1;
				if (start_date && end_date) {
					maxCheckNum =
						Math.abs(
							moment(`${start_date} 00:00:00`).diff(
								moment(`${end_date} 23:59:59`),
								'day',
							),
						) + 1;
				}
				this.setState({
					formData: {
						taskName: name || '',
						date: [moment(start_date), moment(end_date)],
						attendance:
							operationType === 'view' ? target_org_names : target_org_indexes || [],
						checkNum: day_count || '',
					},
					yearValue: school_year || '',
					termValue: semesterMap[semester] || '',
					monthValue: month || '',
					percent: minimum_absence_rate || 0,
					maxCheckNum,

					monthEnum: getSchoolMonth(school_year, semesterMap[semester]) || [],
				});
			}
		});
	};

	// 提交表单
	handleSubmit = (callback) => {
		const { form, onFormPageClose } = this.props;
		form.validateFieldsAndScroll((err, values) => {
			if (!err) {
				const params = {
					call_the_roll: 1,
					day_count: 1,
					start_date:
						(values.date &&
							values.date[0] &&
							moment(values.date[0]).format(DATE_TYPE_DAY)) ||
						'',
					end_date:
						(values.date &&
							values.date[1] &&
							moment(values.date[1]).format(DATE_TYPE_DAY)) ||
						'',
					target_org_indexes: this.state.targetKeys,
				};
				if (localStorage.getItem('csdm')) {
					params.test = 66;
				}
				const fun = addCheckAttendCallRole(params);
				fun.then((res) => {
					if (res && res.error_code === API_ERROR_CODE) {
						message.success(`点名任务成功`);
						onFormPageClose();
					}
				}).finally(() => {
					callback(true);
				});
			} else {
				callback(true);
			}
		});
	};

	// 获取组织
	getOrgTreeList = () => {
		const {
			orgIndex: { org_name, org_index },
		} = this.props;
		const respose = [
			{
				key: org_index,
				title: org_name,
				children: [],
			},
		];

		return getLazyOrganizationsTree({
			org_index,
			type: 1,
			include_child: true,
			lazy_load: true,
			excludeApi: true,
		})
			.then((resp) => {
				resp = resp.map((e) => {
					return {
						key: e.org_index,
						title: e.org_name,
					};
				});
				console.log('[ resp ] >', resp);
				if (resp) {
					respose[0].children = resp;
				} else {
					respose[0].children = [];
				}
			})
			.catch(() => {
				respose[0].children = [];
			})
			.finally(() => {
				this.setState({
					treeData: respose,
				});
			});
	};
	disabledDate = (current) => {
		if (localStorage.getItem('csdm')) {
			return false;
		} else {
			if (current.date() > 15) {
				return true;
			} else {
				return false;
			}
		}
	};

	onChange = (targetKeys) => {
		this.setState({ targetKeys });
	};

	onExpand = (expandedKeys) => {
		this.setState({
			expandedKeys,
			autoExpandParent: false,
		});
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
	isChecked = (selectedKeys, eventKey) => {
		return selectedKeys.indexOf(eventKey) !== -1;
	};
	handleChange = (nextTargetKeys, direction, moveKeys) => {
		this.setState({ targetKeys: nextTargetKeys });
	};
	// filterOption = (inputValue, option) => option.title.indexOf(inputValue) > -1;
	TreeTransfer = ({ dataSource, targetKeys, ...restProps }) => {
		const transferDataSource = [];
		function flatten(list = []) {
			list.forEach((item) => {
				transferDataSource.push(item);
				flatten(item.children);
			});
		}
		flatten(dataSource);
		return (
			<Transfer
				{...restProps}
				targetKeys={targetKeys}
				dataSource={transferDataSource}
				className={style.treetransfer}
				showSearch
				listStyle={{
					height: 600,
				}}
				onChange={this.handleChange}
				onSearch={(dir, val) => {
					if (dir === 'left') {
						const result = findNode(dataSource, val);
						// dataSource=[result]
						this.setState({
							dataSourceData: [result],
						});
					}
				}}
				// filterOption={this.filterOption}
				render={(item) => item.title}
				showSelectAll={false}
			>
				{({ direction, onItemSelect, selectedKeys }) => {
					if (direction === 'left') {
						const checkedKeys = [...selectedKeys, ...targetKeys];
						return (
							<Tree
								blockNode
								className={style.tree}
								checkable
								checkStrictly
								onExpand={this.onExpand}
								autoExpandParent={this.state.autoExpandParent}
								expandedKeys={this.state.expandedKeys}
								checkedKeys={checkedKeys}
								loadData={this.onLoadData}
								onCheck={(
									_,
									{
										node: {
											props: { eventKey },
										},
									},
								) => {
									onItemSelect(eventKey, !this.isChecked(checkedKeys, eventKey));
								}}
								onSelect={(
									_,
									{
										node: {
											props: { eventKey },
										},
									},
								) => {
									onItemSelect(eventKey, !this.isChecked(checkedKeys, eventKey));
								}}
							>
								{this.state.dataSourceData.length
									? this.renderTreeNodes(this.state.dataSourceData)
									: this.renderTreeNodes(this.state.treeData)}
							</Tree>
						);
					}
				}}
			</Transfer>
		);
	};
	onLoadData = async (treeNode) => {
		if (treeNode.props.children) return;
		let res = await getLazyOrganizationsTree({
			org_index: treeNode.props.eventKey,
			type: 1,
			include_child: true,
			lazy_load: true,
			excludeApi: true,
		});
		console.log('[ res ] >', res);
		res = res.map((e) => {
			return { key: e.org_index, title: e.org_name, isLeaf: e.sub_type >= 4 ? true : false };
		});
		treeNode.props.dataRef.children = res;
		this.setState({
			treeData: [...this.state.treeData],
		});
	};
	renderTreeNodes = (data) =>
		data.map((item) => {
			if (item.children) {
				return (
					<TreeNode title={item.title} key={item.key} dataRef={item}>
						{this.renderTreeNodes(item.children)}
					</TreeNode>
				);
			}
			return <TreeNode key={item.key} {...item} dataRef={item} />;
		});

	render() {
		const { form } = this.props;
		const { getFieldDecorator } = form;
		const {
			formData: { date, attendance },
			treeData,
			spinning,
			targetKeys,
		} = this.state;

		return (
			<div style={{ padding: '16px 32px' }}>
				<Spin spinning={spinning}>
					<Form {...formItemLayout} onSubmit={this.handleSubmit}>
						<div className="hz-label" style={{ margin: 10 }}>
							考勤规则
						</div>
						<Item label="考勤时段">
							{getFieldDecorator('date', {
								initialValue: date || [],
								rules: [
									{
										required: true,
										message: '请选择考勤时段',
									},
									{
										validator: (rules, val, callBack) => {
											if (val && val.length === 2 && (!val[0] || !val[1])) {
												callBack('请选择完整的时间段');
											} else {
												callBack();
											}
										},
									},
								],
							})(
								<RangePicker
									style={{ width: 340 }}
									disabledDate={this.disabledDate}
									format="YYYY-MM-DD"
									getCalendarContainer={(triggerNode) => triggerNode.parentNode}
									onChange={(date) => {
										if (date && date.length === 2) {
											const maxCheckNum =
												moment(date[1].startOf('day')).diff(
													moment(date[0].startOf('day')),
													'day',
												) + 1;
											this.setState({ maxCheckNum });
										}
									}}
								/>,
							)}
						</Item>

						<div className="hz-label" style={{ margin: 10 }}>
							考勤对象
						</div>

						<Item label="考勤目标">
							{treeData.length &&
								getFieldDecorator('attendance', {
									initialValue: attendance || '',
									rules: [
										{
											required: true,
											message: '请选择考勤目标',
										},
									],
								})(
									<div style={{ width: '100%', height: '100%' }}>
										<this.TreeTransfer
											dataSource={treeData}
											targetKeys={targetKeys}
											onChange={this.onChange}
										/>
									</div>,
								)}
						</Item>
					</Form>
				</Spin>
			</div>
		);
	}
}

export default Form.create()(FormPageCollRole);
