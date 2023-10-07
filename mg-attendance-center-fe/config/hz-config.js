/*
 * @Author: xiedan WX042
 * @since: 2020-08-27 08:31:19
 * @lastTime: 2021-10-08 10:22:09
 * @文件相对于项目的路径: \biz-scene-attendance-web\config\hz-config.js
 * @Description: router
 */
module.exports = {
  dvaModel: true,
  routes: [
    {
      path: '/sceneAttendance/task/task-management',
      name: '任务管理',
      component: 'pages/task/TaskManagement',
    },
    {
      path: '/sceneAttendance/task/rules-management',
      name: '考勤规则管理',
      component: 'pages/task/Rules',
    },
    {
      path: '/sceneAttendance/task/address-management',
      name: '考勤地点管理',
      component: 'pages/task/Address',
    },
    {
      path: '/sceneAttendance/task/expression-management',
      name: '表达式管理',
      component: 'pages/task/Expression',
    },
    // 统计
    {
      path: '/sceneAttendance/statistics/Province',
      name: '首页',
      component: 'pages/statistics/StatisticsHome/Province',
    },
    {
      path: '/sceneAttendance/statistics/City',
      name: '首页',
      component: 'pages/statistics/StatisticsHome/City',
    },
    {
      path: '/sceneAttendance/statistics/School',
      name: '首页',
      component: 'pages/statistics/StatisticsHome/School',
    },
    {
      path: '/sceneAttendance/statistics/AttendanceStatistics',
      name: '考勤统计',
      component: 'pages/statistics/AttendanceStatistics',
    },
    // {
    //   path: '/sceneAttendance/statistics/nosence',
    //   name: '考勤看板',
    //   component: 'pages/statistics/NoSence',
    // },
    // {
    //   path: '/sceneAttendance/statistics/record',
    //   name: '考勤记录',
    //   component: 'pages/statistics/Record',
    // },
    // {
    //   path: '/sceneAttendance/statistics/detail',
    //   name: '考勤明细',
    //   component: 'pages/statistics/Detail',
    // },

    // {
    //   path: '/sceneAttendance/statistics/detail-daily',
    //   name: '考勤明细_日常',
    //   component: 'pages/statistics/DetailPage',
    // },
    // {
    //   path: '/sceneAttendance/statistics/detail-check',
    //   name: '考勤明细_抽查',
    //   component: 'pages/statistics/DetailPage/checkIndex',
    // },

    // {
    //   path: '/sceneAttendance/statistics/report',
    //   name: '考勤报表',
    //   component: 'pages/statistics/Report',
    // },

    // {
    //   path: '/sceneAttendance/statistics/checkRecommendation',
    //   name: '抽查推荐',
    //   component: 'pages/statistics/Recommendation',
    // },
    // {
    //   path: '/sceneAttendance/statistics/checkRecommendationCity',
    //   name: '抽查推荐_县市级',
    //   component: 'pages/statistics/Recommendation',
    // },
    // 抽查申诉
    {
      path: '/sceneAttendance/appealManage/appeal',
      name: '抽查申诉_省市县级',
      component: 'pages/checkAppeal/Appeal',
    },
    {
      path: '/sceneAttendance/appealManage/appealSchool',
      name: '考勤申诉_校级',
      component: 'pages/checkAppeal/AppealSchool',
    },
    // -------10.18 版本新增申诉统计
    {
      path: '/sceneAttendance/appealManage/appealStatistics',
      name: '申诉统计',
      component: 'pages/checkAppeal/AppealStatistics',
    },
    // 考勤调整
    // {
    //   path: '/sceneAttendance/adjustment',
    //   name: '考勤调整管理',
    //   component: 'pages/adjustment',
    // },
    // 参数设置
    {
      path: '/sceneAttendance/setting',
      name: '考勤参数设置',
      component: 'pages/setting',
    },
    // 学生请假
    {
      path: '/sceneAttendance/askForLeave/apply',
      name: '请假申请_省市县级',
      component: 'pages/askForLeave/Apply',
    },
    // -------10.8版本新增请假统计
    {
      path: '/sceneAttendance/askForLeave/statistics',
      name: '请假统计',
      component: 'pages/askForLeave/Statistics',
    },
    // {
    //   path: '/sceneAttendance/askForLeave/applyToSchool',
    //   name: '请假申请_校级',
    //   component: 'pages/askForLeave/ApplyToSchool',
    // },
    // 实习管理
    {
      path: '/sceneAttendance/practice/apply',
      name: '实习申请_省市县级',
      component: 'pages/practice/Apply',
    },
    {
      path: '/sceneAttendance/practice/statistics',
      name: '实习统计_省市县级',
      component: 'pages/practice/Statistics',
    },
		// -------10.8版本新增实习点名
		{
			path: '/sceneAttendance/practice/roll-call',
			name: '实习点名',
			component: 'pages/practice/rollCall',
		},
    // {
    //   path: '/sceneAttendance/practice/management',
    //   name: '实习管理_校级',
    //   component: 'pages/practice/ManageToSchool',
    // },
    // 业务配置
    // {
    //   path: '/sceneAttendance/config/check',
    //   name: '抽查业务配置',
    //   component: 'pages/config/CheckBusiness',
    // },
    // {
    //   path: '/sceneAttendance/config/schoolStatus',
    //   name: '在离校业务配置',
    //   component: 'pages/config/SchoolStatus',
    // },

	  // 8月版本----日常考勤统计
	  {
		path: '/sceneAttendance/dailyAttendance',
		  name: '日常考勤',
		  component: 'pages/dailyAttendance/dailyAttendanceStatistics',
    },
    // 8月版本----日常考勤配置
    {
      path: '/sceneAttendance/dailyConfig/setting',
      name: '日常考勤设置',
      component: 'pages/daily/index',
    },
    {
      path: '/sceneAttendance/dailyConfig/holiday',
      name: '节假日管理',
      component: 'pages/holiday/index',
    },

    // 10.8版本----抽查考勤任务
	  {
      path: '/sceneAttendance/checkAttendance',
      name: '抽查考勤',
      component: 'pages/checkAttendance/Task',
    },
	  // {
    //   path: '/sceneAttendance/checkAttendance/statistics',
    //   name: '抽查考勤统计',
    //   component: 'pages/checkAttendance/Statistics',
    // },
	  // {
    //   path: '/sceneAttendance/checkAttendance/detail',
    //   name: '抽查考勤明细',
    //   component: 'pages/checkAttendance/Detail',
    // },
    //首页
    {
      path: '/sceneAttendance/home/page',
      name: '首页',
      component: 'pages/homePage/index',
    },
    // 自动资助比对
	  {
	  	path: '/sceneAttendance/auto-fund-comparison',
			name: '自动资助比对',
			component: 'pages/fundComparison/autoFundComparison/index',
	  },
		// 手动资助比对
		{
			path: '/sceneAttendance/manual-fund-comparison',
			name: '手动资助比对',
			component: 'pages/fundComparison/manualFundComparison/index',
		},
    

    {
			path: '/sceneAttendance/statistics-register',
			name: '学生注册状态统计',
			component: 'pages/statistics/register/index',
		},
    {
			path: '/sceneAttendance/statistics-attendance',
			name: '学生考勤结果统计',
			component: 'pages/statistics/attendance/index',
		},

  ],
};
