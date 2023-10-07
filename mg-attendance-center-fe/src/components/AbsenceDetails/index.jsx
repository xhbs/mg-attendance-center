/*
 * @Author: xiedan WX042
 * @since: 2020-11-02 17:40:01
 * @lastTime: 2021-02-02 17:54:36
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\components\AbsenceDetails\index.jsx
 * @Description: 抽查推荐--缺勤明细详情弹框
 */
import React, { useEffect, useState } from 'react';
import { Tooltip, Modal, Button, Spin } from 'antd';
import moment from 'moment';
import ReactScrollbar from 'react-custom-scrollbars';
import classNames from 'classnames';
import { getRecoDetail } from '@/services/recommendation';
import { API_ERROR_CODE, PROVINCE } from '@constants';
import personEmpty from '@hz-design/base/public/person-fail-empty.svg';
import Calendar from '@components/Calendar';
import LeftRightIcon from '@components/LeftRightIcon';
import { dataLabel } from './utils/utils';
import styles from './style/index.less';

const AbsenceDetails = (props) => {
    const { visible, date, onCancel, dataArr, currentIndex, captionType, statisticsInfo = {} } = props;
    const [showIndex, setIndex] = useState(0); // 显示数据的索引值
    const [detailObj, setDetailObj] = useState({});  // 构造的详情数据
    const [absenceList, setAbsenceList] = useState([]);  // 缺勤列表
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if(dataArr && dataArr.length > 0) {
            setIndex(currentIndex);
            getDetail(dataArr[currentIndex]);
        }
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [dataArr, currentIndex])

    // 获取详情
    const getDetail = (detail) => {
        setLoading(true);
        const params = {
            person_no: detail.person_no || '',
            start_time: moment(`${moment(date).startOf('month').format('YYYY-MM-DD')} 00:00:00`).valueOf(),
            end_time: moment(`${moment(date).endOf('month').format('YYYY-MM-DD')} 23:59:59`).set('millisecond', 999).valueOf(),
        }
        getRecoDetail(params).then(res => {
            if (res && res.error_code === API_ERROR_CODE) {
                const { person_info = {}, absence_list = [] } = res.data;
                setAbsenceList(absence_list);
                // 基本信息
                setDetailObj(person_info)
            }
        }).finally(() => {
            setLoading(false);
        })
    }

    // 渲染文本列表 type: imgandlist--图片和列表模式, list--仅有列表
    const renderTextList = (type, label, data = {}) => {
        let labelKey = Object.keys(label);
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
                if(item === 'admission_time') {
                    data[item] = moment(data[item]).format('YYYY-MM-DD');
                }
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

    // 左右点击
    const changeDex = mark => {
        const newDex = mark === 'left' ? showIndex - 1 : showIndex + 1;
        setIndex(newDex);
        getDetail(dataArr[newDex]);
    };

    return (
        <Modal
            visible={visible}
            title="缺勤详情"
            width={700}
            bodyStyle={{
                position: 'relative',
                height: 580,
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
                        {/* 基本信息 */}
                        <div className={styles.item}>
                            <div className="hz-label">学生信息</div>
                            <div className={styles.itemContent}>
                                <div className={styles.itemImg}>
                                    <img
                                        alt='照片'
                                        src={detailObj.person_url || personEmpty}
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
                                        {renderTextList('imgandlist', dataLabel.person_info, detailObj)}
                                    </div>
                                </div>
                            </div>
                        </div>
                        {/* 统计信息 */}
                        <div className={styles.item}>
                            <div className="hz-label">统计信息</div>
                            <ul style={{marginTop: '10px'}}>
                                {renderTextList('list', dataLabel.statistics_info, statisticsInfo)}
                            </ul>
                        </div>
                        {/* 缺勤日期 */}
                        <div className={styles.item}>
                            <div className="hz-label">
                                缺勤日期
                                <span style={{marginLeft: 30}}>
                                    {moment(date).startOf('month').format("YYYY-MM-DD")}
                                    ~
                                    {moment(date).endOf('month').format("YYYY-MM-DD")}
                                </span>
                            </div>
                            <Calendar time={moment(date).format("YYYY-MM")} captionType={captionType} simple={true} dataSource={absenceList} />
                        </div>
                    </div>
                </Spin>
            </ReactScrollbar>
        </Modal>
    )
}

AbsenceDetails.defaultProps ={
    visible: false,
    type: PROVINCE, // 省级 or 县级 or 市级
    onCancel: () => {},
    dataArr: [], // 外面的列表的所有的数据，方便左右翻页
    currentIndex: 0, // 列表点击的索引
    captionType: 'graphics', // 日历下方的说明展示方式  text--文字展示  graphics--图形和文字
}

export default AbsenceDetails;