/*
 * @Author: xiedan WX042
 * @since: 2020-11-03 11:02:58
 * @lastTime: 2021-09-23 14:51:16
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\components\LeaveAndPracticeModal\index.jsx
 * @Description: 请假或者实习详情框，省级校级通用，不同的处理状态通用，不同的请假状态通用
 */
import React, { useEffect, useState } from 'react';
import { Modal, Button, Tooltip, Spin } from 'antd';
import classNames from 'classnames';
import moment from "moment";
import ReactScrollbar from 'react-custom-scrollbars';
// import { HzForm } from '@hz-components/biz-scene-components';
import personEmpty from '@hz-design/base/public/person-fail-empty.svg';
import imgEmpty from '@hz-design/base/public/load-empty-icon.svg';
import StepsGroup from '@components/StepsGroup';
import LeftRightIcon from '@components/LeftRightIcon';
import BigImgModal from '@components/BigImgModal';
import { getLeaveDetail } from "@services/leave";
import { getDetailById } from "@services/practiceManageServices";
import { getStaticDetail } from "@services/practiceStatistics";
import { API_ERROR_CODE, LEAVE_DEAL_STATUS, LEAVE_STATUS, PRACTICE_TYPE, LEAVE_TYPE, ATTEND_STATUS } from "@constants";
// import { DetailForm } from './utils/form';
import ImgGroup from './mod/ImgGroup';
import styles from './index.less';
import {
    dataLabel,
    // data,
} from './data';

const format = 'YYYY-MM-DD';
const formatAll = 'YYYY-MM-DD HH:mm:ss';
const statusMap = {
    '1': LEAVE_DEAL_STATUS,
    '2': LEAVE_STATUS,
    '3': PRACTICE_TYPE,
    '4': ATTEND_STATUS,
}

const LeaveAndPracticeModal = (props) => {
    const { visible, title, onCancel, type, statusType, dataArr, showIndex } = props;
    const [detail, setDetail] = useState({}); // 基本详情
    const [persons, setPersons] = useState([]); // 人员基本信息（照片、姓名、学号），实习详情有多个人员
    const [processes, setProcesses] = useState([]); // 处理流程
    const [imgIndex, setImgIndex] = useState(0); // 当前显示的人员索引值（照片、姓名、学号）
    const [index, setIndex] = useState(0); // 显示数据的索引值
    const [spinning, setSpinning] = useState(false); // 请求详情加载状态
    // 新增：证明照片查看大图
    const [bigImgVis, setBigImgVis] = useState(false);
    const [file, setFile] = useState(null); // 证明材料

    useEffect(() => {
        // console.log('showIndex====::', showIndex)
        if(dataArr && dataArr.length > 0) {
            const d = dataArr[showIndex];
            setIndex(showIndex);
            if(type === 'leave') {
                // 请假
                getLeaveDetailReq(d)
            } else {
                // 实习
                getPracticeDetailReq(d)
            }
        }
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [type, showIndex])

    // 获取详情--请假
    const getLeaveDetailReq = (data = {}) => {
        // 如果为校级，且状态为未上报，则不需要获取详情
        // if(userRole === SCHOOL && currentData && currentData.status === 1) {
        //     return false;
        // }
        setSpinning(true);
        getLeaveDetail(data.id).then(res => {
            if(res && res.error_code === API_ERROR_CODE) {
                const {data: d} = res;
                const target = LEAVE_TYPE.filter( i => i.code === d.type ) || [];
                // 取最后一条审核完成记录的备注
                const arr = d.processes && d.processes.filter(i => i.completed) || [];
                const comment = arr[arr.length - 1] && arr[arr.length - 1].comment || '';

                const status = statusType === '4' ? d.leave_state : d.status;
                const source = statusMap[statusType]; 
                const targetStatus = source.find(item => status === item.code) || {};

                const obj = {
                    leaveInfo: {
                        type: target[0] && target[0].name || '',
                        time: `${d.start_time && moment(d.start_time).format(format)}~${d.end_time && moment(d.end_time).format(format)}`,
                        reason: d.reason || '',
                        department: d.department_name || '',
                    },
                    reportInfo: {
                        org_path_name: d.org_path_name || '',
                        creator_name: d.creator_name || '',
                        creator_role_name: d.creator_role_name || '',
                        reported_at: d.reported_at && moment(d.reported_at).format(formatAll) || '',
                    },
                    dealInfo: {
                        result: status,
                        result_name: targetStatus.name || '',
                        color: targetStatus.color,
                        result_comment: comment,
                    },
                }

                setPersons([{
                    person_url: d.person_url || '',
                    person_name: d.person_name || '',
                    person_no: d.person_no || '',
                }])
                setProcesses(d.processes);
                setDetail(obj || {});
                setFile(d.files && d.files[0]);
            }
        }).finally(() => setSpinning(false));
    }

    // 获取详情--实习
    const getPracticeDetailReq = (data = {}) => {
        setSpinning(true);
        // statusType为3表示实习统计
        const fun = statusType === '3' ? getStaticDetail : getDetailById;
        fun(data.id).then(res => {
            if(res && res.error_code === API_ERROR_CODE) {
                const {data: d} = res;
                // 取最后一条审核完成记录的备注
                let comment = '';
                if(statusType !== '3') {
                    const arr = d.processes && d.processes.filter(i => i.completed) || [];
                    comment = arr[arr.length - 1] && arr[arr.length - 1].comment || '';
                }
                const status = statusType === '3' ? d.practice_status : d.status;
                const source = statusMap[statusType]; 
                const targetStatus = source.find(item => status === item.code) || {};

                const obj = {
                    practiceInfo: {
                        time: `${d.start_time && moment(d.start_time).format(format)}~${d.end_time && moment(d.end_time).format(format)}`,
                        practice_company: d.practice_company || '',
                        company_contacts: d.company_contacts || '',
                        contacts_phone: d.contacts_phone || '',
                    },
                    reportInfo: {
                        org_path_name: d.org_path_name || '',
                        creator_name: d.creator_name || '',
                        creator_role_name: d.creator_role_name || '',
                        reported_at: d.reported_at && moment(d.reported_at).format(formatAll) || '',
                    },
                    dealInfo: {
                        result: status,
                        result_name: targetStatus.name || '',
                        color: targetStatus.color,
                        result_comment: comment,
                    },
                }
                if(statusType === '3') {
                    setPersons([{
                        person_url: d.person_url || '',
                        person_name: d.person_name || '',
                        person_no: d.person_no || '',
                    }])
                } else {
                    setPersons(d.persons || [])
                }
                setProcesses(d.processes || []);
                setDetail(obj || {});
                setFile(d.files && d.files[0]);
            }
        }).finally(() => setSpinning(false));
    }

    // 渲染内容
    const renderListHtml = (valueObj = {}, keyObj) => {
        return Object.keys(keyObj).map((item, index) => {
            return (
                <li className={styles.item} key={index}>
                    <span>
                        {keyObj[item]}
                    </span>
                    <div className={styles.value}>
                        <Tooltip title={valueObj[item]}>
                            <div className='ellipsis'>
                                {valueObj[item] || '--'}
                            </div>
                        </Tooltip>
                    </div>
                </li>
            )
        })
    }

    // 渲染表格列表
    const renderList = (valueObj = {}, keyObj) => {
        // 如果是校级请假的详情，则基本信息要显示'班级'字段，反之则不显示
        if(!(type === 'leave' && statusType === '2')) {
            delete keyObj.department
        }
        return Object.keys(keyObj).map((item, index) => {
            if(!index) return null;
            return (
                <>
                    <div
                        key={index}
                        className={`hz-col hz-col-8 ${styles.itemLabel}`}
                        style={{
                            borderTop: index > 1 && 'none',
                            borderRight: 'none',
                        }}
                    >
                        {keyObj[item]}
                    </div>
                    <div
                        key={1-`index`}
                        className={classNames({
                            'hz-col': true,
                            'hz-col-16': true,
                        })}
                        style={{ borderTop: index > 1 && 'none', color: '#333' }}
                    >
                        <Tooltip title={valueObj[item]}>
                            {
                                index > 2 ? (
                                    <div className='ellipsis'>
                                        {valueObj[item] || '--'}
                                    </div>
                                ) : (
                                    <div className='ellipsis'>
                                        {persons[imgIndex] && persons[imgIndex][item] || '--'}
                                    </div>
                                )
                            }
                        </Tooltip>
                    </div>
                </>
            )
        })
    }

    // 渲染mark
    const renderMark = (data) => (
        <div
            className={styles.mark}
            style={{
                color: data.color || '',
                border: `solid 2px ${data.color}`,
                display: !!data.result_name ? 'block' : 'none',
            }}
        >
            {data.result_name || ''}
        </div>
    )

    // 左右点击
    const changeDex = mark => {
        const newDex = mark === 'left' ? index - 1 : index + 1;
        const d = dataArr[newDex];
        setIndex(newDex);

        if(type === 'leave') {
            // 请假
            getLeaveDetailReq(d);
        } else {
            // 实习
            getPracticeDetailReq(d);
        }
    };

    return (
      <>
        <Modal
            title={`${title}详情`}
            width={900}
            bodyStyle={{
                height: 580,
                position: 'relative',
            }}
            centered
            destroyOnClose
            maskClosable={false}
            visible={visible}
            onCancel={onCancel}
            footer={[
                <Button key="back" onClick={onCancel}>
                    取消
                </Button>,
            ]}
        >
            <LeftRightIcon
                dataLen={dataArr.length}
                index={index}
                changeDex={changeDex}
            />

            <ReactScrollbar renderTrackHorizontal={() => <div />}>
                <Spin spinning={spinning}>
                    <div className={styles.mainWapper}>
                    {
                      <React.Fragment>
                          <div className={styles.topContent}>
                              <div className={styles.topLeft}>
                                  <div className="hz-label">
                                      {`${title}信息`}
                                      {
                                          persons && persons.length > 1 && (
                                              <span style={{color: '#666', fontSize: '14px', marginLeft: 20, fontWeight: '400'}}>共计{persons.length}人</span>
                                          )
                                      }
                                  </div>
                                  {
                                      persons && persons.length> 1 && <ImgGroup data={persons} index={imgIndex} onSelect={(index) => { setImgIndex(index) }} />
                                  }

                                  <div className={styles.itemContent}>
                                      <div style={{width: 136}}>
                                          <div>{type === 'leave' ? dataLabel.leaveInfo.person_url : dataLabel.practiceInfo.person_url}</div>
                                          <div className={styles.itemImg}>
                                              <img
                                                  alt='照片'
                                                  src={persons[imgIndex] && persons[imgIndex].person_url || personEmpty}
                                                  onError={(e) => {
                                                      e.target.onerror = null;
                                                      e.target.src = personEmpty;
                                                  }}
                                                  style={{height: '100%'}}
                                              />
                                          </div>
                                      </div>
                                      <div className={classNames({
                                          'hz-layout-grid': true,
                                          [`${styles.itemList}`]: true,
                                          [`${styles.itemTable}`]: true,
                                          })}
                                      >
                                          <div className="hz-row" style={{lineHeight: '34px'}}>
                                              {
                                                  type === 'leave'
                                                  ? renderList(detail.leaveInfo, dataLabel.leaveInfo)
                                                  : renderList(detail.practiceInfo, dataLabel.practiceInfo)
                                              }
                                          </div>
                                      </div>
                                  </div>
                              </div>
                              <div className={styles.topRight}>
                                  <div className="hz-label">上报信息</div>
                                  <ul style={{margin: '10px 0'}}>
                                      {renderListHtml(detail.reportInfo, dataLabel.reportInfo)}
                                  </ul>
                                  {/* 证明照片 */}
                                  <div className="hz-label">{type === 'leave' ? '请假' : '实习'}证明</div>
                                  <div
                                    style={{
                                      width: 120,
                                      margin: "10px auto",
                                    }}
                                  >
                                        <div className="hz-rate-image-horizontal">
                                          <div className="rate-container">
                                            <img
                                              alt='证明照片'
                                              src={file && file.file_path || imgEmpty}
                                              onError={(e) => {
                                                  e.target.onerror = null;
                                                  e.target.src = imgEmpty;
                                              }}
                                              style={{height: '100%', width: '100%'}}
                                              onClick={() => {setBigImgVis(true)}}
                                            />
                                          </div>
                                        </div>
                                  </div>
                              </div>
                              {
                                  detail.dealInfo && (
                                      renderMark(detail.dealInfo)
                                  )
                              }
                          </div>
                          <div className={styles.middleContent}>
                              <div className="hz-label">处理信息</div>
                              <StepsGroup data={processes} />
                          </div>
                          {/* 同意 或者 拒绝 状态的审批结果(实习/请假统计不展示审批结果) */}
                          {
                              (statusType !== '3' && statusType !== '4' && detail.dealInfo &&  [3, 4].includes(detail.dealInfo.result)) && (
                                  <div className={styles.middleContent}>
                                      <div className="hz-label">审批结果</div>
                                      <ul style={{marginTop: 10}}>
                                          {renderListHtml(detail.dealInfo, dataLabel.dealInfo)}
                                      </ul>
                                  </div>
                              )
                          }
                      </React.Fragment>
                    }
                </div>
                </Spin>
            </ReactScrollbar>
        </Modal>
        {/* 查看大图 */}
        <BigImgModal visible={bigImgVis} currentPic={file && file.file_path || ''} onCancel={() => setBigImgVis(false)} />
      </>
    )
}

LeaveAndPracticeModal.defaultProps = {
    visible: false, // 是否显示
    title: '请假', // modal的标题
    onCancel: () => {}, // 点击取消
    children: null, // 自定义内容
    dataArr: [], // 当前列表数据
    showIndex: 0, // 展示数据的索引
    type: 'leave', // 类型 leave--请假  practice--实习
    statusType: '1', // 状态要展示的类型  1--处理状态  2--实习/请假状态  3--实习统计的实习状态  4--请假统计的请假状态
}

export default LeaveAndPracticeModal;