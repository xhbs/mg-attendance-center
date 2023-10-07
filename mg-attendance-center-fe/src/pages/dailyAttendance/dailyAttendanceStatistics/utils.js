/**
 * 工具  枚举  常量
 */

// 请求成功
export const REQUEST_SUCCESS = '0000000000';

// 学期 枚举
export const semesterEnum = [
	{ name: '春季', code: 0 },
	{ name: '秋季', code: 1 },
];

// 学期类型
export const semesterType = {
	// 春季
	spring: 0,
	// 秋季
	autumn: 1,
};

// 表格类型
export const tableTypeEnum = {
	// 学生
	STUDENT: 1,
	// 不是学生  市区校班
	NOT_STUDENT: 2,
};

// 状态 枚举
export const stateEnum = [
	{ name: '在校', code: 0 },
	{ name: '缺勤', code: 99 },
];

// 状态
export const stateType = {
	// 在校
	IS_SCHOOL: 0,
	// 缺勤
	ABSENTEEISM: 99,
};

// 性别
export const sexEnum = [
	{ name: '男', code: 1 },
	{ name: '女', code: 2 },
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
	// 申诉变更
	COMPLAINT: 3,
};

// 考勤结果
export const attendanceResEnum = [
	{ name: '休息', code: -1, className: 'rest', color: '#999', bgc: 'rgba(153, 153, 153, 0.2)' },
	{ name: '在校', code: 0, className: 'normal', color: '#999', bgc: 'rgba(153, 153, 153, 0.2)' },
	{ name: '误报', code: 1, className: 'error', color: '#999', bgc: 'rgba(153, 153, 153, 0.2)' },
	{ name: '实习', code: 2, className: 'internaship', color: '#ff9900', bgc: 'rgba(255, 153, 0, 0.2)' },
	{ name: '请假', code: 3, className: 'leave', color: '#ff9900', bgc: 'rgba(255, 153, 0, 0.2)' },
	{ name: '申述', code: 4, className: 'complaint', color: '#5584ff', bgc: 'rgba(85, 132, 255, 0.2)' },
	{ name: '缺勤', code: 99, className: 'absenteeism', color: '#ff0000', bgc: 'rgba(255, 0, 0, 0.2)' },
];

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
	// 申述通过
	COMPLAINT: 4,
	// 缺勤
	ABSENTEEISM: 99,
};

// 组织类型
export const organizationType= {
	// 根
	ROOT: 0,
	// 省
	PROVINCE: 1,
	// 市/州
	CITY: 2,
	// 区/县
	AREA: 3,
	// 学校
	SCHOOL: 4,
	// 班级
	CLASS: 5,
	// 学生
	STUDENT: 6,
};

// 星期
export const weekType = {
	// 星期一
	MONDAY: 1,
	// 星期二
	TUESDAY: 2,
	// 星期三
	WEDNESDAY: 3,
	// 星期四
	THURSDAY: 4,
	// 星期五
	FRIDAY: 5,
}

// 请假类型 枚举
export const leaveTypeEnum = [
	{ name: '病假', code: 1 },
	{ name: '事假', code: 2 },
	{ name: '其他', code: 99 },
];

/**
 * 获取枚举中的name
 * @param en
 * @param code
 * @param type
 * @returns {*}
 */
export const getEnumValue = (en, code, type = 'name') => {
	return en.find(item => item.code * 1 === code * 1)?.[type];
}

/**
 * 学年计算  9.1 ~ 次年7.31
 * @param oldNum 往前多少年
 * @returns {*[]}
 */
export const getSchoolYear = (oldNum = 5) => {
	const nowDate = new Date();
	const nowYear = nowDate.getFullYear();
	const nowMonth = nowDate.getMonth() + 1;
	const endYear = nowMonth > 7 ? nowYear + 1 : nowYear;
	const yearOption = [];
	for (let i = 0; i < oldNum; i++) {
		const end = endYear - i;
		const start = end - 1;
		yearOption.push(`${start}-${end}`);
	}
	return yearOption;
};

/**
 * 获取当前月份所在学期   3-8：春，9-12，1-2：秋
 * @returns {number}
 */
export const getSchoolTerm = () => {
	const nowMonth = new Date().getMonth() + 1;
	let term = semesterType.autumn;
	if (nowMonth < 9 && nowMonth >= 3) {
		term = semesterType.spring;
	}
	return term;
}

/**
 * 获取学期 月份
 * @param schoolYear 学年
 * @param semester 学期
 * @returns {*}
 */
export const getSchoolMonth = (schoolYear, semester) => {
	const [start, end] = schoolYear.split('-');
	const springMonth = ['03', '04', '05', '06', '07'];
	const autumnMonth = ['09', '10', '11', '12', '01'];
	const springMonthList = springMonth.map(item => `${end}-${item}`);
	const autumnMonthList = autumnMonth.map(item => {
		if (item > 7) {
			return `${start}-${item}`;
		}
		return `${end}-${item}`;
	});
	const list = [springMonthList, autumnMonthList];
	return  list[semester] || [...autumnMonthList, ...springMonthList];
}

/**
 * 过滤对象中的空参数
 * @param {Object} param
 * @returns {Object}
 */
export const filterNullParam = (param) => {
	if (Object.prototype.toString.call(param) !== '[object Object]') {
		return {};
	}
	const nullList = [null, undefined, ''];
	const obj = {};
	Object.keys(param).forEach(key => {
		const value = param[key];
		!nullList.includes(value) && (obj[key] = value);
	});
	return obj;
}
