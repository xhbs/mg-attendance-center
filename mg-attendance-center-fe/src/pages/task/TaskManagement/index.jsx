/*
 * @Author: xiedan WX042
 * @since: 2020-08-27 09:34:29
 * @lastTime: 2021-01-26 20:42:17
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\task\TaskManagement\index.jsx
 * @Description: 考勤任务---任务管理
 */
import React from 'react';
import { Modal, message, Button } from 'antd';
import classNames from 'classnames';
import ReactScrollbar from 'react-custom-scrollbars';
import { CreateDrawer } from '@hz-components/ists-common';
import { Table } from '@hz-components/react-base';
import { HandleBarOptions } from '@components/CreateHandleBarOptions/index';
import FormPage from './components/FormPage';
import { getTaskList, deleteTask, updateStatus, getRulesEnum } from '@services/taskService';
import { getSysConfig, getUserRoleInfo } from '@services/common';
import { API_ERROR_CODE, CITY, COUNTY } from '@constants';
import { createSearchBarOptions, tableColumns } from './utils/tableOptions';
import styles from './styles/index.less';

class TaskManagement extends React.Component {
    state = {
        userRole: '',

        visible: false,
        operationType: 'add', // 新建--add, 编辑--update
        selectedRowKeys: [],
        editRowData: {}, // 待编辑的表格行
        isHigh: false, // 是否高级  false--云南中职  true--原来的功能
        isLoading: false, // 表单页面确认按钮是否加载中

        taskType: '', // 任务类型，用于获取不同的规则
        rulesEnumList: [], // 考勤规则枚举值
        ruleValue: '', // 规则筛选
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
                    // userRole: 'city_admin',
                })
            }
        }).finally(() => {
            this.getRulesEnum();
            this.listRef.dataLoad();
        })
    }

    shouldComponentUpdate(nextP, nextS) {
        const { isHigh } = this.state;
        const { isHigh: newIsHigh } = nextS;

        if(newIsHigh !== isHigh) {
            this.getRulesEnum(newIsHigh)
            this.listRef.dataLoad({ishight: newIsHigh});
        }

        return true;
    }

    // 获取考勤规则  县市级只有抽查
    getRulesEnum = (ishight = '') => {
        const { isHigh, taskType, userRole } = this.state;
        const a = ishight !== '' ? ishight : isHigh;
        const param = a ? {} : {
            life_cycle: [CITY, COUNTY].includes(userRole) ? '1' : taskType,
        }
        getRulesEnum(param).then(res => {
            if (res && res.error_code === API_ERROR_CODE) {
                this.setState({
                    rulesEnumList: res.data || [],
                })
            }
        })
    }

    /**
     * 分页查询列表
     * @param query
     */
    createPromise = (params) => {
        const init = this.initFlied(params);
        return new Promise(resolve => {
            getTaskList(init).then(res => {
                if(res && res.error_code === API_ERROR_CODE) {
                    const { data, paging } = res.data;
                    resolve({
                        pageIndex: paging && paging.page_num || 1,
                        totalCount: paging && paging.total || 0,
                        currentPageResult: data || [],
                    })
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
        const { pageIndex, pageSize, search, taskStatus, rule, task_type, ishight = '' } = info || {};
        const a = ishight !== '' ? ishight : isHigh;
        const obj = {
            page_size: pageSize,
            page_num: pageIndex,
            status: taskStatus,
            rule_name: rule,
            task_type: [CITY, COUNTY].includes(userRole) ? '1' : task_type,
        };
        if(!a) {
            obj.task_type = [CITY, COUNTY].includes(userRole) ? '1' : task_type;
            obj.accurate_query = search;
        } else {
            obj.query = search;
        }

        // if(!isHigh) {
        //     obj.accurate_query = search;
        // } else {
        //     obj.query = search;
        // }
        return obj;
    };

    // 改变任务类型
    onChangeType = (v) => {
        this.setState({
            taskType: v,
            ruleValue: '',
        },
            this.getRulesEnum
        )
        this.listRef.dataLoad({
            task_type: v,
            rule: '',
        });
    }

    // 改变任务规则
    onChangeRule = (v) => {
        this.setState({
            ruleValue: v,
        })
        this.listRef.dataLoad({
            rule: v,
        });
    }

    // 点击新建
    handleAdd = () => {
        this.setState({
            visible: true,
            operationType: 'add',
        })
    }

    // 编辑
    handleEdit = (data) => {
        console.log('data=====::', data)
        this.setState({
            editRowData: data,
            visible: true,
            operationType: 'update',
        })
    }

    // 删除
    handleDel = (ids) => {
        const idsArr = Array.isArray(ids) && ids || [ids];
        deleteTask(idsArr).then(res => {
            if(res && res.error_code === API_ERROR_CODE) {
                message.success('删除成功');
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

    // 点击启用
    useing = (obj) => {
        const {status_name: name, id } = obj;
        const options = {
            title: `确定${name === '启用' ? '禁用' : '启用'}该任务吗？`,
            okText: '确定',
            okType: 'info',
            cancelText: '取消',
            onOk: () => {
                console.log('启用！！！', obj)
                const params = {
                    status: name === '启用' ? 'stop' : 'running',
                    ids: [id],
                }
                updateStatus(params).then(res => {
                    if(res && res.error_code === API_ERROR_CODE) {
                        message.success('操作成功');
                        this.listRef.dataLoad();
                    }
                })
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
            })
        })
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
        const { visible, selectedRowKeys, operationType, editRowData, isHigh, rulesEnumList, ruleValue, userRole } = this.state;
        const rowSelection = {
          onChange: selectedRowKeys => {
            this.setState({ selectedRowKeys })
          },
          selectedRowKeys,
        }
    
        const wapperDisplay = visible ? 'none' : 'block';
        return (
            <div className={classNames(
                'hz-layout-vertical',
                styles.taskManagmentWapper
            )}>
                <div className="hz-layout-vertical-body" style={{margin: 16, display: wapperDisplay}}>
                    <Table
                        rowKey="id"
                        hasSerialNo
						serialNoOverlying
                        handleBarOptions={HandleBarOptions({
                            handleAdd: this.handleAdd,
                            handleDel: this.batchDelete,
                            disabledDel: selectedRowKeys.length === 0,
                            searchPlaceholder: '任务名称/考勤地点/考勤对象',
                        })}
                        searchBarOptions={createSearchBarOptions({
                            rulesEnumList,
                            isHigh,
                            isTaskType: ![CITY, COUNTY].includes(userRole),
                            onChangeType: this.onChangeType,
                            onChangeRule: this.onChangeRule,
                            value: {
                                ruleValue,
                            },
                        })}
                        columns={tableColumns({
                            handleEdit: this.handleEdit,
                            useing: this.useing,
                            confirmDelete: this.confirmDelete,
                            isHigh,
                            isTaskType: ![CITY, COUNTY].includes(userRole),
                        })}
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
                        <FormPage
                            wrappedComponentRef={ref=>{this.FormRefs = ref}}
                            onFormPageClose={this.onCancel}
                            operationType={operationType}
                            detailId={editRowData.id}
                            userRole={userRole}
                            rulesEnumList={rulesEnumList}
                        />
                    </ReactScrollbar>
                </CreateDrawer>
            </div>
        )
    }
}

export default TaskManagement;