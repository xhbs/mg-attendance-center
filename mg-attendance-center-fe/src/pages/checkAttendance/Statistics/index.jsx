import React,{Component} from 'react';
import { Table } from '@hz-components/react-base';
import {message} from 'antd'
import classNames from 'classnames';
import { CreateDrawer } from '@hz-components/ists-common';
import LeftTreeSelect from '@components/LeftTreeSelectCom';
import {tableColumnsHight,createSearchBarOptions,createHandleBarOptions} from './utils/tableOptions';
import {  getUserOrgIndex } from '@services/common';
import { getCheckAttendStatisList,getAttendanceDateData} from '@services/checkAttendance/checkAttendanceStatistics';
import { API_ERROR_CODE, SCHOOL } from '@constants';
import Detail from '../Detail';
import qs from 'querystring';
import styles from './styles/index.less';

export default class index extends Component{
  state={
    treeData: [],
    orgIndexObj: '', // 当前用户的组织
    taskName: '',//任务名称
    task_id: '',//任务id
    queryInfo: {},//查询参数
    showDetailPage: false,//控制详情组件的显隐
    statisticsRowData: {},//当前行的数据
    attendanceDateList: [],
    filterIndexPath: '', // 组织    
    activeTreeNode: null, // 左侧选择的组织
  }

  //获取组织树结构数据,在钩子componnetDidMount中调用
  // 获取组织
  getOrgTreeList = () => {
    getUserOrgIndex().then(res => {
        if(res) {
          this.setState({
            userCode: res.user_code,
            orgIndexObj: {
              org_index: res.org_index,
              org_name: res.org_name,
            },
          });
          // 获取当前用户所属组织的详情，得到index_path，拼接得到treeData
          // getOrgDetailByOrgIndex(res.org_index).then(resp => {
          //   if(resp) {
          //     this.setState({
          //       treeData: [{
          //         org_index: resp.org_index,
          //         org_name: resp.org_name,
          //         index_path: resp.index_path,
          //         sub_type: resp.sub_type,
          //         child: [],
          //       }],
          //       fir_index_path: resp.index_path,
          //     });
          //   }
          // }).catch(()=>{
          //   this.setState({
          //     treeData: [],
          //   })
          // })
        }
    })
  }


    /**
   * 分页查询考勤点列表
   * @param query
   */
    createPromise = (params) => {
      return new Promise(resolve => {
        getCheckAttendStatisList(params).then(res => {
          if(res && res.error_code===API_ERROR_CODE) {
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
  transformQuery=(params)=>{
    let {task_id,taskName,queryInfo,activeTreeNode}=this.state
    let { pageIndex, pageSize,sorter,school_name,attendance_date} = params || {};
    const { order } = sorter || {};
    const obj = {
      page_size: pageSize,
      page_num: pageIndex,
      task_id,
      order_rule: order && order === 'descend' ? 'DESC' : 'ASC', // 默认升序
      order_field: 'absence_rate',
      attendance_date,
    };

    if(activeTreeNode) {
      const { index_path, org_index, sub_type } = activeTreeNode;
      if(sub_type === 4) {
        // 校级传参
        obj.school_org_index = `${org_index}`;
      } else {
        obj.org_index_paths = [`${index_path}${org_index}`];
      }
    }

    if(school_name){
      obj['school_name']=school_name.trim()
    }

    queryInfo={
      task_id,
      title: taskName,
      attendance_date: attendance_date ||  '',
			org_index_paths: obj.org_index_paths || '',
			school_name: school_name || '',
      order_rule: order && order === 'descend' ? 'DESC' : 'ASC', // 默认升序
      order_field: 'absence_rate',
    }
    this.setState({
      queryInfo,
    })
    return {...obj}
  }

  //导出
  exportData = async () => {
    let {queryInfo}=this.state
    if (this.listRef.getList().length === 0) {
      return message.error('暂无数据');
    }
    const param = qs.stringify(queryInfo);
    const url=`/api/biz-scene/v1/attendance/spot-check-attend/export-count`
    window.open(`${url}?${param.toString()}`, '_self');
  }

  // 打开详情组件
  openDetailPage = (data) => {
    this.setState({
      statisticsRowData: data,
      showDetailPage: true,
    })
  }

  // 关闭详情组件
  closeDetailPage = () => {
    this.setState({
      statisticsRowData: {},
      showDetailPage: false,
    })
  }

  //获取考勤日期列表
  fetchAttendanceDate=(id)=>{
    getAttendanceDateData({id}).then(res=>{
      if(res && res.error_code===API_ERROR_CODE){
        this.setState({
          attendanceDateList: res.data,
        })
      }
    })
  }

  componentDidMount(){
    if(this.props.data){
      let {name,start_date,end_date,id}=this.props.data
      this.fetchAttendanceDate(id)
      this.setState({
        taskName: name,
        attendanceCycle: start_date+'~'+end_date,
        task_id: id,
      })
    }
    if(this.props.userRole !== SCHOOL) {
      this.getOrgTreeList()
    }
  }
  
  changeTreeNode = (node) => {
    this.setState({
      activeTreeNode: node,
    }, this.listRef.dataLoad)
  }

  render(){
    const { attendanceDateList,statisticsRowData,showDetailPage,task_id }=this.state
    const {visible, data, closePage, userRole}=this.props

    return (
      showDetailPage ? (
        <Detail statisticsRowData={statisticsRowData} closeDetailPage={this.closeDetailPage} task_id={task_id} />
      ) : (
        <>
        <CreateDrawer
        visible={visible}
        title={`抽查考勤统计（${data.name}）`}
        handleCancel={closePage}
        className={styles.tjWapper}
        footer={[]}
      >
        <div className="hz-layout-vertical">
          <div className={classNames(
            'hz-layout-horizontal',
            styles.mainWapper
          )}>
            {
              userRole !== SCHOOL ? (
                <div className="hz-layout-fl" style={{
                  width: 280,
                  height: '100%',
                  backgroundColor: '#fff',
                }}>
                  <LeftTreeSelect
                    setActiveInfo={this.changeTreeNode}
                    // rootNodeObj={preNodeObj}
                    showSubType={3} // 抽查统计只展示到校级的上一层，并且校级管理员没有组织树
                    placeholder='所属组织'
                  />
                </div>
              ) : null
            }
            <div className="hz-layout-main" style={{ height: '100%' }}>
              <div className="hz-layout-vertical">
                <div className="hz-layout-vertical-body">
                  <Table
                    rowkey="id"
                    hasSerialNo
                    serialNoOverlying
                    columns={tableColumnsHight({
                      openDetailPage: this.openDetailPage,
                    })}
                    handleBarOptions={createHandleBarOptions({
                      exportData: this.exportData,
                    })}
                    searchBarOptions={createSearchBarOptions({
                      attendanceDateList,
                    })}
                    createPromise={this.createPromise}
                    transformQuery={this.transformQuery}
                    setRef={listRef=>this.listRef=listRef}
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
        </CreateDrawer>
        </>
      )
    )
  }
}
