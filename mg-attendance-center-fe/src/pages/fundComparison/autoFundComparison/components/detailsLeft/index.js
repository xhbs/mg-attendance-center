/**
 * 详情左侧
 */

import React from 'react';
import { Button } from 'antd';

import { statusType } from '../../../utils'
import notImg from '@/assets/not_img.png';
import passImg from '../../../assets/pass.png';
import notPassImg from '../../../assets/not_pass.png';
import styles from './index.less';

// 定义字段
const field = [
	{
		title: '基础信息',
		list: {
			person_pic: '',
			person_name: '学生姓名',
			person_no: '学号',
			org_name: '班级',
			org_parent_name: '所属学校',
		},
	},
	{
		title: '考勤总览',
		list: {
			time_frame: '考勤时段',
			absent_rate: '缺勤率',
			check_week: '考勤周期',
			normal_num: '正常次数',
			absent_num: '缺勤次数',
		},
	},
];

const nullLst = [null, undefined, ''];

function DetailsLeft(props) {
	const {
		info,
		onCancel,
	} = props;

	return (
		<div className={styles['details-left']}>
			{
				field.map(item => (
					<div
						className={styles['item-box']}
						key={item.title}
					>
						<div className={styles.title}>{item.title}</div>
						<ul>
							{
								Object.keys(item.list).map(key => (
									<li
										className={styles['item-list']}
										key={key}
									>
										<span className={styles.label}>{item.list[key]}</span>
										{
											key === 'person_pic' ? (
												<span className={styles['avatar-box']}>
													<img
														className={styles.avatar}
														src={info[key] || notImg}
														alt='学生照片'
													/>
												</span>
											) : (
												<span
													className={styles.content}
													style={{
														color: key === 'absent_num' ? '#fc2344' : '',
													}}
												>
													{nullLst.includes(info[key]) ? '--' : info[key]}
												</span>
											)
										}
									</li>
								))
							}
						</ul>
						{
							item.title === '考勤总览' && (
								<img
									className={styles.tag}
									src={info.status === statusType.PASS ? passImg : notPassImg}
									alt=''
								/>
							)
						}
					</div>
				))
			}
			<Button
				className={styles.back}
				type='primary'
				onClick={onCancel}
			>
				返回
			</Button>
		</div>
	);
}

export default DetailsLeft;
