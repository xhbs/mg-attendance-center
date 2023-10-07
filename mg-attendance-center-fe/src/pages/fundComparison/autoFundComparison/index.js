/**
 * 自动资助比对
 */

import React, { useState } from 'react';

import LeftTree from '../../dailyAttendance/dailyAttendanceStatistics/components/leftTreeSelect';
import RightTable from './components/rightTable';
import { organizationType } from '../../dailyAttendance/dailyAttendanceStatistics/utils'
import styles from './index.less';
import { comparisonType } from '../utils';

function AutoFundComparison(props) {
	const [activeInfo, setActiveInfo] = useState({});

	const {
		type = comparisonType.AUTO,
	} = props;

	return (
		<div className={styles['fund-comparison']}>
			<LeftTree
				showSubType={organizationType.SCHOOL}
				setActiveInfo={setActiveInfo}
			/>
			<RightTable
				activeInfo={activeInfo}
				setActiveInfo={setActiveInfo}
				type={type}
			/>
		</div>
	);
}

export default AutoFundComparison;
