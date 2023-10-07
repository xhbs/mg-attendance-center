import React from 'react';
import RecordTable from './table';
import styles from '../common.less';

export default () => {
	return (
		<div className={styles.record}>
			<RecordTable />
		</div>
	);
};
