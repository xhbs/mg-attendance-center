/**
 * 左侧树形选择
 * setActiveInfo：Func 修改当前激活的树节点信息 参数{}
 * showSubType： Number 显示显示到某一个级别 sub_type
 */
import React, { useEffect, useState } from 'react';
import { Tree } from '@hz-components/react-base';

import { getLazyOrganizationsTree, getUserOrgIndex } from '@/services/common';

// 组织树 org_index
let orgIndex = '';

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
  const [defaultNode, setDefaultNode] = useState(null); // 初始化的根节点
	// 默认展开的节点
	const {
		setActiveInfo,
    showSubType = '', // 最小展示层级
    rootNodeObj = null, // 初始化的根节点
    placeholder = '市(州)/区(县)/校',
    selectedKey = '', // 被选中的节点
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
        // const xiaojiNode = result.filter(i => i.sub_type === 4);
        // const notXiaojiNode = result.filter(i => i.sub_type < 4);
        // let arr = [];
        // if(xiaojiNode.length) {
        //   const copy = xiaojiNode.map(i => {
        //     return {
        //       ...i,
        //       org_parent_index_copy: i.org_parent_index,
        //       org_parent_index: `${orgIndex}123456`,
        //     }
        //   })
        //   arr.push({
        //     org_name: '直属学校',
        //     child: copy,
        //     org_parent_index: orgIndex,
        //     org_index: `${orgIndex}123456`,
        //   })
        // }

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
			// 3、showSubType不为空时， sub_type 小于 sub_type
			return Array.isArray(treeNode.child) && !treeNode?.lastChild && (showSubType ? sub_type < showSubType : true);
		},
	}

	/**
	 * 修改激活的数据
	 */
	const editActiveInfo = (info) => {
		setActiveInfo(info || defaultNode);
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
      let node = {};
      if(rootNodeObj) {
        // 外部传入的根节点
        node = rootNodeObj;
      } else {
        // 获取当前用户绑定的tree
        node = await getUserOrgIndex();
      }
			// 获取当前用户绑定的tree
			const {
				org_id,
				org_index,
				org_name,
			} = node;
			// 是否是搜索
			let isSearch = !!params?.search;
			let data;
			if (isSearch) {
				// 搜索内容
				const result = await getLazyOrganizationsTree({
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
				const res  = await getLazyOrganizationsTree({
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
				data = await getLazyOrganizationsTree({
					...requestConfig,
					search: org_name,
				});
				// 从架构中找出本身
				data = getTreeById(data, org_id);
			}
			data = [data];
			// 如果传了showSubType就截取sub_type小于showSubType的数据
			if (showSubType) {
				spliceTree(data, showSubType, 'sub_type')
			}
      setDataTree(data);
      if(!isSearch) {
        editActiveInfo(data[0]);
        setDefaultNode(data[0]);
      }
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
				padding: 16,
				height: '100%',
			}}
		>
			<Tree
				key={treeKey}
				data={dataTree}
				dataSetting={dataSetting}
				hasSearch
				onSearch={handleSearch}
				onSelect={(_ , target) => {editActiveInfo(_)}}
				searchSetting={{
					placeholder,
        }}
        {
          ...selectedKey ? { selectedKey } : {}
        }
			/>
		</div>
	);
}

export default LeftTreeSelect;
