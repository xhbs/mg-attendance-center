import React, { Component } from 'react';
import {Table,Modal,Button,Popover,Icon, message,Spin} from 'antd';
import ReactScrollbar from 'react-custom-scrollbars';
import styles from '../styles/popup.less';
import {tableColumns} from '../utils/tableOptions'
import {
          getEnumValue,sexEnum,leaveTypeEnum,
          resultTypeEnum,infoEnum,attendanceResEnum,
          modeEnum,modeType,attendanceResType,
       } from '../utils/utils';
import {getAttendanceDetailList} from '@services/checkAttendance/checkAttendanceDetail';
import { API_ERROR_CODE } from '@constants';
import personEmpty from '@hz-design/base/public/person-fail-empty.svg';

export default class DetailPopup extends Component {

  componentDidMount(){
    this.getDetailData()
  }

  state={
    data: {},//个人考勤信息
    loading: false,//Spin组件的加载状态
  }

  getDetailData=()=>{
    //发起请求前设置loading为true，请求完成后设置loading为false
    this.setState({
      loading: true,
    },async ()=>{
      try{
        let {attendance_date,currentRowData,task_id}=this.props
        console.log(task_id);
        let res=await getAttendanceDetailList({
          attendance_date,
          person_no: currentRowData.person_no,
          task_id,
        })

        let {data,error_code, message: msg} = res || {};    
        if(res&&error_code===API_ERROR_CODE){        
          let {
            attendance_date,
            attendance_time,
            person_info,
            task_info,
            leave_info,
            practice_info,
            result,
            // change_records,
            // recent_records,
          }=data
          //处理对象字段
          //人员信息
          data.person_info = {
            ...person_info,
            gender: getEnumValue(sexEnum, person_info.gender),
          }   
    
          // data.attendance_info = {
          //   attendance_time,
          //   attendance_date,
          // }
    
          //考勤信息添加考勤日期字段
          task_info && (
            (result===0 || result===4)
            ?
            data.task_info={
              ...task_info,
              attendance_time,
            }
            :
            data.task_info={
              ...task_info,
              attendance_date,
            }
          )
    
          //请假信息(条件渲染可能存在可能不存在)
          leave_info && (
            data.leave_info={
              ...leave_info,
              type: getEnumValue(leaveTypeEnum,leave_info.type),
              time: `${leave_info.start_date}~${leave_info.end_date}`,
            }
          )
          //实习信息(条件渲染可能存在可能不存在)
          practice_info && (
            data.practice_info={
              ...practice_info,        
              time: `${practice_info.start_date}~${practice_info.end_date}`,
              // practice_status: getEnumValue(practiceTypeEnum,practice_info.practice_status),
            }
          )
    
          this.setState({
            data: {...data},
          })
        }else{
          message.error(msg)
        }
        
      }catch(err){
        console.log(err);
      }finally {
        this.setState({loading: false})
      }
    })
  }

  render() {
    const {detailPopupVisible,handleClosePopup}=this.props
    let {data,loading}=this.state
    return (
      <Modal
        visible={detailPopupVisible}
        centered
        onCancel={handleClosePopup}
        title="考勤详情"
        width={700}
        maskClosable={false}
        bodyStyle={{
            height: 580,
            position: 'relative',
        }}
        footer={
          <Button onClick={handleClosePopup}>取消</Button>
        }
      >
        <ReactScrollbar renderTrackHorizontal={() => <div />}>
          {
            loading ? (
              <div className={styles['loading']}>
                <Spin />
              </div>
            ) : (
              <div className={styles['info-container']}>
                {
                  data && (
                    <div 
                      className={styles['tag']} 
                      style={{color:getEnumValue(resultTypeEnum,data.result,'color'),backgroundColor:getEnumValue(resultTypeEnum,data.result,'bgc')}}
                    >
                      {getEnumValue(resultTypeEnum,data.result,'info')}            
                    </div>
                  )
                }       
                {/* 循环遍历 data有值显示考勤详情没有值显示考勤记录不存在*/}
                {
                  data ? Object.keys(infoEnum).map(key=>{
                    const {title,type,list}=infoEnum[key]
                    return data[key] && (
                      <div key={key}>
                        {/* <h5 className={styles['title']}>{title}</h5> */}
                        <div className='hz-label'>{title}</div>
                        <ul className={type==='step' ? styles['row'] : styles['column']}>
                          {
                            type==='list' && Object.keys(list).map(i=>{
                              return (
                                data[key][i] && (
                                  <li key={i} className={styles['item']}>
                                    <span className={styles.label}>{list[i]}</span>
                                    {
                                      i==='person_url' 
                                        ?  (
                                          <img
                                            className={styles['avatar']}
                                            src={data[key][i] || personEmpty}
                                            alt='照片'
                                            onError={(e) => {
                                                e.target.onerror = null;
                                                e.target.src = personEmpty;
                                            }}
                                            />
                                          )
                                        : 
                                        <span>{data[key][i]}</span>                                
                                    }
                                  </li>
                                )
                              )
                            })
                          }

                          {
                            type==='table' && (
                              <Table
                                rowKey='id'
                                columns={tableColumns}
                                dataSource={data[list]}
                                pagination={false}
                              />
                            )
                          }
                          {
                            type==='step' && (
                              data[list].length > 0 
                              ?
                              data[list].map((item, index) => {       
                                return (
                                  <li
                                    className={styles['step-item']}
                                    key={item.changed_at + index}
                                  >
                                    <div className={styles['step-info']}>
                                      <span
                                        className={styles.state}
                                        style={{
                                          color: getEnumValue(attendanceResEnum, item.result, 'color'),
                                          backgroundColor: getEnumValue(attendanceResEnum, item.result, 'bgc'),
                                        }}
                                      >
                                        {getEnumValue(attendanceResEnum, item.result)}
                                      </span>

                                      <span>
                                        {getEnumValue(modeEnum, item.mode)}
                                        {
                                          item.mode === modeType.MANUAL && (
                                            <Popover
                                              title='原因备注'
                                              content={item.comment}
                                              overlayClassName='attendance-details-popup-popover'
                                            >
                                              <Icon
                                                type='hz-tips'
                                                style={{
                                                  marginLeft: 5,
                                                }}
                                              />
                                            </Popover>
                                          )
                                        }
                                      </span>
                                      <span>{item.changed_at}</span>
                                    </div>
                                    {index<data[list].length-1 ? <Icon type='hz-next-arrow' /> : ''}
                                  </li>
                                )                   
                              })
                              :
                              <li className={styles['not-edit']}>无更改记录</li>
                            )
                          }
                        </ul>
                        {
                          (key === 'task_info' && (data.result !== attendanceResType.NORMAL && data.result !==attendanceResType.PASS) ) && (
                            <div className={styles['not-attendance-record']}>无考勤记录</div>
                          )
                        }

                      </div>
                    )

                  })
                  : <div className={styles['not-attendance']}>考勤记录不存在</div>
                }
              </div>
            )
          }
        </ReactScrollbar>
      </Modal>
    )
  }
}