/*
 * @Author: xiedan WX042
 * @since: 2020-08-27 09:34:29
 * @Description: 实习管理---实习管理-校级
 */
import React from 'react';
import { Button, message } from 'antd';
// import moment from 'moment';
import classNames from 'classnames';
import { Table } from '@hz-components/react-base';
// import ReactScrollbar from 'react-custom-scrollbars';
// import { CreateDrawer } from '@hz-components/ists-common';
import { HandleBarOptions } from '@components/CreateHandleBarOptions/index';
import LeaveAndPracticeModal from '@components/LeaveAndPracticeModal';
import { queryPracticeManage } from '@/services/practiceManageServices';
// import FormPage from './mod/FormPage';
import { DATE_TYPE_DAY, API_ERROR_CODE, SCHOOL } from '@constants';
import { createSearchBarOptions, tableColumns } from './utils/tableOptions';
import styles from './styles/index.less';

class ManageToSchool extends React.Component {
    state = {
        visible: false,
        formVisible: false, // 表单
        selectedRowKeys: [],
        detailData: {}, // 详情信息
        dataArr: [], // 列表数据
        showIndex: 0,  // 查看详情的索引
        operationType: 'add', // 新增 or 编辑
        isLoading: false, // 表单页面确认按钮是否加载中
    }

    /**
     * 分页查询考勤点列表
     * @param query
     */
    createPromise = (params) => {
        this.setState({ selectedRowKeys: [] })
        const obj = {
            currentPageResult: [],
            totalCount: 0,
        };
        return queryPracticeManage(params).then(res => {
            const { data, error_code, message: msg } = res || {};
            if (error_code !== API_ERROR_CODE) {
                message.error(msg || '无法获取数据');
                this.setState({ dataArr: [] });
                return obj;
            }
            const { data: arr, paging } = data || {};
            this.setState({ dataArr: arr || [] });
            obj.currentPageResult = arr || [];
            obj.totalCount = paging.total || 0;
            return obj;
        }).catch(() => {
            message.error('无法获取数据');
            this.setState({ dataArr: [] });
            return obj;
        });
    };

    /**
     * @description: 构造表格检索参数
     * @param info
     */
    transformQuery = (info) => {
        const { pageIndex, pageSize, search, status, times, sorter } = info || {};
        const { order } = sorter || {};
        const obj = {
            page_size: pageSize,
            page_num: pageIndex,
            order_rule: order && order === 'descend' ? 'DESC' : 'ASC', // 默认升序
            order_field: 'status',
        };
        if (times) {
            if (times[0]) {
                obj.start_time = times[0].format(DATE_TYPE_DAY);
            }
            if (times[1]) {
                obj.end_time = times[1].format(DATE_TYPE_DAY);
            }
        }
        if (search || search === 0) {
            obj.search = search;
        }
        if (status || status === 0) {
            obj.status = status;
        }
        return obj;
    };

    // 关闭新建页面
    onCancel = () => {
        this.setState({ formVisible: false, detailData: {} })
        this.listRef.dataLoad();
    }

    // 点击确认调用表单提交
    // onConfirm = (report) => {
    //     // 确认按钮先变成loading状态
    //     this.setState({
    //         isLoading: true,
    //     }, () => {
    //         const { handleSubmit } = this.FormRefs;
    //         if (handleSubmit) {
    //             handleSubmit((values, flag) => {
    //                 if(!flag) {
    //                     this.setState({
    //                         isLoading: false,
    //                     })
    //                 } else {
    //                     this.addPracticeReq(values, report);
    //                 }
    //             })
    //         }
    //     })
    // }
    
    // 新增及修改req
    // addPracticeReq = (values, report) => {
    //     const { operationType, detailData } = this.state;
    //     const { company_contacts, contacts_phone, practice_company, person_nos, date } = values;
    //     const rArr = person_nos.map(x => x.user_code || x.person_no) || [];
    //     const params = {
    //         data: {
    //             company_contacts,
    //             contacts_phone,
    //             practice_company,
    //             person_nos: rArr,
    //             start_time: date[0] && moment(date[0]).format('YYYY-MM-DD'),
    //             end_time: date[1] && moment(date[1]).format('YYYY-MM-DD'),
    //             report, // 是否上报
    //         },
    //     }
    //     let fun = operationType === 'add' ? addPracticeApply : updatePracticeApply ;
    //     if(operationType !== 'add') {
    //         params.id = detailData.id;
    //     }
    //     fun(params).then(res => {
    //         if(res && res.error_code === API_ERROR_CODE) {
    //             message.success('操作成功');
    //             // 新建成功后列表返回第一页
    //             if(operationType === 'add') {
    //                 this.listRef.dataLoad({ pageIndex: 1 });
    //             } else {
    //                 this.listRef.dataLoad();
    //             }
    //             this.setState({
    //                 visible: false,
    //                 formVisible: false,
    //                 detailData: {},
    //             })
    //         } else {
    //             message.error( res && res.message || '无法操作');
    //         }
    //     }).finally(() => {
    //         this.setState({
    //             isLoading: false,
    //         })
    //     })
    // }

    // 新增实习申请
    // handleAdd = () => {
    //     this.setState({
    //         formVisible: true,
    //         detailData: {},
    //         operationType: 'add',
    //     })
    // }

    // 详情弹框
    handleEdit = (record, index) => {
        // const { status } = record || {};
        // 未上报的支持跳转至到表单页修改
        // if (status === 1) {
        //     this.setState({
        //         detailData: record,
        //         formVisible: true,
        //         operationType: 'edit',
        //     });
        // } else {
            // 其他状态的记录打开详情框
            this.setState({
                visible: true,
                showIndex: index,
            })
        // }
    }

    // 上报
    // handleReport = record => {
    //     Modal.confirm({
    //         title: '确定上报所选数据吗？',
    //         content: '此操作不可逆，请慎重考虑。',
    //         okText: '上报',
    //         okType: 'warn',
    //         cancelText: '取消',
    //         onOk: () => this.reportingData(record),
    //     });
    // }

    // api上报
    // reportingData = id => {
    //     const { selectedRowKeys } = this.state;
    //     const ids = id ? [id] : selectedRowKeys;
    //     reportBatch(ids).then(res => {
    //         const { error_code, message: msg } = res || {};
    //         if (error_code !== API_ERROR_CODE) {
    //             return message.error(msg || '无法上报数据');
    //         }
    //         message.success('上报成功');
    //         this.listRef.dataLoad();
    //         if (selectedRowKeys.length !== 0) {
    //             this.setState({ selectedRowKeys: [] });
    //         }
    //     }).catch(message.error('无法上报数据'));
    // }

    // 删除
    // handleDel = record => {
    //     Modal.confirm({
    //         title: '确定删除所选数据吗？',
    //         content: '此操作不可逆，请慎重考虑。',
    //         okText: '删除',
    //         okType: 'danger',
    //         cancelText: '取消',
    //         onOk: () => this.batchDelete(record),
    //     });
    // }

    // api删除
    // batchDelete = id => {
    //     const { selectedRowKeys } = this.state;
    //     const ids = id ? [id] : selectedRowKeys;
    //     deleteData({ ids: ids.join(',') }).then(res => {
    //         console.log(res);
    //         const { error_code, message: msg } = res || {};
    //         if (error_code !== API_ERROR_CODE) {
    //             return message.error(msg || '无法删除数据');
    //         }
    //         message.success('删除成功');
    //         this.listRef.dataLoad();
    //         if (selectedRowKeys.length !== 0) {
    //             this.setState({ selectedRowKeys: [] });
    //         }
    //     }).catch(() => message.error('无法删除数据'));
    // }

    // footer
    renderFooter = () => {
        // const { isLoading } = this.state;
        return (
            <React.Fragment>
                {/* <Button
                    onClick={() => this.onConfirm(false)}
                    type="primary"
                    style={{ marginRight: 16 }}
                    loading={isLoading}
                >
                    保存
				</Button>
                <Button
                    onClick={() => this.onConfirm(true)}
                    style={{ marginRight: 16 }}
                    loading={isLoading}
                >
                    保存并上报
				</Button> */}
                <Button onClick={this.onCancel}>取消</Button>
            </React.Fragment>
        );
    }

    render() {
        const {
            visible,
            // selectedRowKeys,
            // formVisible,
            detailData,
            // operationType,
            dataArr,
            showIndex,
        } = this.state;
        // const rowSelection = {
        //     onChange: selectedRowKeys => {
        //         this.setState({ selectedRowKeys })
        //     },
        //     selectedRowKeys,
        //     getCheckboxProps: record => ({
        //         disabled: record.status * 1 !== 1, // 只有未处理的能删除
        //     }),
        // }

        return (
            <div className={classNames('hz-layout-vertical', styles.practiceWapper)}>
                <div className="hz-layout-vertical-body" style={{ margin: 16 }}>
                    <Table
                        rowKey="id"
                        hasSerialNo
                        serialNoOverlying
                        handleBarOptions={HandleBarOptions({
                            searchPlaceholder: '学生姓名/上报人',
                            // handleDel: this.handleDel,
                            // disabledDel: !selectedRowKeys.length,
                            // customizeBtn: [{
                            //     antdProps: {
                            //         icon: 'hz-add',
                            //         children: '实习申请',
                            //         onClick: () => this.handleAdd(),
                            //     },
                            // }, {
                            //     antdProps: {
                            //         icon: 'hz-edit',
                            //         children: '上报',
                            //         disabled: !selectedRowKeys.length,
                            //         onClick: () => this.handleReport(),
                            //     },
                            // }],
                        })}
                        searchBarOptions={createSearchBarOptions()}
                        columns={tableColumns({
                            handleEdit: this.handleEdit,
                            // handleReport: this.handleReport,
                            // handleDel: this.handleDel,
                        })}
                        createPromise={this.createPromise}
                        // antdProps={{ rowSelection }}
                        setRef={ref => this.listRef = ref}
                        transformQuery={this.transformQuery}
                    />
                </div>
                {/* 表单页面 */}
                {/* <CreateDrawer
                    visible={formVisible}
                    title={operationType === 'add' ? '新建' : '编辑'}
                    handleCancel={this.onCancel}
                    footer={this.renderFooter()}

                >
                    <ReactScrollbar renderTrackHorizontal={() => <div />}>
                        <FormPage
                            wrappedComponentRef={ref => { this.FormRefs = ref }}
                            onFormPageClose={this.onCancel}
                            operationType={operationType}
                            detailId={detailData.id || null}
                        />
                    </ReactScrollbar>
                </CreateDrawer> */}

                {/* 详情弹框 */}
                {
                    visible && (
                        <LeaveAndPracticeModal
                            visible={visible}
                            data={detailData}
                            title='实习申请'
                            type='practice'
                            statusType='2'
                            userRole={SCHOOL}
                            onCancel={() => this.setState({ visible: false })}
                            showIndex={showIndex}
                            dataArr={dataArr}
                        />
                    )
                }
            </div>
        )
    }
}

export default ManageToSchool;