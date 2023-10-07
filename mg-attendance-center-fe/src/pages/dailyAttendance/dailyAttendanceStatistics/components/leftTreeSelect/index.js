/**
 * 左侧树形选择
 * setActiveInfo：Func 修改当前激活的树节点信息 参数{}
 * showSubType： Number 显示显示到某一个级别 sub_type
 */
import React, { useEffect, useState } from 'react';
import { Tree } from '@hz-components/react-base';

import { getOrganizationsTree } from '@/services/attendance';

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
	let dailyAttendancePramsStr = localStorage.getItem('dailyAttendancePrams') 
	let dailyAttendancePrams = {}
	if (dailyAttendancePramsStr) {
		dailyAttendancePrams = JSON.parse(dailyAttendancePramsStr)
	}

	// tree  数据
	const [dataTree, setDataTree] = useState([]);
	// tree 状态    选中
	const [treeKey, setTreeKey] = useState(dailyAttendancePrams.org_index || '');
	const [activeTreeKey, setActiveTreeKey] = useState(dailyAttendancePrams.org_index || '');
	const [expandedKeys, setExpandedKeys] = useState([]);

	// 默认展开的节点
	const {
		setActiveInfo,
		showSubType,
	} = props;

	// tree  data设置
	const dataSetting = {
		dataKey: 'org_index',
		dataViewKey: 'org_name',
		childArrayKey: 'childrens',
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
		setActiveTreeKey(org_index)
		setActiveInfo({
			subType: sub_type == null ? 1 : sub_type,
			activeOrgIndex: org_index,
			indexPath,
			topInfo,
		});
	}

	
	
	const genarateDefaultKeys = (data) => {
		if (dailyAttendancePrams?.org_index) {
			if (data.org_index === dailyAttendancePrams.org_index) {
				editActiveInfo({
					org_index: data.org_index,
					org_name: data.org_name,
					sub_type: data.sub_type,
				})
			} else {
				setExpandedKeys(data.childrens?.map(item => item.org_index) || [])

				let obj = data.childrens?.find(item => {
					if (item.org_index === dailyAttendancePrams.org_index) return true
					if (!item.childrens?.length) return false
					if (item.childrens.find(item2 => item2.org_index === dailyAttendancePrams.org_index)) return true
					return false
				})
				if (!obj) return
				editActiveInfo({
					org_index: obj.org_index,
					org_name: obj.org_name,
					sub_type: obj.sub_type,
				})
			}
		}
	}
	/**
	 * 获取组织结构
	 */
	const getTreeData = async (params) => {
		getOrganizationsTree().then(res => {
			let { success, data } = res || {}
			if (success) {
				setDataTree([data])
				genarateDefaultKeys(data)
				return
			}

		})
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

	// const onUnload = () => {
	// 	if (document.visibilityState === 'hidden') {
	// 		localStorage.removeItem('dailyAttendancePrams')
	// 	}
	// }
	// useEffect(() => {
	// 	window.addEventListener('visibilitychange', onUnload);
  //   return () => {
  //     window.removeEventListener('visibilitychange', onUnload);
  //   };
	// }, [])
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
				expandedKeys={expandedKeys}
				dataSetting={dataSetting}
				hasSearch
				selectedKey={activeTreeKey}
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
