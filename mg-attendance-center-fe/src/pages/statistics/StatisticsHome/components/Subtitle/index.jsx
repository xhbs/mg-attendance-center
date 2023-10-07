import React, { memo } from 'react'
import styles from './index.less'
const Subtitle = (props) => {
  let { title = '省直属学校学生考勤统计', tools = null } = props
  return (
    <div className={styles['wrap']}>
      <span>{title}</span>
      {tools}
    </div>
  )
}

export default memo(Subtitle)