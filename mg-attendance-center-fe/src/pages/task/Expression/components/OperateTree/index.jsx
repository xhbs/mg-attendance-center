/*
 * @Desc: 左侧操作树
 * @Author: ranguangyu
 * @Date: 2020-09-02 10:07:06
 */
import React, { PureComponent } from 'react';
import { Icon, Input, Empty, Modal, message } from 'antd';
import { HzForm } from '@hz-components/biz-scene-components';
import request from '@/utils/request';
import Scrollbars from 'react-custom-scrollbars';
import classNames from 'classnames';

import styles from './index.less';

const { Search } = Input;

class OperateTree extends PureComponent {
	state = {
		dataSource: [],
		searchResults: [],
		visible: false,
		node: null, // 选中节点
		nodeCopy: null,
	};

	componentDidMount() {
		this.dataLoad();
	}

	dataLoad = () => {
		const { nodeCopy } = this.state;
		request({
			url: '/api/biz-scene/v1/attendance/express-group/list',
			method: 'post',
			data: {
				page_num: 1,
				page_size: 1000,
			},
		}).then(result => {
			if(result) {
				const list = this.makeSelected(result.data || [], nodeCopy);
				this.setState({
					dataSource: list,
					searchResults: list,
				});
			}
		});
	};

	/**
	 * 选中状态
	 */
	makeSelected = (list, node) => {
		return list.map(item => {
			if (node && item.id === node.id) {
				item.isSelected = true;
			}
			return item;
		});
	}

	/**
	 * 点击节点
	 */
	handleSelect = node => {
		const { onSelect } = this.props;
		let { dataSource } = this.state;
		let currentNode = null;
		dataSource = dataSource.map(item => {
			if (node.id === item.id) {
				item.isSelected = !item.isSelected;
				if (item.isSelected) {
					currentNode = { ...item };
				}
			} else {
				item.isSelected = false;
			}
			return item;
		});
		this.setState({ dataSource, node: currentNode, nodeCopy: currentNode });
		onSelect && onSelect(currentNode);
	};

	/**
	 * 新增和编辑
	 */
	handleNode = () => {
		const { onSelect } = this.props;
		const { node, nodeCopy } = this.state;
		this.HzFormRef.submit().then(values => {
			let data = {};
			let method = '';

			if (node) {
				// 编辑
				data = {
					...node,
					...values,
				};
				method = 'put';
			} else {
				// 新增
				data = {
					...values,
				};
				method = 'post';
			}
			request({
				url: '/api/biz-scene/v1/attendance/express-group',
				method,
				data,
			}).then(() => {
				onSelect && onSelect(nodeCopy);
				this.setState({ visible: false, node: data });
				this.dataLoad();
			});
		});
	};

	/**
	 * 删除
	 */
	deleteNode = () => {
		const { onSelect } = this.props;
		const { node } = this.state;
		Modal.confirm({
			title: `确定要删除分组( ${node.group_name} )`,
			content: '该操作不可逆,请谨慎操作',
			onOk: () => {
				request({
					url: `/api/biz-scene/v1/attendance/express-group/${node.id}`,
					method: 'delete',
				}).then(() => {
					onSelect && onSelect(undefined);
					this.dataLoad();
				});
			},
		});
	};

	/**
	 * 搜索
	 */
	handleSearch = value => {
		const { dataSource } = this.state;
		const searchResults = dataSource.filter(item => {
			if (item.group_name.match(value)) {
				return true;
			}
			return false;
		});
		this.setState({ searchResults });
	};

	render() {
		const { searchResults, visible, node } = this.state;

		return (
			<div className={styles['operate-tree']}>
				<div className={styles['operate-tree-handler']}>
					<a
						onClick={() => {
							this.setState({ visible: true, node: null });
						}}
					>
						<Icon type="plus" style={{ marginRight: 5 }} />
						新增
					</a>
					<a
						onClick={() => {
							if (!node) {
								message.warn('请先选择分组');
								return;
							}
							this.setState({ visible: true });
						}}
					>
						<Icon type="form" style={{ marginRight: 5 }} />
						编辑
					</a>
					<a
						onClick={() => {
							if (!node) {
								message.warn('请先选择分组');
								return;
							}
							this.deleteNode();
						}}
					>
						<Icon type="delete" style={{ marginRight: 5 }} />
						删除
					</a>
				</div>
				<div className={styles['operate-tree-search']}>
					<Search
						placeholder="搜索分组"
						onSearch={value => {
							this.handleSearch(value);
						}}
					/>
				</div>
				
				<div className={styles['operate-tree-list']}>
					<Scrollbars>
						{searchResults.length > 0 ? (
							searchResults.map(item => {
								return (
									<div
										className={classNames({
											[styles['node-item']]: true,
											[styles['node-item-selected']]: item.isSelected,
										})}
										key={item.id}
										onClick={() => this.handleSelect(item)}
										title={item.group_name}
									>
										{item.group_name}
									</div>
								);
							})
						) : (
							<div
								style={{
									display: 'flex',
									flexDirection: 'column',
									justifyContent: 'center',
									height: '100%',
								}}
							>
								<Empty
									image={Empty.PRESENTED_IMAGE_SIMPLE}
									description={<span style={{ color: '#ccc' }}>暂无数据</span>}
								/>
							</div>
						)}
					</Scrollbars>
				</div>
				<Modal
					title="分组名称"
					width={400}
					centered
					destroyOnClose
                    maskClosable={false}
					visible={visible}
					onOk={this.handleNode}
					onCancel={() => this.setState({ visible: false })}
				>
					<HzForm
						{...{
							labelCol: { span: 4 },
							wrapperCol: { span: 20 },
						}}
						fields={[
							{
								label: '名称',
								comp: {
									fieldName: 'group_name',
									component: 'Input',
									options: {
										initialValue: node && node.group_name,
										rules: [
											{
												required: true,
												message: '分组名称不能为空',
											},
											{
												validator: (rule, value, callback) => {
													if (!value) {
														callback();
														return;
													}
													if (!value.replace(/\s+/g, '')) {
														callback('分组名称不能为空');
														return;
													}
													callback();
												},
											},
											{
												max: 100,
												message: '不能超过100个字符',
											},
										],
									},
								},
							},
						]}
						setRef={ref => (this.HzFormRef = ref)}
					/>
				</Modal>
			</div>
		);
	}
}

export default OperateTree;
