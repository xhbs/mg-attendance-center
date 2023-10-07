/*
 * @Author: xiedan WX042
 * @since: 2020-11-02 17:40:01
 * @lastTime: 2021-10-09 10:20:15
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\components\AppealDetail\index.jsx
 * @Description: 抽查申诉详情弹框
 */
import React, { useEffect, useState } from 'react';
import { Tooltip, Modal, Button, Icon, Spin } from 'antd';
import moment from 'moment';
import classNames from 'classnames';
import ReactScrollbar from 'react-custom-scrollbars';
import { HzForm } from '@hz-components/biz-scene-components';
import StepsGroup from '@components/StepsGroup';
import { getAppealDetail } from '@/services/appeal';
import { API_ERROR_CODE, DATE_TYPE_DAY, PROVINCE, CITY, COUNTY, LEAVE_DEAL_STATUS, APPEAL_STATUS, SCHOOL_SUB_TYPE } from '@constants';
import { dealFields } from "@utils/commonFields";
import styles from './index.less';
import { SCHOOL } from '../../constants';
// import { data } from './data';

const AppealDetail = (props) => {
    const { visible, onCancel, detail, userRole, onDeal } = props;
    const [baseInfo, setBaseInfo] = useState({});  // 基本信息
    const [persons, setPersons] = useState([]);  // 人员列表

    const [loading, setLoading] = useState(false);
	const [HzFormRef, setHzFormRef] = useState(null);

    useEffect(() => {
        if(detail && detail.id) {
            getDetail();
        }
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [detail])

    // 获取详情
    const getDetail = () => {
        setLoading(true);
        getAppealDetail(detail.id).then(res => {
            if (res && res.error_code === API_ERROR_CODE) {
                const { data = {} } = res;
                // 省市县要取不同的字段
                if(userRole !== SCHOOL) {
                    // const filed = STATUS_FILED_MAP[userRole];
                    const target = LEAVE_DEAL_STATUS.filter(i => i.code === data.status) || [];
                    // data.status = data[filed];
                    data.status_name = target[0] && target[0].name;
                } else {
                    const target = APPEAL_STATUS.filter(i => i.code === data.status) || [];
                    data.status_name = target[0] && target[0].name;
                }
                // 取最后一条审核完成记录的备注
                const arr = data.processes && data.processes.filter(i => i.completed) || [];
                data.result_comment = arr[arr.length - 1] && arr[arr.length - 1].comment || '';
                // 基本信息
                setBaseInfo(data)
                // 人员列表
                setPersons(data.persons);
            }
        }).finally(() => {
            setLoading(false);
        })
    }

    const renderModalFooter = () => {
        let footer = [
            <Button onClick={onCancel}>
              取消
            </Button>,
        ];
        // 10.8版本修改，满足条件：1、省市县角色； 2、该记录为当前登录角色的下一级的申请； 3、未审核状态
        if([CITY, COUNTY, PROVINCE].includes(userRole) && SCHOOL_SUB_TYPE[baseInfo.school_parent_sub_type] === userRole && baseInfo.status === 2) {
            // 满足条件 1、省级管理员；2、非直属学校；2、状态为未处理时；直接更新考勤状态即可
            // if(userRole === PROVINCE && !baseInfo.is_province_school) {
            //     /* 更新考勤  省级对对抽查申诉审批通过的考勤内容进行考勤状态更改 */
            //     footer = [(
            //         <Button type='primary' onClick={()=>{
            //             onDeal({ status: '1' });
            //         }}>
            //          更新考勤
            //         </Button>
            //         ),
            //         <Button onClick={onCancel}>
            //           取消
            //         </Button>,
            //     ]
            // } else {
                footer = [(
                    <Button type='primary' onClick={()=>{
                        HzFormRef.submit().then(values => {
                            onDeal(values);
                        })
                    }}>
                       确认
                    </Button>
                    ),
                    <Button onClick={onCancel}>
                      取消
                    </Button>,
                ]
            // }
        }
        return footer;
    }

    // 渲染mark
    const renderMark = () => {
        let map = LEAVE_DEAL_STATUS;
        if(userRole === SCHOOL) {
            map = APPEAL_STATUS;
        }
        const arr = map.filter(i => i.code === baseInfo.status) || [];
        const target = arr[0] || {};
        return (

            <div
                className={styles.mark}
                style={{
                    color: target.color || '',
                    border: `solid 2px ${target.color}`,
                    display: !!target.name ? 'block' : 'none',
                }}
            >
                {target.name || ''}
            </div>
        )
    }

    // 触发下载事件
    const courseDownload = (url, filename) => {
        getBlob(url, (blob) => {
            saveAs(blob, filename);
        })
    }

    // 转换成blob
    const getBlob = (url, cb) => {
        const xhr = new XMLHttpRequest();
        xhr.open('GET', url, true);
        xhr.responseType = 'blob';
        xhr.onload = function () {
            if(xhr.status === 200) {
                cb(xhr.response);
            }
        }
        xhr.send();
    }

    // 下载文件
    const saveAs = (blob, filename) => {
        if(window.navigator.msSaveOrOpenBlob) {
            navigator.msSaveBlob(blob, filename);
        } else {
            const link = document.createElement('a');
            const body = document.querySelector('body');
            link.href = window.URL.createObjectURL(blob);
            link.download = filename;

            // fix firefox
            link.style.display = 'none';
            body.appendChild(link);

            link.click();
            body.removeChild(link);

            window.URL.revokeObjectURL(link.href);
        }
    }

    return (
        <Modal
            visible={visible}
            title="申诉明细"
            width={900}
            bodyStyle={{
                position: 'relative',
                height: 580,
            }}
            centered
            destroyOnClose
            maskClosable={false}
            onCancel={onCancel}
            footer={renderModalFooter()}
        >
            <ReactScrollbar renderTrackHorizontal={() => <div />}>
                <Spin spinning={loading}>
                    <div className={styles.detailWapper} id='detail'>
                        {/* mark标签 */}
                        {
                            baseInfo.status &&  renderMark()
                        }
                        {/* 申诉信息 */}
                        <div className={styles.item}>
                            <div className="hz-label">申诉信息</div>
                            <div className={styles.itemContent}>
                                <div className={classNames({
                                    'hz-layout-grid': true,
                                    })}
                                >
                                    <div className="hz-row" style={{lineHeight: '30px'}}>
                                        <ul style={{marginTop: '10px'}}>
                                            <li>
                                                <span className={styles.itemLabel}>申诉标题</span>
                                                <Tooltip title={baseInfo.title}>
                                                    <div className='ellipsis'>
                                                        {baseInfo.title || '--'}
                                                    </div>
                                                </Tooltip>
                                            </li>
                                            <li>
                                                <span className={styles.itemLabel}>申诉内容</span>
                                                <Tooltip title={baseInfo.content}>
                                                    <div className='ellipsis'>
                                                        {baseInfo.content || '--'}
                                                    </div>
                                                </Tooltip>
                                            </li>
                                            <li>
                                                <span className={styles.itemLabel}>附件文件</span>
                                                <Tooltip title={baseInfo.files && baseInfo.files[0] && baseInfo.files[0].file_name}>
                                                    {/* bugfix: 修改a标签下载的文件和显示的名称不一致问题 */}
                                                    <a
                                                        onClick={() => courseDownload(
                                                            baseInfo.files && baseInfo.files[0] && baseInfo.files[0].file_path,
                                                            baseInfo.files && baseInfo.files[0] && baseInfo.files[0].file_name
                                                        )}
                                                    >
                                                        <Icon type="file-word" theme='filled' style={{color: '#4C98FC', fontSize: '16px', verticalAlign: 'center'}} />
                                                        <span style={{margin: '0 15px 0 5px'}}>{baseInfo.files && baseInfo.files[0] && baseInfo.files[0].file_name || '--'}</span>
                                                        <Icon type="hz-import" theme="outlined" style={{color: '#5584ff', cursor: 'pointer'}} />
                                                    </a>
                                                </Tooltip>
                                            </li>
                                            <li>
                                                <span className={styles.itemLabel}>申诉学生名单</span>
                                                <p style={{width: '100%'}}>
                                                  <div>共计<span style={{color: '#5584ff', margin: '0 5px'}}>{persons && persons.length}</span>人</div>
                                                  <div className={styles.box}>
                                                      {
                                                          persons && persons.map((item, index) => {
                                                              return (
                                                                  <span key={index}>{item.person_name}（{item.person_no}）</span>
                                                              )
                                                          })
                                                      }
                                                  </div>
                                                </p>
                                            </li>
                                            <li>
                                                <span className={styles.itemLabel}>考勤时段</span>
                                                <Tooltip title={`
                                                    ${baseInfo.start_time && moment(baseInfo.start_time).format(DATE_TYPE_DAY)}
                                                    ~
                                                    ${baseInfo.end_time && moment(baseInfo.end_time).format(DATE_TYPE_DAY)}
                                                `}>
                                                    <div className='ellipsis'>
                                                        {
                                                            (baseInfo.start_time || baseInfo.end_time) ?
                                                            `${baseInfo.start_time && moment(baseInfo.start_time).format(DATE_TYPE_DAY)}
                                                            ~
                                                            ${baseInfo.end_time && moment(baseInfo.end_time).format(DATE_TYPE_DAY)}`
                                                            : '--'
                                                        }
                                                    </div>
                                                </Tooltip>
                                            </li>
                                            <li>
                                                <span className={styles.itemLabel}>申诉组织</span>
                                                <Tooltip title={baseInfo.org_path_name}>
                                                    <div className='ellipsis'>
                                                        {baseInfo.org_path_name || '--'}
                                                    </div>
                                                </Tooltip>
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>
                        {/* 申诉流程 */}
                        <div className={styles.item}>
                            <div className="hz-label">申诉流程</div>
                            <StepsGroup data={baseInfo.processes} />
                        </div>
                        {/* 申诉结果 状态为 同意 或者 拒绝 的时候有申诉结果 */}
                        {
                            [3, 4].includes(baseInfo.status) && (
                                <div className={styles.item}>
                                    <div className="hz-label">申诉结果</div>
                                    <ul style={{marginTop: '10px'}}>
                                        <li>
                                            <span className={styles.itemLabel}>申诉结果</span>
                                            <div className='ellipsis'>
                                                { baseInfo.status_name || '--'}
                                            </div>
                                        </li>
                                        <li>
                                            <span className={styles.itemLabel}>原因备注</span>
                                            <Tooltip title={baseInfo.result_comment}>
                                                <div className='ellipsis'>
                                                    {baseInfo.result_comment || '--'}
                                                </div>
                                            </Tooltip>
                                        </li>
                                    </ul>
                                </div>
                            )
                        }
                        {/* 满足条件 1、省级 直属学校 且未处理状态；2、县市级 且未处理状态； */}
                        {/* 申诉处理  省级对直属学校、市级对本级组织下的学校、县级对本级组织下的学校 的 未处理状态 才显示 */}

                        {/* 10.8版本修改为：只能审核 下一级的 未处理的 申请 */}
                        {
                            ([PROVINCE, CITY, COUNTY].includes(userRole) && SCHOOL_SUB_TYPE[baseInfo.school_parent_sub_type] === userRole && baseInfo.status === 2) ? (
                                <div className={styles.item} >
                                    <div className="hz-label">申诉处理</div>
                                    <div style={{margin: '10px 0 0 10px'}}>
                                        <HzForm
                                            {...{
                                                labelCol: { span: 4 },
                                                wrapperCol: { span: 18 },
                                            }}
                                            fields={dealFields()}
                                            setRef={ref => setHzFormRef(ref)}
                                        />
                                    </div>
                                </div>
                            ) : null
                        }
                    </div>
                </Spin>
            </ReactScrollbar>
        </Modal>
    )
}

AppealDetail.defaultProps ={
    visible: false,
    onCancel: () => {},
    onDeal: () => {}, // 处置
    currentIndex: 0, // 列表点击的索引
    userRole: '', // 当前用户的角色
}

export default AppealDetail;
