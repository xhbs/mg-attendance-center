/*
 * @Author: xiedan WX042
 * @since: 2020-08-27 15:23:05
 * @lastTime: 2021-01-26 17:08:59
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\task\Rules\index.jsx
 * @Description: 考勤任务---考勤规则
 */
import React, { PureComponent } from 'react';
import { Modal, message, Button } from 'antd';
import classNames from 'classnames';
import ReactScrollbar from 'react-custom-scrollbars';
import { CreateDrawer } from '@hz-components/ists-common';
import { Table } from '@hz-components/react-base';
import { HandleBarOptions } from '@components/CreateHandleBarOptions/index';
import FormPage from './components/FormPage';
import NewDetailPage from './components/NewDetailPage';
import { getRulesList, deleteRule } from '@services/ruleService';
import { getSysConfig, getUserRoleInfo } from '@services/common';
import { API_ERROR_CODE, CITY, COUNTY } from '@constants';
import { tableColumns, tableColumnsHight, createSearchBarOptions } from './utils/tableOptions';
import styles from './styles/index.less';

class RulesManagement extends PureComponent {
	state = {
        userRole: '',

		visible: false,  // 表单页显示隐藏
		selectedRowKeys: [],
		operationType: 'add',
        editRowData: {}, // 待编辑的表格行
        isHigh: false, // 是否高级  false--云南中职  true--原来的功能
        isLoading: false, // 表单页面确认按钮是否加载中
    }
    
    static defaultProps = {
        eventsRuleDetailToProps: () => {},  // 外部事件，查看详情
    }

    UNSAFE_componentWillMount() {
        // 获取系统加载模式
        getSysConfig().then(res => {
            if (res && res.error_code === API_ERROR_CODE) {
                this.setState({
                    isHigh: res.data,
                })
            }
        })
    }

	componentDidMount() {
        getUserRoleInfo().then(res => {
            if(res) {
                this.setState({
                    userRole: res[0] && res[0].role_code || '',
                })
            }
        }).finally(() => {
            this.listRef.dataLoad()
        })
    }

    shouldComponentUpdate(nextP, nextS) {
        const { isHigh } = this.state;
        const { isHigh: newIsHigh } = nextS;

        if(newIsHigh !== isHigh) {
            this.listRef.dataLoad({ishight: newIsHigh});
        }

        return true;
    }

    /**
     * 分页查询考勤点列表
     * @param query
     */
    createPromise = (params) => {
        const init = this.initFlied(params);
        return new Promise(resolve => {
            getRulesList(init).then(res => {
                if(res && res.error_code === API_ERROR_CODE) {
                    const { data, paging } = res.data;
                    resolve({
                        pageIndex: paging && paging.page_num || 1,
                        totalCount: paging && paging.total || 0,
                        currentPageResult: data || [],
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
        const { isHigh, userRole } = this.state;
        const { pageIndex, pageSize, search = '', life_cycle, ishight = '' } = info || {};
        const a = ishight !== '' ? ishight : isHigh;
        const obj = {
            page_size: pageSize,
            page_num: pageIndex,
            query: search.trim(),
        };
        if(!a) {
            obj.life_cycle = [CITY, COUNTY].includes(userRole) ? '1' : life_cycle;
        }
        return obj;
	};

    // 点击新建
    handleAdd = () => {
        this.setState({
            visible: true,
            operationType: 'add',
        })
    }

    // 删除
    handleDel = (ids) => {
        const idsArr = Array.isArray(ids) && ids || [ids];
        deleteRule(idsArr).then(res => {
            if(res && res.error_code === API_ERROR_CODE) {
                message.success('删除成功')
                this.setState({ selectedRowKeys: [] })
                this.listRef.dataLoad();
            }
        })
    }

    // 批量删除
    batchDelete = () => {
        const { selectedRowKeys } = this.state;
        const ids = selectedRowKeys || []
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

    // 编辑
    handleEdit = (data) => {
        this.setState({
            editRowData: data,
            visible: true,
            operationType: 'update',
        })
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
        // 确认按钮先变成loading状态
        this.setState({
            isLoading: true,
        }, () => {
            const { handleSubmit } = this.FormRefs;
            handleSubmit((v) => {
                if(v) {
                    this.setState({
                        isLoading: false,
                    })
                }
            });
        });
    }

	renderFooter = () => {
        const { isLoading } = this.state;
		return (
			<React.Fragment>
				<Button
					onClick={this.onConfirm}
					type="primary"
                    style={{ marginRight: 16 }}
                    loading={isLoading}
				>
					确认
				</Button>
				<Button onClick={this.onCancel}>取消</Button>
			</React.Fragment>
		);
    }

	render() {
		const { visible, selectedRowKeys, operationType, editRowData, isHigh, userRole } = this.state;
		const rowSelection = {
			onChange: selectedRowKeys => {
			  this.setState({ selectedRowKeys })
			},
			selectedRowKeys,
		}
        const columns = isHigh ? tableColumnsHight : tableColumns;
        const wapperDisplay = visible ? 'none' : 'block';
		return (
			<div className={classNames('hz-layout-vertical', styles.rulesWapper)}>
				<div className="hz-layout-vertical-body" style={{margin: 16, display: wapperDisplay}}>
					<Table
                        rowKey="id"
                        hasSerialNo
						serialNoOverlying
                        handleBarOptions={HandleBarOptions({
                            handleAdd: this.handleAdd,
                            handleDel: this.batchDelete,
                            disabledDel: selectedRowKeys.length === 0,
                            searchPlaceholder: '规则名称/创建人',
                        })}
                        columns={columns({
                            handleEdit: this.handleEdit,
                            confirmDelete: this.confirmDelete,
                            isRulesType: ![CITY, COUNTY].includes(userRole),
                        })}
                        searchBarOptions={(!isHigh && ![CITY, COUNTY].includes(userRole)) ? createSearchBarOptions : false}
                        createPromise={this.createPromise}
                        antdProps={{
                            rowSelection,
                        }}
                        setRef={ref => {
                            this.listRef = ref;
                        }}
                        forceFirstLoadDisabled
					/>
				</div>
				<CreateDrawer
                    visible={visible}
                    title={operationType === 'add' ? '新建' : '编辑'}
					// handleConfirm={this.onConfirm}
					handleCancel={this.onCancel}
                    footer={this.renderFooter()}
				>
					<ReactScrollbar renderTrackHorizontal={() => <div />}>
                        {
                            isHigh ? (
                                <FormPage
                                    operationType={operationType}
                                    editId={editRowData.id}
                                    onFormPageClose={this.onCancel}
                                    wrappedComponentRef={ref=>{this.FormRefs = ref}}
                                />
                            ) : (
                                <NewDetailPage
                                    operationType={operationType}
                                    editId={editRowData.id}
                                    onFormPageClose={this.onCancel}
                                    wrappedComponentRef={ref=>{this.FormRefs = ref}}
                                    isHigh={isHigh}
                                    userRole={userRole}
                                />
                            )
                        }
					</ReactScrollbar>
				</CreateDrawer>
			</div>
		);
	}
}

export default RulesManagement;
