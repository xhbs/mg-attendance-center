import React, { useState, useEffect } from 'react';
import { Icon, message, Modal } from 'antd';
import { HzForm } from '@hz-components/biz-scene-components';
import { Tree } from '@hz-components/react-base';
import {
	getDeviceGroupTree,
} from '@services/common';
import { addDevicesGroup, updateDevicesGroup, deleteDevicesGroup } from '@services/deviceService';
import { API_ERROR_CODE } from '@constants';
import styles from '../styles/leftTree.less';

const dataSetting = {
	dataKey: 'id',
	dataViewKey: 'name',
	childArrayKey: 'child_groups',
}

const LeftPage = (props) => {
	const { selectedTreeFun = () => { } } = props;
	const [tree, setTree] = useState([]);
	const [nodeData, setNodeData] = useState(null);
	const [visible, setVisible] = useState(false);
	const [type, setType] = useState('add');   // add or update
	const [HzFormRef, setRef] = useState(null);
	const [defaultExpandedKeys, setExpanded] = useState([]);   // tree组件默认展开项

	const getTreeData = (isReflesh = false) => {
		getDeviceGroupTree().then(res => {
			if (res && res.error_code === API_ERROR_CODE) {
				setTree(res.data || []);
				if (isReflesh) {
					setExpanded(res.data[0] && [res.data[0].id]);
				}
			}
		})
	}

	// 新增
	const addGroupReq = () => {
		const node = nodeData && nodeData.index_path || '';
		const level = node.split('/') || [];
		// 最多添加20级
		if (level.length > 19) {
			return message.warn('考勤分组层级已达上限')
		}
		setType('add');
		setVisible(true);
	}

	// 更新
	const updateGroupReq = () => {
		if (!nodeData) {
			return message.error('未选择分组')
		}
		setType('update');
		setVisible(true);
	}

	// 弹框点击确认
	const handleNode = () => {
		HzFormRef.submit().then(values => {
			if (type === 'add') {
				const params = {
					id: '',
					name: values.groupName || '',
					parent_id: nodeData && `${nodeData.id}` || '0',
				}
				addDevicesGroup(params).then(res => {
					if (res && res.error_code === API_ERROR_CODE) {
						message.success('新增分组成功')
						getTreeData();
					}
				})
			} else {
				const params = {
					id: nodeData && nodeData.id,
					name: values.groupName || '',
					parent_id: `${nodeData && nodeData.parent_id}`,
				}
				updateDevicesGroup(params).then(res => {
					if (res && res.error_code === API_ERROR_CODE) {
						message.success('编辑分组成功')
						getTreeData();
						nodeData.name = values.groupName;
						setNodeData(nodeData)
					}
				})
			}
			setVisible(false);
		})
	}

	// 删除
	const deleteGroupReq = () => {
		if (!nodeData) {
			return message.error('未选择分组')
		}
		Modal.confirm({
			title: `确定要删除分组( ${nodeData.name} )`,
			content: '该操作不可逆,请谨慎操作',
			onOk: () => {
				deleteDevicesGroup(nodeData.id).then(res => {
					if (res && res.error_code === API_ERROR_CODE) {
						message.success('删除分组成功');
						setNodeData(null);
						selectedTreeFun(null);
						getTreeData(true);
					}
				})
			},
		});
	}

	// 选择分组
	const onSelect = (nodeData) => {
		setNodeData(nodeData)
		selectedTreeFun(nodeData)
	}

	// 搜索
	const onSearch = (value) => {
		console.log(value)
	}

	const closeModal = () => {
		setVisible(false);
	}

	useEffect(() => {
		getTreeData(true);
	}, [])

	useEffect(() => {
		if (nodeData && nodeData.id) {
			setExpanded([nodeData.id])
		}
	}, [nodeData])

	return (
		<div className={styles.leftTreeWapper}>
			<div className={styles.operateTreeHandler}>
				<a onClick={addGroupReq}>
					<Icon type="plus" style={{ marginRight: 5 }} />
					新增
				</a>
				<a onClick={updateGroupReq}>
					<Icon type="form" style={{ marginRight: 5 }} />
					编辑
				</a>
				<a onClick={deleteGroupReq}>
					<Icon type="delete" style={{ marginRight: 5 }} />
					删除
				</a>
			</div>
			<div style={{ height: 'calc(100% - 30px)' }}>
				<Tree
					dataSetting={dataSetting}
					data={tree}
					onSelect={onSelect}
					onSearch={onSearch}
					expandedKeys={defaultExpandedKeys}
					hasSearch
					searchSetting={{
						placeholder: '搜索分组',
					}}
				/>
			</div>
			<Modal
				title="分组名称"
				// width={400}
				// centered
				destroyOnClose
				maskClosable={false}
				visible={visible}
				onOk={handleNode}
				onCancel={closeModal}
			>
				{
					visible && (
						<HzForm
							{...{
								labelCol: { span: 4 },
								wrapperCol: { span: 20 },
							}}
							fields={[
								{
									label: '名称',
									comp: {
										fieldName: 'groupName',
										component: 'Input',
										options: {
											initialValue: type === 'update' && nodeData && nodeData.name || '',
											rules: [
												{
													required: true,
													message: '分组名称不能为空',
												},
												{
													validator: (rule, val, callBack) => {
														if (val && val.length > 100) {
															callBack('分组名称不能超过100个字符');
															return;
														}
														if(val.indexOf(' ') > -1) {
															callBack('分组名称不能包含空格');
															return;
														}
														callBack();
													},
												},
											],
										},
									},
								},
							]}
							setRef={ref => setRef(ref)}
						/>
					)
				}
			</Modal>
		</div>
	)
}

export default LeftPage;