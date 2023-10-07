/*
 * @Author: xiedan WX042
 * @since: 2020-09-07 09:51:25
 * @lastTime: 2020-11-06 16:18:15
 * @文件相对于项目的路径: \biz-scene-attendance-web\src\pages\statistics\hooks\useEnumData.js
 * @Description: 
 */
import { useEffect, useState } from 'react';
import { getTaskList } from '@/services/detail';

// 考勤任务
export function useTaskData() {
	const [data, setData] = useState([]);
	useEffect(() => {
		getTaskList()
			.then(res => {
				if (!res || !res.data) return;
				setData(
					res.data.map(v => {
						return {
							code: v.task_id,
							name: v.task_name,
						};
					}),
				);
			})
			.catch(err => {
				throw err;
			});
	}, []);
	return data;
}
