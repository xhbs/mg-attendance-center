import React, { memo } from 'react'
import styles from './index.less'
import Item from './Item'

const ChartCard = (props) => {
  let { Subtitle = '', list = [], onActiveItem } = props
  return (
    <div className={styles.wrap}>
      {Subtitle}
      <div className={styles['card-wrap']}>
        {
          list.map((item, index) => (
            <Item key={index} item={item} onActiveItem={onActiveItem}></Item>
          ))
        }
      </div>
    </div>
  )
}

export default memo(ChartCard)