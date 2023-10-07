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
			leave_type: '请假类型',
			time: '请假时段',
			reason: '请假原因',
		},
	},
	practice_info: {
		title: '实习信息',
		type: 'list',
		list: {
			time_frame: '实习时段',
			practice_company: '实习单位',
			company_contacts: '单位联系人',
			contacts_phone: '联系人电话',
		},
	},
	attendance_info: {
		title: '考勤信息',
		type: 'list',
		list: {
			task_name: '考勤任务',
			creator_name: '创建人',
			creator_role_name: '创建人角色',
			creator_org_name: '创建人组织',
			attendance_date: '考勤日期',
			attendance_time: '考勤时间',
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

// 表格字段枚举
export const tableColumns = [
	{
		title: '序号',
		key: 'order',
		render: (text, row, index) => index + 1,
	},
	{
		title: '抓拍时间',
		dataIndex: 'captured_at',
		key: 'captured_at',
	},
	{
		title: '抓拍地点',
		dataIndex: 'device_name',
		key: 'device_name',
	},
];
