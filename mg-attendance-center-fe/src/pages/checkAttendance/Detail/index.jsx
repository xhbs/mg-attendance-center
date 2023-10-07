import React,{Component} from 'react';
import { Table } from '@hz-components/react-base';
import {
	tableColumnsHight,
	createSearchBarOptions,
	createHandleBarOptions,
} from './utils/tableOptions';
import {message} from 'antd';
import {getAttendanceDetailData} from '@services/checkAttendance/checkAttendanceDetail';
import DetailPopup from './components/DetailPopup';
import { API_ERROR_CODE } from '@constants';
import qs from 'querystring';
export default class Detail extends Component{

  /**
	 * 导出
	 */
	exportData = async () => {
    let {queryInfo}=this.state
		if (this.listRef.getList().length === 0) {
			return message.error('暂无数据');
		}
		const param = qs.stringify(queryInfo);
		const url = `/api/biz-scene/v1/attendance/spot-check-attend/export-list`
		window.open(`${url}?${param.toString()}`, '_self');
	}


  /**
   * 分页查询考勤点列表
   * @param params
   */
  createPromise = (params) => {
    return new Promise(resolve => {
      getAttendanceDetailData(params).then(res => {
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
   * @param params
   */
  transformQuery=(params)=>{
    let {task_id,statisticsRowData}=this.props
    let {detailTaskName}=this.state
    let {attendance_date,school_org_index}=statisticsRowData
    let { pageIndex, pageSize,sorter,search_key,result} = params || {};
    const { order } = sorter || {};
    const obj = {
      page_size: pageSize,
      page_num: pageIndex,
      task_id,
      order_rule: order && order === 'descend' ? 'DESC' : 'ASC', // 默认升序
      order_field: 'result',
      attendance_date,
      result,
      school_org_index,
    };
    if(search_key){
      obj['search_key']=search_key.trim()
    }

    const queryInfo={
      attendance_date: statisticsRowData.attendance_date,
      task_id,
      title: detailTaskName,
      result: result===undefined ? '' : result,
      search_key: search_key ? search_key.trim() : '',
      order_rule: order && order === 'descend' ? 'DESC' : 'ASC', // 默认升序
      order_field: 'result',
      school_org_index,
    }

    this.setState({
      queryInfo,
    },()=>console.log(this.state.queryInfo))
    return obj
  }

  state={
    // dataSource:[]
    currentRowData: {},//当前行数据
    detailPopupVisible: false,//控制详情弹窗的显示
    detailTaskName: '',//详情任务名称
    queryInfo: {},//接口导出参数
  }

  componentDidMount(){
    if(this.props.statisticsRowData){
      let {school_org_name,attendance_date}=this.props.statisticsRowData
      this.setState({
        detailTaskName: `${school_org_name}${attendance_date}抽查考勤`,
      })
    }
  }

  //点击考勤详情，打开弹窗页面保存currentRowData
  openPopupPage=(data)=>{
    console.log(data);
    this.setState({
      currentRowData: data,
      detailPopupVisible: true,
    })
  }

  //关闭弹窗组件
  handleClosePopup=()=>{
    this.setState({
      currentRowData: {},
      detailPopupVisible: false,
    }, () => {
      if(this.listRef) {
        this.listRef.dataLoad()
      }
    })
  }

  render(){
    const {detailPopupVisible,currentRowData,detailTaskName}=this.state
    const {closeDetailPage,statisticsRowData,task_id}=this.props
    return (
      <div className="hz-layout-vertical">
        <div className="hz-layout-vertical-body" style={{margin: 16}}>
          <Table
            hasSerialNo
            createPromise={this.createPromise}
            columns={tableColumnsHight({
              openPopupPage: this.openPopupPage,

            })}
            handleBarOptions={createHandleBarOptions({
              onCancel: closeDetailPage,
              exportData: this.exportData,
              detailTaskName,
            })}
            searchBarOptions={createSearchBarOptions()}
            setRef={listRef=>this.listRef=listRef}
            transformQuery={this.transformQuery}
          />
          {
            detailPopupVisible && (
                <DetailPopup
                  detailPopupVisible={detailPopupVisible}
                  currentRowData={currentRowData}
                  handleClosePopup={this.handleClosePopup}
                  attendance_date={statisticsRowData.attendance_date}
                  task_id={task_id}
                />
            )
          }
        </div>
      </div>
    )
  }
}



