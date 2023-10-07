/**
 * 右侧表格
 */
import React, { useEffect, useState } from 'react';
import { Table } from '@hz-components/react-base';
import { message } from 'antd';
import qs from 'querystring';

import {
	tableTypeEnum,
	organizationType,
	getSchoolYear,
	REQUEST_SUCCESS,
	filterNullParam,
	getSchoolTerm,
} from '../../utils';
import {
	createHandleBarOptions,
	createSearchBarOptions,
	createColumns,
	createStudentColumns,
} from './utils';
import AttendanceDetails from '../attendanceDetails';
import {
	getHighAttendance,
	getStudentAttendance,
	getOrganizationsTree,
} from '../../../../../services/dailyAttendance';
import './index.less';

// 学年
const yearOption = getSchoolYear(5);
// 请求参数  用于缓存状态~~~   useState不能实时更新 所以定义一个局部变量
let paramData = {};
// 之前的表格类型
let oldTableType = 'not_student';


function RightTable(props) {
	// 表格 ref
	const [listRef, setListRef] = useState(null);
	// 筛选 - 学期
	const [semester, setSemester] = useState(getSchoolTerm());
	// 表格类型
	const [tableType, setTableType] = useState(tableTypeEnum.NOT_STUDENT);
	// 显示 个人考勤列表 弹窗
	const [visible, setVisible] = useState(false);
	// 学生编号
	const [personNo, setPersonNo] = useState('');
	// 筛选
	const [screen, setScreen] = useState({
		// 学年
		school_year: yearOption[0],
		// 学期
		school_term: getSchoolTerm(),
		// 月份
		year_month: '',
	});
	// 数据来源  true: props  false: 表格按钮点击
	const [dataSource, setDataSource] = useState(true);
	// 返回上一级是否可用
	const [backDisabled, setBackDisabled] = useState(true);

	const {
		activeInfo,
		setActiveInfo,
	} = props;

	// 当前表格类型是否是非学生类型
	const notStudent = tableType === tableTypeEnum.NOT_STUDENT;

	// 初始设置请求参数
	const initSetData = () => {
		const subType = dataSource ? activeInfo.subType : paramData.sub_type;
		const { activeOrgIndex } = activeInfo;
		// 上一次请求的参数
		const {
			org_parent_index,
			org_index,
			pageSize,
			page_num,
			page_size,
			...obj
		} = paramData;
		const data = {
			...obj,
			org_parent_index: dataSource ? activeOrgIndex : org_parent_index,
			org_index: dataSource ? activeOrgIndex : org_index,
			school_year: screen.school_year,
			school_term: screen.school_term,
			sub_type: subType + 1,
			pageIndex: 1,
		};
		listRef.dataLoad(data);
		setDataSource(true);
	}

	useEffect(() => {
		const { activeOrgIndex } = activeInfo;
		if (!activeOrgIndex) {
			return;
		}
		// 判断sub_type来源
		const subType = dataSource ? activeInfo.subType : paramData.sub_type;
		// 判断当前是学生列表
		const isStudent = subType === organizationType.CLASS;
		const { STUDENT, NOT_STUDENT } = tableTypeEnum;
		// 改变表格类型
		setTableType(isStudent ? STUDENT : NOT_STUDENT);
		const newTableType = isStudent ? 'student' : 'not_student';
		// 如果表格类型不表就掉用  表格类型变动了就走listRef调用
		if (oldTableType === newTableType) {
			initSetData();
		}
		console.log(newTableType);
		oldTableType = newTableType;
	}, [activeInfo]);

	useEffect(() => {
		if (!listRef) {
			return;
		}
		initSetData();
	}, [listRef]);

	/**
	 * 获取数据的请求 数据处理
	 */
	const transformQuery = params => {
		const notStudent = params.sub_type - 1 !== organizationType.CLASS;
		const {
			pageIndex: page_num,
			pageSize: page_size,
			...obj
		} = params;
		// 删除这两条数据  因为paramData记录的是上一次请求接口的请求参数  在清空搜索框内容时这两个值不会在params中。
		delete paramData.org_name;
		delete paramData.like_no_or_name;
		delete paramData?.[notStudent ? 'org_index' : 'org_parent_index'];
		delete obj?.[notStudent ? 'org_index' : 'org_parent_index'];
		return {
			sub_type: notStudent && activeInfo.subType,
			...paramData,
			page_num,
			page_size,
			...obj,
		}
	}

	/**
	 * 获取数据
	 */
	const getData = async params => {
		const notStudent = params.sub_type - 1 !== organizationType.CLASS;
		if ((notStudent && !params.org_parent_index) || (!notStudent && !params.org_index)) {
			return;
		}
		paramData = params;
		console.log( activeInfo.topInfo);
		console.log(params.sub_type, activeInfo.topInfo.sub_type);
		setBackDisabled(params?.sub_type - 1 === activeInfo.topInfo.sub_type);
		const api = notStudent ? getHighAttendance : getStudentAttendance;
		try {
			const result = await api(filterNullParam(params));
			const {
				data: {
					data: currentPageResult = [],
					paging: {
						page_num: pageIndex,
						total: totalCount,
					} = {},
				},
				error_code,
				message: msg,
			} = result || {};
			if (error_code === REQUEST_SUCCESS) {
				return {
					totalCount,
					currentPageResult,
					pageIndex,
				}
			}
			message.error(msg);
		} catch (err) {
			console.log(err);
		}
	}

	// 筛选 学年
	const handleChangeSchoolYear = school_year => {
		setScreen({
			...screen,
			school_year,
			year_month: '',
		});
		paramData.year_month = '';
		listRef.dataLoad({
			school_year,
			year_month: '',
		});
	}

	// 筛选 学期
	const handleChangeSemester = school_term => {
		setSemester(school_term);
		setScreen({
			...screen,
			school_term,
			year_month: '',
		});
		paramData.school_term = school_term;
		paramData.year_month = '';
		listRef.dataLoad({
			school_term,
			year_month: '',
		});
	}

	/**
	 * 筛选 月份
	 */
	const handleChangeMonth = year_month => {
		setScreen({
			...screen,
			year_month,
		});
		paramData.year_month = year_month;
		listRef.dataLoad({ year_month });
	}

	/**
	 * 导出
	 */
	const exportData = async () => {
		if (listRef.getList().length === 0) {
			return message.error('暂无数据');
		}
		const param = qs.stringify(filterNullParam(paramData));
		const url = notStudent
			? '/api/biz-scene/v1/attendance/statictis/level/export'
			: '/api/biz-scene/v1/attendance/statictis/stu/export';
		window.open(`${url}?${param.toString()}`, '_self');
	}

	/**
	 * 查看明细 省市区校班
	 */
	const handleItemDetails = row => {
		const {
			sub_type,
			org_index,
		} = row;
		if (sub_type === organizationType.CLASS) {
			paramData.org_index = org_index;
			paramData.org_parent_index = '';
			paramData.sub_type = sub_type;
			setDataSource(false);
			oldTableType = 'student'
			return setTableType(tableTypeEnum.STUDENT);
		}
		listRef.dataLoad({
			sub_type: sub_type + 1,
			org_parent_index: org_index,
			pageIndex: 1,
		});
	}

	/**
	 * 考勤明细 学生
	 */
	const handleStudentItemDetails = row => {
		const { person_no } = row;
		setVisible(true);
		setPersonNo(person_no);
	}

	/**
	 * 返回上一级
	 */
	const handleBack = async () => {
		const {
			org_parent_index,
			org_index,
		} = paramData;
		/**
		 * 掉接口查找父级！
		 * 这里掉两次接口的考虑：
		 * 第一次根据org_parent_index获取到父级的org_index
		 * 第二次根据父级的org_index获取到父级的sub_type
		 * 因为整个tree存在班级级别 数据量可能很庞大。所以不查整个tree来对比
		 */
		try {
			const treeNode = await getOrganizationsTree({
				type: 1,
				org_index: org_parent_index || org_index,
			});
			const { org_parent_index: parentIndex } = treeNode?.[0] || {};
			const parentTreeNode = await getOrganizationsTree({
				type: 1,
				org_index: parentIndex,
			});
			const { sub_type } = parentTreeNode?.[0] || {}
			setActiveInfo({
				...activeInfo,
				activeOrgIndex: parentIndex,
				subType: sub_type,
			})
		} catch (err) {
			console.log(err);
		}
	}

	return (
		<>
			<Table
				className='daily-attendance-statistics-table'
				// 不可省略  不然表格不会重新渲染
				key={notStudent ? '1' : '2'}
				columns={notStudent ? createColumns(handleItemDetails) : createStudentColumns(handleStudentItemDetails)}
				transformQuery={transformQuery}
				createPromise={getData}
				hasSerialNo
				searchBarOptions={createSearchBarOptions({
					handleChangeSchoolYear,
					yearOption,
					handleChangeSemester,
					handleChangeMonth,
					semester,
					screen,
				})}
				handleBarOptions={createHandleBarOptions(exportData, tableType, handleBack, backDisabled)}
				forceFirstLoadDisabled={true}
				setRef={listRef => setListRef(listRef)}
			/>
			{
				visible && (
					<AttendanceDetails
						visible={visible}
						personNo={personNo}
						screen={screen}
						onCancel={() => setVisible(false)}
					/>
				)
			}
		</>
	);
}

export default RightTable;
