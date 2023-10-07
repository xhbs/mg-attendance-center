import SpotCheckAttendance from '../../assets/homePage/spot_check _attendance.png';
import DailyAttendance from '../../assets/homePage/daily_ attendance.png';
import StudentRegistration from '../../assets/homePage/student_ registration.png';
import FundingComparison from '../../assets/homePage/funding_comparison.png';
import LeaveManagement from '../../assets/homePage/leave_management.png';
import PracticeManagement from '../../assets/homePage/practice_management.png';
import AppealManagement from '../../assets/homePage/appeal_management.png';
import ManualFundingComparison from '../../assets/homePage/manual_funding_comparison.png';
import UserAuthorization from '../../assets/homePage/user_authorization.png';
import DailyAttendanceConfiguration from '../../assets/homePage/daily _attendance_configuration.png';

import React, { Component } from 'react';
import {Tooltip} from 'antd';
import styles from './styles/index.less';
import { getUserRoleInfo } from '@/services/common';

//首页角色管理菜单列表枚举
const roleMenuInfo={
  admin: {  
    business: {
      header: '业务功能',
      type: 'fir',
      list: [
        {
          title: '抽查考勤',
          subTitle: 'Spot Check Attendance',
          src: SpotCheckAttendance,
          url: '/sceneAttendance/checkAttendance',
        },
        {
          title: '日常考勤',
          subTitle: 'Daily Attendance',
          src: DailyAttendance,
          url: '/sceneAttendance/dailyAttendance',
        },
        {
          title: '学生信息',
          subTitle: 'Student Registration',
          src: StudentRegistration,
          url: '/objectManage/studentsStatistical',
        },
        {
          title: '自动资助比对',
          subTitle: 'Funding Comparison',
          src: FundingComparison,
          url: '/sceneAttendance/auto-fund-comparison',
        },
        {
          title: '请假管理',
          subTitle: 'Leave Management',
          src: LeaveManagement,
          url: '/sceneAttendance/askForLeave/apply',
        },
        {
          title: '实习管理',
          subTitle: 'Practice Management',
          src: PracticeManagement,
          url: '/sceneAttendance/practice/apply',
        },
        {
          title: '申诉管理',
          subTitle: 'Appeal Management',
          src: AppealManagement,
          url: '/sceneAttendance/appealManage/appeal',
        },
        {
          title: '手动资助比对',
          subTitle: 'Manual Funding Comparison',
          src: ManualFundingComparison,
          url: '/sceneAttendance/manual-fund-comparison',
        },
      ],
    },
    setting: {
      header: '配置功能',
      type: 'two',
      list: [
        {
          title: '用户授权',
          subTitle: 'User Authorization',
          src: UserAuthorization,
          url: '/uuv/xUsers/Users',
        },
        {
          title: '日常考勤配置',
          subTitle: 'Daily Attendance Configuration',
          src: DailyAttendanceConfiguration,
          url: '/sceneAttendance/dailyConfig',
        },
      ],
    },  
  },
  provincial_admin: {  
    business: {
      header: '业务功能',
      type: 'fir',
      list: [  
        {
          title: '抽查考勤',
          subTitle: 'Spot Check Attendance',
          src: SpotCheckAttendance,
          url: '/sceneAttendance/checkAttendance',
        },      
        {
          title: '日常考勤',
          subTitle: 'Daily Attendance',
          src: DailyAttendance,
          url: '/sceneAttendance/dailyAttendance',
        },
        {
          title: '学生信息',
          subTitle: 'Student Registration',
          src: StudentRegistration,
          url: '/objectManage/studentsStatistical',
        },
        {
          title: '自动资助比对',
          subTitle: 'Funding Comparison',
          src: FundingComparison,
          url: '/sceneAttendance/auto-fund-comparison',
        },
        {
          title: '申诉管理',
          subTitle: 'Appeal Management',
          src: AppealManagement,
          url: '/sceneAttendance/appealManage/appeal',
        },
        {
          title: '手动资助比对',
          subTitle: 'Manual Funding Comparison',
          src: ManualFundingComparison,
          url: '/sceneAttendance/manual-fund-comparison',
        },
      ],
    },
    setting: {
      header: '配置功能',
      type: 'two',
      list: [
        {
          title: '用户授权',
          subTitle: 'User Authorization',
          src: UserAuthorization,
          url: '/uuv/xUsers/Users',
        },
        {
          title: '日常考勤配置',
          subTitle: 'Daily Attendance Configuration',
          src: DailyAttendanceConfiguration,
          url: '/sceneAttendance/dailyConfig',
        },
      ],
    },  
  },
  city_admin: {  
    business: {
      header: '业务功能',
      type: 'fir',
      list: [    
        {
          title: '抽查考勤',
          subTitle: 'Spot Check Attendance',
          src: SpotCheckAttendance,
          url: '/sceneAttendance/checkAttendance',
        },    
        {
          title: '日常考勤',
          subTitle: 'Daily Attendance',
          src: DailyAttendance,
          url: '/sceneAttendance/dailyAttendance',
        },
        {
          title: '学生信息',
          subTitle: 'Student Registration',
          src: StudentRegistration,
          url: '/objectManage/studentsStatistical',
        },
        {
          title: '申诉管理',
          subTitle: 'Appeal Management',
          src: AppealManagement,
          url: '/sceneAttendance/appealManage/appeal',
        },
      ],
    },     
  },
  county_admin: {  
    business: {
      header: '业务功能',
      type: 'fir',
      list: [     
        {
          title: '抽查考勤',
          subTitle: 'Spot Check Attendance',
          src: SpotCheckAttendance,
          url: '/sceneAttendance/checkAttendance',
        },   
        {
          title: '日常考勤',
          subTitle: 'Daily Attendance',
          src: DailyAttendance,
          url: '/sceneAttendance/dailyAttendance',
        },
        {
          title: '学生信息',
          subTitle: 'Student Registration',
          src: StudentRegistration,
          url: '/objectManage/studentsStatistical',
        },
        {
          title: '申诉管理',
          subTitle: 'Appeal Management',
          src: AppealManagement,
          url: '/sceneAttendance/appealManage/appeal',
        },
      ],
    },     
  },
  school_admin: {  
    business: {
      header: '业务功能',
      type: 'fir',
      list: [  
        {
          title: '抽查考勤',
          subTitle: 'Spot Check Attendance',
          src: SpotCheckAttendance,
          url: '/sceneAttendance/checkAttendance',
        },      
        {
          title: '日常考勤',
          subTitle: 'Daily Attendance',
          src: DailyAttendance,
          url: '/sceneAttendance/dailyAttendance',
        },
        {
          title: '学生信息',
          subTitle: 'Student Registration',
          src: StudentRegistration,
          url: '/objectManage/studentsStatistical',
        },
        {
          title: '请假管理',
          subTitle: 'Leave Management',
          src: LeaveManagement,
          url: '/sceneAttendance/askForLeave/apply',
        },
        {
          title: '实习管理',
          subTitle: 'Practice Management',
          src: PracticeManagement,
          url: '/sceneAttendance/practice/apply',
        },
      ],
    },      
  },
  head_teacher: {  
    business: {
      header: '业务功能',
      type: 'fir',
      list: [        
        {
          title: '学生信息',
          subTitle: 'Student Registration',
          src: StudentRegistration,
          url: '/objectManage/studentsStatistical',
        },
      ],
    },     
  },
}

const tooltipTitle=['基于指定的考勤学生名单，执行设定天数的随机有感考勤任务','全员7*24h人脸考勤结合老师有感考勤统计']

export default class index extends Component {
  state={
    userRole: '',//用户角色
    homePageInfo: {},//首页渲染数据
  }
  componentDidMount(){
    getUserRoleInfo().then(res=>{
      console.log(res);
      if (res) {
        this.setState({
          userRole: res[0] && res[0].role_code || '',
        },()=>this.fetchHomePageInfo(this.state.userRole))
      }
    })
  }  

  //跳转菜单页面
  handleClick=(url)=>{
    window.open(url,'_blank')    
  }

  //获取角色渲染数据
  fetchHomePageInfo=(role)=>{
    this.setState({
      homePageInfo: roleMenuInfo[role],
    })
  }

  render() {
    let {homePageInfo}=this.state
    return (
      <div className={styles["home-wrapper"]}>
          <div className={styles["home-box"]}>            
            {
              Object.keys(homePageInfo).map(key=>{
                const {header,type,list}=homePageInfo[key]
                return (
                  <div className={type==='fir' ? styles["home-box-top"] : styles["home-box-bottom"]} key={key}>
                    <div className={styles["title"]}>
                      <span></span>
                      <p>{header}</p>
                    </div>
                    <div className={styles["box-flex"]}>
                      {
                        list.map(({title,subTitle,src,url},index)=>{
                          return (
                            <div className={styles["box-item"]}
                              onClick={()=>this.handleClick(url)} 
                              key={title}
                            >
                              <img src={src} alt=""/>
                              <span>{title}</span>
                              <span>{subTitle}</span>
                              {
                                ((type==='fir'&&title==='抽查考勤') || (type==='fir'&&title==='日常考勤')) && <Tooltip title={tooltipTitle[index]}><span className={styles["mark"]}></span></Tooltip>
                              }                              
                            </div>
                          )
                        })
                      }
                    </div>
                  </div>
                )
              })
            } 
        </div>
      </div>
    )
  }
}