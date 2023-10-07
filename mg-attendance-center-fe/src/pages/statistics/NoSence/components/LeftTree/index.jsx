/*
 * @Author: xiedan WX042
 * @since: 2020-09-01 20:44:22
 * @lastTime: 2020-09-18 14:18:14
 * @FilePath: \biz-scene-coree:\hz\biz-scene-attendance-web\src\pages\statistics\NoSence\components\LeftTree\index.jsx
 * @Description: 考勤看板---左侧内容
 */
import React, { Component } from "react";
// import { Tabs, Spin } from "antd";
import TreeFlat from "./Tree";
// import PropTypes from 'prop-types';
import ClassNames from 'classnames';
import styles from './leftTree.less';


export default class LeftTree extends Component {

    constructor() {
        super(); 
        this.state = {
            isLoading: true,
            refreshKey: '0',
            tabKey: 'resource',
        };
    }

    /** 获取组织树考勤节点 */
    getAtsPoint = point => {
        const { getAtsPoint } = this.props;
        if(getAtsPoint) {
            getAtsPoint(point);
        }
    }

    render() {
        return (
            <div
                ref='videoLeft'
                className={ClassNames(styles.videoLeft, styles.resourceTree)}
            >
                <div ref="tabsBox" className={styles.treeFlat}>
                    <TreeFlat
                        ref={treeDatas => this.treeDatas = treeDatas}
                        setVideoNumber={this.props.setVideoNumber}
                        {...this.props}
                        getAtsPoint={this.getAtsPoint}
                    />
                </div>
            </div>
        );
    }
}