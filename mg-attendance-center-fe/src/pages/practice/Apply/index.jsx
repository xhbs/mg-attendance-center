/*
 * @Author: xiedan WX042
 * @since: 2020-08-27 09:34:29
 * @lastTime: 2021-08-31 10:17:38
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\practice\Apply\index.jsx
 * @Description: 实习管理---实习申请-校级
 */
import React from 'react';
import { message } from 'antd';
import classNames from 'classnames';
import { Table } from '@hz-components/react-base';
import { HandleBarOptions } from '@components/CreateHandleBarOptions/index';
import LeaveAndPracticeModal from '@components/LeaveAndPracticeModal';
import DealModal from '@components/DealModal';
import { dealBatch, queryPracticeManage } from "@services/practiceManageServices";
import { API_ERROR_CODE, DATE_TYPE_DAY } from "@constants";
import { createSearchBarOptions, tableColumns } from './utils/tableOptions';
import styles from './styles/index.less';

class Apply extends React.Component {
    state = {
        visible: false,
        modalVisible: false, // 处置弹框
        selectedRowKeys: [],
        dataArr: [], // 列表数据
        showIndex: 0,  // 查看详情的索引
        dealIds: [], // 处置的id集合
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
            queryPracticeManage(init).then(res => {
                if(res && res.error_code === API_ERROR_CODE) {
                    const { data, paging } = res.data || {};

                    this.setState({
                        dataArr: data || [],
                    })
                    resolve({
                        pageIndex: paging.page_num,
                        totalCount: paging.total,
                        currentPageResult: data || [],
                    })
                } else {
                    message.error( res && res.message || '无法获取列表');
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
        const { pageIndex, pageSize, search = '', status, date, sorter } = info || {};
        const { order } = sorter || {};
        const obj = {
            page_size: pageSize,
            page_num: pageIndex,
            search,
            status,
            order_rule: order && order === 'descend' ? 'DESC' : 'ASC', // 默认升序
            order_field: 'status',
        };
        if (date && date.length === 2) {
            obj.start_time = date[0].format(DATE_TYPE_DAY);
            obj.end_time = date[1].format(DATE_TYPE_DAY);
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

    // 处置
    handleDeal = (record) => {
        const { selectedRowKeys } = this.state;
        const ids = record && [record.id] || selectedRowKeys;
        this.setState({
            modalVisible: true,
            dealIds: ids,
        })
    }

    // 处置
    onConfirm = (values) => {
        this.setState({
            dealBtnLoading: true,
        })
        const { dealIds } = this.state;
        const { status, comment } = values;
        const data = {
            ids: dealIds,
            result: status,
            comment,
        }
        dealBatch(data).then(res => {
            if(res && res.error_code === API_ERROR_CODE) {
                message.success('操作成功');
                this.listRef.dataLoad();
            } else {
                message.error( res && res.message || '无法操作');
            }
        }).finally(() => {
            this.setState({
                modalVisible: false,
                visible: false,
                selectedRowKeys: [],
                dealIds: '',
                dealBtnLoading: false,
            })
        })
    }

    // 详情弹框
    handleEdit = (record, index) => {
        this.setState({
            visible: true,
            showIndex: index,
        })
    }

    render() {
        const { visible, selectedRowKeys, modalVisible, dataArr, showIndex, dealBtnLoading } = this.state;
        const rowSelection = {
          onChange: selectedRowKeys => {
            this.setState({ selectedRowKeys })
          },
          selectedRowKeys,
          getCheckboxProps: record => ({
              disabled: record.status !== 2,
          }),
        }

        return (
            <div className={classNames(
                'hz-layout-vertical',
                styles.practiceWapper
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
                                  onClick: () => this.handleDeal(null),
                                },
                              }],
                        })}
                        searchBarOptions={createSearchBarOptions()}
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
                            title='实习申请'
                            type='practice'
                            statusType='2'
                            dataArr={dataArr}
                            showIndex={showIndex}
                            onCancel={() => {
                                this.setState({
                                    visible: false,
                                })
                            }}
                        />
                    )
                }

                {/* 修改状态弹框 */}
                <DealModal
                    visible={modalVisible}
                    loading={dealBtnLoading}
                    onOk={this.onConfirm}
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

export default Apply;