/*
 * @Author: xiedan WX042
 * @since: 2020-12-02 18:36:45
 * @lastTime: 2020-12-16 16:26:36
 * @FilePath: \biz-scene-attendance-web\src\components\LeaveAndPracticeModal\mod\ImgGroup.jsx
 * @Description: 图片滚动
 */
import React, { useState } from 'react';
import { Icon } from 'antd';
import emptyImg from '@hz-design/base/public/load-empty.svg';
import errorImg from '@hz-design/base/public/load-fail-empty.svg';
import styles from './imgGroup.less';

export default ({data = [], index = 0, onSelect = () => {}}) => {
    const [page, setPage] = useState(1); // 图片翻页
    const [showIndex, setShowIndex] = useState(index); // 被选中的图片索引
    const imgNum = 5; // 图片每页展示的数量
    const len = (80 * imgNum) + (10 * imgNum);  // 可视图片的宽度加上margin的宽度

    return (
        <div className={styles.imgGroupWapper} style={{width: len}}>
            <Icon
                type="hz-previous"
                theme="outlined"
                className={styles.icon}
                style={{
                    left: '-30px',
                    cursor: page > 1 ? 'pointer' : 'not-allowed',
                    color: page > 1 ? '#666' : '#cccccc',
                }}
                onClick={() => {
                    if(page > 1) {
                        document.getElementById('imgGroupBox').style.marginLeft = `-${ len * (page - 2) }px`
                        setPage(page-1)
                    }
                }}
            />
            <Icon
                type="hz-next-arrow"
                theme="outlined"
                className={styles.icon}
                style={{
                    right: '-30px',
                    cursor: page < Math.ceil(data.length / imgNum) ? 'pointer' : 'not-allowed',
                    color: page < Math.ceil(data.length / imgNum) ? '#666' : '#cccccc',
                }}
                onClick={() => {
                    if(page < Math.ceil(data.length / imgNum)) {
                        document.getElementById('imgGroupBox').style.marginLeft = `-${ len * page }px`
                        setPage(page+1)
                    }
                }}
            />
            <div style={{overflow: 'hidden'}}>
                <div className={styles.imgGroupBox} id='imgGroupBox' style={{width: (80 * data.length) + (10 * data.length)}}>
                    {
                        data.map((item, index) => {
                            return (
                                <div
                                className={styles.imgBox}
                                key={index}
                                style={{
                                    border: showIndex === index ? 'solid 1px #5584FF' : '',
                                }}
                                onClick={(e) => {
                                    onSelect(index)
                                    setShowIndex(index)
                                }}
                                >
                                    <img
                                        alt=''
                                        src={item.person_url || emptyImg}
                                        style={{ width: '100%', height: '100%' }}
                                        onError={(e) => {
                                            e.target.onerror = null;
                                            e.target.src = errorImg;
                                        }}
                                    />
                                </div>
                            )
                        })
                    }
                </div>
            </div>
        </div>
    )
}