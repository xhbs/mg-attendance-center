/*
 * @Author: xiedan WX042
 * @since: 2020-11-27 16:00:58
 * @lastTime: 2021-01-20 13:46:35
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\task\Address\index.jsx
 * @Description: 考勤地点---省级、校级
 */
import React, { PureComponent } from 'react';
import classNames from 'classnames';
// import { Modal, message } from 'antd';
import { Modal, Radio, Menu, Dropdown, Button, message } from 'antd';
import ReactScrollbar from 'react-custom-scrollbars';
import { CreateDrawer } from '@hz-components/ists-common';
import { Table } from '@hz-components/react-base';
import { HandleBarOptions } from '@components/CreateHandleBarOptions/index';
import LeftPage from './components/LeftPage';
import FormPage from './components/FormPage';
import { getDevicesById, deleteDevices, getProductLists } from '@services/deviceService';
import { API_ERROR_CODE, DEVICE_TYPE } from '@constants';
import { tableColumns, tableColumnsHight, createSearchBarOptions } from './utils/tableOptions';
import styles from './styles/index.less';

// 云南中职屏蔽
const { Group, Button: RButton  } = Radio;
const MenuItem = Menu.Item;

const MAX_ITEM = 3;  // 最多显示3个类型，其余的放下拉列表，目前只有三种类型

class AddressManagement extends PureComponent {
    state = {
        visible: false,
        operationType: 'add', // 新建--add, 编辑--update
        selectedRowKeys: [],
        devicesType: DEVICE_TYPE,  // 卡口筛选列表,暂时前端写死
        selectedDevicesType: 1,  // 1--编码器  4--人脸卡口  5--门禁  云南中职默认编码器
        selectedGroup: '',  // 左侧选中的设备
        sourceData: [],
        productLists: [], // 厂商列表

        isHigh: false, // 是否高级  false--云南中职  true--原来的功能
    }

    componentDidMount() {
        getProductLists().then(res => {
            if(res) {
                this.setState({
                    productLists: res.encoder_producer || [],
                })
                this.listRef.dataLoad();
            }
        })
    }

    /**
     * 分页查询考勤点列表
     * @param query
     */
    createPromise = (params) => {
        const init = this.initFlied(params);
        return new Promise(resolve => {
            getDevicesById(init).then(res => {
                if(res && res.error_code === API_ERROR_CODE) {
                    const { data, paging } = res.data;
                    const newData = data.map(item => {
                        const a = item;
                        a.producer = this.getTransformDic(a.producer);
                        return a;
                    })
                    resolve({
                        pageIndex: paging.page_num || 1,
                        totalCount: paging.total || 0,
                        currentPageResult: newData || [],
                    });
                }
            })
        })
    };

    /**
     * @description: 构造表格检索参数
     * @param info
     */
    initFlied = (info) => {
        const { isHigh } = this.state;
        const { pageIndex, pageSize, type, search = '', groupId, devicesType = '' } = info || {};
        const obj = {
            page_size: pageSize,
            page_num: pageIndex,
            type: type,
            query: search.trim(),
            group_id: groupId,
            sub_type_list: isHigh && devicesType || [1,4,5],

        };
        return obj;
    };

    // 将列表数据的产商key值转换成name值
    getTransformDic = (value = '') => {
        const { productLists } = this.state;
        if(value) {
            const name = productLists.filter(item => {
                return item.key === value;
            })
            return name[0] && name[0].value || null;
        }
        return null;
    }
    
    // 选择设备
    selectedTreeFun = (obj = {}) => {
        this.setState({
            selectedGroup: obj && obj.id || '',
        })
        this.listRef.dataLoad({ groupId: obj && obj.id || '' });
    }

	handleAdd = () => {
        this.setState({
            visible: true,
            operationType: 'add',
        })
	}

    // 删除
    handleDel = (ids) => {
        const idsArr = Array.isArray(ids) && ids || [ids];
        deleteDevices(idsArr).then(res => {
            if(res && res.error_code === API_ERROR_CODE) {
                message.success('删除成功')
                this.listRef.dataLoad();
            }
        })
    }

    // 批量删除
    batchDelete = () => {
        const { selectedRowKeys } = this.state;
        const ids = selectedRowKeys || ''
        this.confirmDelete(ids)
    }

    // 点击单个数据的删除确认
    confirmDelete = ids => {
        const options = {
            title: '确定删除数据吗？',
            content: '此操作不可逆，请慎重考虑。',
            okText: '删除',
            okType: 'danger',
            cancelText: '取消',
            onOk: () => {
                this.handleDel(ids)
            },
        }
        Modal.confirm(options)
    }

    // 关闭新建页面
    onCancel = () => {
        this.setState({
            visible: false,
        })
        this.listRef.dataLoad();
    }

    // 点击确认调用表单提交
    onConfirm = () => {
        const { handleSubmit } = this.FormRefs;
        handleSubmit()
    }

    // 点击下拉菜单 云南中职屏蔽
    onMenu = ({item}) => {
        this.setState({
            selectedDevicesType: item.props.value,
        })
        this.listRef.dataLoad({ devicesType: item.props.value });
    }

    // 点击设备类型单选按钮  云南中职屏蔽
    onChangeDevicesType = (e) => {
        this.setState({
            selectedDevicesType: e.target.value,
            treeFlag: true,
        })
        // setTimeout(this.getFun, 200)
        this.listRef.dataLoad({ devicesType: e.target.value });
    }

	render() {
        const { visible, selectedRowKeys, operationType, devicesType, selectedDevicesType, selectedGroup, sourceData, isHigh } = this.state;
        const rowSelection = {
          onChange: selectedRowKeys => {
            this.setState({ selectedRowKeys })
          },
          selectedRowKeys,
        }
        /* 云南中职屏蔽 */
        const menu = (
            <Menu onClick={this.onMenu}>
                {
                    devicesType.slice(MAX_ITEM).map((item, index) => {
                        return <MenuItem key={index} value={item.code} className={selectedDevicesType === item.code && styles.menuItemBtn}>{item.name}</MenuItem>;
                    })
                }
            </Menu>
        );

        const columns = isHigh && tableColumnsHight || tableColumns;

		return (
			<div className="hz-layout-vertical">
				<div className={classNames(
					'hz-layout-horizontal',
					styles.addressWapper
				)}>
					<div className="hz-layout-fl" style={{
						width: 260,
						height: '100%',
						backgroundColor: '#fff',
					}}>
						<LeftPage selectedTreeFun={this.selectedTreeFun} />
					</div>
					<div className="hz-layout-main" style={{height: '100%'}}>

						<div className="hz-layout-vertical">
                            {/* 云南中职屏蔽 */}
                            {
                                isHigh && (
                                    <div className="hz-layout-vertical-header no-md" style={{
                                        height: 64,
                                        backgroundColor: '#fff',
                                        lineHeight: '64px',
                                        padding: '0 24px',
                                    }}>
                                        <Group buttonStyle="solid" onChange={this.onChangeDevicesType} value={selectedDevicesType}>
                                            {
                                                devicesType.slice(0, MAX_ITEM).map((item, index) => {
                                                    return <RButton key={index} value={item.code}>{item.name}</RButton>
                                                })
                                            }
                                            {/* 超出菜单最大显示数量，用列表展示 */}
                                            {
                                                devicesType && devicesType.length > MAX_ITEM && (
                                                    <Dropdown
                                                        overlay={menu}
                                                        getPopupContainer={ triggerNode=> triggerNode.parentNode}
                                                    >
                                                        <Button style={{borderTopLeftRadius: 0, borderBottomLeftRadius: 0, borderLeft: 'none'}} value="0">...</Button>
                                                    </Dropdown>
                                                )
                                            }
                                        </Group>
                                    </div>
                                )
                            }

                            <div
                                className="hz-layout-vertical-body"
                                style={{ height: 'calc(100% - 64px)', marginTop: isHigh && '-8px'}}
                            >

                                <Table
                                    rowKey="id"
                                    hasSerialNo
                                    serialNoOverlying
                                    handleBarOptions={HandleBarOptions({
                                        handleAdd: this.handleAdd,
                                        handleDel: this.batchDelete,
                                        disabledAdd: !selectedGroup || (isHigh && !selectedDevicesType), // 云南中职屏蔽
                                        disabledDel: selectedRowKeys.length === 0,
                                        searchPlaceholder: isHigh && '考勤点名称' || '设备名称/设备编码/IP地址',
                                    })}
									searchBarOptions={isHigh && createSearchBarOptions} // 云南中职屏蔽
                                    columns={columns({
                                        confirmDelete: this.confirmDelete,
                                    })}
                                    createPromise={this.createPromise}
                                    antdProps={{
                                        rowSelection,
                                    }}
                                    setRef={ref => {
                                        this.listRef = ref;
                                    }}
                                    forceFirstLoadDisabled  // 设置首次不加载，等表头获取完之后再加载
                                />
                            </div>
                        </div>
					</div>
				</div>

				<CreateDrawer
					visible={visible}
                    title={operationType === 'add' ? '新建' : '编辑'}
					handleConfirm={this.onConfirm}
					handleCancel={this.onCancel}
				>
					<ReactScrollbar renderTrackHorizontal={() => <div />}>
                        <FormPage
                            wrappedComponentRef={ref => this.FormRefs = ref}
                            onFormPageClose={this.onCancel}
                            operationType={operationType}
                            selectedGroup={selectedGroup}
                            selectedDevicesType={selectedDevicesType}
                            sourceData={sourceData}
                            isHigh={isHigh}
                        />
					</ReactScrollbar>
				</CreateDrawer>
			</div>
		);
	}
}

export default AddressManagement;
