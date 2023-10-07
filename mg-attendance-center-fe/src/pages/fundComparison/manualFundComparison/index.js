/**
 * 手动资助对比
 */

import React from 'react';

import AutoFundComparison from '../autoFundComparison';
import { comparisonType } from '../utils';

function ManualFundComparison(props) {
	return (
		<AutoFundComparison type={comparisonType.MANUAL} />
	);
}

export default ManualFundComparison;
