// import { Table } from '@hz-components/react-base';
// const {Ellipsis}=Table
// 字段枚举
export const infoEnum = {
	person_info: {
		title: '学生信息',
		type: 'list',
		list: {
			person_url: '人脸照',
			person_name: '姓名',
			gender: '性别',
			person_no: '学号',
			admission_date: '入学时间',
			org_index_path_name: '所属组织',
		},
	},

	leave_info: {
		title: '请假信息',
		type: 'list',
		list: {			
			type: '请假类型',
			time: '请假时段',
			reason: '请假原因',
			//需要处理字段请假时段
			// start_date: '开始日期',
			// end_date: '结束日期',
		},
	},

	practice_info: {
		title: '实习信息',
		type: 'list',
		list: {	
			time: '实习时段',		
			practice_company: '实习单位',
			company_contacts: '单位联系人',
			contacts_phone: '联系人电话',
			// practice_status: '实习状态',
			//需要处理字段实习时段
			// start_date: '开始日期',
			// end_date: '结束日期',
		},
	},

	task_info: {
		title: '考勤信息',
		type: 'list',
		list: {
			task_name: '考勤任务',
			creator_role_name: '创建人角色',
			attendance_date: '考勤日期',//其余均显示考勤日期
			attendance_time: '考勤时间',//申述通过显示考勤时间
			creator_name: '创建人',
			creator_org_name: '创建人组织',
			// task_id: '考勤任务ID',
		},
	},	
	
	recent_records: {
		title: '最近抓拍记录',
		type: 'table',
		list: 'recent_records',
	},

	change_records: {
		title: '考勤状态更改记录',
		type: 'step',
		list: 'change_records',
	},
};

// 性别枚举
export const sexEnum = [
	{ name: '男', code: 1 },
	{ name: '女', code: 2 },
];

//请假信息枚举
export const leaveTypeEnum=[
	{name: '病假',code: 1},
	{name: '事假',code: 2},
	{name: '其他',code: 99},
]

//实习状态枚举
export const practiceTypeEnum=[
	{name: '待实习',code: 1},
	{name: '实习中',code: 2},
	{name: '实习结束',code: 3},
]

//结果类型枚举
export const resultTypeEnum=[
	{name: '在校',code: 0,color: '#999', bgc: 'rgba(153, 153, 153, 0.2)',info: '正常'},	
	{name: '实习',code: 2,color: '#ff9900', bgc: 'rgba(255, 153, 0, 0.2)',info: '实习'},
	{name: '请假',code: 3,color: '#ff9900', bgc: 'rgba(255, 153, 0, 0.2)',info: '请假'},
	{name: '申述通过',code: 4,color: '#999', bgc: 'rgba(153, 153, 153, 0.2)',info: '正常'},
	{name: '缺勤',code: 99,color: '#ff0000', bgc: 'rgba(255, 0, 0, 0.2)',info: '缺勤'},
]

// 考勤结果枚举
export const attendanceResEnum = [
	{ name: '休息', code: -1, className: 'rest', color: '#999', bgc: 'rgba(153, 153, 153, 0.2)' },
	{ name: '在校', code: 0, className: 'normal', color: '#999', bgc: 'rgba(153, 153, 153, 0.2)' },
	{ name: '误报', code: 1, className: 'error', color: '#999', bgc: 'rgba(153, 153, 153, 0.2)' },
	{ name: '实习', code: 2, className: 'internaship', color: '#ff9900', bgc: 'rgba(255, 153, 0, 0.2)' },
	{ name: '请假', code: 3, className: 'leave', color: '#ff9900', bgc: 'rgba(255, 153, 0, 0.2)' },
	{ name: '申诉', code: 4, className: 'leave', color: '#ff9900', bgc: 'rgba(255, 153, 0, 0.2)' },
	{ name: '缺勤', code: 99, className: 'absenteeism', color: '#ff0000', bgc: 'rgba(255, 0, 0, 0.2)' },
];

// 考勤变更类型 枚举
export const modeEnum = [
	{ name: '原始考勤', code: 0 },
	{ name: '系统变更', code: 1 },
	{ name: '手动变更', code: 2 },
	{ name: '申诉变更', code: 3 },
];

// 考勤变更类型
export const modeType = {
	// 初始变更
	INIT: 0,
	// 系统变更
	SYSTEM: 1,
	// 手动变更
	MANUAL: 2,
};


// 考勤结果 类型
export const attendanceResType = {
	REST: -1,
	// 正常
	NORMAL: 0,
	// 误报
	ERROR: 1,
	// 实习
	INTERNASHIP: 2,
	// 请假
	LEAVE: 3,
	// 缺勤
	PASS: 4,
	// 申诉通过
	ABSENTEEISM: 99,
};




/**
 * 获取枚举中的name
 * @param en
 * @param code
 * @param type
 * @returns {*}
 */
 export const getEnumValue = (en, code, type = 'name') => {
	//?. 可选链操作对象本身含有某个字段返回当前对象的字段值不满足返回undefined
  return en.find(item => item.code * 1 === code * 1)?.[type];
}
