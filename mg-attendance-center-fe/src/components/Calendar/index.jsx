/*
 * @Author: xiedan WX042
 * @since: 2020-12-08 15:13:03
 * @lastTime: 2021-02-01 11:31:33
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\components\Calendar\index.jsx
 * @Description: 日历展示统计
 */

 import React, { useEffect, useState } from 'react';
 import moment from 'moment';
 import classNames from 'classnames';
 import { STATISTICS_TYPE_MAP } from '@constants';
 import styles from './index.less';
 /**
  * @description: description
  * @param {*} captionType  说明内容的展示类型
  * @param {*} align
  * @param {*} dataSource
  * @param {*} simple  是否简单模式--只有缺勤
  * @return {*}
  */
 export default ({ time = '', captionType, align = 'left', dataSource = [], simple = false }) => {
     const [dates, setDates] = useState([]);
     const [emptyWeek, setEmptyWeek] = useState([]);  // 当月的第一天是星期几，方便前面空格

     useEffect(() => {
        // 1、获取当前月共有几天
        const dates = moment(time).daysInMonth();
        setDates(Array.from({length: dates}));
        // 2、获取当前月份1号
        const startDay = moment(time).startOf('month').format("YYYY-MM-DD");
        // 3、获取第一天是星期几，构造前面要空格的数组  0--星期天
        const empty = Number(moment(startDay).format('d'));
        setEmptyWeek(Array.from({length: (empty || 7) - 1}));

     }, [time])

     return (
         <div className={styles.CalendarWapper}>
            <div className={styles.datesWapper} style={{margin: align === 'center' ? '0 auto' : ''}}>
                <div className={styles.time}>
                    {moment(time).format("YYYY年MM月")}
                </div>
                <div className={styles.datesList}>
                    {
                        ['周一', '周二', '周三', '周四', '周五', '周六', '周日'].map((item, index) => {
                            return (
                                <div className={styles.title} key={item}>{item}</div>
                            )
                        })
                    }
                </div>
                <div className={styles.datesList}>
                    {/* 空格 */}
                    {
                        emptyWeek.map((item, index) => {
                            return (
                                <div className={styles.date} key={`0-${index}`} />
                            )
                        })
                    }
                    {/* 日期 */}
                    {
                        dates.map((item, index) => {
                            if(simple) {
                                // 抽查推荐
                                return (
                                    <div
                                        key={index}
                                        className={classNames({
                                            [styles.date]: true,
                                            [styles.red]: dataSource.includes(index + 1), // 缺勤
                                        })}
                                        title='缺勤'
                                    >
                                        {index + 1}
                                    </div>
                                )
                            }
                            // 找出与日历每天对应的数据，date的日期位和 index+1 比较
                            const d = dataSource.filter(i => (index + 1) === Number(i.date.substr(8,2))) || [];
                            const da = d[0] || {};
                            return (
                                <div
                                    key={index}
                                    className={classNames({
                                        [styles.date]: true,
                                        [styles.blue]: STATISTICS_TYPE_MAP[da.type] === '正常', // 正常
                                        [styles.red]: STATISTICS_TYPE_MAP[da.type] === '缺勤', // 缺勤
                                        [styles.green]: STATISTICS_TYPE_MAP[da.type] === '请假', // 请假
                                        [styles.yellow]: STATISTICS_TYPE_MAP[da.type] === '实习', // 实习
                                    })}
                                    title={da.result}
                                >
                                    {index + 1}
                                </div>
                            )
                        })
                    }
                </div>
             </div>
             {
                 captionType === 'none' ? null : captionType === 'text' ? (
                    <div className={styles.datesCaption} style={{margin: align === 'center' ? '10px auto' : '10px 0'}}>
                        *标红日期表示考勤缺勤
                    </div>
                 ) : (
                    <div className={styles.datesList} style={{margin: align === 'center' ? '0 auto' : ''}}>
                        <div className={styles.colorItem}><span className={styles.blue} /> 正常</div>
                        <div className={styles.colorItem}><span className={styles.red} /> 缺勤</div>
                        <div className={styles.colorItem}><span className={styles.green} /> 请假</div>
                        <div className={styles.colorItem}><span className={styles.yellow} /> 实习</div>
                    </div>
                 )
             }
         </div>
     )
 }