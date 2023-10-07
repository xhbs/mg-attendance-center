/*
 * @Author: xiedan WX042
 * @since: 2020-08-27 08:31:19
 * @lastTime: 2021-10-13 15:09:25
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\constants.js
 * @Description: 
 */
// 考勤记录保存时长
export const RECORD_KEEP_TIME = [
	{ code: '1', name: '1个月' },
	{ code: '3', name: '3个月' },
	{ code: '6', name: '6个月' },
	{ code: '9', name: '9个月' },
	{ code: '12', name: '12个月' },
	{ code: '18', name: '18个月' },
	{ code: '24', name: '24个月' },
	{ code: '30', name: '30个月' },
	{ code: '36', name: '36个月' },
];

export const DATE_TYPE = 'YYYY-MM-DD HH:mm:ss';

export const DATA_TYPE_NO_SPACE = 'YYYYMMDDHHmmss';

export const DATE_TYPE_DAY = "YYYY-MM-DD";

export const PLACEHOLDER = '--';

// 手机号码校验
export const PHONE_REG = /^1[3456789]\d{9}$/;

// 接口请求成功code
export const API_ERROR_CODE = '0000000000';

// 考勤规则表单页，循环的日期 or 星期
export const DATE_ARR_MAP = {
	'0': ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
	'1': Array.from({ length: 31 }, (v, k) => k + 1),
};

// 调整类型
export const ADJUST_TYPE = [
	{ code: 0, name: '请假' },
	{ code: 1, name: '出差' },
	{ code: 2, name: '调休' },
	{ code: 3, name: '补卡' },
	{ code: 4, name: '加班' },
	{ code: 5, name: '弹性' },
	{ code: 6, name: '其他' },
];

// 考勤方式
export const ATTENDANCE_WAYS = [
	{ code: 'face_recognition', name: '人脸' },
	// { code: 'KAFKA_DISPOSITION', name: '布控' },
];

// 任务名称等的校验----只能输入中文、英文、数字、_、-
export const REGEX_NAME = /^[A-z0-9\u4e00-\u9fa5_-]*$/;

// 考勤点类型
export const DEVICE_TYPE = [{
	code: 4,
	name: '人脸卡口',
},
// {
//     code: 8,
//     name: '车辆卡口',
// },
{
	code: 5,
	name: '门禁',
}, {
	code: 1,
	name: '编码器',
}]

// 实习/请假/申诉--处理状态、抽查申诉、实习申请
export const LEAVE_DEAL_STATUS = [
	{ code: 2, name: '未处理', color: '#52c41a' },
	{ code: 3, name: '同意', color: '#ccc' },
	{ code: 4, name: '拒绝', color: '#FC2344' },
	// { code: 5, name: '无需处理', color: '#ffb000' }, // 只有省级才有
]
// 请假--请假类型
export const LEAVE_TYPE = [
	{ code: 1, name: '病假' },
	{ code: 2, name: '事假' },
	// { code: 3, name: '实习登记' },
	{ code: 99, name: '其他' },
]
// 校级的考勤申诉
export const APPEAL_STATUS = [
	{ code: 1, name: '未申诉', color: '#52c41a' },
	{ code: 2, name: '审批中', color: '#5584ff' },
	{ code: 3, name: '同意', color: '#cccccc' },
	{ code: 4, name: '拒绝', color: '#FC2344' },
]
// 实习/请假--实习/请假状态  (校级)
export const LEAVE_STATUS = [
	{ code: 2, name: '未处理', color: '#5584ff' },
	{ code: 3, name: '同意', color: '#cccccc' },
	{ code: 4, name: '拒绝', color: '#FC2344' },
]

// 实习统计--实习状态
export const PRACTICE_TYPE = [
	{ code: 1, name: '待实习', color: '#52c41a' },
	{ code: 2, name: '实习中', color: '#52c41a' },
	{ code: 3, name: '实习结束', color: '#cccccc' },
];

// 考勤明细--考勤状态
export const ATTEND_DETAIL_STATUS = [
	{ code: 0, name: '正常', color: '#cccccc' },
	{ code: 1, name: '误报', color: '#cccccc' },
	{ code: 2, name: '实习', color: '#ffb000' },
	{ code: 3, name: '请假', color: '#52c41a' },
	{ code: 99, name: '缺勤', color: '#fc2344' },
];

// 历史考勤统计的类型
// export const STATISTICS_TYPE_MAP = ['请假', '出差', '调休', '补卡', '加班', '弹性', '其它', '缺勤', '误报', '正常', '实习'];
export const STATISTICS_TYPE_MAP = {'0': '正常', '1': '误报', '2': '实习', '3': '请假', '99': '缺勤', '-1': '休息'};

// 用户角色
export const PROVINCE = 'provincial_admin'; // 省级
export const CITY = 'city_admin'; // 市级
export const COUNTY = 'county_admin'; // 县级
export const SCHOOL = 'school_admin'; // 校级

// 各个角色的请假、实习、申诉记录的状态的字段名称
export const STATUS_FILED_MAP = {
	[PROVINCE]: 'provincial_approve_status',
	[CITY]: 'city_approve_status',
	[COUNTY]: 'county_approve_status',
	[SCHOOL]: 'status',
}

// 学校上级组织类型，1省 2市 3区 (用来判断当前角色是否可以审核申诉记录)
export const SCHOOL_SUB_TYPE = {
  1: PROVINCE,
  2: CITY,
  3: COUNTY,
}

// 10.8版本新增
// 抽查考勤任务---状态枚举
export const CHECK_ATTEND_TASK_STATUS = [
	{ code: 2, name: '进行中', color: '#52c41a' },
	{ code: 1, name: '未开始', color: '#5584ff' },
	{ code: 3, name: '已结束', color: '#cccccc' },
];

// 学期枚举
export const TERM_ENUM = [
  { code: '1', name: '秋季' },
  { code: '0', name: '春季' },
]

// 10.8版本新增
// 考勤明细--考勤状态
export const ATTENDANCE_STATUS = [
	{ code: 0, name: '在校', color: '#cccccc' },
	{ code: 2, name: '实习', color: '#ffb000' },
	{ code: 3, name: '请假', color: '#52c41a' },
	{ code: 4, name: '申诉通过', color: '#5584ff' },
	{ code: 99, name: '缺勤', color: '#fc2344' },
];

// 请假统计--请假状态
export const ATTEND_STATUS = [
	{ code: 1, name: '请假待生效', color: '#52c41a' },
	{ code: 2, name: '请假中', color: '#5584ff' },
	{ code: 3, name: '请假结束', color: '#cccccc' },
];