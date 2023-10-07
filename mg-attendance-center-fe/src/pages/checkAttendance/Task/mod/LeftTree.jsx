/**
 * 左侧树形选择
 * setActiveInfo：Func 修改当前激活的树节点信息 参数{}
 * showSubType： Number 显示显示到某一个级别 sub_type
 */
import React, { useEffect, useState } from 'react';
import { Tree } from '@hz-components/react-base';

import { getLazyOrganizationsTree } from '@/services/common';
import { getUserOrgIndex } from '@/services/common';

// 组织树 org_index
let orgIndex = '';
let indexPath = '';
// 接口参数
const requestConfig = {
	type: 1,
	lazy_load: true,
	include_child: true,
	excludeApi: true,
};

function LeftTree(props) {
	// tree  数据
	const [dataTree, setDataTree] = useState([]);
	// const [expandedKeys, setExpandedKeys] = useState(null); // 展开的节点

	// 默认展开的节点
	const {
		setActiveInfo,
		placeholder = '创建组织',
		showSubType = '', // 最小展示层级
		rootPath = '',
	} = props;

	// tree  data设置
	const dataSetting = {
		dataKey: 'org_index',
		dataViewKey: 'org_name',
		childArrayKey: 'child',
		loadData: async () => {
			try {
				const result = await getLazyOrganizationsTree({
					...requestConfig,
					org_index: orgIndex,
				});
				let arr = result;

				// 判断当前点击的节点的路径，如果包含当前用户的组织路径，则表示为子节点
				if (indexPath.indexOf(rootPath) === -1) {
					// 通过当前用户所属组织的路径（至根节点），去掉本级以上无关的兄弟节点
					arr = result.filter((i) => rootPath.indexOf(i.org_index) > -1);
				}
				// 去掉学校和班级节点
				arr = filterNode(arr);
				return {
					isSuccess: true,
					data: arr || [],
				};
			} catch (err) {
				console.log(err);
			}
		},
		needLoadData: (treeNode) => {
			const { org_index, index_path, sub_type } = treeNode;
			orgIndex = org_index;
			indexPath = index_path;
			// 定义在哪种情况下展示下拉箭头
			// 1、child为数组
			// 2、自定义的lastChild为ture 即最后一层
			// 3、showSubType不为空时， sub_type 小于 sub_type
			return (
				Array.isArray(treeNode.child) &&
				!treeNode?.lastChild &&
				(showSubType ? sub_type < showSubType : true)
			);
		},
	};

	/**
	 * 修改激活的数据
	 */
	const editActiveInfo = (info) => {
		setActiveInfo(info);
	};

	// 去掉校级及以下
	const filterNode = (data = []) => {
		const filitered = data.filter((i) => i.sub_type < 4);
		return filitered.map((item) => {
			if (!item.child) return item;
			return {
				...item,
				child: filterNode(item.child),
			};
		});
	};

	// 展开最靠前的节点
	// const getKey = (data) => {
	//   const a = data && data[0] || {};
	//   if(a.child && a.child.length) {
	//     return getKey(a.child)
	//   }
	//   return a.org_parent_index;
	// }

	/**
	 * 获取组织结构
	 */
	const getTreeData = async (params) => {
		try {
			let node = await getUserOrgIndex();
			// 获取当前用户绑定的tree
			const { org_name } = node;

			let data = await getLazyOrganizationsTree({
				...requestConfig,
				search: org_name,
			});
			data = filterNode(data);

			const arr =
				data && data[0] && data[0].child && data[0].child.length ? data[0].child : data;
			setDataTree(arr);
			// setExpandedKeys(getKey(arr))
		} catch (err) {
			console.log(err);
		}
	};

	useEffect(() => {
		getTreeData();
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	return (
		<div
			style={{
				padding: 16,
				height: '100%',
			}}
		>
			<Tree
				// key={treeKey}
				data={dataTree}
				dataSetting={dataSetting}
				// expandedKeys={expandedKeys && [expandedKeys]}
				hasSearch
				onSelect={(_, target) => {
					editActiveInfo(_);
				}}
				searchSetting={{
					placeholder,
				}}
				treeNodeRender={(nodeData) => {
					return {
						disabled: nodeData.sub_type >= 4,
					};
				}}
			/>
		</div>
	);
}

export default LeftTree;
