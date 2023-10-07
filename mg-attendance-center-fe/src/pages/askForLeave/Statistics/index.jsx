/*
 * @Author: xiedan WX042
 * @FilePath: Do not edit
 * @Description: 请假管理---请假统计
 * @since: 2021-09-18 14:59:50
 * @lastTime: 2021-10-14 15:48:58
 */
import React from 'react';
import { message } from 'antd';
import moment from 'moment';
import classNames from 'classnames';
import { Table } from '@hz-components/react-base';
import LeaveAndPracticeModal from '@components/LeaveAndPracticeModal';
import { HandleBarOptions } from '@components/CreateHandleBarOptions/index';
import LeftTreeSelect from '@components/LeftTreeSelectCom';
import { queryLeaveStatistics, exportLeaveStatistics } from '@/services/leave';
import { getUserRoleInfo } from '@/services/common';
import { DATE_TYPE_DAY, API_ERROR_CODE, DATA_TYPE_NO_SPACE, SCHOOL } from '@/constants';
import { downloadFileStream } from '@/utils/utils';
import { createSearchBarOptions, tableColumns } from './utils/tableOptions';
import styles from './styles/index.less';

class Statistics extends React.Component {
  state = {
    userRole: '',

    visible: false, // 详情弹窗
    dataArr: [], // 列表数据
    showIndex: 0,  // 查看详情的索引
    queryCondition: {}, // 实时查询条件
    total: 0, // 列表总数

    activeTreeNode: null, // 左侧选择的组织
  }

  componentDidMount() {
    getUserRoleInfo().then(res => {
      if (res) {
        this.setState({
          userRole: res[0] && res[0].role_code || '',
          // userRole: SCHOOL,
        })
      }
    })
  }

  /**
   * 分页查询统计数据
   * @param query
   */
  createPromise = (params) => {
    const obj = {
      currentPageResult: [],
      totalCount: 0,
    };
    return queryLeaveStatistics(params).then(res => {
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
    console.log('activeTreeNode====::', activeTreeNode)
    const {
      pageIndex,
      pageSize,
      search,
      leave_status,
      leave_type,
      times,
      sorter,
    } = params || {};
    const { order } = sorter || {};
    const obj = {
      page_size: pageSize,
      page_num: pageIndex,
      order_by_state_desc: order && order === 'descend',
    };
    if (search) {
      obj.search = search;
    }
    if (leave_status || leave_status === 0) {
      obj.leave_state = leave_status;
    }
    if (leave_type || leave_type === 0) {
      obj.type = leave_type;
    }
    
    if(activeTreeNode) {
      const { index_path, org_index, sub_type } = activeTreeNode;
      if(sub_type === 5) {
        obj.class_org_index = `${org_index}`;
      } else {
        obj.org_index_path = `${index_path}${org_index}`;
      }
    }

    if (times) {
      if (times[0]) {
        obj.start_date = times[0].format(DATE_TYPE_DAY);
      }
      if (times[1]) {
        obj.end_date = times[1].format(DATE_TYPE_DAY);
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
    if (total > 100000) {
      message.info('最大导出10万条数据', 3);
    }

    exportLeaveStatistics(queryCondition).then(res => {
      const name = `请假统计_${moment().format(DATA_TYPE_NO_SPACE)}.xlsx`;
      downloadFileStream(name, res)
    }).catch(() => message.error('无法导出数据'));
  };

  changeTreeNode = (node) => {
    this.setState({
      activeTreeNode: node,
    }, this.listRef.dataLoad)
  }

  render() {
    const {
      visible,
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

          <div className="hz-layout-main" style={{ height: '100%' }}>
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
                  })}
                  transformQuery={this.transformQuery}
                  createPromise={this.createPromise}
                  setRef={ref => this.listRef = ref}
                  forceFirstLoadDisabled
                  // scrollX={1500}
                />
              </div>

              {/* 详情弹框 */}
              {
                visible && (
                  <LeaveAndPracticeModal
                    visible={visible}
                    title='请假'
                    statusType='4'
                    type='leave'
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
            </div>
          </div>
        </div>
      </div>
    )
  }
}

export default Statistics;