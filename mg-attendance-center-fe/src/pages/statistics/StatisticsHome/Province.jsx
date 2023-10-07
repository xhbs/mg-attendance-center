import React, { useState, useEffect } from 'react'
import styles from './common.less'
import ChartCard from './components/ChartCard'
import CardList from './components/CardList'
import Subtitle from './components/Subtitle'
import TimeSelection from './components/TimeSelection'

import { useApi } from './hooks'
const StatisticsProvince = () => {
  const [flagLaoding, setFlagLaoding] = useState(false);
  
  const [directlyAffiliatedSchool, setDirectlyAffiliatedSchool ] = useState({
    list: [],
    count: 0,
  })
  const [state, setState ] = useState({
    list: [],
    count: 0,
  })
  const [topSchoolFive, setTopShoolFive] = useState([])
  const [schoolCount, setSchoolCount ] = useState({
    list: [],
    count: 0,
  })
  const [schoolAttendance, setSchoolAttendance] = useState([])

  const [provincialSchool, setProvincialSchool] = useState([])
  const [provincialListSchool, setProvincialListSchool] = useState([])
  const [provincialShoolParams, setProvincialShoolParams] = useState({})

  const Api = useApi()
  const changeProvincialStatistics = (obj) => {
    if (flagLaoding) return
    Api.provincialStatistics(obj).then(res => {
      setFlagLaoding(true)
      let { success, data } = res
      if (success) {
        setDirectlyAffiliatedSchool({
          list: [
            {
              type: '在校学生数',
              value: data.normal_weeks ?? 0,
              proportion: data.normal_weeks_percent ?  (data.normal_weeks_percent * 100)?.toFixed(2) : 0,
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
        setDirectlyAffiliatedSchool({
          list: [],
          count: 0,
        })
      }
    }).finally(() => {
      setFlagLaoding(false)
    })
  }
  const changeState = (obj) => {
    Api.provincialStatistics(Object.assign(obj, {type: 'city'})).then(res => {
      let { success, data } = res
      if (success) {
        setState({
          list: [
            {
              type: '在校学生数',
              value: data.normal_weeks || 0,
              proportion: data.normal_weeks_percent ? (data.normal_weeks_percent * 100).toFixed(2) : 0,
            },
            {
              type: '缺勤学生数',
              value: data.absent_weeks || 0,
              proportion: data.absent_weeks_percent ? (data.absent_weeks_percent * 100).toFixed(2) : 0,
            },
          ],
          count: data.count || 0,
        })
      } else {
        setState({
          list: [],
          count: 0,
        })
      }
    }).finally(() => {
    })
  }

  const getAllSchoolTop = (obj) => {
    Api.provincialSchoolTop(obj).then(res => {
      let { success, data } = res
      if (success) {
        setTopShoolFive(data)
        return
      }
      setTopShoolFive([])
    })
  }

  const getAllSchool = () => {
    Api.provincialAllSchool().then(res => {
      let { success, data } = res
      if (success) {
        setSchoolCount({
          list: [
            {
              type: '州市级学校',
              value: data.city_school_count ?? 0,
              proportion: data.city_school_count_percent ? (data.city_school_count_percent * 100).toFixed(2) : 0,
            },
            {
              type: '省直属学校',
              value: data.province_school_count ?? 0,
              proportion: data.province_school_percent ? (data.province_school_percent * 100).toFixed(2) : 0,
            },
          ],
          count: data.count ?? 0,
        })
        return
      }
      setSchoolCount({
        list: [],
        count: 0,
      })
      // setTopShoolFive([])
    })
  }

  const getProvincialSchoolAttendance = (obj) => {
    
    Api.provincialSchoolAttendance(obj).then(res => {
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
    }).catch(() => {
      setSchoolAttendance([])
    })
  }

  const changeSchoolAttendanceRatesByCity = (obj) => {
    Api.getProvincialSchool(obj).then(res => {
      let { success, data } = res
      if (success && data?.length) {
        setProvincialSchool(data.map(item => {
          return {
            type: item.name,
            value: item.normal_weeks_percent ? (item.normal_weeks_percent * 100).toFixed(2) : 0,
            item: item,
          }
        }))
        return
      }
      setProvincialSchool([])
    })
  }
  const changeStateSttendanceStatistics = (obj) => {
    setProvincialShoolParams(obj)
    Api.getProvincialSchool(obj).then(res => {
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
  useEffect(() => {
    getAllSchool()
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
  return (
    <div className={styles['home-wrap']}>
      <div className={styles.wrap}>
        <div className={styles['layout-lt']}>
          <div className={styles['col-layout']}>
            <div className={styles['item']}>
              <ChartCard
                type='pie'
                list={directlyAffiliatedSchool.list}
                count={directlyAffiliatedSchool.count}
                Subtitle={<Subtitle tools={<TimeSelection onchange={changeProvincialStatistics} />} />}></ChartCard>
            </div>
            <div className={styles['item']}>
              <ChartCard
                type='pie'
                list={state.list}
                count={state.count}
                Subtitle={<Subtitle
                  title='各地州学生统计'
                  tools={<TimeSelection onchange={changeState}  />} />}></ChartCard>
            </div>
          </div>
        </div>
        <div className={styles['layout-rt']}>
          <div className={styles['col-layout']}>
            <div className={styles['item']}>
              <div className={styles['col-item']}>
                <ChartCard
                  type='progress'
                  list={topSchoolFive}
                  Subtitle={<Subtitle
                    title='全省学校考勤TOP'
                    tools={<TimeSelection onchange={getAllSchoolTop} />} />}></ChartCard>
              </div>
              <div className={styles['col-item']}>
                <ChartCard
                  type='pie'
                  color='#8D5DFF'
                  list={schoolCount.list}
                  count={schoolCount.count}
                  Subtitle={<Subtitle title='全省学校统计' />}></ChartCard>
              </div>
            </div>
            <div className={styles['item']}>
              <ChartCard
                type='line'
                list={schoolAttendance}
                color='#8D5DFF'
                Subtitle={<Subtitle
                  title='省直属学校学生在校率'
                  tools={<TimeSelection isWeek={false} onchange={getProvincialSchoolAttendance} />} />}></ChartCard>
            </div>
          </div>
        </div>
      </div>
      <div className={styles['layout-middle']}>
        <div className={styles['body']}>
          <ChartCard
            type='column'
            list={provincialSchool}
            Subtitle={<Subtitle
              title='各地州学生在校率'
              tools={<TimeSelection onchange={changeSchoolAttendanceRatesByCity} />} />}></ChartCard>
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

export default StatisticsProvince