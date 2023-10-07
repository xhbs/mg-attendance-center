import React, { memo, useState, useEffect } from 'react';
import { Select } from 'antd';
import moment from 'moment';
import styles from './index.less'

const { Option } = Select;

const weeks = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19]

const LastSemester = [9, 10, 11, 12, 1]

const thisYear = new Date().getFullYear()
const thisMonth = new Date().getMonth() + 1
const currentTerm =  LastSemester.includes(thisMonth) ? '1' : '0'
let diffDays = 0
if (LastSemester.includes(thisMonth)) {
  diffDays = moment(new Date()).diff(moment(`${thisYear}-09-01 00:00:00`), 'days')
} else {
  diffDays = moment(new Date()).diff(moment(`${thisYear}-03-01 00:00:00`), 'days')
}
const currentWeek = Math.ceil(diffDays/7 ) > 0 ? Math.ceil(diffDays/7 ) : undefined

const TimeSelection = (props) => {
  console.log(new Date().getFullYear(), 'new Date().getFullYear()')
  let { onchange =() => {}, isWeek = true } = props

  const [years, setYears] = useState([])
  const [currentYear, setCurrentYear] = useState(thisYear)
  const [term, setTerm] = useState(currentTerm)
  const [week, setWeek] = useState(currentWeek)

  const genarateYears = () => {
    setYears([ thisYear - 2, thisYear - 1, thisYear ])
    return [ thisYear - 2, thisYear - 1, thisYear ]
  }

  useEffect(() => {
    genarateYears()
  }, []);

  useEffect(() => {
    let obj = {
      school_term: Number(term),
      school_year: currentYear,
      check_week: week,
    }
    onchange(obj)
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])
  
  return (
    <div className={styles.wrap}>
      {
        years.map(item => (
          <div 
            onClick={() => {
              setCurrentYear(item)
              onchange({
                school_term: Number(term),
                school_year: item,
                check_week: week,
              })
            }}
          className={item === currentYear ? styles['active'] : ''} 
          key={item}>{item === thisYear ? '本学年' : item}</div>
        ))
      }
      <div>
        <Select value={term} style={{ width: '68px' }} onChange={(value) => {
          setTerm(value)
          onchange({
            school_term: Number(value),
            school_year: currentYear,
            check_week: week,
          })
        }}>
          <Option key={'1'} value="1">上学期</Option>
          <Option key={'0'} value="0">下学期</Option>
        </Select>
      </div>
      {
        isWeek ? (
          <div>
            <Select value={week} style={{ width: '68px' }} onChange={(value) => {
              setWeek(value)
              console.log('onchange')
              onchange({
                school_term: Number(term),
                school_year: currentYear,
                check_week: value,
              })
            }}>
              {
                weeks.map(item => (
                  <Option key={item} value={item}>第{++item}周</Option>
                ))
              }
              
            </Select>
          </div>
        ) : null
      }
      
    </div>
  );
}

export default memo(TimeSelection);
