/**
 * 上方操作区
 * 资助名单、考勤时段、比对规则
 */
import React, { useEffect, useRef, useState } from 'react';
import { Icon, Button, message } from 'antd';
import { findSubListindex } from '../../../../../services/fundComparison';
import TopHandleModal from '../topHandleModal';
import styles from './index.less';
import { REQUEST_SUCCESS } from '../../../../dailyAttendance/dailyAttendanceStatistics/utils';
import { comparisonType } from '../../../utils';
import { uploadExcel } from '../../../../../services/fundComparison';
import TemplateSelect from '../templateSelect';

export const TITLE_ENUM = [
	{
		name: '资助名单',
		code: 1,
		icon: 'calendar',
		field: 'list',
		fieldBefore: '',
		fieldAfter: '',
	},
	{
		name: '学年',
		code: 2,
		icon: 'hz-wait',
		field: 'year',
		fieldBefore: '',
		fieldAfter: '',
	},
	{
		name: '学期',
		code: 3,
		icon: 'hz-wait',
		field: 'semester',
		fieldBefore: '',
		fieldAfter: '',
	},
	{
		name: '考勤时段',
		code: 4,
		icon: 'hz-wait',
		field: 'timeFrameText',
		fieldBefore: '',
		fieldAfter: '',
	},
	{
		name: '比对规则',
		code: 5,
		icon: 'hz-set',
		field: 'rules',
		fieldBefore: '缺勤率≥',
		fieldAfter: '%，则比对不通过',
	},
];

export const TITLE_TYPE = {
	LIST: 1,
	YEAR: 2,
	SEMESTER: 3,
	TIME_FRAME: 4,
	RULES: 5,
};

// 上传名单类型
let uploadType = 0;

function TopHandle(props) {
	// 是否显示弹窗
	const [visible, setVisible] = useState(false);
	// 是否显示名单模板选择弹框
	const [visibleTPS, setVisibleTPS] = useState(false);
	// 弹框类型
	const [popupType, setPopupType] = useState('');
	// 操作项信息
	const [handleInfo, setHandleInfo] = useState({
		rules: 50,
		rule: 1,
	});
	const [projectList, setProjectList] = useState([]);

	// input['file'] ref
	const inputRef = useRef(null);

	const { type, setScreenInfo, comparator, loading, activeInfo } = props;

	useEffect(() => {
		setScreenInfo(handleInfo);
	}, [handleInfo, setScreenInfo]);
	const getFindSubListindex = async () => {
		const { success, data } = await findSubListindex();
		if (success) {
			setProjectList(data);
		}
	};
	useEffect(() => {
		getFindSubListindex();
	}, []);
	/**
	 * 点击显示弹窗
	 */
	const handleShowPopup = (code) => {
		setPopupType(code);
		setVisible(true);
	};

	/**
	 * 修改数据
	 */
	const handleSelect = (info) => {
		const { project, time_frame } = info;
		const params = {
			...handleInfo,
			...info,
		};
		if (project) {
			const dateText = project?.split('+')[1];
			// const projectText = `项目: ${getEnumValue(projectEnum, project)}`;
			params.list = dateText;
		}
		if (time_frame) {
			const [startTime, endTime] = time_frame;
			params.timeFrameText = `${startTime.format('YYYY-MM-DD')}~${endTime.format(
				'YYYY-MM-DD',
			)}`;
		}
		setHandleInfo(params);
	};

	/**
	 * 重置
	 */
	const handleReset = () => {
		setHandleInfo({
			rules: 50,
		});
	};

	/**
	 * 点击上传名单
	 */
	const handleUpload = () => {
		setVisibleTPS(true);
	};

	/**
	 * 上传
	 */
	const onSelect = (type) => {
		uploadType = type;
		inputRef.current.value = '';
		inputRef.current.click();
	};

	/**
	 * 上传方法
	 */
	const handleChangeFile = async () => {
		const [file] = inputRef.current.files;
		const { name } = file;
		const excel = ['.xlsx', '.xls', '.csv'];
		if (!excel.some((item) => name.includes(item))) {
			return message.error('格式错误，请上传excel。');
		}
		const formData = new FormData();
		formData.append('file', file);
		try {
			const res = await uploadExcel(uploadType, formData);
			const { data, error_code, message: msg } = res;
			if (error_code === REQUEST_SUCCESS) {
				console.log(data);
				const { message: mg, resp_code, sub_list_index } = data;
				setVisibleTPS(false);
				inputRef.current.value = '';
				// 失败
				if (resp_code === 0) {
					return message.error(mg);
				}
				// 部分失败
				if (resp_code === 2) {
					message.error(mg);
				}
				return setHandleInfo({
					...handleInfo,
					list: name,
					listText: sub_list_index,
				});
			}
			message.error(msg || '上传失败。');
		} catch (err) {
			console.log(err);
		}
	};

	/**
	 * 清空上传的文件
	 */
	const handleCloseFile = () => {
		setHandleInfo({
			...handleInfo,
			list: '',
		});
	};
	// 判断 比对类型  是否是手动比对  如果是手动比对就需要上传文件
	const renderItem = (item) => {
		if (type === comparisonType.MANUAL && item.code === TITLE_TYPE.LIST) {
			return (
				<span className={styles.content}>
					{!handleInfo[item.field] ? (
						<a onClick={handleUpload}>点击上传名单</a>
					) : (
						<>
							{handleInfo[item.field]}
							<Icon
								className={styles.close}
								type="hz-close"
								theme="filled"
								onClick={handleCloseFile}
							/>
						</>
					)}

					<input ref={inputRef} type="file" hidden={true} onChange={handleChangeFile} />
				</span>
			);
		}
		return (
			<>
				<span className={styles.content}>
					{`${
						(item.name === '比对规则' && handleInfo.rule === 0 ? '日常' : '') +
						(item.name === '比对规则' && handleInfo.rule === 1 ? '点名' : '') +
						item.fieldBefore +
						(handleInfo[item.field] || '--') +
						item.fieldAfter
					}`}
				</span>
				<Icon
					className={styles['icon']}
					type={item.icon}
					onClick={() => {
						handleShowPopup(item.code);
					}}
				/>
			</>
		);
	};

	return (
		<div className={styles['box']}>
			<ul className={styles['top-handle']} style={{ borderTopRightRadius: 0, flex: 1 }}>
				{TITLE_ENUM.map((item) => (
					<li className={styles['handle-item']} key={item.code}>
						<span>{item.name}</span>
						{renderItem(item)}
					</li>
				))}
			</ul>
			<ul className={styles['top-handle']} style={{ borderTopLeftRadius: 0 }}>
				<li className={styles['btns']}>
					<Button
						className={styles['primary-btn']}
						type="primary"
						loading={loading}
						disabled={
							!handleInfo.list ||
							!handleInfo.timeFrameText ||
							!handleInfo.rules ||
							!activeInfo.activeOrgIndex
						}
						onClick={(e) => {
							comparator();
							e.target.blur();
						}}
					>
						比对
					</Button>
					<Button onClick={handleReset}>重置</Button>
				</li>
				{visible && (
					<TopHandleModal
						title={popupType}
						visible={visible}
						onCancel={() => {
							setVisible(false);
						}}
						info={handleInfo}
						setInfo={handleSelect}
						projectList={projectList}
					/>
				)}
				{visibleTPS && (
					<TemplateSelect
						title="选择上传名单"
						visible={visibleTPS}
						onSelect={onSelect}
						onCancel={() => {
							setVisibleTPS(false);
						}}
					/>
				)}
			</ul>
		</div>
	);
}

export default TopHandle;
