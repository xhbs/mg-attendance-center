/**
 * 日常考勤统计
 */
import React, { useState } from 'react';

import LeftTreeSelect from './components/leftTreeSelect';
import RightTable from './components/RightTable';
import { organizationType } from './utils';
import styles from './index.less';

function DailyAttendanceStatistics() {
	// tree 激活的节点信息
	const [activeInfo, setActiveInfo] = useState({});
	return (
		<div className={styles['daily-attendance-statistics']}>
			<LeftTreeSelect
				setActiveInfo={setActiveInfo}
				showSubType={organizationType.SCHOOL}
			/>
			<div className={styles['right-table']}>
				<RightTable
					activeInfo={activeInfo}
					setActiveInfo={setActiveInfo}
				/>
			</div>
		</div>
	);
}

export default DailyAttendanceStatistics;
