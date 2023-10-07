import React, { memo } from 'react'
import styles from './index.less'
import PieChat from './Chat/PieChat'
import ProgressChart from './Chat/ProgressChart'
import LineChart from './Chat/LineChart'
import ColumnChart from './Chat/ColumnChart'
const ChartCard = (props) => {
  let { Subtitle = '', list = [], count=0, type='pie', color=null } = props
  return (
      <div className={styles.wrap}>
        {Subtitle}
        <div className={styles.chart}>

          {
            type === 'pie' ? (  
              <PieChat color={color} list={list} count={count}></PieChat>
            ) : null
          }
          {
            type === 'progress' ? (  
              <ProgressChart list={list}></ProgressChart>
            ) : null
          }
          {
            type === 'line' ? (  
              <LineChart list={list} color={color}></LineChart>
            ) : null
          }
          {
            type === 'column' ? (
              <ColumnChart list={list} color={color}></ColumnChart>
            ) : null
          }
        </div>

      </div>
  )
}

export default memo(ChartCard)