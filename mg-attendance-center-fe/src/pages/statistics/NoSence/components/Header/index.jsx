/*
 * @Author: xiedan WX042
 * @since: 2020-09-01 19:28:01
 * @lastTime: 2020-12-14 11:14:13
 * @FilePath: \biz-scene-attendance-web\src\pages\statistics\NoSence\components\Header\index.jsx
 * @Description: 考勤看板---头部
 */
import React, { Component } from 'react';
import { message } from 'antd';
import { getBoardTitle } from '@services/nosence';
import styles from './header.less';
import { API_ERROR_CODE } from '@constants';

export default class Header extends Component {
    state = {
        boardTitle: '',
    }
    
    componentDidMount() {
        this.getBoardTitle()
    }

    /** 获取标题 */
    getBoardTitle = () => {
        getBoardTitle().then(res => {
            if(res && res.error_code === API_ERROR_CODE) {
                const data = res.data && res.data[0] && res.data[0].board_title || '';
                this.setState({ boardTitle: data })
            }
        })
        .catch(() => {
            message.error('服务器错误，稍后再试。')
        })
    }

    render() {
        const { boardTitle } = this.state;
        return (
        <React.Fragment>
            <div className={styles.headerBg}>
            <p className={styles.title}>{boardTitle || "考勤数据实时看板"}</p>
            <div className={styles.link}>
                <a href='/sceneAttendance/statistics/record' target='_blank'>考勤记录</a>
                <a href='/sceneAttendance/statistics/detail' target='_blank'>考勤明细</a>
                <a href='/sceneAttendance/statistics/report' target='_blank'>考勤报表</a>
            </div>
            </div>
        </React.Fragment>
        )
    }
}  