/*
 * @Author: xiedan WX042
 * @since: 2020-09-02 10:34:50
 * @lastTime: 2020-09-27 10:01:27
 * @FilePath: \biz-scene-coree:\hz\biz-scene-attendance-web\src\pages\statistics\NoSence\components\Record\index.jsx
 * @Description: 考勤看板---右侧的推送列表
 */
import React from 'react'
import { Progress } from "antd";
import moment from 'moment';
import ClassNames from 'classnames';
import ReactScrollbar from 'react-custom-scrollbars';
import PropsType from 'prop-types';
import styles from './record.less';

const Record = (props) => {
    const { majorData, seconData } = props;

    return (
        <div className={styles.record}>
            <div className={styles.title}>
                <span className={styles.block} />
                <p className={styles.text}>考勤记录</p>
            </div>
            {majorData.length ? majorData.map((item, index) => (
                <div className={styles.major} key={index}>
                    <div className={styles.majorLeft}>
                        <img alt="" style={{ backgroundImage: `url('${item.snapshot_url}')` }} />
                        <img alt="" style={{ backgroundImage: `url('${item.base_url}')` }} />
                        {
                            !item.similarity ? null : (
                                <div className={styles.progress}>
                                    <span>相似度:</span>
                                    <Progress percent={item.similarity || 0} />
                                </div>
                            )
                        }
                    </div>
                    <ul className={styles.majorRight}>
                        <div className={ClassNames(styles.firstName, 'ellipsis')} title={item.person_name}>{item.person_name}</div>
                        <li className={styles.time}>{moment(item.time).format('YYYY-MM-DD HH:mm:ss')}</li>
                        <li className={styles.number}>
                            <div>编号：</div>
                            <div className='ellipsis' title={item.person_no}>{item.person_no}</div>
                        </li>
                        <li>
                            <div>部门：</div>
                            <div className='ellipsis' title={item.department_name}>{item.department_name}</div>
                        </li>
                        <li>
                            <div>考勤点：</div>
                            <div className='ellipsis' title={item.site_name}>{item.site_name}</div>
                        </li>
                        <li>
                            <div>结果：</div>
                            <div className='ellipsis' title={item.result}>{item.result}</div>
                        </li>
                    </ul>
                </div>
            )
            ) : null}
            <div className={styles.items}>
                <ReactScrollbar renderTrackHorizontal={() => <div />}>
                    {seconData.length > 0 ? seconData.map((item, index) => (
                        <div className={ClassNames(styles.item, styles.itemLeft)} key={index}>
                            <div className={styles.itemPic}>
                                <img src={item.snapshot_url} alt="抓拍图片" />
                                <img src={item.base_url} alt="人脸底图" />
                            </div>
                            {
                                !item.similarity ? <div /> : (
                                    <div className={styles.progress} style={{ width: '110%' }}>
                                        <span>相似度:</span>
                                        <Progress percent={item.similarity || 0} />
                                    </div>
                                )
                            }
                            <div className={styles.bottom}>
                                <div className='ellipsis' title={item.person_name}>{item.person_name}</div>
                                <div>{moment(item.time).format('YYYY-MM-DD HH:mm:ss')}</div>
                            </div>
                        </div>
                    )) : null}
                </ReactScrollbar>
                {majorData.length === 0 && seconData.length === 0 && (
                    <div className={styles.noData}>
                        <div className={styles.noDataImg} />
                    </div>
                )}
            </div>
        </div>
    )
}

Record.propTypes = {
    majorData: PropsType.array.isRequired,  // 详细展示数据
    seconData: PropsType.array.isRequired,  // 列表展示数据
}

export default Record;
