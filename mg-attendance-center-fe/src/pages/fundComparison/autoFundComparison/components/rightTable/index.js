/**
 * 右侧表格
 */

import React, { useEffect, useState, useCallback } from 'react';
import { Table } from '@hz-components/react-base';
import { message } from 'antd';
import qs from 'querystring';

import TopHandle from '../topHandle';
import {
	highColumns,
	studentColumns,
	createSearchOptions,
	createHandleOptions,
	comparisonType,
} from '../../../utils';
import { comparison, highList, studentList } from '../../../../../services/fundComparison';
import {
	REQUEST_SUCCESS,
	organizationType,
	filterNullParam,
} from '../../../../dailyAttendance/dailyAttendanceStatistics/utils';
import Details from '../details';
import styles from './index.less';
import { getOrganizationsTree } from '../../../../../services/dailyAttendance';

// 是否已经对比过
let isComparison = false;
// 之前的表格类型
let oldTableType = 'not_student';
// 上一次请求的参数
let oldParams = {};
// 上一次的org_parent_index
let oldOrgParentIndex = '';
// 上一次请求的sub_type
let oldSubType = 0;

function RightTable(props) {
	// 表格类型  是否是学生类
	const [isStudent, setIsStudent] = useState(false);
	// 是否显示详情
	const [visible, setVisible] = useState(false);
	// 查看哪个学生详情
	const [selectInfo, setSelectInfo] = useState({});
	// table ref
	const [listRef, setListRef] = useState(null);
	// 头部筛选 信息
	const [screenInfo, setScreenInfo] = useState(null);
	// 返回上一级是否可用
	const [backDisabled, setBackDisabled] = useState(true);
	// 对比按钮 loading
	const [loading, setLoading] = useState(false);

	const { type, activeInfo, setActiveInfo } = props;

	useEffect(() => {
		const { subType } = activeInfo;
		const type = subType === organizationType.SCHOOL;
		setIsStudent(type);
		oldSubType = subType;
		const newTableType = type ? 'student' : 'not_student';
		if (oldTableType === newTableType) {
			loadTable();
		}
		oldTableType = newTableType;
	}, [activeInfo, loadTable]);

	useEffect(() => {
		loadTable();
		oldOrgParentIndex = '';
	}, [listRef, loadTable]);

	/**
	 * 公共发起请求方法
	 */
	const loadTable = useCallback(() => {
		const { activeOrgIndex: org_parent_index, subType: sub_type } = activeInfo;
		listRef &&
			listRef.dataLoad({
				...oldParams,
				org_parent_index: oldOrgParentIndex ? oldOrgParentIndex : org_parent_index,
				sub_type,
				pageIndex: 1,
				status: '',
			});
	});

	/**
	 *  比对 生成数据
	 */
	const comparisonData = async () => {
		const {
			data: data_type,
			project,
			listText,
			timeFrameText,
			rules: absent_rate,
			rule,
			year: school_year,
			semester: school_term,
		} = screenInfo;
		const [chk_date_st, chk_date_ed] = timeFrameText.split('~');
		let params = {
			subsid_type: type,
			chk_date_st,
			chk_date_ed,
			absent_rate,
			org_index: activeInfo.activeOrgIndex,
			rule,
			school_term,
			school_year,
		};
		if (rule === 1) {
			params = { ...params, call_the_roll_absent_rate: absent_rate };
		}
		// 自动资助比对的时候
		type === comparisonType.AUTO &&
			(params.subsid_roster_info = {
				data_type: data_type * 1,
				prj_type: project?.split('+')[0][project?.split('+')[0].length-1],
				roster_date: project?.split('+')[0].slice(0,7),
			});
		// 手动资助比对
		type === comparisonType.MANUAL && (params.sub_list_index = listText);
		setLoading(true);
		try {
			const res = await comparison(params);
			console.log('%c [ res ]-130', 'font-size:13px; background:pink; color:#bf2c9f;', res);
			const {
				error_code,
				data: { subsid_rule_id } = {},
				message: msg,
			} = res;
			if (error_code === REQUEST_SUCCESS) {
				isComparison = true;
				const { activeOrgIndex, subType } = activeInfo;
				const { org_parent_index } = oldParams;
				console.log(oldParams);
				return listRef.dataLoad({
					subsid_rule_id,
					org_parent_index: org_parent_index || activeOrgIndex,
					sub_type: oldSubType || subType,
				});
			}
			if (error_code === '01010512') {
				message.warning(msg);
        return
			}
			if (error_code === '0101000001') {
				message.warning(msg || '所选名单无考勤记录');
        return
			}
			
			isComparison = false;
			message.error(msg || '对比失败， 请重试!');
		} catch (err) {
			isComparison = false;
			console.log(err, 'error');
			setLoading(false);
			if(err.message.includes('timeout')){   // 判断请求异常信息中是否含有超时timeout字符串
				console.log("错误回调", err);
				message.warning('自动比对计算中，请等待2-5分钟再次查询')
				return
			}
			message.warning( err && err?.message || '自动比对计算中，请等待2-5分钟再次查询');
		} finally{
			setLoading(false);
		}
	};

	/**
	 * 请求参数整理
	 */
	const transformQuery = (params) => {
		const {
			pageIndex: page_num,
			pageSize: page_size,
			org_parent_index,
			sorter,
			...obj
		} = params;
		const { timeFrameText = '', rules: absent_rate } = screenInfo;
		const [chk_date_st, chk_date_ed] = timeFrameText.split('~');
		let order_field = '';
		let order_rule = '';
		if (isStudent && sorter) {
			const { field, order } = sorter;
			order_field = field;
			order_rule = order === 'ascend' ? 'asc' : 'desc';
		}
		if (isStudent && !sorter) {
			order_field = 'absent_rate';
			order_rule = 'desc';
		}
		return {
			...obj,
			org_parent_index,
			page_num,
			page_size,
			absent_rate,
			chk_date_st,
			chk_date_ed,
			order_field,
			order_rule,
		};
	};

	/**
	 * 请求 table
	 */
	const createPromise = async (params) => {
		if (!isComparison) {
			return;
		}
		oldParams = params;
		console.log(params);
		setBackDisabled(oldSubType === activeInfo.topInfo.sub_type);
		const api = isStudent ? studentList : highList;
		try {
			const res = await api(filterNullParam(params));
			console.log(res);
			const {
				error_code,
				data: {
					data = [],
					paging: { total: totalCount, page_num: pageIndex },
				},
				message: msg,
			} = res;
			if (error_code === REQUEST_SUCCESS) {
				return {
					currentPageResult: data.map((item) => {
						let { chk_date_st, chk_date_ed, absent_num, normal_num, absent_rate } =
							item;
						let time_frame;
						if (chk_date_st && chk_date_ed) {
							time_frame = `${chk_date_st}~${chk_date_ed}`;
						}
						if (chk_date_st && !chk_date_ed) {
							time_frame = chk_date_st;
						}
						if (!chk_date_st && chk_date_ed) {
							time_frame = chk_date_ed;
						}
						if (!chk_date_st && !chk_date_ed) {
							time_frame = '--';
						}
						return {
							...item,
							time_frame,
							check_week: normal_num + absent_num,
							absent_rate: `${absent_rate}%`,
						};
					}),
					totalCount,
					pageIndex,
				};
			}
			message.error(msg || '请求失败');
		} catch (err) {
			console.log(err);
		}
	};

	/**
	 * 省市区 详情
	 */
	const handleHighDetails = (row) => {
		console.log(row);
		const { sub_type, org_index } = row;
		oldSubType = sub_type + 1;
		if (sub_type === organizationType.SCHOOL) {
			oldTableType = 'student';
			oldOrgParentIndex = org_index;
			return setIsStudent(true);
		}
		listRef.dataLoad({
			org_parent_index: org_index,
			sub_type: sub_type + 1,
		});
	};

	/**
	 * 学生 详情
	 */
	const handleStudentDetails = (row) => {
		console.log(row);
		setSelectInfo(row);
		setVisible(true);
	};

	/**
	 * 导出方法
	 */
	const exportFn = async () => {
		if (listRef.getList().length === 0) {
			return message.error('暂无数据');
		}
		const url = isStudent
			? '/api/biz-scene/v1/attendance/subsid/stu/exportPageExcel'
			: '/api/biz-scene/v1/attendance/subsid/level/exportPageExcel';
		const { page_num, page_size, ...obj } = oldParams;
		const { search_key, subsid_rule_id, order_field, order_rule, org_parent_index } = obj;
		const newObj = {
			search_key,
			subsid_rule_id,
			order_field,
			order_rule,
			org_parent_index,
			page_num,
			page_size,
		};
		const param = qs.stringify(filterNullParam(newObj));
		window.open(`${url}?${param}`, '_self');
	};

	/**
	 * 比对状态 筛选
	 */
	const handleChange = (value) => {
		listRef.dataLoad({
			status: value,
		});
	};

	/**
	 * 返回上一级
	 */
	const handleBack = async () => {
		const { org_parent_index } = oldParams;
		// 掉接口查找父级！
		try {
			const tree = await getOrganizationsTree({
				type: 1,
				org_index: org_parent_index,
			});
			const { org_parent_index: parentIndex } = tree?.[0] || {};
			const parentTreeNode = await getOrganizationsTree({
				type: 1,
				org_index: parentIndex,
			});
			const { sub_type } = parentTreeNode?.[0] || {};
			setActiveInfo({
				...activeInfo,
				activeOrgIndex: parentIndex,
				subType: sub_type,
			});
		} catch (err) {
			console.log(err);
		}
	};

	return (
		<div className={styles['right-table']}>
			<TopHandle
				type={type}
				setScreenInfo={setScreenInfo}
				listRef={listRef}
				comparator={comparisonData}
				loading={loading}
				activeInfo={activeInfo}
			/>
			<div
				className="fund-comparison-table"
				style={{
					height: 'calc(100% - 68px)',
				}}
			>
				<Table
					key={isStudent ? '1' : '2'}
					rowKey="id"
					hasSerialNo
					columns={
						isStudent
							? studentColumns(handleStudentDetails)
							: highColumns(handleHighDetails)
					}
					transformQuery={transformQuery}
					createPromise={createPromise}
					handleBarOptions={createHandleOptions(
						exportFn,
						isStudent,
						handleBack,
						backDisabled,
					)}
					searchBarOptions={createSearchOptions(isStudent, handleChange)}
					forceFirstLoadDisabled={true}
					setRef={(ref) => setListRef(ref)}
				/>
			</div>
			{visible && (
				<Details
					info={selectInfo}
					subsidRuleId={oldParams.subsid_rule_id}
					onCancel={() => {
						setVisible(false);
					}}
				/>
			)}
		</div>
	);
}

export default RightTable;
