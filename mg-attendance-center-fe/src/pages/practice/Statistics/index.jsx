/*
 * @Author: xiedan WX042
 * @since: 2020-08-27 09:34:29
 * @Description: 实习管理---实习统计-省级、县级、市级
 */
import React from 'react';
import { message } from 'antd';
import moment from 'moment';
import classNames from 'classnames';
import { Table } from '@hz-components/react-base';
import LeaveAndPracticeModal from '@components/LeaveAndPracticeModal';
import { HandleBarOptions } from '@components/CreateHandleBarOptions/index';
import LeftTreeSelect from '@components/LeftTreeSelectCom';
import { queryPracticeStatistics, exportPracticeStatistics } from '@/services/practiceStatistics';
import { getUserRoleInfo } from '@/services/common';
import { DATE_TYPE_DAY, API_ERROR_CODE, DATA_TYPE_NO_SPACE, SCHOOL } from '@/constants';
import { downloadFileStream } from '@/utils/utils';
import HistoryDetail from './mod/HistoryDetail';
import { createSearchBarOptions, tableColumns } from './utils/tableOptions';
import styles from './styles/index.less';

class Statistics extends React.Component {
    state = {
        userRole: '',

        visible: false, // 详情弹窗
        modalVisible: false, // 历史考勤弹框
        detailData: {}, // 详情数据
        dataArr: [], // 列表数据
        showIndex: 0,  // 查看详情的索引
        queryCondition: {}, // 实时查询条件
        // defaultOrgKeys: [],
        total: 0, // 列表总数

        activeTreeNode: null, // 左侧选择的组织
    }

    componentDidMount() {
        getUserRoleInfo().then(res => {
			if(res) {
				this.setState({
          userRole: res[0] && res[0].role_code || '',
          // userRole: SCHOOL,
				})
			}
		})
    }

    /**
     * 分页查询实习统计数据
     * @param query
     */
    createPromise = (params) => {
        const obj = {
            currentPageResult: [],
            totalCount: 0,
        };
        return queryPracticeStatistics(params).then(res => {
            const { data, error_code, message: msg } = res || {};
            if (error_code !== API_ERROR_CODE) {
                message.error(msg || '无法获取数据');
                this.setState({ total: 0, dataArr: [] });
                return obj;
            }
            const { data: arr, paging } = data || {};
            obj.currentPageResult = arr || [];
            obj.totalCount = paging.total || 0;
            this.setState({ total: paging.total || 0, dataArr: arr || [] });
            return obj;
        }).catch(() => {
            message.error('无法获取数据');
            this.setState({ total: 0, dataArr: [] });
            return obj;
        });
    };

    /**
     * @description: 构造表格检索参数
     * @param info
     */
    transformQuery = params => {
      const { activeTreeNode } = this.state;
      console.log('activeTreeNode========::', activeTreeNode)
        const {
            pageIndex,
            pageSize,
            search,
            practice_status,
            times,
            sorter,
        } = params || {};
        const { order } = sorter || {};
        const obj = {
            page_size: pageSize,
            page_num: pageIndex,
            order_rule: order && order === 'descend' ? 'DESC' : 'ASC', // 默认升序
            order_field: 'practice_status',
        };
        if (search) {
            obj.search = search;
        }
        if (practice_status || practice_status === 0) {
            obj.practice_status = practice_status;
        }
        if(activeTreeNode) {
          const { sub_type, index_path, org_index } = activeTreeNode;
          if(sub_type !== 5) {
            obj.org_index_paths = [`${index_path}${org_index}`];
          } else {
            // 选择班级
            obj.class_org_indexes = [org_index];
          }
        }
        if (times) {
            if (times[0]) {
                obj.start_time = times[0].format(DATE_TYPE_DAY);
            }
            if (times[1]) {
                obj.end_time = times[1].format(DATE_TYPE_DAY);
            }
        }
        this.setState({
            queryCondition: obj,
        });
        return obj;
    };

    // 关闭新建页面
    onCancel = () => {
        this.setState({ visible: false })
        this.listRef.dataLoad();
    }

    // 点击确认调用表单提交
    onConfirm = () => {
        const { handleSubmit } = this.FormRefs;
        handleSubmit()
    }

    // 历史考勤
    historyDetail = (record) => {
        this.setState({
            modalVisible: true,
            detailData: record,
        })
    }

    // 详情弹框
    handleEdit = (record, index) => {
        this.setState({
            visible: true,
            showIndex: index,
        })
    }

    // 导出数据
    handleExport = () => {
		message.destroy();
        const { queryCondition, total } = this.state;
		if(total > 100000) {
			message.info('最大导出10万条数据', 3);
		}

        exportPracticeStatistics(queryCondition).then(res => {
            const name = `实习统计_${moment().format(DATA_TYPE_NO_SPACE)}.xlsx`;
            downloadFileStream(name, res)
        }).catch(() => message.error('无法导出数据'));
    };

    changeTreeNode = (node) => {
      console.log(node)
      this.setState({
        activeTreeNode: node,
      }, this.listRef.dataLoad)
    }

    render() {
        const {
            visible,
            modalVisible,
            detailData,
            total,
            dataArr,
            showIndex,
            userRole,
        } = this.state;

        return (
          <div className="hz-layout-vertical">
            <div className={classNames(
              'hz-layout-horizontal',
              styles.practiceSWapper
            )}>
              <div className="hz-layout-fl" style={{
                width: 280,
                height: '100%',
                backgroundColor: '#fff',
              }}>
                {
                  userRole ? (
                    <LeftTreeSelect setActiveInfo={this.changeTreeNode} showSubType={userRole !== SCHOOL ? 4 : ''} />
                  ) : null
                }
              </div>
              <div className="hz-layout-main" style={{height: '100%'}}>

                <div className={classNames('hz-layout-vertical', styles.practiceBox)}>
                <div className="hz-layout-vertical-body">
                    <Table
                        rowKey="id"
                        hasSerialNo
                        serialNoOverlying
                        handleBarOptions={HandleBarOptions({
                            searchPlaceholder: '学生姓名/学号',
                            handleExport: this.handleExport,
                            disabledExp: total === 0,
                        })}
                        searchBarOptions={createSearchBarOptions()}
                        columns={tableColumns({
                            handleEdit: this.handleEdit,
                            historyDetail: this.historyDetail,
                        })}
                        transformQuery={this.transformQuery}
                        createPromise={this.createPromise}
                        setRef={ref => this.listRef = ref}
                    />
                </div>

                {/* 详情弹框 */}
                {
                    visible && (
                        <LeaveAndPracticeModal
                            visible={visible}
                            title='实习'
                            type='practice'
                            statusType='3'
                            userRole={userRole}
                            showIndex={showIndex}
                            dataArr={dataArr}
                            onCancel={() => {
                                this.setState({
                                    visible: false,
                                })
                            }}
                        />
                    )
                }

                {/* 历史考勤 */}
                {
                    modalVisible && (
                        <HistoryDetail
                            visible={modalVisible}
                            onCancel={() => this.setState({ modalVisible: false })}
                            data={detailData}
                        />
                    )
                }
            </div>
              </div>
            </div>
          </div>
        )
    }
}

export default Statistics;