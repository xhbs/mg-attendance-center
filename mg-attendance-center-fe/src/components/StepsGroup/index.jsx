/*
 * @Author: xiedan WX042
 * @since: 2020-11-02 17:40:01
 * @lastTime: 2021-01-05 16:30:10
 * @文件相对于项目的路径: \biz-scene-attendance-web\src\components\StepsGroup\index.jsx
 * @Description: 流程展示组件
 */
import React, {useState} from 'react';
import { Steps, Icon } from 'antd';
import moment from "moment";
import emptyImg from "@hz-design/base/public/simple-empty.svg";
import { DATE_TYPE, ATTEND_DETAIL_STATUS } from "@constants";
import PopConfirm from './mod/PopConfirm';
import styles from './index.less';
import { useEffect } from 'react';

const { Step } = Steps;

const StepsGroup = ({ data = [], current = 1, status = 'process', style = {}, type = '' }) => {
    const [popVisArr, setPopVis] = useState([]);  // 气泡确认框集合

    useEffect(() => {
        if(data && data.length > 0) {
            setPopVis(Array.from({length: data.length}, v => false))
        }
    }, [data])

    // 内容
    const renderDescription = () => {
        // 考勤明细有小气泡框
        if(type === 'attend_detail') {
            return data.map((item, index) => {
                const arr = ATTEND_DETAIL_STATUS.filter(i => i.name === item.status) || [];
                const obj = arr[0] || {};
                return (
                    <Step
                        icon={
                            <span
                                style={{
                                    fontSize: '14px',
                                    color: obj.color || '#ccc',
                                    height: 32,
                                    width: 32,
                                    lineHeight: '32px',
                                    display: 'inline-block',
                                    border: `1px solid ${obj.color || '#ccc'}`,
                                    borderRadius: '100%',
                                }}
                            >
                                {item.status && item.status.substr(0,2) || '--'}
                            </span>
                        }
                        status='finish'
                        description={
                            <div>
                                <div style={{color: '#000'}}>{item.status || '--'}</div>
                                <div>
                                    {item.type || '--'}
                                    {/* 变更原因 */}
                                    {
                                        ((item.status && item.misreport_info) || (item.type && item.reason)) && (
                                            <PopConfirm
                                                visible={popVisArr[index]}
                                                onCancel={() => {
                                                    const arr = [...popVisArr];
                                                    arr[index] = false;
                                                    setPopVis(arr)
                                                }}
                                                getPopupContainer={() => document.getElementById('detail')}
                                                data={item.status === '误报' ? item.misreport_info : (item.type === '手动变更' ? item.reason : '')}
                                                type={item.status === '误报' ? '2' : '1'}
                                            >
                                                <Icon
                                                    type="hz-file-exception"
                                                    theme="outlined"
                                                    style={{color: '#ffb000', marginLeft: '6px'}}
                                                    onClick={() => {
                                                        const arr = [...popVisArr];
                                                        arr[index] = true;
                                                        setPopVis(arr)
                                                    }}
                                                />
                                            </PopConfirm>
                                        )
                                    }
                                </div>
                                <div>{item.time && moment(item.time).format(DATE_TYPE) || '--'}</div>
                            </div>}
                    />
                )
            })
        } else {
            return data.map(item => {
                return (
                    <Step
                        title={
                            <span style={{color: (!item.completed && !item.current) ? '#999' : '#000'}}>
                                {item.name || '--'}
                            </span>
                        }
                        status={item.result === 1 ? 'finish' : item.result === 2 ? 'error' : item.current ? 'process' : 'wait'}
                        description={
                            <div>
                                <div>{item.assignee_user_name || '--'}</div>
                                <div>{item.assignee_role_name || '--'}</div>
                                {
                                    item.completed && (
                                        <div>{item.completed_at && moment(item.completed_at).format(DATE_TYPE) || '--'}</div>
                                    )
                                }
                            </div>}
                    />
                )
            })
        }
    }

    return (
        <div className={styles.stepsWapper} style={{marginTop: 20, overflowX: 'scroll', ...style}}>
            {
                !data || (data && data.length < 1) ? (
                    <div style={{textAlign: 'center', margin: '20px', color: '#ccc'}}>
                        <img alt='' src={emptyImg} />
                        <p>暂无记录</p>
                    </div>
                ) : (
                    <div style={{width: (150 + 36) * data.length, marginLeft: '-25px'}}>
                        <Steps current={current} status={status} labelPlacement='vertical'>
                            { renderDescription() }
                        </Steps>
                    </div>
                )
            }
        </div>
    )
}

export default StepsGroup;