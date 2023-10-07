/*
 * @Author: xiedan WX042
 * @since: 2020-08-27 09:34:29
 * @lastTime: 2021-07-12 14:30:01
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\askForLeave\ApplyToSchool\index.jsx
 * @Description: 学生请假---请假申请-校级
 */
import React from 'react';
// import { Button, message, Modal } from 'antd';
import moment from 'moment';
import classNames from 'classnames';
import { Table } from '@hz-components/react-base';
// import { HzForm } from '@hz-components/biz-scene-components';
// import ReactScrollbar from 'react-custom-scrollbars';
// import { CreateDrawer } from '@hz-components/ists-common';
import { HandleBarOptions } from '@components/CreateHandleBarOptions/index';
import LeaveAndPracticeModal from '@components/LeaveAndPracticeModal';
import { getLeaveList,
  // addLeave, updateLeave, delLeave, reportLeave
} from "@services/leave";
import { API_ERROR_CODE, SCHOOL } from "@constants";
import { createSearchBarOptions, tableColumns } from './utils/tableOptions';
// import { AddAskForLeaveForm } from './utils/form';
import styles from './styles/index.less';

class ApplyToProvincial extends React.Component {
    state = {
        addVisable: false, // 新增请假页面是否显示
        visible: false, // 编辑弹框是否显示
        selectedRowKeys: [],
        detailData: {},
        dataArr: [], // 列表数据
        showIndex: 0,  // 查看详情的索引
        defaultGrade: [], // 班级
        isLoading: false, // 表单页面确认按钮是否加载中
    }

    /**
     * 分页查询考勤点列表
     * @param query
     */
    createPromise = (params) => {
        this.setState({ selectedRowKeys: [] })
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
            status,
            type,
            order_rule: order && order === 'descend' ? 'DESC' : 'ASC', // 默认升序
            order_field: 'status',
        };

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

    // 打开详情弹框
    handleEdit = (record, index) => {
        const obj = {...record};
        obj.date = [
            obj.start_time && moment(obj.start_time) || null,
            obj.end_time && moment(obj.end_time) || null,
        ]
        this.setState({
            visible: true,
            detailData: obj,
            showIndex: index,
        })
    }

    // 打开新增请假
    // openAddLeavePage = () => {
    //     this.setState({
    //         addVisable: true,
    //     })
    // }

    // 关闭新建页面
    // closeAddLeavePage = () => {
    //     this.setState({
    //         addVisable: false,
    //     })
    //     this.listRef.dataLoad();
    // }

    // 新增页面点击确认调用表单提交 report: 0--仅保存  1--保存并上报
    // onConfirm = (report) => {
    //     this.setState({
    //         isLoading: true,
    //     }, () => {
    //         this.hzAddFormRef.submit().then(values => {
    //             // console.log('values====::', values)
    //             if(values) {
    //                 this.addLeaveReq(values, report, 'add', () => {
    //                     this.setState({
    //                         isLoading: false,
    //                     })
    //                 });
    //             } else {
    //                 this.setState({
    //                     isLoading: false,
    //                 })
    //             }
    //         }).catch(() => {
    //             this.setState({
    //                 isLoading: false,
    //             })
    //         });
    //     })
    // }

    // 新增请假及上报req
    // addLeaveReq = (values, report, ty, callback = () => {}) => {
    //     const { detailData } = this.state;
    //     const { type, person, date, reason } = values;
    //     const rArr = person && person.map(x => x.user_code || x.person_no) || [];
    //     const params = {
    //         data: {
    //             type: type || '',
    //             start_time: date[0] && moment(moment(date[0]).format('YYYY-MM-DD') + ' 00:00:00').valueOf(),
    //             end_time: date[1] && moment(moment(date[1]).format('YYYY-MM-DD') + ' 23:59:59').set('millisecond', 999).valueOf(),
    //             reason,
    //             report, // 是否上报
    //         },
    //     }

    //     let fun = ty === 'add' ? addLeave : updateLeave ;
    //     if(ty === 'add') {
    //         params.data.person_nos = rArr;
    //     } else {
    //         params.data.person_no = rArr;
    //         params.id = detailData.id;
    //     }
    //     fun(params).then(res => {
    //         if(res && res.error_code === API_ERROR_CODE) {
    //             message.success('操作成功');
    //             // 新建成功后列表返回第一页
    //             if(ty === 'add') {
    //                 this.listRef.dataLoad({ pageIndex: 1 });
    //             } else {
    //                 this.listRef.dataLoad();
    //             }
    //             this.setState({
    //                 visible: false,
    //                 addVisable: false,
    //             })
    //         } else {
    //             message.error( res && res.message || '无法操作');
    //         }
    //     }).finally(() => {
    //         callback()
    //     })
    // }

    // 批量上报
    // batchReport = () => {
    //     const { selectedRowKeys } = this.state;
    //     const ids = selectedRowKeys || ''
    //     this.handleReport(ids)
    // }

    // 上报
    // handleReport = (ids) => {
    //     const batch = Array.isArray(ids);
    //     const data = batch && ids || [ids];
    //     const options = {
    //         title: `确定要上报${batch ? '所选' : ''}数据吗？`,
    //         content: '此操作不可逆，请慎重考虑。',
    //         okText: '上报',
    //         okType: 'warn',
    //         cancelText: '取消',
    //         onOk: () => {
    //             this.reportLeaveReq(data);
    //         },
    //     }
    //     Modal.confirm(options)
    // }

    // 上报req请求
    // reportLeaveReq = (data = []) => {
    //     reportLeave(data).then(res => {
    //         if(res && res.error_code === API_ERROR_CODE) {
    //             message.success('上报成功');
    //             this.setState({
    //                 selectedRowKeys: [],
    //             })
    //             this.listRef.dataLoad();
    //         } else {
    //             message.error( res && res.message || '无法上报');
    //         }
    //     })
    // }

    // 批量删除
    // batchDelete = () => {
    //     const { selectedRowKeys } = this.state;
    //     const ids = selectedRowKeys || ''
    //     this.handleDel(ids)
    // }

    // 删除
    // handleDel = (ids) => {
    //     const data = Array.isArray(ids) && ids.join(',') || ids;
    //     const options = {
    //         title: '确定删除数据吗？',
    //         content: '此操作不可逆，请慎重考虑。',
    //         okText: '删除',
    //         okType: 'danger',
    //         cancelText: '取消',
    //         onOk: () => {
    //             delLeave(data).then(res => {
    //                 if(res && res.error_code === API_ERROR_CODE) {
    //                     message.success('删除成功');
    //                     this.setState({
    //                         selectedRowKeys: [],
    //                     })
    //                     this.listRef.dataLoad();
    //                 } else {
    //                     message.error( res && res.message || '无法删除');
    //                 }
    //             })
    //         },
    //     }
    //     Modal.confirm(options)
    // }

    render() {
        const { addVisable, visible, selectedRowKeys, detailData, dataArr, showIndex, defaultGrade } = this.state;
        // const rowSelection = {
        //   onChange: selectedRowKeys => {
        //     this.setState({ selectedRowKeys })
        //   },
        //   selectedRowKeys,
        //   // 只有未上报的才允许选中，即上报和删除
        //   getCheckboxProps: record => ({
        //     disabled: record.status !== 1,
        //   }),
        // }
    
        const wapperDisplay = addVisable ? 'none' : 'block';
        return (
            <div className={classNames(
                'hz-layout-vertical',
                styles.applyWapper
            )}>
                <div className="hz-layout-vertical-body" style={{margin: 16, display: wapperDisplay}}>
                    <Table
                        rowKey="id"
                        hasSerialNo
						serialNoOverlying
                        handleBarOptions={HandleBarOptions({
                            // handleDel: this.batchDelete,
                            disabledDel: selectedRowKeys.length === 0,
                            searchPlaceholder: '学生姓名/学号',
                            // customizeBtn: [{
                            //     antdProps: {
                            //       icon: 'hz-add',
                            //       children: '请假',
                            //       onClick: this.openAddLeavePage,
                            //     },
                            //   }, {
                            //     antdProps: {
                            //       icon: 'hz-file-exception',
                            //       children: '上报',
                            //       disabled: selectedRowKeys.length === 0,
                            //       onClick: this.batchReport,
                            //     },
                            //   }],
                        })}
                        searchBarOptions={createSearchBarOptions({
                            defaultGrade,
                        })}
                        columns={tableColumns({
                            handleEdit: this.handleEdit,
                            // handleReport: this.handleReport,
                            // handleDel: this.handleDel,
                        })}
                        createPromise={this.createPromise}
                        // antdProps={{
                        //     rowSelection,
                        // }}
                        setRef={ref => {
                            this.listRef = ref;
                        }}
                    />
                </div>

                {/* 新增请假 */}
                {/* <CreateDrawer
                    visible={addVisable}
                    title='请假'
					// handleConfirm={this.onConfirm}
					handleCancel={this.closeAddLeavePage}
                    footer={[
                        <Button key="save" type="primary" onClick={() => this.onConfirm(false)} style={{marginRight: '15px'}} loading={isLoading}>
                        保存
                        </Button>,
                        <Button key="saveandreport" type="primary" onClick={() => this.onConfirm(true)} style={{marginRight: '15px'}} loading={isLoading}>
                        保存并上报
                        </Button>,
                        <Button key="close" onClick={this.closeAddLeavePage}>
                        取消
                        </Button>,
                    ]}
                >
                    <ReactScrollbar renderTrackHorizontal={() => <div />}>
                        <div style={{padding: '20px 0'}}>
                            <HzForm
                                {...{
                                    labelCol: { span: 2 },
                                    wrapperCol: { span: 8 },
                                }}
                                fields={AddAskForLeaveForm()}
                                setRef={ref => this.hzAddFormRef = ref}
                            />
                        </div>
                    </ReactScrollbar>
                </CreateDrawer> */}

                {/* 详情弹框 tips：当状态为 未上报'1' 时，可以编辑信息上报 */}
                {
                    visible && (
                        <LeaveAndPracticeModal
                            visible={visible}
                            title='请假'
                            data={detailData}
                            dataArr={dataArr}
                            showIndex={showIndex}
                            statusType='2'
                            type='leave'
                            userRole={SCHOOL}
                            // modalWidth={detailData.status === 1 && 700}
                            onCancel={() => {
                                this.setState({
                                    visible: false,
                                    detailData: {},
                                })
                            }}
                            // handleSubmitFun={this.addLeaveReq}
                            // footer={detailData.status === 1 && [
                            //     <Button key="save" type="primary" onClick={() => this.handleSaveModal(false)} loading={isLoading}>
                            //     保存
                            //     </Button>,
                            //     <Button key="saveandreport" type="primary" onClick={() => this.handleSaveModal(true)} loading={isLoading}>
                            //     保存并上报
                            //     </Button>,
                            // ] || []}
                            // children={detailData.status === 1 && (
                            //     <HzForm
                            //         {...{
                            //             labelCol: { span: 3 },
                            //             wrapperCol: { span: 12 },
                            //         }}
                            //         fields={DetailForm({
                            //             detailData,
                            //         })}
                            //         setRef={ref => this.hzDetailFormRef = ref}
                            //     />
                            // )}
                        />
                    )
                }
            </div>
        )
    }
}

export default ApplyToProvincial;