import React, { useState } from 'react'
import styles from './common.less'
import ChartCard from './components/ChartCard'
import Subtitle from './components/Subtitle'
import TimeSelection from './components/TimeSelection'
import CardList from './components/CardList'

import { useApi } from './hooks'
const StatisticsCity = () => {
  const api = useApi()

  const [cityStatistics, setCityStatistics] = useState({
    list: [],
    count: 0,
  })
  const [topSchoolFive, setTopShoolFive] = useState([])
  const [schoolAttendance, setSchoolAttendance] = useState([])

  const [provincialListSchool, setProvincialListSchool] = useState([])
  const [provincialShoolParams, setProvincialShoolParams] = useState({})

  const changeProvincialStatistics = (obj) => {
    api.getCityStatistics(obj).then(res => {
      let { success, data } = res
      if (success) {
        setCityStatistics({
          list: [
            {
              type: '在校学生数',
              value: data.normal_weeks ?? 0,
              proportion: data.normal_weeks_percent ? (data.normal_weeks_percent * 100)?.toFixed(2) : 0,
            },
            {
              type: '缺勤学生数',
              value: data.absent_weeks ?? 0,
              proportion: data.absent_weeks_percent ? (data.absent_weeks_percent * 100)?.toFixed(2) : 0,
            },
          ],
          count: data.count ?? 0,
        })
      } else {
        setCityStatistics({
          list: [],
          count: 0,
        })
      }
    })
  }

  const getSchoolTop = (obj) => {
    api.getCitySchoolTop(obj).then(res => {
      let { success, data } = res
      if (success) {
        setTopShoolFive(data)
        return
      }
      setTopShoolFive([])
    })
  }

  const changeTerm = (obj) => {
    api.getCityTerm(obj).then(res => {
      let { success, data } = res
      if (success && data?.length) {
        setSchoolAttendance(data.sort((a, b) => a.school_term - b.school_term).map(item => {
          return {
            type: `第${(Number(item.school_term) + 1)}周`,
            value: item.data?.normal_weeks_percent ? (item.data?.normal_weeks_percent * 100).toFixed(2) : 0,
            item: item.data,
          }
        }))
        return
      }
      setSchoolAttendance([])
    })
  }

  const changeStateSttendanceStatistics = (obj) => {
    setProvincialShoolParams(obj)
    api.getCitySchool(obj).then(res => {
      let { success, data } = res
      if (success && data?.length) {
        setProvincialListSchool(data)
        return
      }
      setProvincialListSchool([])
    })
    
  }

  const onActiveItem = (item) => {
    localStorage.setItem('dailyAttendancePrams', JSON.stringify(Object.assign(item, provincialShoolParams)))
    location.href = '/sceneAttendance/dailyAttendance'
  }
  return (
    <div className={styles['home-wrap']}>
      <div className={styles.wrap}>
        <div className={styles['layout-lt']}>
          <div className={styles['col-layout']}>
            <div className={styles['item']}>
              <ChartCard
                type='pie'
                list={cityStatistics.list}
                count={cityStatistics.count}
                Subtitle={<Subtitle title='学生考勤统计' tools={<TimeSelection onchange={changeProvincialStatistics} />} />}></ChartCard>
            </div>
            <div className={styles['item']}>
              <ChartCard
                type='progress'
                list={topSchoolFive}
                Subtitle={<Subtitle
                  title='班级考勤top'
                  tools={<TimeSelection onchange={getSchoolTop} />} />}></ChartCard>
            </div>
          </div>
        </div>
        <div className={styles['layout-rt']}>
          <div className={styles['item-full']}>
            <ChartCard
              type='line'
              list={schoolAttendance}
              Subtitle={
                <Subtitle
                  title='学生在校率'
                  tools={<TimeSelection isWeek={false} onchange={changeTerm} />} />}></ChartCard>
          </div>
        </div>
      </div>
 
      <div className={styles['layout-card']}>
        <div className={styles['body']}>
          <CardList 
            onActiveItem={onActiveItem}
              list={provincialListSchool}
              Subtitle={<Subtitle
              title='各地州考勤统计'
              tools={<TimeSelection onchange={changeStateSttendanceStatistics} />}
            />}
            ></CardList>
        </div>
      </div>
    </div>
  )
}

export default StatisticsCity