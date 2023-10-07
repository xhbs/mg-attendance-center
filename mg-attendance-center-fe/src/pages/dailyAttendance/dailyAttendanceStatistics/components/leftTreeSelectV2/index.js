/**
 * 左侧树形选择
 * setActiveInfo：Func 修改当前激活的树节点信息 参数{}
 * showSubType： Number 显示显示到某一个级别 sub_type
 */
import React, { useEffect, useState } from 'react';
import { Tree } from '@hz-components/react-base';

import { getOrganizationsTree } from '@/services/dailyAttendance';
import { getUserOrgIndex } from '@/services/common';

// 组织树 org_index
let orgIndex = '';
// 用户权限（模拟）  实际是sub_type
let auth = 0;
// tree顶层的信息
let topInfo = {};

// 接口参数
const requestConfig = {
	type: 1,
	lazy_load: true,
	include_child: true,
	excludeApi: true,
}

function LeftTreeSelect(props) {
	// tree  数据
	const [dataTree, setDataTree] = useState([]);
	// tree 状态    选中
	const [treeKey, setTreeKey] = useState('');
	// 默认展开的节点
	const {
		setActiveInfo,
		showSubType,
	} = props;

	// tree  data设置
	const dataSetting = {
		dataKey: 'org_index',
		dataViewKey: 'org_name',
		childArrayKey: 'child',
		loadData: async () => {
			try {
				const result = await getOrganizationsTree({
					...requestConfig,
					org_index: orgIndex,
				});
				return {
					isSuccess: true,
					data: result || [],
				}
			} catch (err) {
				console.log(err);
			}
		},
		needLoadData: treeNode => {
			const {
				org_index,
				sub_type,
			} = treeNode
			orgIndex = org_index;
			// 定义在哪种情况下展示下拉箭头
			// 1、child为数组
			// 2、自定义的lastChild为ture 即最后一层
			// 3、showSubType不为空时， sub_type 小于 showSubType，权限未非校级管理员就不展示小于showSubType的  未校级管理员就展示
			const isChild = Array.isArray(treeNode.child) && !treeNode?.lastChild && (showSubType && showSubType !== auth ? sub_type < showSubType : true);
			return isChild;
		},
	}

	/**
	 * 修改激活的数据
	 */
	const editActiveInfo = (info = {}) => {
		const {
			org_index,
			sub_type,
			index_path: indexPath,
		} = info;
		setActiveInfo({
			subType: sub_type == null ? 1 : sub_type,
			activeOrgIndex: org_index,
			indexPath,
			topInfo,
		});
	}

	/**
	 * 获取tree最后一级
	 */
	const getLastTree = (tree) => {
		let lastList;
		tree.forEach(item => {
			const { child } = item;
			if (Array.isArray(child) && child.length !== 0) {
				lastList = getLastTree(child);
			} else {
				lastList = item;
			}
		})
		return lastList;
	}

	/**
	 * 根据id查找tree
	 */
	const getTreeById = (tree, id) => {
		let target = null;
		tree.forEach(item => {
			if (item.org_id === id) {
				target = item;
			}
			if (Array.isArray(item.child)) {
				const res = getTreeById(item.child, id);
				res && (target = res);
			}
		});
		return target;
	}

	/**
	 * 截取tree
	 */
	const spliceTree = (tree, value, type) => {
		tree.forEach(item => {
			if (item[type] === value) {
				item.child = null;
			}
			if (Array.isArray(item.child)) {
				spliceTree(item.child, value, type)
			}
		});
	}

	/**
	 * 获取组织结构
	 */
	const getTreeData = async (params) => {
		try {
			// 获取当前用户绑定的tree
			const {
				org_id,
				org_index,
				org_name,
			} = await getUserOrgIndex();
			// 是否是搜索
			let isSearch = !!params?.search;
			let data;
			if (isSearch) {
				// 搜索内容
				const result = await getOrganizationsTree({
					...requestConfig,
					org_index,
					...params,
				});
				// 根据搜索树  匹配当前权限的tree
				data = getTreeById(result, org_id);
				if (!data) {
					return setDataTree([]);
				}
				// data中的最后一层的数据
				const target = getLastTree([data]);
				// 获取搜索的下一个级别的内容
				const res  = await getOrganizationsTree({
					...requestConfig,
					org_index: target.org_index,
				});
				if (res.length !== 0) {
					target.child  = res;
				} else {
					target.lastChild = true;
				}
			} else {
				// 获取自上而下到权限所在级别的架构
				data = await getOrganizationsTree({
					...requestConfig,
					search: org_name,
				});
				// 从架构中找出本身
				data = getTreeById(data, org_id);
			}
			auth = data.sub_type;
			data = [data];
			// 如果传了showSubType就截取sub_type小于showSubType的数据
			if (showSubType && auth !== showSubType) {
				spliceTree(data, showSubType, 'sub_type')
			}
			topInfo = data?.[0] || {};
			setDataTree(data);
			!isSearch && editActiveInfo(data[0]);
		} catch (err) {
			console.log(err);
		}
	}

	useEffect(() => {
		getTreeData();
	// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	/**
	 * 搜索
	 */
	const handleSearch = async value => {
		!value && setTreeKey(Date.now().toString());
		await getTreeData({
			search: value,
			org_index: '',
		})
	}

	return (
		<div
			style={{
				padding: 15,
				width: 280,
				height: '100%',
				borderRadius: 8,
				backgroundColor: '#fff',
			}}
		>
			<Tree
				key={treeKey}
				data={dataTree}
				dataSetting={dataSetting}
				hasSearch
				onSearch={handleSearch}
				onSelect={(_ , target) => {editActiveInfo(target)}}
				searchSetting={{
					placeholder: '市(州)/区(县)/校/班',
				}}
			/>
		</div>
	);
}

export default LeftTreeSelect;
