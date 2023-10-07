/*
 * @Author: xiedan WX042
 * @since: 2020-12-03 14:46:42
 * @lastTime: 2020-12-31 15:36:20
 * @文件相对于项目的路径: \biz-scene-attendance-web\src\components\StepsGroup\mod\PopConfirm.jsx
 * @Description: 确认气泡框
 */

import React from 'react';
import { Popconfirm, Divider, Icon } from 'antd';
import personEmpty from '@hz-design/base/public/person-fail-empty.svg';
import styles from './popConfirm.less';

export default ({ visible, onCancel, children, getPopupContainer, type = '1', data }) => {
    // type：1--显示原因  2--核实误报
    // 展示类型：1、手动变更要展示原因，2、误报要展示核实信息
    return (
        <Popconfirm
            overlayClassName={styles.overlayClassName}
            icon={null}
            visible={visible}
            title={(
                <div className={styles.popWapper}>
                    {type === '1' ? '修改原因' : '核实信息'}

                    <Icon type="hz-close" theme="outlined" className={styles.close} onClick={onCancel} />
                    <Divider style={{margin: '6px 0'}} />
                    {
                        type === '1' ? (data || '--') : (
                            <>
                            <div className={styles.textItem}>核实位置<span>{data.check_location || '--'}</span></div>
                            <div className={styles.textItem}>
                                拍照反馈
                                <span style={{color: data.match_result ? '#52c41a' : '#fc2344'}}>
                                    {data.match_result === 1 ? '比对成功' : (data.match_result === 0 ? '比对失败' : '--')}
                                </span>
                            </div>
                            <div style={{margin: '10px 0 0 66px', display: 'flex'}}>
                                <div style={{height: '80px', width: '80px', overflow: 'hidden'}}>
                                    <img
                                        alt='注册照'
                                        src={data.person_url ||personEmpty}
                                        onError={(e) => {
                                            e.target.onerror = null;
                                            e.target.src = personEmpty;
                                        }}
                                        style={{height: '100%'}}
                                    />
                                </div>
                                <div style={{height: '80px', width: '80px', overflow: 'hidden'}}>
                                    <img
                                        alt='核实照'
                                        src={data.match_image || personEmpty}
                                        onError={(e) => {
                                            e.target.onerror = null;
                                            e.target.src = personEmpty;
                                        }}
                                        style={{height: '100%'}}
                                    />
                                </div>
                            </div>
                            </>
                        )
                    }
                </div>
            )}
            getPopupContainer={getPopupContainer}
        >
            {children}
        </Popconfirm>
    )
}