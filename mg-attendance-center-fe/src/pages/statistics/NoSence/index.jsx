/*
 * @Author: xiedan WX042
 * @since: 2020-08-27 15:31:25
 * @lastTime: 2020-09-01 19:28:29
 * @FilePath: \biz-scene-coree:\hz\biz-scene-attendance-web\src\pages\statistics\NoSence\index.jsx
 * @Description: 考勤看板
 */
import React, { PureComponent } from 'react';
import Header from './components/Header';
import RealTimeVideo from './components/RealTimeVideo';
import styles from './styles/index.less';

class NoSence extends PureComponent {
	render() {
		return (
			<React.Fragment>
			  <div className={styles.dataBoard}>
				<Header />
				<RealTimeVideo />
			  </div>
			</React.Fragment>
		);
	}
}

export default NoSence;
