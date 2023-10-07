/*
 * @Author: xiedan WX042
 * @since: 2020-11-02 17:40:01
 * @lastTime: 2021-02-04 09:09:13
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\components\AttendDetailModal\check.jsx
 * @Description: 考勤明细详情弹框   抽查
 */
import React, { useEffect, useState } from 'react';
import { Tooltip, Card, Modal, Button, Spin } from 'antd';
import moment from 'moment';
import classNames from 'classnames';
import ReactScrollbar from 'react-custom-scrollbars';
import StepsGroup from '@components/StepsGroup';
import LeftRightIcon from '@components/LeftRightIcon';
import MarkStatus from './mod/mark';
import { getCheckDetail } from '@/services/detailPage';
import { API_ERROR_CODE, SCHOOL } from '@constants';
import personEmpty from '@hz-design/base/public/person-fail-empty.svg';
import styles from './index.less';
import { dataLabel } from './utils/utils';

const format = 'YYYY-MM-DD HH:mm:ss';

const AttendDetailModalCheck = (props) => {
    const { visible, onCancel, dataArr, currentIndex, userRole } = props;
    const [showIndex, setIndex] = useState(0); // 显示数据的索引值
    const [hasLeave, setHasLeave] = useState(false);  // 是否有请假信息
    const [hasPractice, setHasPractice] = useState(false);  // 是否有实习信息
    const [detailObj, setDetailObj] = useState({});  // 构造的详情数据
    const [loading, setLoading] = useState(false);
    const [status, setStatus] = useState(''); // 右上角的标签

    useEffect(() => {
        if(dataArr && dataArr.length > 0) {
            setIndex(currentIndex);
            getDetail(dataArr[currentIndex]);
        }
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [])

    // 获取详情
    const getDetail = (detail) => {
        setLoading(true);
        const params = {
            // id: 1,
            id: detail.id,
        }
        getCheckDetail(params).then(res => {
            if (res && res.error_code === API_ERROR_CODE) {
                const { person_info = {}, attendance_info = {}, leave_info, practice_info, recent_records = [], status_changed_record = [] } = res.data;
                // 基本信息
                person_info.admission_time = person_info.admission_time && moment(person_info.admission_time).format('YYYY-MM-DD') || '';

                // 考勤信息  校级的只有考勤日期
                attendance_info.time = attendance_info.time && moment(attendance_info.time).format('YYYY-MM-DD') || '';

                // 请假信息
                if(leave_info) {
                    leave_info.time = (leave_info.start_time || leave_info.end_time)
                        ? `${leave_info.start_time && moment(leave_info.start_time).format('YYYY-MM-DD') || '--'}
                            ~
                            ${leave_info.end_time && moment(leave_info.end_time).format('YYYY-MM-DD') || '--'}`
                        : '';
                }

                // 实习信息
                if(practice_info) {
                    practice_info.time = (practice_info.start_time || practice_info.end_time)
                        ? `${practice_info.start_time && moment(practice_info.start_time).format('YYYY-MM-DD') || '--'}
                            ~
                            ${practice_info.end_time && moment(practice_info.end_time).format('YYYY-MM-DD') || '--'}`
                        : '';
                }

                // 取最后一次修改记录的状态
                const lastChangedRecord = status_changed_record[status_changed_record.length - 1];
                const status = lastChangedRecord && lastChangedRecord.status || ''; 
                setStatus(status);

                setHasLeave(!!leave_info);
                setHasPractice(!!practice_info);
                setDetailObj({
                    person_info,
                    attendance_info,
                    leave_info,
                    practice_info,
                    recent_records,
                    status_changed_record,
                    personUrl: person_info.person_url,
                });
            }
        }).finally(() => {
            setLoading(false);
        })
    }

    // 渲染文本列表 type: imgandlist--图片和列表模式, list--仅有列表
    const renderTextList = (type, label, data = {}, mark) => {
        let labelKey = Object.keys(label);
        if(mark === 'attend' && userRole === SCHOOL) {
            // 校级考勤信息只有 日期
            labelKey = ['time'];
        }

        if(type === 'list') {
            return labelKey.map((item, index) => {
                return (
                    <li key={index}>
                        <span className={styles.itemLabel}>{label[item]}</span>
                        <Tooltip title={data[item]}>
                            <div className='ellipsis'>
                                {data[item] || '--'}
                            </div>
                        </Tooltip>
                    </li>
                )
            })
        } else {
            return labelKey.map((item, index) => {
                // 最后一个数据的显示样式
                const flag = index % 2 === 0 && index === labelKey.length - 1;
                return (
                    <>
                        <div
                            key={index}
                            className={`hz-col hz-col-5 ${styles.itemLabel}`}
                            style={{
                                borderTop: index > 1 && 'none', // 去掉多余的boder样式
                                borderLeft: index % 2 === 1 && 'none',
                            }}
                        >
                            {label[item]}
                        </div>
                        <div
                            key={1-`index`}
                            className={classNames({
                                'hz-col': true,
                                'hz-col-7': !flag,
                                'hz-col-19': flag,
                            })}
                            style={{
                                borderTop: index > 1 && 'none',
                                borderLeft: 'none',
                            }}
                        >
                            <Tooltip title={data[item]}>
                                <div className='ellipsis'>
                                    {data[item] || '--'}
                                </div>
                            </Tooltip>
                        </div>
                    </>
                )
            })
        }
    }

    // 渲染图片列表
    const renderImgList = (data = []) => {
        return data.map((item, index) => {
            return (
                <div className={styles.imgCard} key={index}>
                    <Card>
                        <div className="hz-card-meta">
                            <div className="hz-card-meta-fl" style={{
                                width: 70,
                            }}>
                                <div className="hz-rate-image-vertical">
                                    <img
                                        alt='照片'
                                        src={item.attendance_identification || personEmpty}
                                        onError={(e) => {
                                            e.target.onerror = null;
                                            e.target.src = personEmpty;
                                        }}
                                    />
                                </div>
                            </div>
                            <div className="hz-card-meta-main">
                                <p>{item.pass_time && moment(item.pass_time).format(format) || '--'}</p>
                                <Tooltip title={item.site_name}>
                                    <p className='ellipsis'>{item.site_name || '--'}</p>
                                </Tooltip>
                            </div>
                        </div>
                    </Card>
                </div>
            )
        })
    }

    // 左右点击
    const changeDex = mark => {
        const newDex = mark === 'left' ? showIndex - 1 : showIndex + 1;
        setIndex(newDex);
        getDetail(dataArr[newDex]);
    };

    return (
        <Modal
            visible={visible}
            title="考勤明细"
            width={700}
            bodyStyle={{
                position: 'relative',
                height: 600,
            }}
            centered
            destroyOnClose
            maskClosable={false}
            onCancel={onCancel}
            footer={[
              <Button onClick={onCancel}>
                取消
              </Button>,
            ]}
        >
            <LeftRightIcon
                dataLen={dataArr.length}
                index={showIndex}
                changeDex={changeDex}
            />

            <ReactScrollbar renderTrackHorizontal={() => <div />}>
                <Spin spinning={loading}>
                    <div className={styles.detailWapper} id='detail'>
                        {
                            status && (
                                <MarkStatus status={status} />
                            )
                        }
                        {/* 基本信息 */}
                        <div className={styles.item}>
                            <div className="hz-label-sm">学生信息</div>
                            <div className={styles.itemContent}>
                                <div className={styles.itemImg}>
                                    <img
                                        alt='照片'
                                        src={detailObj.personUrl || personEmpty}
                                        onError={(e) => {
                                            e.target.onerror = null;
                                            e.target.src = personEmpty;
                                        }}
                                        style={{height: '100%'}}
                                    />
                                </div>
                                <div className={classNames({
                                    'hz-layout-grid': true,
                                    [`${styles.itemList}`]: true,
                                    [`${styles.itemTable}`]: true,
                                    })}
                                >
                                    <div className="hz-row" style={{lineHeight: '30px'}}>
                                        {renderTextList('imgandlist', dataLabel.person_info, detailObj.person_info)}
                                    </div>
                                </div>
                            </div>
                        </div>
                        {/* 抓拍记录 */}
                        {
                            detailObj.recent_records && detailObj.recent_records.length > 0 && (
                                <div>
                                    <div className="hz-label-sm">最近抓拍记录</div>
                                    <div className={styles.imgList}>
                                        { renderImgList(detailObj.recent_records) }
                                    </div>
                                </div>
                            )
                        }
                        {/* 请假信息 */}
                        {
                            hasLeave && (
                                <div className={styles.item}>
                                    <div className="hz-label-sm">请假信息</div>
                                    <ul style={{marginTop: '10px'}}>
                                        {renderTextList('list', dataLabel.leave_info, detailObj.leave_info)}
                                    </ul>
                                </div>
                            )
                        }
                        {/* 实习信息 */}
                        {
                            hasPractice && (
                                <div className={styles.item}>
                                    <div className="hz-label-sm">实习信息</div>
                                    <ul style={{marginTop: '10px'}}>
                                        {renderTextList('list', dataLabel.practice_info, detailObj.practice_info)}
                                    </ul>
                                </div>
                            )
                        }
                        {/* 考勤信息 */}
                        <div className={styles.item}>
                            <div className="hz-label-sm">考勤信息</div>
                            <ul style={{marginTop: '10px'}}>
                                {renderTextList('list', dataLabel.attendance_info, detailObj.attendance_info, 'attend')}
                                {/* 缺勤、请假、实习状态显示无考勤记录 */}
                                {
                                    (!status || ['缺勤', '请假', '实习'].includes(status)) && (
                                        <li key='a'>
                                            <span className={styles.itemLabel}>考勤记录</span>
                                            <div style={{color: '#FC2344'}}>无考勤记录</div>
                                        </li>
                                    )
                                }
                            </ul>
                        </div>
                        {/* 考勤状态更改记录 */}
                        <div className={styles.item}>
                            <div className="hz-label-sm">考勤状态更改记录</div>
                            <StepsGroup
                                data={detailObj.status_changed_record}
                                type='attend_detail'
                            />
                        </div>
                    </div>
                </Spin>
            </ReactScrollbar>
        </Modal>
    )
}

AttendDetailModalCheck.defaultProps ={
    visible: false,
    onCancel: () => {},
    dataArr: [], // 外面的列表的所有的数据，方便左右翻页
    currentIndex: 0, // 列表点击的索引
}

export default AttendDetailModalCheck;