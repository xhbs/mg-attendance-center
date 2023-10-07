/*
 * @Author: xiedan WX042
 * @since: 2020-09-02 08:52:58
 * @lastTime: 2020-09-28 15:30:20
 * @FilePath: \biz-scene-coree:\hz\biz-scene-attendance-web\src\pages\statistics\NoSence\components\LeftTree\Tree\index.jsx
 * @Description: 考勤看板---左侧---资源树
 */

import React, { Component } from "react";
import { Tree, Input, Icon } from "antd";
import classNames from 'classnames';
import { getDeviceGroupTree } from '@services/common';
import { getDevicesById } from '@services/deviceService';
import { API_ERROR_CODE } from '@constants';
import styles from './index.less';

import qiuBlue from '../images/qiuBlue.png'
import banqiuBlue from '../images/banqiu.png'
import menjinBlue from '../images/menjinBlue.png'
import qiangBlue from '../images/qiangBlue.png'

const { TreeNode, DirectoryTree } = Tree;
const Search = Input.Search;

export default class TreeFlat extends Component {
    
    constructor() {
        super();
        this.state = {
            expandedKeys: null,
            selectedKeys: [],
            checkedKeys: [],
            searchValue: "", // 组织树搜索内容
            showIcon: false,
            selectedNodes: [], // 选中的节点id
            resourceTreeNodes: [], // 保存资源树数据
            autoExpandParent: false,  // 自动展开父节点
            leafList: [], // 所有子节点合集，用于检索
        }
    }

    componentDidMount() {
        // this.getTree()
        this.getDeviceGroupTreeReq()
    }

    treeHight = () => {
        window.addEventListener("resize", this.getTreeHights)
    }

    // 查询考勤点以及下面的设备
    getTree = () => {    
        const attendancePointList = [
            {name: '10000000001311000007', id: '11_1', channel_code: '10000000001311000007'},
            {name: '10000000051191000002', id: '11_2', channel_code: '10000000051191000002'},
            {name: '333333333', id: '11_3', channel_code: '333333333'},
            {name: '4444444444', id: '11_4', channel_code: '4444444444'},
            {name: '555555555', id: '11_5', channel_code: '555555555'},
        ]

        this.setState({ 
            resourceTreeNodes: [...attendancePointList],
        })
    }

    // 设备资源树接口请求
    getDeviceGroupTreeReq = () => {
        getDeviceGroupTree().then(res => {
            if(res && res.data) {
                this.setState({ 
                    resourceTreeNodes: [...res.data],
                })
            }
        })
    }
    
    // 双击
    onDoubleClick = (obj) => {
        const { onDoubleClick } = this.props;
        if(onDoubleClick) {
            onDoubleClick(obj)
        }
    }

    // 离线设备双击事件
    offDoubleClick = (obj) => {
        const { offDoubleClick } = this.props;
        if(offDoubleClick) {
            offDoubleClick(obj)
        }
    }

    // 点击展开/收起
    // 注意：此处 onExpand 与 onSelectGroup 单独设置是因为：点击三角图标时节点不会被选中和高亮，所以websoket单独放到 onSelectGroup 里面去实现
    onExpand = (expandedKeys) => {
        this.setState({
            expandedKeys,
            searchValue: '',
            autoExpandParent: false,
        })
    }

    // 点击考勤分组时触发 websoket
    onSelectGroup = (id) => {
        if (id) {
            const { getAtsPoint } = this.props;
            const { selectedNodes } = this.state;
            if(selectedNodes.indexOf(id) < 0) {
                this.setState({
                    selectedNodes: [...selectedNodes, id],
                });
                if(getAtsPoint) {
                    getAtsPoint(id);
                }
            }
        }
    }

    // 开始拖动
    // onDragStart = (param) => {
    //     const { onDragStart } = this.props;
    //     if(onDragStart) {
    //         onDragStart(param)
    //     }
    // };

    // // 拖动结束
    // onDragEnd = (param) => {
    //     // console.log(param)
    //     const { onDragEnd } = this.props;
    //     const online = param.node && param.node.props.dataRef.is_online && param.node.props.dataRef.is_online.toString() === "1"
    //     if (online && onDragEnd) {
    //         onDragEnd(param)
    //     }
    // }

    // onCheck = (checked, node) => {
    //     const parentNode = node.node.props.dataRef
    //     if (node.checked && parentNode.child && !parentNode.loaded) {
    //         message.info('请先加载子节点')
    //     }
    //     const { onCheck, treeData } = this.props;
    //     if (onCheck) {
    //         const checkedKeys = onCheck(checked, treeData)
    //         this.setState({
    //             checkedKeys,
    //         })
    //     }
    // }

    /**
     * model表示舍设备类型 4：人脸，5：门禁，8：车辆，1：编码器
     * is_online表示设备在线状态 0：登录中，1：登录成功，2：登录失败，9：其他
     */
    getIcon = device => {
        const qiu = <img src={qiuBlue} alt="icon"/>
        const banqiu = <img src={banqiuBlue} alt="icon"/>
        const qiang = <img src={qiangBlue} alt="icon"/>
        const menjin = <img src={menjinBlue} alt="icon"/>
        if (device.sub_type === 4) {
            return qiu
        } else if (device.sub_type === 5) {
            return menjin
        } else if (device.sub_type === 8) {
            return qiang
        }  else if (device.sub_type === 1) {
            return banqiu
        } else {
            return <span />
        }
    }

    /** 树节点图标为空 */
    setIcon = () => {
        return <span />
    }

    /* // 渲染非子节点的名称 */
    renderTitle = (item) => {
        return (
            <div
                style={{display:"inline-block",width:"100%", height: 24, marginLeft: "-30px", paddingLeft: "30px"}}
                onClick={() => {
                    this.onSelectGroup(item.id || '')
                }}
            >{item.name}</div>
        )
    }

    // 渲染节点
    renderTreeNodes = data => {
        const { selectedNodes } = this.state;
        return data.map(item => {
            const { id } = item;
            const selected = selectedNodes.indexOf(id) > -1;
            // 有channel_code则表示设备
            if (!item.channel_code) {
                return (
                    <TreeNode
                        title={this.renderTitle(item)}
                        icon={this.setIcon()}
                        key={id}
                        dataRef={item}
                        className={selected ? styles.selectedNode : ""}
                    >
                        {this.renderTreeNodes(item.child_groups || [])}
                    </TreeNode>
                );
            }
            return (
                <TreeNode
                    key={id}
                    dataRef={item}
                    switcherIcon={this.setIcon()}
                    // icon={this.setIcon()}
                    icon={this.getIcon(item)}
                    title={this.titleDom(item)}
                    // 注意：此处子节点设置为禁止选中，是因为子节点不需要高亮并且只有双击事件
                    selectable={false}
                />
            );
        });
    }

    // 叶子节点自定义
    titleDom = (item, online = true) => {
        const { searchValue } = this.state;
        const index = item.name.indexOf(searchValue);
        // 在线 or 离线
        const fun = online ? this.onDoubleClick : this.offDoubleClick;
        return (
            <span
                className={classNames({
                    [`${styles.tree_name}`]: searchValue && index > -1,
                })}
                onDoubleClick={() => fun(item)}
                title={item.name}
            >
                {item.name}
            </span>
        )
    } 

    // 懒加载数据
    onLoadData = treeNode => {
        const { resourceTreeNodes, leafList } = this.state;
        return new Promise((resolve, reject) => {
            // 只有组才能请求子节点
            if(!treeNode.props.dataRef.channel_code) {
                const obj = {
                    page_size: 1000,
                    page_num: 1,
                    sub_type: '',
                    type: '',
                    query: '',
                    group_id: treeNode.props.dataRef.id,
                };
                getDevicesById(obj).then(res => {
                    if(res && res.error_code === API_ERROR_CODE) {
                        const { data } = res.data;
                        const leafArr = [...leafList, ...data];
                        const oldArr = treeNode.props.dataRef.child_groups || [];
                        const arr = [...oldArr, ...data];
                        treeNode.props.dataRef.child_groups = arr;
                        this.setState({
                            resourceTreeNodes: [...resourceTreeNodes],
                            leafList: leafArr,
                        });
                        resolve();
                    }
                }).catch(() => {
                    reject();
                })
            }
        })
    }


    // 组织树搜索禁止输入空格
    onChange = e => {
        const { resourceTreeNodes } = this.state;
        const { value } = e.target;
        const expandedKeys = this.getFilter(value, resourceTreeNodes)
        this.setState({
          expandedKeys,
          searchValue: value,
          autoExpandParent: true,
        });
    };

    // 根据检索值筛选
    getFilter = (value) => {
        const { leafList, resourceTreeNodes } = this.state;
        const expandedKeys = leafList
        .map(item => {
          if (item.name.indexOf(value) > -1) {
            return this.getParentKey(item.id, resourceTreeNodes);
          }
          return null;
        })
        .filter((item, i, self) => item && self.indexOf(item) === i);
        // console.log('expandedKeys========::', expandedKeys)
      return expandedKeys
    }

    // 获取相应的父节点id，用于展开节点
    getParentKey = (id, tree) => {
        let parentKey;
        for (let i = 0; i < tree.length; i++) {
          const node = tree[i];
          const { child_groups } = node;
          if (child_groups) {
            if (child_groups.some(item => item.id === id)) {
              parentKey = node.id;
            } else if (this.getParentKey(id, child_groups)) {
              parentKey = this.getParentKey(id, child_groups);
            }
          }
        }
        return parentKey;
    };

    render() {
        const {
            expandedKeys,
            selectedKeys,
            checkedKeys,
            resourceTreeNodes,
            autoExpandParent,
            searchValue,
        } = this.state

        const {
            defaultExpandedKey,
            defaultSelectedKey,
        } = this.props;

        return (
            <div className={styles.search}>
                <div className={styles.searchBlock}>
                    <Search
                        ref="searchTxt"
                        placeholder="搜索通道名称"
                        maxLength={50}
                        value={searchValue}
                        onChange={this.onChange}
                    />
                </div>
                <div className={styles.treeStructure} ref='treeBox'>
                    {
                        <DirectoryTree
                            ref='treeNode'
                            // 注意：此处 onExpand 与 onSelectGroup（设置在节点上） 单独设置是因为：点击三角图标以及三角图标左侧区域时节点不会被选中和高亮，所以websoket单独放到 onSelectGroup 里面去实现
                            onExpand={this.onExpand}
                            checkedKeys={checkedKeys}

                            expandedKeys={expandedKeys || defaultExpandedKey}
                            autoExpandParent={autoExpandParent}
                            
                            defaultSelectedKeys={selectedKeys || defaultSelectedKey}
                            // onCheck={this.onCheck}
                            // onDragEnd={this.onDragEnd} // 电视墙用到的
                            // onDragStart={this.onDragStart}

                            // checkable={this.props.checkable}
                            // draggable={this.props.draggable}
                            // onRightClick={this.onRightClick}
                            defaultExpandAll
                            hasSearch

                            loadData={this.onLoadData}
                        >
                            {
                            resourceTreeNodes && resourceTreeNodes.length > 0
                                ? this.renderTreeNodes(resourceTreeNodes)
                                : <Icon type="loading" />
                            }
                        </DirectoryTree>
                    }
                </div>
            </div>
        )
    }
}