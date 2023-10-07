import React, { useState } from 'react';
import styles from './common.less';
import ChartCard from './components/ChartCard';
import Subtitle from './components/Subtitle';
import TimeSelection from './components/TimeSelection';
import { useApi } from './hooks'
const StatisticsHome = () => {
	const api = useApi()

	const [statistics, setStatistics] = useState({
    list: [],
    count: 0,
  })
	
	const [topSchoolFive, setTopShoolFive] = useState([])

	const [schoolAttendance, setSchoolAttendance] = useState([])

	const changeSchoolStatistics = (obj) => {
    api.getSchoolStatistics(obj).then(res => {
      let { success, data } = res
      if (success) {
        setStatistics({
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
        setStatistics({
          list: [],
          count: 0,
        })
      }
    })
  }

	const changeSchoolTop = (obj) => {
		api.getSchoolTop(obj).then(res => {
      let { success, data } = res
      if (success) {
        setTopShoolFive(data)
        return
      }
      setTopShoolFive([])
    })
	}
	const changeTerm = (obj) => {
    api.getSchoolTerm(obj).then(res => {
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
	return (
		<div className={styles.wrap}>
			<div className={styles['layout-lt']}>
				<div className={styles['col-layout']}>
					<div className={styles['item']}>
							<ChartCard
								type="pie"
								list={statistics.list}
								count={statistics.count}
								Subtitle={
									<Subtitle
										title='学生考勤统计'
										tools={<TimeSelection onchange={changeSchoolStatistics} />}
									/>
								}
							></ChartCard>
					</div>
					<div className={styles['item']}>
							<ChartCard
								type="progress"
								list={topSchoolFive}
								Subtitle={
									<Subtitle
										title="班级考勤top"
										tools={<TimeSelection onchange={changeSchoolTop} />}
									/>
								}
							></ChartCard>
					</div>
				</div>
			</div>
			<div className={styles['layout-rt']}>
				<div className={styles['item-full']}>
						<ChartCard
							type="line"
							list={schoolAttendance}
							count={850}
							Subtitle={
								<Subtitle
									title="学生在校率"
									tools={<TimeSelection isWeek={false} onchange={changeTerm} />}
								/>
							}
						></ChartCard>
				</div>
			</div>
		</div>
	);
};

export default StatisticsHome;