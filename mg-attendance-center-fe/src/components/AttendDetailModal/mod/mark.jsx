/*
 * @Author: xiedan WX042
 * @since: 2021-01-27 17:10:37
 * @lastTime: 2021-02-01 11:23:18
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\components\AttendDetailModal\mod\mark.jsx
 * @Description: 考勤明细详情框右上角状态标签
 */
import React, { useEffect, useState } from 'react';
import { ATTEND_DETAIL_STATUS } from '@/constants';
import styles from '../index.less';

const MarkStatus = ({ status }) => {
    const [color, setColor] = useState('#ccc');

    useEffect(() => {
        // 通过判断最后两个字符
        const target = ATTEND_DETAIL_STATUS.find(item => status === item.name) || {};
        setColor(target.color || '#ccc')
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [status])

    return (
        <div
            className={styles.mark}
            style={{
                color: color || '',
                border: `solid 2px ${color}`,
            }}
        >
            {status || ''}
        </div>
    )
}

export default MarkStatus;