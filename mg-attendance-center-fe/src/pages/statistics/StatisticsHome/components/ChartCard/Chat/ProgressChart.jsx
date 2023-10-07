import React, { memo } from 'react';
import { Progress, Popover, Empty } from 'antd';

import styles from '../index.less'
const ProgressChart = (props) => {
  let { list = [] } = props
  return (
    <div className={styles['progress-chart']}>
      {
        list.length ? list.map((item, index) => (
          <div key={index} className={styles['progress-item']}>
            <div className={styles['infos']}>
              <span className={styles['index']}>{ ++index }</span>
              <span className={styles['name']}>
                <Popover title={item.name} content={
                  <div>
                    <div className={styles['lh-36']}>
                      <span>在校学生数</span>
                      <span className={styles['ml-8']}>{item.normal_weeks}人<span className={styles['mx-4']}>|</span>{(item.normal_weeks_percent * 100)?.toFixed(2)}%</span>
                    </div>
                    <div className={styles['lh-36']}>
                      <span>缺勤学生数</span>
                      <span className={styles['ml-8']}>{item.absent_weeks}人<span className={styles['mx-4']}>|</span>{(item.absent_weeks_percent * 100)?.toFixed(2)}%</span>
                    </div>
                  </div>
                }>
                  {item.name}
                </Popover>
              </span>
            </div>
            <div className={styles['progress']}>
              <Progress
                strokeWidth={16}
                strokeColor={{
                  '0%': '#5584FF',
                  '100%': '#B9D4FF',
                }}
              percent={Number((item.normal_weeks_percent * 100 || 0).toFixed(2))} />
            </div>
          </div>
        )) : (
          <div style={{marginTop: '36px'}}>
            <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description='暂无考勤数据' />
          </div>
        )
      }
    </div>
  );
}

export default memo(ProgressChart);
