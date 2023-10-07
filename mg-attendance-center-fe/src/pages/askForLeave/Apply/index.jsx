/*
 * @Author: xiedan WX042
 * @since: 2020-08-27 09:34:29
 * @lastTime: 2021-08-31 10:23:56
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\askForLeave\Apply\index.jsx
 * @Description: 学生请假---请假申请-省市县级
 */
import React from 'react';
import { message } from 'antd';
import moment from 'moment';
import classNames from 'classnames';
import { Table } from '@hz-components/react-base';
import { HandleBarOptions } from '@components/CreateHandleBarOptions/index';
import LeaveAndPracticeModal from '@components/LeaveAndPracticeModal';
import DealModal from '@components/DealModal';
import { getLeaveList, dealLeave } from "@services/leave";
import { API_ERROR_CODE } from "@constants";
import { createSearchBarOptions, tableColumns } from './utils/tableOptions';
import styles from './styles/index.less';

class ApplyToProvincial extends React.Component {
    state = {
        visible: false,
        modalVisible: false, // 处置弹框
        dataSourceArr: [], // 列表数据
        selectedRowKeys: [],
        detailData: {},
        dataArr: [], // 列表数据
        showIndex: 0,  // 查看详情的索引
        defaultGrade: [], // 班级
        dealBtnLoading: false, // 处置弹框按钮的加载状态
    }

    /**
     * 分页查询考勤点列表
     * @param query
     */
    createPromise = (params) => {
        this.setState({
            selectedRowKeys: [],
        })
        const init = this.initFlied(params);
        return new Promise(resolve => {
            getLeaveList(init).then(res => {
                if(res && res.error_code === API_ERROR_CODE) {
                    const { paging, data } = res.data;
                    this.setState({
                        dataArr: data || [],
                    })
                    resolve({
                        pageIndex: paging.page_no,
                        totalCount: paging.total,
                        currentPageResult: data || [],
                    })
                } else {
                    this.setState({ dataArr: [] })
                    resolve({
                        currentPageResult: [],
                        totalCount: 0,
                    });
                }
            }).catch(() => {
                this.setState({ dataArr: [] })
                resolve({
                    currentPageResult: [],
                    totalCount: 0,
                });
            });
        })
    };

    /**
     * @description: 构造表格检索参数
     * @param info
     */
    initFlied = (info) => {
        const { pageIndex, pageSize, search = '', status, type, department_codes, date, sorter } = info || {};
        const { order } = sorter || {};
        this.setState({
            defaultGrade: department_codes && department_codes.selectedKeys || [],
        })

        const obj = {
            page_size: pageSize,
            page_num: pageIndex,
            search,
            type,
            status,
            order_rule: order && order === 'descend' ? 'DESC' : 'ASC', // 默认升序
            order_field: 'status',
        };

        // 校级
        if(department_codes) {
            const arr = department_codes.selectedNodes.map(i => i.org_index);
            obj.department_codes = arr || [];
        }
        if(date && date.length === 2 ) {
            obj.start_time = date[0] && moment(moment(date[0]).format('YYYY-MM-DD') + ' 00:00:00').valueOf();
            obj.end_time = date[1] && moment(moment(date[1]).format('YYYY-MM-DD') + ' 23:59:59').set('millisecond', 999).valueOf();
        }
        
        return obj;
    };

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

    // 处置  type=1 批量
    handleDeal = (record, type = 0) => {
        if(type === 0) {
            this.setState({
                modalVisible: true,
                detailData: record,
            })
        } else {
            this.setState({
                modalVisible: true,
                detailData: {},
            })
        }
    }

    // 处置 type=1 则为详情弹框操作
    handleSet = (values) => {
        this.setState({
            dealBtnLoading: true,
        })
        const { detailData, selectedRowKeys } = this.state;
        let ids = [];
        if(detailData && detailData.id) {
            ids = [detailData.id];
        } else if(selectedRowKeys.length > 0) {
            ids = selectedRowKeys;
        }

        const { status, comment } = values || {};
        // 同意
        if(status) {
            dealLeave({
                ids,
                result: status,
                comment,
            }).then(res => {
                if(res && res.error_code === API_ERROR_CODE) {
                    message.success('操作成功');
                    this.listRef.dataLoad();
                }
            }).finally(() => {
                this.setState({
                    modalVisible: false,
                    visible: false,
                    selectedRowKeys: [],
                    detailData: {},
                    dealBtnLoading: false,
                })
            })
        } else {
            this.setState({
                dealBtnLoading: false,
            })
        }
    }

    // 详情弹框
    handleEdit = (record, index) => {
        this.setState({
            visible: true,
            detailData: record,
            showIndex: index,
        })
    }

    render() {
        const { visible, selectedRowKeys, modalVisible, dataArr, showIndex, defaultGrade, dealBtnLoading } = this.state;
        const rowSelection = {
          onChange: selectedRowKeys => {
            this.setState({ selectedRowKeys })
          },
          selectedRowKeys,
          // 只有未处理的才允许选中，即处置
          getCheckboxProps: record => ({
            disabled: record.status !== 2,
          }),
        }

        return (
            <div className={classNames(
                'hz-layout-vertical',
                styles.applyWapper
            )}>
                <div className="hz-layout-vertical-body" style={{margin: 16}}>
                    <Table
                        rowKey="id"
                        hasSerialNo
						            serialNoOverlying
                        handleBarOptions={HandleBarOptions({
                            searchPlaceholder: '学生姓名/上报人',
                            customizeBtn: [{
                                antdProps: {
                                  icon: 'hz-set',
                                  children: '处置',
                                  disabled: selectedRowKeys.length === 0,
                                  onClick: () => this.handleDeal(null, 1),
                                },
                              }],
                        })}
                        searchBarOptions={createSearchBarOptions({
                            defaultGrade,
                        })}
                        columns={tableColumns({
                            handleEdit: this.handleEdit,
                            deal: this.handleDeal,
                        })}
                        createPromise={this.createPromise}
                        antdProps={{
                            rowSelection,
                        }}
                        setRef={ref => {
                            this.listRef = ref;
                        }}
                    />
                </div>

                {/* 详情弹框 */}
                {
                    visible && (
                        <LeaveAndPracticeModal
                            visible={visible}
                            title='请假'
                            statusType='2'
                            type='leave'
                            dataArr={dataArr}
                            showIndex={showIndex}
                            onCancel={() => {
                                this.setState({
                                    visible: false,
                                    detailData: {},
                                })
                            }}
                        />
                    )
                }

                {/* 修改状态弹框 */}
                <DealModal
                    visible={modalVisible}
                    loading={dealBtnLoading}
                    onOk={this.handleSet}
                    onCancel={() => {
                        this.setState({
                            modalVisible: false,
                            dealBtnLoading: false,
                        })
                    }}
                />
            </div>
        )
    }
}

export default ApplyToProvincial;