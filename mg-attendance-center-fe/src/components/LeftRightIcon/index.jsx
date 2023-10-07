/*
 * @Author: xiedan WX042
 * @since: 2021-01-20 20:53:14
 * @lastTime: 2021-01-20 21:00:55
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\components\LeftRightIcon\index.jsx
 * @Description: 弹框左右切换按钮组合
 */

import React from 'react';
import { Icon } from 'antd';

const iconDiv = {
    position: 'absolute',
    width: 40,
    height: 100,
    zIndex: 9999,
    lineHeight: '120px',
    top: '220px',
}

export default (props) => {
    const { styleLeft = {}, styleRight = {}, dataLen = 0, index = 0, changeDex = () => {} } = props;
    return (
        <>
            <div
                style={{
                    ...iconDiv,
                    left: -60,
                    display: dataLen < 1 ? 'none' : 'block',
                    ...styleLeft,
                }}
            >
                <Icon
                    onClick={() => {
                        if (index) {
                            changeDex('left');
                        }
                    }}
                    type="left-circle"
                    style={{
                        cursor: index ? 'pointer' : 'not-allowed',
                        fontSize: 40,
                        color: index ? '#ffffff' : '#cccccc',
                    }}
                />
            </div>
            <div
                style={{
                    ...iconDiv,
                    right: -60,
                    display: dataLen < 1 ? 'none' : 'block',
                    ...styleRight,
                }}
            >
                <Icon
                    onClick={() => {
                        if (index < dataLen - 1) {
                            changeDex('right');
                        }
                    }}
                    type="right-circle"
                    style={{
                        cursor: index < dataLen - 1 ? 'pointer' : 'not-allowed',
                        fontSize: 40,
                        color: index < dataLen - 1 ? '#ffffff' : '#cccccc',
                    }}
                />
            </div>
        </>
    )
}