import React, { memo } from 'react';
import { Progress } from 'antd'

import IconSchool from '@/assets/attendance/icon-school.png'
import IconAbsence  from '@/assets/attendance/icon-absence.png'
import styles from './index.less'

function milliFormat(num) {
  return num && num.toString()
  .replace(/\d+/, function(s){
        return s.replace(/(\d)(?=(\d{3})+$)/g, '$1,')
    })
}
const Item = (props) => {
  let { item, onActiveItem = {} } = props
  return (
    <div className={styles['item-wrap']}>
      <div className={styles['card-meta']}>
        <div className={styles['head']}>
          <span>{item.name}</span>
          <a onClick={(e) => {
            e.preventDefault()
            onActiveItem(item)
            // todo 跳转页面
          }}>更多</a>
        </div>
        <div className={styles['middle']}>
          <span>学生总数</span>
          <b>{milliFormat(item.count)}</b>
        </div>
        <Progress
          strokeWidth={20}
          strokeColor={{
            '0%': '#524EF1',
            '100%': '#C7AFFF',
          }}
          width={'86%'}
          percent={Number(((item.normal_weeks_percent || 0) * 100).toFixed(2))} />
        
      </div>
      <div className={styles['footer']}>
          <div>
            <div className={styles['icon']}>
              <img width={24} height={24} src={IconSchool} alt="" srcset="" />
              <span>在校</span>
            </div>
            <b>{milliFormat(item.normal_weeks || 0)}</b>
          </div>
          <div>
            <div className={styles['icon']}>
              <img width={24} height={24} src={IconAbsence} alt="" srcset="" />
              <span>缺席</span>
            </div>
            <b>{milliFormat(item.absent_weeks || 0)}</b>
          </div>
        </div>
    </div>
  );
}

export default memo(Item);
